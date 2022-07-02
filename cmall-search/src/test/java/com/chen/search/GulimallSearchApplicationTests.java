package com.chen.search;

import com.alibaba.fastjson.JSON;
import com.chen.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        String s = "白;黑;红";
        List<String> values = Arrays.asList(s.split(";"));
        for(String str:values)
            System.out.println(str);
    }

    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setName("chne");
        user.setPassword("123456");
        String userJson = JSON.toJSONString(user);
        indexRequest.source(userJson, XContentType.JSON);
        //执行request
        IndexResponse response = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(response);
    }
    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");

        //指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //聚合
        //按照年龄分布聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(100);
        //计算每个年龄的平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        ageAgg = ageAgg.subAggregation(balanceAvg);
        sourceBuilder.aggregation(ageAgg);
        searchRequest.source(sourceBuilder);
        System.out.println("检索条件："+sourceBuilder.toString());
        //执行检索
        SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        //分析结果
        //System.out.println(response.toString());
        //获取所有查到的数据
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit:searchHits){
            System.out.println(hit.getSourceAsString());
            //TODO 将这个json字符串转换为javabean
        }
        //获得这次检索的分析信息
        Aggregations aggregations = response.getAggregations();
        Terms agg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : agg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄"+keyAsString);
            Aggregations subAgg = bucket.getAggregations();
            Avg avg = subAgg.get("balanceAvg");
            System.out.println("该年龄段平均薪资"+avg.getValue());

        }

    }
    @Data
    static class User{
        String name;
        String password;
    }
}
