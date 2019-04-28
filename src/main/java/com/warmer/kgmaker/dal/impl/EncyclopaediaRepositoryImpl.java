package com.warmer.kgmaker.dal.impl;

import com.warmer.kgmaker.dal.EncyclopaediaRepository;
import com.warmer.kgmaker.service.EncyclopaediaService;
import com.warmer.kgmaker.util.Neo4jUtil;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;


/**
 * @author Administrator
 */
@Repository
public class EncyclopaediaRepositoryImpl implements EncyclopaediaRepository {


    @Autowired
    private Neo4jUtil neo4jUtil;

    /**
     * 获取百科信息
     *
     * @param baiKeName
     * @return
     */
    @Override
    public HashMap<String, Object> getBaiKeEntity(String baiKeName) {
        HashMap<String, Object> map = new HashMap<>();

        String cql = String.format("MATCH (n) WHERE n.name='%s' return *", baiKeName);


        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

        Record record = statementResult.next();
        List<Pair<String, Value>> pairs = record.fields();
        for (Pair<String, Value> pair : pairs) {
            String typeName = pair.value().type().name();
            if ("NODE".equals(typeName)) {
                Node node = pair.value().asNode();

                Map<String, Object> nodeMap = node.asMap();
                Set<Map.Entry<String, Object>> entries = nodeMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return map;
    }


    /**
     * 查询原因与机制
     *
     * @param baiKeName
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getCausesAndMechanisms(String baiKeName) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap();
        String cql = String.format("match(a)-[r:原因与机制]-(b) where a.name = '%s' return b", baiKeName);
        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);
        List<HashMap<String, Object>> causesAndMechanisms = getNodeInfoUtil(statementResult);
        return causesAndMechanisms;
    }


    /**
     * 查询法医学鉴定
     *
     * @param baiKeName
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getMedicolegalExpertise(String baiKeName) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap();
        String cql = String.format("match(a)-[r:法医学鉴定]-(b) where a.name = '%s' return b", baiKeName);

        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);
        List<HashMap<String, Object>> medicolegalExpertise = getNodeInfoUtil(statementResult);


        return medicolegalExpertise;
    }


    /**
     * 临床表现
     *
     * @param baiKeName
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getClinicalFeature(String baiKeName) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap();
        String cql = String.format("match(a)-[r:临床表现]-(b) where a.name = '%s' return b", baiKeName);
        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);





        List<HashMap<String, Object>> clinicalFeature = getNodeInfoUtil(statementResult);


        return clinicalFeature;
    }



    public List<HashMap<String , Object>> getNodeInfoUtil(StatementResult statementResult) {
        List<HashMap<String, Object>> list = new ArrayList<>();



        if (statementResult.hasNext()) {
            List<Record> records = statementResult.list();
            for (Record record : records) {
                List<Pair<String, Value>> pairs = record.fields();
                for (Pair<String, Value> pair : pairs) {
                    String typeName = pair.value().type().name();
                    System.out.println(typeName);

                    if ("NODE".equals(typeName)) {
                        HashMap<String, Object> map = new HashMap<>();
                        Node node = pair.value().asNode();
                        Map<String, Object> nodeMap = node.asMap();
                        Set<Map.Entry<String, Object>> entries = nodeMap.entrySet();
                        for (Map.Entry<String, Object> entry : entries) {
                            map.put(entry.getKey(), entry.getValue());
                        }
                        list.add(map);
                    }

                }


            }
        }

        return list;
    }
}
