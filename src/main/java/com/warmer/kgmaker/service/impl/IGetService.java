package com.warmer.kgmaker.service.impl;


import com.warmer.kgmaker.util.R;
import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.List;

/**
 * @author alphonso
 */
public interface IGetService {


    /**
     * 查询两点之间最短（关系查询）
     * @param sourceName
     * @param targetName
     * @return
     */
    HashMap<String ,Object> getBtnRelationship(String sourceName, String targetName );

    public HashMap<String, Object> feNodeDetail(List<Long> ids);

    List<Long> getIdByName(String sourceName, String targetName);
}
