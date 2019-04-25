package com.warmer.kgmaker.dal.impl;


import com.warmer.kgmaker.dal.IGetRepository;
import com.warmer.kgmaker.util.Neo4jUtil;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;


/**
 *
 */
@Repository
public class GetRepository implements IGetRepository {

    @Autowired
    private Neo4jUtil neo4jUtil;


    @Override
    public HashMap<String, Object> getBtnRelationship(String sourceName, String targetName) {
        HashMap<String, Object> reslist = new HashMap<>();
        HashMap<Long, Object> nodeResIdTarId = new HashMap<>();
        HashMap<Long, Object> btnNode = new HashMap<>();
        HashMap<String, Object> nodeInf = new HashMap<>();
        HashMap<String, Object> nodeInfRe = new HashMap<>();
        List linkList = new ArrayList();
        List nodeList = new ArrayList();
        String nodeName = "";
        HashMap<String, Long> linkMap = new HashMap<>();
        //path 集合

        Map<String, Object> pathList = new HashMap<>();
        try {

            String cql = String.format("Match (p1), (p2) ,p = shortestpath((p1)-[*..100]-(p2)) where p1.name = '%s' and p2.name = '%s' return * limit 10", sourceName, targetName);
            //获取结果集
            StatementResult result = neo4jUtil.excuteCypherSql(cql);

            //获取结果对象
            Record next = result.next();


//            if (result.hasNext()) {
            //获取结果集合
            List<Value> values = next.values();
            System.out.println(values);
            Path neo4jPath = null;
            for (Value value : values) {
                String typeName = value.type().name();
//                System.out.println(typeName);

                // 结果里面只要类型为节点的值
                if ("NODE".equals(typeName)) {

                    Node neo4jNode = value.asNode();
                    String key = "";
                    String keyName = "";
                    Object value1 = "";
                    Map<String, Object> map = neo4jNode.asMap();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        key = entry.getKey();
                        System.out.println("nodeKey:" + key);


                        //属性值  对应  name  belong   detail
                        value1 = entry.getValue();

                        nodeInf.put(key, value1);
//                        if (reslist.containsKey(key)) {
//                            String oldValue = reslist.get(key).toString();
//                            String newValue = oldValue + "," + entry.getValue();
//                            reslist.replace(key, newValue);
//                            System.out.println("添加节点信息" + reslist);
//
//                        } else {
//                            reslist.put(key, entry.getValue());
//                        }


                    }
//                    long id = neo4jNode.id();
                    String id = neo4jNode.id() + "";
                    nodeInfRe.put(id, nodeInf);

                }


                //抽取类型为PATH的对象
                if ("PATH".equals(typeName)) {

                    //
                    //var links = [{source: 0, target: 1}, {source: 4, target: 2},
                    //{source: 0, target: 3}, {source: 1, target: 4}, {source: 1, target: 4}];

                    neo4jPath = value.asPath();

                    //取出关系集合  pathIterable[relationship<221>, relationship<205>, relationship<122>]
                    Iterable<Relationship> iterable = neo4jPath.relationships();
                    Iterator<Relationship> iterator = iterable.iterator();
                    System.out.println("pathIterable" + iterable);


                    long startNodeId = -1;
                    long endNodeId = -1;
                    String linkId = "";
                    String btnId = "";
                    for (Relationship relationship : iterable) {
                        startNodeId = relationship.startNodeId();
                        endNodeId = relationship.endNodeId();
                        long id = relationship.id();
//                        linkMap.put("sourceId",startNodeId);
//                        linkMap.put("targetId",endNodeId);
                        // 拼接 Json   瞎搞
                        linkId = "{" + "source" + ":" + startNodeId + "," + "target" + ":" + endNodeId + "}";
                        linkList.add(linkId);
                        //获取所有节点对应id
                        btnId = startNodeId + "-" + endNodeId;
                        nodeResIdTarId.put(id, btnId);

                        Map<String, Object> btnRel = relationship.asMap();
                        //获取关联节点id
                        btnNode.put(relationship.id(), btnRel);

                        Iterable<Node> nodes = neo4jPath.nodes();
                        for (Node node : nodes) {

                            Set<Map.Entry<String, Object>> entries = node.asMap().entrySet();
                            Object getValue = null;
                            for (Map.Entry<String, Object> entry : entries) {
                                String key = entry.getKey();
                                System.out.println(key);
                                getValue = entry.getValue();
                                System.out.println("getValue" + getValue);
                                if ("name".equals(key)) {
                                    nodeName = "{" + "name:" + getValue + "," + "id:" + node.id() + "}";

                                }
                            }
                            nodeList.add(nodeName);


                        }
//                        long id = relationship.id();
//                        long startId = relationship.startNodeId();
//                        if (startId == relationship.startNodeId()) {
//                            startNodeId = relationship.startNodeId();
//                            pathList.put(startId + "", relationship.asMap());
//                        }
//
//
//                        long endId = relationship.endNodeId();
//                        if (endId == relationship.endNodeId()) {
//                            endNodeId = relationship.endNodeId();
//                            pathList.put(endId + "", relationship.asMap());
//                        }
//                        System.out.println("pathMap" + pathList);
//                        System.out.println("relationship.type():" + relationship.type());
//                        System.out.println("relationship.startNodeId():" + relationship.startNodeId());

                        //这样弄不是期望的值
//                        nodeResIdTarId.put(startNodeId, endNodeId);
                    }


                    //将集合添加进 结果集
//                    reslist.put("pathIterable", iterable);


                    reslist.put("btnNode", btnNode);
                    reslist.put("nodeResIdTarId", nodeResIdTarId);
                    reslist.put("nodeInfRe", nodeInfRe);
                    reslist.put("links", linkList);
//                    reslist.put("nodes", nodeList);
                    System.out.println("nodeResIdTarId" + nodeResIdTarId);

                }

//                Iterable<Node> nodes = neo4jPath.nodes();
//                System.out.println("nodes:" + nodes);


                // 循环
//                for (Node node : nodes) {
//
//                    Set<Map.Entry<String, Object>> entries = node.asMap().entrySet();
//                    Object getValue = null;
//                    for (Map.Entry<String, Object> entry : entries) {
//                        String key = entry.getKey();
//                        System.out.println(key);
//                        getValue = entry.getValue();
//                        System.out.println("getValue" + getValue);
//                        if ("name".equals(key)) {
//                            nodeName = "{" + "name:" + getValue +","+ "id:" + node.id() + "}";
//                            nodeList.add(nodeName);
//                        }
//                    }
//
//
//                }


                //将path（路径）集合添加进 结果集
                reslist.put("PathList", pathList);
                reslist.put("nodes", nodeList);
            }

//            }

//            String cqlre = String.format("Match (p1),(p2),p = shortestpath((p1)-[*..100]-(p2)) where p1.name = %s and p2.name = %s return p",sourceName,targetName);
//            System.out.println(cqlre);


            System.out.println("statementResultList" + reslist);
        } catch (Exception e) {

        }

