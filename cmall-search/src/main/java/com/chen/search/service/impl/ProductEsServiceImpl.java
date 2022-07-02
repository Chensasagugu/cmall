package com.chen.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.chen.common.to.SkuEsModel;
import com.chen.search.config.ElasticSearchConfig;
import com.chen.search.service.ProductEsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author chen
 * @date 2022.05.25 14:16
 */
@Slf4j
@Service("EsService")
public class ProductEsServiceImpl implements ProductEsService {
    @Autowired
    RestHighLevelClient client;

    @Override
    public boolean batchIndexProduct(List<SkuEsModel> objects, String indexName) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for(SkuEsModel o:objects)
        {
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.id(o.getSkuId().toString());
            String jsonString = JSON.toJSONString(o);
            indexRequest.source(jsonString,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        boolean b = bulkResponse.hasFailures();
        for (BulkItemResponse item : bulkResponse.getItems()) {
            if(item.isFailed())
                log.error("skuId={}的商品上架出错，错误信息是：{}",item.getId(),item.getFailureMessage());
        }
        return !b;
    }
}
