package com.warmer.kgmaker.util;

import io.swagger.models.auth.In;

import java.util.*;

public class getNodeUtil {


    public List<Integer> getIdStringToLong(HashMap<Long, Object> map, Long id1, Long id2) {
        List<Integer> idList = new ArrayList<>();

        Set<Map.Entry<Long, Object>> entries = map.entrySet();

        for (Map.Entry<Long, Object> entry : entries) {
//            idList.add(entry.getValue());
        }
        return idList;
    }
}
