package com.chen.gulimallproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.chen.common.valid.AddGroup;
import com.chen.common.valid.UpdateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 商品三级分类
 * 
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-13 13:25:34
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@NotNull(message = "更新时分类id不能为空",groups = {UpdateGroup.class})
	@Null(message = "新增时不能带分类id",groups = {AddGroup.class})
	@TableId
	private Long catId;
	/**
	 * 分类名称
	 */
	@NotNull(message = "分类名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	@TableLogic(value = "1",delval = "0")
	private Integer showStatus;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;

	/*
	*  子类型
	* */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntity> children;
}
