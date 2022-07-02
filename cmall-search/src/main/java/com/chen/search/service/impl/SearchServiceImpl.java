package com.chen.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chen.common.constant.ESIndexEnum;
import com.chen.common.constant.EsContant;
import com.chen.common.to.SkuEsModel;
import com.chen.search.config.ElasticSearchConfig;
import com.chen.search.service.SearchService;
import com.chen.search.vo.SearchParam;
import com.chen.search.vo.SearchResponseVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
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
        SearchRequest request = generateSearchRequest(param);

        //获得结果
        SearchResponseVo responseVo = new SearchResponseVo();
        SearchResponse response;
        try {
            response = client.search(request, ElasticSearchConfig.COMMON_OPTIONS);
            System.out.println(response.toString());

            //products
            SearchHits hits = response.getHits();
            SearchHit[] hitArray = hits.getHits();
            List<SkuEsModel> skuEsModelList = new ArrayList<>();
            for(SearchHit hit:hitArray)
            {
                String hitSourceString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(hitSourceString,new TypeReference<SkuEsModel>(){});
                skuEsModelList.add(esModel);
            }
            responseVo.setProducts(skuEsModelList);
            //获得聚合结果
            Aggregations aggs = response.getAggregations();
            //总页数
            responseVo.setTotalPage(hitArray.length%EsContant.PRODUCT_PAGESIZE==0?hitArray.length/EsContant.PRODUCT_PAGESIZE:
                    hitArray.length/EsContant.PRODUCT_PAGESIZE+1);
            //总记录数
            responseVo.setTotal(Long.valueOf(hitArray.length));
            //当前分类下的其他品牌
            List<SearchResponseVo.BrandVo> brandVos = new ArrayList<>();
            Terms brandAgg = aggs.get("brand_agg");
            for(Terms.Bucket bucket:brandAgg.getBuckets())
            {
                SearchResponseVo.BrandVo brandVo = new SearchResponseVo.BrandVo();
                brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));
                //子聚合
                Aggregations brandSubAgg = bucket.getAggregations();
                Terms brandName = brandSubAgg.get("brand_name");
                brandVo.setBrandName(brandName.getBuckets().get(0).getKeyAsString());
                Terms brandLogo = brandSubAgg.get("brand_logo");
                brandVo.setBrandLogo(brandLogo.getBuckets().get(0).getKeyAsString());
                brandVos.add(brandVo);
            }
            responseVo.setBrands(brandVos);

            //所有不同分类
            List<SearchResponseVo.CatalogVo> catalogVos = new ArrayList<>();
            Terms catalogAgg =aggs.get("catalog_agg");
            for (Terms.Bucket bucket:catalogAgg.getBuckets())
            {
                SearchResponseVo.CatalogVo catalogVo = new SearchResponseVo.CatalogVo();
                catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
                Aggregations catalogSubAgg = bucket.getAggregations();
                Terms catalogName = catalogSubAgg.get("catalog_name");
                catalogVo.setCatalogName(catalogName.getBuckets().get(0).getKeyAsString());
                catalogVos.add(catalogVo);
            }
            responseVo.setCatalogs(catalogVos);

            //所有不同属性以及属性值
            List<SearchResponseVo.AttrVo> attrVos = new ArrayList<>();
            ParsedNested attrAgg = aggs.get("attr_agg");
            ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id");
            for(Terms.Bucket bucket:attrIdAgg.getBuckets())
            {
                SearchResponseVo.AttrVo attrVo = new SearchResponseVo.AttrVo();
                //属性id
                attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
                //属性名字
                Terms attrName = bucket.getAggregations().get("attr_name");
                attrVo.setAttrName(attrName.getBuckets().get(0).getKeyAsString());
                //属性值
                List<String> valueList = new ArrayList<>();
                Terms attrValue = bucket.getAggregations().get("attr_value");
                for(Terms.Bucket value:attrValue.getBuckets())
                {
                    valueList.add(value.getKeyAsString());
                }
                attrVo.setAttrValue(valueList);
                attrVos.add(attrVo);
            }
            responseVo.setAttrs(attrVos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseVo;
    }

    private SearchRequest  generateSearchRequest(SearchParam param)
    {
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
        //价格区间
        if(StringUtils.hasLength(param.getPriceRange()))
        {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getPriceRange().split("_");
            if(s.length==2)
            {
                rangeQuery.gte(s[0]).lte(s[1]);
            }else {
                if(param.getPriceRange().startsWith("_"))
                {
                    rangeQuery.lte(s[0]);
                }else
                    rangeQuery.gte(s[0]);
            }
            filter.add(rangeQuery);
        }
        sourceBuilder.query(boolQuery);
        //排序
        if(StringUtils.hasLength(param.getSortRule()))
        {
            String[] s = param.getSortRule().split("_");
            sourceBuilder.sort(s[0],s[1].equalsIgnoreCase("asc")? SortOrder.ASC:SortOrder.DESC);
        }
        //分页
        sourceBuilder.from((param.getPageNum()-1)*EsContant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsContant.PRODUCT_PAGESIZE);

        //高亮
        if(StringUtils.hasLength(param.getKeyword()))
        {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        //聚合
        //品牌信息
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        //品牌子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_logo").field("brandImg").size(1));
        sourceBuilder.aggregation(brandAgg);
        //分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catelogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name").field("catelogName").size(1));
        sourceBuilder.aggregation(catalogAgg);
        //属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg","attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id");
        attrIdAgg.field("attrs.attrId").size(20);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value").field("attrs.attrValue").size(20));

        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        System.out.println("DSL="+sourceBuilder.toString());
        request.source(sourceBuilder);
        return request;
    }
}
