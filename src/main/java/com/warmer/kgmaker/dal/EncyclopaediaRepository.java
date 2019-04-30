package com.warmer.kgmaker.dal;


import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


public interface EncyclopaediaRepository {


    HashMap<String,Object> getBaiKeEntity(String  baiKeName);

    List<HashMap<String, Object>> getCausesAndMechanisms(String baiKeName);

    List<HashMap<String, Object>> getMedicolegalExpertise(String baiKeName);

    List<HashMap<String, Object>> getClinicalFeature(String baiKeName);

    String getLabel(String baiKeName);


    List<String> getRelNodeList(List<String> baiKeNameList,String baiKeName);


    public List<HashMap<String,Object>> getTreeList(String baiKeName);

    public HashMap<String, Object> getTreeIdAndLabel(StatementResult statementResult);


    public HashMap<String, Object> treeList(String baiKeName);




}
