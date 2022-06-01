package com.chen.gulimallsearch.service.impl;

import com.chen.common.constant.ESIndexEnum;
import com.chen.gulimallsearch.config.ElasticSearchConfig;
import com.chen.gulimallsearch.service.SearchService;
import com.chen.gulimallsearch.vo.SearchParam;
import com.chen.gulimallsearch.vo.SearchResponseVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author chen
 * @date 2022.05.30 15:01
 */
@Service("SearchService")
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResponseVo search(SearchParam param) {
        SearchRequest request = new SearchRequest();
        request.indices(ESIndexEnum.PRODUCT_INDEX.getIndexName());

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<QueryBuilder> must = boolQuery.must();
        List<QueryBuilder> filter = boolQuery.filter();
        //关键字
        if(StringUtils.hasLength(param.getKeyword()))
        {
            QueryBuilder keyWordMatch = QueryBuilders.matchQuery("skuTitle",param.getKeyword());
            must.add(keyWordMatch);
        }
        //分类Id
        if(param.getCatalogId()!=null)
        {
            QueryBuilder catalogIdTerm = QueryBuilders.termQuery("catelogId",param.getCatalogId());
            filter.add(catalogIdTerm);
        }
        //品牌id
        if(param.getBrandId()!=null&&param.getBrandId().size()!=0)
        {
            QueryBuilder brandsTerms = QueryBuilders.termsQuery("brandId",param.getBrandId());
            filter.add(brandsTerms);
        }
        //有没有库存
        if(param.getHasStock()!=null)
        {
            boolean hasStock = param.getHasStock()==1?true:false;
            QueryBuilder hasStockTerm = QueryBuilders.termsQuery("hasStock",hasStock);
            filter.add(hasStockTerm);
        }
        //属性
        if(param.getAttrs()!=null&&param.getAttrs().size()!=0)
        {
            for(String attr:param.getAttrs())
            {
                BoolQueryBuilder attrBool = QueryBuilders.boolQuery();
                List<QueryBuilder> attrMust = attrBool.must();
                String[] str = attr.split("_");
                QueryBuilder attrQuery = QueryBuilders.termQuery("attrs.attrId",str[0]);
                List<String> values = Arrays.asList(str[1].split(";"));
                QueryBuilder valueQuery = QueryBuilders.termsQuery("attrs.attrValue",values);
                attrMust.add(attrQuery);
                attrMust.add(valueQuery);
                QueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs",attrBool, ScoreMode.None);
                filter.add(nestedQuery);
            }
        }
        sourceBuilder.query(boolQuery);
        request.source(sourceBuilder);

        //获得结果
        SearchResponse response;
        try {
            response = client.search(request, ElasticSearchConfig.COMMON_OPTIONS);
            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
