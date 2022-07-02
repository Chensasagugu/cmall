package com.chen.common.constant;

/*
* 枚举在es中的index名
* */
public enum ESIndexEnum {
    PRODUCT_INDEX("product");
    String indexName;

    ESIndexEnum(String indexName){
        this.indexName = indexName;
    }

    public String getIndexName()
    {
        return indexName;
    }
}
