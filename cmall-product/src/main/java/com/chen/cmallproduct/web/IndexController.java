package com.chen.cmallproduct.web;

import com.chen.cmallproduct.entity.CategoryEntity;
import com.chen.cmallproduct.service.CategoryService;
import com.chen.cmallproduct.vo.web.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author chen
 * @date 2022.05.29 11:25
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //查出所有一级分类
        List<CategoryEntity> level1Categories = categoryService.getLevel1Categories();

        model.addAttribute("categories",level1Categories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catelog.json")
    public Map<String,List<Catalog2Vo>> getCatelogJSON(){
        Map<String, List<Catalog2Vo>> catelog = categoryService.getCatelogJSON();
        return catelog;
    }
}
