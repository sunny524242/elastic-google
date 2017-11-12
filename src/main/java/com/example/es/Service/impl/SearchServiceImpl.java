package com.example.es.Service.impl;

import com.example.es.Service.SearchService;
import com.example.es.common.vo.JsonResult;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/11/12 0012.
 */
@Service
public class SearchServiceImpl implements SearchService{
    public JsonResult simpleSearch(String searchContent){
        //发起ajax请求


        return new JsonResult(200,"获取搜索成功","搜索结果");
    }
}
