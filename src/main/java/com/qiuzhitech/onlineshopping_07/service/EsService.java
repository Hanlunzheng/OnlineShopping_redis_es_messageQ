package com.qiuzhitech.onlineshopping_07.service;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingCommodity;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
@Slf4j
public class EsService {
    public static final String COMMODITY_INDEX= "commodity";
    @Resource
    RestHighLevelClient  esClient;
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public int addCommodity(OnlineShoppingCommodity onlineShoppingCommodity) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(COMMODITY_INDEX);
        boolean isExist;
        isExist = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!isExist) {
            //create commodity index into ES
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .startObject("dynamic_templates")
                    .startObject("strings")
                    .field("match_mapping_type", "string")
                    .startObject("mapping")
                    .field("type", "text")
                    .field("analyzer", "ik_smart")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(COMMODITY_INDEX);
            createIndexRequest.source(builder);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            if (!createIndexResponse.isAcknowledged()) {
                log.error("Failed to create ES Index {}", COMMODITY_INDEX );
                return RestStatus.INTERNAL_SERVER_ERROR.getStatus();
        }
        }
        String jsonDoc = JSON.toJSONString(onlineShoppingCommodity);
        IndexRequest indexRequest = new IndexRequest(COMMODITY_INDEX).source(jsonDoc, XContentType.JSON);
        indexRequest.id(onlineShoppingCommodity.getCommodityId().toString());
        IndexResponse res = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("AddCommodity To ES, Commodity: {}, response: {}", jsonDoc, res);
        return res.status().getStatus();
    }

    public List<OnlineShoppingCommodity> searchCommodity(String keyword, int from, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(COMMODITY_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder= multiMatchQuery(keyword, "commodityName", "commodityDesc");
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        sourceBuilder.query(multiMatchQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<OnlineShoppingCommodity> result = new ArrayList<>();
        for (SearchHit hit : hits1) {
            OnlineShoppingCommodity commodity = JSON.parseObject(hit.getSourceAsString(), OnlineShoppingCommodity.class);
            result.add(commodity);
        }
        return result;
    }
    }
