package com.chen.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.common.utils.PageUtils;
import com.chen.gulimallware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author chen
 * @email chen@gmail.com
 * @date 2022-04-15 10:32:41
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

