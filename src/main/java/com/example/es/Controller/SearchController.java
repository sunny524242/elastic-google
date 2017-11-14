package com.example.es.Controller;


import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import com.example.es.common.vo.SearchParam;

import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


import static org.elasticsearch.common.settings.Settings.EMPTY;


/**
 * 搜索接口
 */
@Controller
@RequestMapping(value = "search")
public class SearchController {

    @Autowired
    SearchService searchService;


    /**
     * 创建索引
     * 其实一般不会在代码中创建索引，就像很少会在代码中创建数据库一样
     * @return
     * @throws UnknownHostException
     */
    @RequestMapping(value = "createIndexTest")
    @ResponseBody
    public JsonResult createIndexTest() throws UnknownHostException {
        // on startup
        TransportClient transportClient = new PreBuiltTransportClient(EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        transportClient.prepareIndex("test2","wiki_article")
        .setSource();
        // on shutdown
        transportClient.close();
        return new JsonResult(200,"java测试成功","测试结果");
    }


    /**
     * 搜索功能测试（对应测试页面/index.html）
     * @param searchParam
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "queryTest")
    @ResponseBody
    public JsonResult queryTest(SearchParam searchParam) throws IOException {
        return searchService.queryTest(searchParam);
    }


}
