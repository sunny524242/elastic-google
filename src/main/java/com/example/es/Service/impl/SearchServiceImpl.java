package com.example.es.Service.impl;

import com.alibaba.fastjson.JSON;
import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import com.example.es.common.vo.SearchParam;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;

import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.settings.Settings.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


/**
 * Created by Administrator on 2017/11/12 0012.
 */
@Service
public class SearchServiceImpl implements SearchService{

    private static TransportClient transportClient;
    static {
        try {
            InetSocketTransportAddress localhost = new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300);
            transportClient = new PreBuiltTransportClient(EMPTY).addTransportAddress(localhost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索测试(5.6.4)
     * @param searchParam
     * @return
     * @throws UnknownHostException
     */
    public JsonResult queryTest(SearchParam searchParam) {
        // on startup

        MatchQueryBuilder titleQuery = matchQuery("title", searchParam.getSearchContent());
        MatchQueryBuilder textQuery = matchQuery("text", searchParam.getSearchContent());

        BoolQueryBuilder should = boolQuery().should(titleQuery).should(textQuery);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title",100,1);
        highlightBuilder.field("text",500,1);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");

        SearchResponse searchResponse = transportClient.prepareSearch("test")
                .setTypes("wiki_article")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(should)
                .highlighter(highlightBuilder)
                .setFrom(searchParam.getPage()*searchParam.getPageSize()).setSize(searchParam.getPageSize())
                .execute()
                .actionGet();

        HashMap<String, Object> rsMap = new HashMap<>();
        SearchHits hits = searchResponse.getHits();
        rsMap.put("total",hits.totalHits());
        ArrayList<Object> hitsRs = new ArrayList<>();
        rsMap.put("take",((double)searchResponse.getTookInMillis())/1000);
        rsMap.put("hits",hitsRs);
        for (SearchHit hit : hits) {
            HashMap<String, Object> singleHit = new HashMap<>();
            singleHit.put("index",hit.getIndex());
            singleHit.put("type",hit.getType());//需要转换为中文意义名
            singleHit.put("id",hit.getId());//需要补充对应链接名
            singleHit.put("score",hit.getScore());
            singleHit.put("_source",hit.getSource());

            singleHit.put("link","http://www.baidu.com");
            //用高亮字段替换搜索字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (String fieldKey : highlightFields.keySet()) {
                HighlightField field = highlightFields.get(fieldKey);
                if (field == null) {
                    continue;
                }
                Text[] titleTexts = field.fragments();
                String value = "";
                for (Text text : titleTexts) {
                    value += text;
                }
                hit.getSource().put(fieldKey, value);
            }

            hitsRs.add(singleHit);
        }
        //transportClient.close();
        System.out.println(JSON.toJSONString(rsMap));
        return new JsonResult(200,"java测试成功", rsMap);
    }
}
