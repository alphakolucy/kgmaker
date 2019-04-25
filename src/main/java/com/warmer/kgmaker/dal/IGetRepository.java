package com.warmer.kgmaker.dal;

import java.util.HashMap;
import java.util.List;


/**
 * 查询
 * @author alphonso
 */
public interface IGetRepository {


    /**
     * 查询两点之间最短距离（关系查询）
     * @param sourceName
     * @param targetName
     * @return
     */
     HashMap<String ,Object> getBtnRelationship(String sourceName, String targetName );

    HashMap<String, Object> feNodeDetail(List<Long> ids);

    List<Long> getIdByName(String sourceName, String targetName);
}
