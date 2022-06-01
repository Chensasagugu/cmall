package com.chen.gulimallsearch.service;

import com.chen.gulimallsearch.vo.SearchParam;
import com.chen.gulimallsearch.vo.SearchResponseVo;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
    SearchResponseVo search(SearchParam param);
}
