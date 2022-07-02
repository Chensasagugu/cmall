package com.chen.search.service;

import com.chen.common.to.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author chen
 * @date 2022.05.25 14:15
 */
@Service
public interface ProductEsService {

    boolean batchIndexProduct(List<SkuEsModel> objects, String indexName) throws IOException;
}
