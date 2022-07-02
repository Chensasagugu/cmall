package com.chen.search.service;

import com.chen.search.vo.SearchParam;
import com.chen.search.vo.SearchResponseVo;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
    SearchResponseVo search(SearchParam param);
}
