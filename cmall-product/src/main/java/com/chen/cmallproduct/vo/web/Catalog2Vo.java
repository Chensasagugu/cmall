package com.chen.cmallproduct.vo.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chen
 * @date 2022.05.29 11:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2Vo {
    private String catalog1Id;
    private List<Catalog3Vo> catalog3List;
    private String id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catalog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }
}
