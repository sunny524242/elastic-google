package com.example.es.Controller;

import com.alibaba.fastjson.JSON;
import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 搜索接口
 */
@Controller
@RequestMapping(value = "search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = "simple")
    @ResponseBody
    public JsonResult simpleSearch(String searchContent){
        return searchService.simpleSearch(searchContent);
    }

    @RequestMapping(value = "createIndexTest")
    @ResponseBody
    public JsonResult test() throws UnknownHostException {
        // on startup
        TransportClient client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        client.prepareIndex("test2","wiki_article")
        .setSource();
        // on shutdown
        client.close();
        return new JsonResult(200,"java测试成功","测试结果");
    }


    @RequestMapping(value = "queryTest")
    @ResponseBody
    public JsonResult queryTest(String searchContent) throws IOException {
        // on startup
        TransportClient client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));


        TermQueryBuilder titleQuery = termQuery("title", searchContent);
        TermQueryBuilder textQuery = termQuery("text", searchContent);
        BoolQueryBuilder should = boolQuery().should(titleQuery).should(textQuery);
        ;
        SearchResponse searchResponse = client.prepareSearch("test")
                .setTypes("wiki_article")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(should)             // Query
                //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                .setFrom(0).setSize(10)
                .execute()
                .actionGet();
        //System.out.println("\n\n"+searchResponse.toString());

        HashMap<String, Object> rsMap = new HashMap<>();
        SearchHits hits = searchResponse.getHits();
        rsMap.put("total",hits.totalHits());
        ArrayList<Object> hitsRs = new ArrayList<>();
        rsMap.put("hits",hitsRs);
        for (SearchHit hit : hits) {
            HashMap<String, Object> singleHit = new HashMap<>();
            singleHit.put("index",hit.getIndex());
            singleHit.put("type",hit.getType());//需要转换为中文意义名
            singleHit.put("id",hit.getId());//需要补充对应链接名
            singleHit.put("score",hit.getScore());
            singleHit.put("_source",hit.getSource());
            if (hit.getSource().get("text").toString().length()>500) {
                hit.getSource().put("text", hit.getSource().get("text").toString().substring(0, 500)+"...");
            }
            singleHit.put("highlight",hit.getHighlightFields());

                singleHit.put("link","http://www.baidu.com");
            hitsRs.add(singleHit);
        }
        client.close();
        return new JsonResult(200,"java测试成功", rsMap);
    }


}
