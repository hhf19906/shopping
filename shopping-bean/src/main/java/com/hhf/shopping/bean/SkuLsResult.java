package com.hhf.shopping.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

//用户根据的输入条件查询到的数据，输出对象
@Data
public class SkuLsResult  implements Serializable {
    List<SkuLsInfo> skuLsInfoList;//

    long total;//查出来的总条数

    long totalPages;//总页数

    List<String> attrValueIdList;
}