        return reslist;
    }

    @Override
    public HashMap<String, Object> feNodeDetail(List<Long> ids) {
        HashMap<String, Object> map = new HashMap<>();
        map = neo4jUtil.feNodeDetail(ids);
        return map;
    }

    @Override
    public List<Long> getIdByName(String sourceName, String targetName) {
//        HashMap<String , Object> map = new HashMap<>();
        List<Long> ids = new ArrayList<>();


        String cql1 = String.format("MATCH (n) where   name(n)= '%s' RETURN * LIMIT 100", sourceName);
        String cql2 = String.format("MATCH (n) where   name(n)= '%s' RETURN * LIMIT 100", targetName);

        StatementResult sourceNameResult = neo4jUtil.excuteCypherSql(cql1);
        StatementResult targetNameResult = neo4jUtil.excuteCypherSql(cql2);

        if (sourceNameResult.hasNext()) {
            List<Record> list = sourceNameResult.list();
            for (Record record : list) {
                List<Pair<String, Value>> f = record.fields();
                HashMap<String, Object> rss = new HashMap<String, Object>();
                for (Pair<String, Value> pair : f) {
                    String typeName = pair.value().type().name();
                    if (typeName.equals("NODE")) {
                        Node node = pair.value().asNode();

                        ids.add(node.id());

                    }
                }
            }

        }


        if (targetNameResult.hasNext()) {
            List<Record> list1 = targetNameResult.list();
            for (Record record1 : list1) {
                List<Pair<String, Value>> f1 = record1.fields();
                HashMap<String, Object> rss1 = new HashMap<String, Object>();
                for (Pair<String, Value> pair : f1) {
                    String typeName = pair.value().type().name();
                    if (typeName.equals("NODE")) {
                        Node node2 = pair.value().asNode();

                        ids.add(node2.id());
                    }

                }
            }
        }

        return ids;
    }


}
