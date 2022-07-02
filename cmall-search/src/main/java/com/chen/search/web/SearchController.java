package com.chen.search.web;

import com.chen.search.service.SearchService;
import com.chen.search.vo.SearchParam;
import com.chen.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chen
 * @date 2022.05.30 14:04
 */
@Controller
public class SearchController {
    @Autowired
    SearchService searchService;

    @ResponseBody
    @GetMapping({"/list"})
    public SearchResponseVo list(SearchParam param, Model model)
    {
        System.out.println(param.toString());
        SearchResponseVo response = searchService.search(param);
        //return "list";
        return response;
    }
}
