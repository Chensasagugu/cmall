package com.chen.cmallproduct.vo;

import lombok.Data;

/**
 * @author chen
 * @date 2022.04.29 10:00
 */
@Data
public class AttrRespVo extends AttrVo{

    /**
     * 分类的名字
     */
    private String catelogName;
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 分组名字
     */
    private String groupName;
    /**
     * 所属分类路径
     */
    private Long[] catelogPath;
}
