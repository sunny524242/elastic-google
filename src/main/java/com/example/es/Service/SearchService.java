package com.example.es.Service;

import com.example.es.common.vo.JsonResult;
import com.example.es.common.vo.SearchParam;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/11/12 0012.
 */
public interface SearchService {

    /**
     * 搜索测试
     * @param searchParam
     * @return
     * @throws UnknownHostException
     */
    JsonResult queryTest(SearchParam searchParam) throws IOException;
}
