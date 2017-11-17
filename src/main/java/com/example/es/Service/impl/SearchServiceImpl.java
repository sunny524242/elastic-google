package com.example.es.Service.impl;

import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import com.example.es.common.vo.SearchParam;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;


/**
 * 搜索Service实现示例
 */
@Service
public class SearchServiceImpl implements SearchService{

    //客户端
    private static RestHighLevelClient client;
    //客户端连接服务端
    static {
        try {
            getClientWithXpack();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //当服务端未安装X-PACK插件时连接方式
    private static void getClientWithXpack() throws UnsupportedEncodingException {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.1.117", 9200, "http"));
        String auth="Basic "+Base64.getEncoder().encode("elastic:changeme".getBytes("UTF-8")).toString();
        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", auth)};
        builder.setDefaultHeaders(defaultHeaders);
        RestClient lowLevelRestClient = builder.build();

        client =new RestHighLevelClient(lowLevelRestClient);

    }

    //当服务端安装了X-PACK插件时连接方式
    private static void getClientWithoutXpack(){
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        RestClient lowLevelRestClient = builder.build();
        client = new RestHighLevelClient(lowLevelRestClient);

    }





     /**
     * 搜索测试(V5.6.4)
     * @param searchParam 搜索参数（关键词+分页信息）
     * @return
     * @throws UnknownHostException
     */
    public JsonResult queryTest(SearchParam searchParam) throws IOException {

        SearchRequest searchRequest = new SearchRequest("test");
        searchRequest.types("wiki_article");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //匹配查询
        MatchQueryBuilder titleQuery = new MatchQueryBuilder("title", searchParam.getSearchContent());
        MatchQueryBuilder textQuery = new MatchQueryBuilder("text", searchParam.getSearchContent());
        //或逻辑
        BoolQueryBuilder should = boolQuery().should(titleQuery).should(textQuery);
        //文本高亮配置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title",100,1);
        highlightBuilder.field("text",500,1);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");


        searchSourceBuilder.query(should)
                .highlighter(highlightBuilder)
                .from(searchParam.getPage()*searchParam.getPageSize())
                .size(searchParam.getPageSize())
                .timeout(new TimeValue(60, TimeUnit.SECONDS));

        SearchRequest source = searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        //整理搜索结果，方便前端进行展示
        HashMap<String, Object> rsMap = new HashMap<>();
        //搜索结果数量
        SearchHits hits = searchResponse.getHits();
        rsMap.put("total",hits.totalHits());
        ArrayList<Object> hitsRs = new ArrayList<>();
        //搜索用时
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
        return new JsonResult(200,"java测试成功", rsMap);
    }
}
