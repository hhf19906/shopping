package com.hhf.shopping.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户输入的参数
 */
@Data
public class SkuLsParams implements Serializable {
    String  keyword;

    String catalog3Id; //三级分类id

    String[] valueId; //平台属性值id

    int pageNo=1; //第一页

    int pageSize=20; //总共20页
}
