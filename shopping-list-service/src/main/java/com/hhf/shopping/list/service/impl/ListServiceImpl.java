package com.hhf.shopping.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hhf.shopping.bean.SkuLsInfo;
import com.hhf.shopping.bean.SkuLsParams;
import com.hhf.shopping.bean.SkuLsResult;
import com.hhf.shopping.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;

    public static final String ES_INDEX = "shopping";

    public static final String ES_TYPE = "SkuInfo";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {

        // 保存数据
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult documentResult = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultForSearch(searchResult, skuLsParams);
        return skuLsResult;
    }

    private SkuLsResult makeResultForSearch(SearchResult searchResult, SkuLsParams skuLsParams) {
        SkuLsResult skuLsResult = new SkuLsResult();
        // 声明一个集合来存储SkuLsInfo 数据
        ArrayList<SkuLsInfo> skuLsInfoArrayList = new ArrayList<>();
        // 集合赋值
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            // 获取skuName 的高亮
            if (hit.highlight!=null && hit.highlight.size()>0){
                Map<String, List<String>> highlight = hit.highlight;
                List<String> list = highlight.get("skuName");
                String skuNameHI = list.get(0);// 表示获取集合中的第一个数据
                skuLsInfo.setSkuName(skuNameHI);
            }
            skuLsInfoArrayList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoArrayList);
        skuLsResult.setTotal(searchResult.getTotal());
        // 计算总页数
        long totalPages = (searchResult.getTotal()+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
        // 声明一个集合来存储平台属性值Id
        ArrayList<String> stringArrayList = new ArrayList<>();
        // 获取平台属性值Id
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        // 循环遍历
        for (TermsAggregation.Entry bucket : buckets) {
            String valueId = bucket.getKey();
            stringArrayList.add(valueId);
        }

        skuLsResult.setAttrValueIdList(stringArrayList);
        return skuLsResult;

    }

    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        // 定义一个查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 创建 bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 判断keyword 是否为空
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            // 创建match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            // 创建must
            boolQueryBuilder.must(matchQueryBuilder);
            // 设置高亮
            HighlightBuilder highlighter = searchSourceBuilder.highlighter();

            // 设置高亮的规则
            highlighter.field("skuName");
            highlighter.preTags("<span style=color:red>");
            highlighter.postTags("</span>");

            // 将设置好的高亮对象放入查询器中
            searchSourceBuilder.highlight(highlighter);
        }

        // 判断平台属性值Id
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            // 循环
            for (String valueId : skuLsParams.getValueId()) {
                // 创建term
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                // 创建filter 并添加term
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        // 判断 三级分类Id
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 设置分页
        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        // size 每页显示的条数
        searchSourceBuilder.size(skuLsParams.getPageSize());

        // 设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        // 聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr");
        groupby_attr.field("skuAttrValueList.valueId");
        // 放入查询器中
        searchSourceBuilder.aggregation(groupby_attr);

        String query = searchSourceBuilder.toString();
        System.out.println("query:=" + query);
        return query;




    }
}
