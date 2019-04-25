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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Administrator
 */
@Repository
public class EncyclopaediaRepositoryImpl implements EncyclopaediaRepository {



    @Autowired
    private Neo4jUtil neo4jUtil;

    /**
     * 获取百科信息
     * @param baiKeName
     * @return
     */
    @Override
    public HashMap<String, Object> getBaiKeEntity(String baiKeName) {
        HashMap<String, Object> map = new HashMap<>();

        String cql = String.format("MATCH (n) WHERE n.name='%s' return *",baiKeName);


        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

        Record record = statementResult.next();
        List<Pair<String, Value>> pairs = record.fields();
        Map<String, Object> recordMap = record.asMap();
        List<Value> values = record.values();
//        Set<Map.Entry<String, Object>> entries = recordMap.entrySet();

        for (Pair<String, Value> pair : pairs) {
            String typeName = pair.value().type().name();
            if ("NODE".equals(typeName)){
                Node node = pair.value().asNode();

                Map<String, Object> nodeMap = node.asMap();
                Set<Map.Entry<String, Object>> entries = nodeMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    map.put(entry.getKey(),entry.getValue());
                }
            }
        }
//        for (Map.Entry<String, Object> entry : entries) {
//            String key = entry.getKey();
//            Object value = entry.getValue().;
//            System.out.println(key);
//            System.out.println(value);
//        }
        return map;
    }
}
