package com.example.es.Controller;


import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import com.example.es.common.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * 搜索接口
 */
@Controller
@RequestMapping(value = "search")
public class SearchController {

    @Autowired
    SearchService searchService;


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
