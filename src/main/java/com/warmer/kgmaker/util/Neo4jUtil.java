package com.warmer.kgmaker.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class Neo4jUtil {
    @Autowired
    private Driver neo4jDriver;


    String[] colorArray = new String[]{"#ff4500", "#ff8373", "#f9c62c", "#a5ca34", "#6fce7a", "#70d3bd", "#ea91b0", "#0AE192", "#FF006A", "#9900FF"};

    public boolean isNeo4jOpen() {
        try (Session session = neo4jDriver.session()) {
            System.out.println("连接成功：" + session.isOpen());
            return session.isOpen();
        } catch (Exception e) {

        }
        return false;
    }


    public StatementResult excuteCypherSql(String cypherSql) {
        StatementResult result = null;
        try (Session session = neo4jDriver.session()) {
            System.out.println(cypherSql);
            result = session.run(cypherSql);
            session.close();
        } catch (Exception e) {
            throw e;
        }
        return result;
    }


    public HashMap<String, Object> getDetailByName(String cypherSql) {
        StatementResult result = null;
        HashMap<String, Object> detailMap = new HashMap<>();
        try (Session session = neo4jDriver.session()) {
            System.out.println(cypherSql);
            result = session.run(cypherSql);
            try {

                StatementResult resultBysourceName = excuteCypherSql(cypherSql);
                List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
                if (resultBysourceName.hasNext()) {
                    List<Record> records = resultBysourceName.list();
                    for (Record recordItem : records) {
                        List<Pair<String, Value>> fList = recordItem.fields();
                        for (Pair<String, Value> pair : fList) {
                            String typeName = pair.value().type().name();
                            if ("NULL".equals(typeName)) {
                                System.out.println("typeName=null");
                                continue;
                            } else if ("NODE".equals(typeName)) {
                                Node node = pair.value().asNode();
                                Map<String, Object> nodeMap = node.asMap();
                                Set<Entry<String, Object>> entries = nodeMap.entrySet();
                                String name = "";
                                for (Entry<String, Object> entry : entries) {
                                    String key = entry.getKey();
                                    if ("name".equals(key)) {
                                        name = (String) entry.getValue();
                                    }
                                    if ("detail".equals(key)) {
                                        //    节点名  ：  节点细节
                                        detailMap.put(name, entry.getValue());
                                    }


                                }
                            }

                        }

                    }
                }

            } catch (Exception e) {
                throw e;
            }
            System.out.println("获取detail:" + detailMap);
            return detailMap;
        }
    }


//    public StatementResult tranExcuteCypherSql(String cypherSql) {
//        StatementResult result = null;
//        try (Session transaction = neo4jDriver.session()) {
//            System.out.println(cypherSql);
//            result = transaction.readTransaction(cypherSql);
//            session.close();
//        } catch (Exception e) {
//            throw e;
//        }
//        return result;
//    }


    public HashMap<String, Object> feNodeDetail(List<Long> ids) {
        HashMap<String, Object> rss = new HashMap<String, Object>();

        try {
            for (Long id : ids) {
                String cypherSql = String.format("MATCH (n) where   id(n)= %s RETURN * LIMIT 100", id);
                StatementResult result = excuteCypherSql(cypherSql);
                if (result.hasNext()) {
                    List<Record> records = result.list();
                    String keyName = "";
                    for (Record recordItem : records) {
                        for (Value value : recordItem.values()) {
                            if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
                                Node noe4jNode = value.asNode();
                                Map<String, Object> map = noe4jNode.asMap();
                                Set<Entry<String, Object>> entries = map.entrySet();

                                //封装节点 名字和信息

                                for (Entry<String, Object> entry : entries) {
                                    if ("name".equals(entry.getKey())) {
                                        keyName = (String) entry.getValue();
                                    }

                                    if ("detail".equals(entry.getKey())) {
                                        rss.put(keyName, entry.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        return rss;
    }


    public HashMap<String, Object> GetEntityMap(String cypherSql) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    for (Value value : recordItem.values()) {
                        if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
                            Node noe4jNode = value.asNode();
                            Map<String, Object> map = noe4jNode.asMap();
                            for (Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                if (rss.containsKey(key)) {
                                    String oldValue = rss.get(key).toString();
                                    String newValue = oldValue + "," + entry.getValue();
                                    rss.replace(key, newValue);
                                } else {
                                    rss.put(key, entry.getValue());
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rss;
    }

    //GetGraphNode修改后  根据MATCH n=(a:法医临床学)-->() where a.name = '%s' RETURN n limit %s 匹配
    public List<HashMap<String, Object>> GetGraphNode(String cypherSql) {
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> startRss = new HashMap<>();
        Boolean fg = true;

        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();

                for (Record recordItem : records) {
                    int n = 0;
                    n++;
                    System.out.println("测试结果：" + recordItem + "----" + n);
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rss = new HashMap<String, Object>();


                        //封装起点
//                        if (fg) {
//                            fg = false;
//                            startRss = GetStartNodeInf(pair.value().asPath().start());
//                            ents.add(startRss);
//                        }
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            String uuid = String.valueOf(noe4jNode.id());
                            Map<String, Object> map = noe4jNode.asMap();
                            //遍历节点属性
                            String key = null;
                            String s = null;
                            List<String> belong = new ArrayList<>();
                            for (Entry<String, Object> entry : map.entrySet()) {
                                key = entry.getKey();
                                s += entry.toString() + ",";
                                Object value = entry.getValue();
                                //层级给颜色
                                if (!s.contains("color")) {
                                    String color = null;
                                    if ("belong".equals(key)) {
                                        String belongValue = (String) entry.getValue();
                                        belong.add(key);
                                        String s1 = belong.get(0);
                                        switch (belongValue) {
                                            case "脊柱与脊髓损伤":
                                                color = colorArray[0];
                                                rss.put("color", color);
                                                break;

                                            case "冻伤":
                                                color = colorArray[1];
                                                rss.put("color", color);
                                                break;
                                            case "活体损伤":
                                                color = colorArray[2];
                                                rss.put("color", color);
                                                break;
                                            case "呼吸困难":
                                                color = colorArray[3];
                                                rss.put("color", color);
                                                break;
                                            case "脂肪栓塞综合征":
                                                color = colorArray[4];
                                                rss.put("color", color);
                                                break;
                                            case "颈椎损伤":
                                                color = colorArray[5];
                                                rss.put("color", color);
                                                break;
                                            case "胸腰椎损伤":
                                                color = colorArray[6];
                                                rss.put("color", color);
                                                break;
                                            default:
                                                color = colorArray[9];
                                                rss.put("color", color);
                                                break;
                                        }
                                    }
                                }
                                rss.put(key, value);
                            }
                            rss.put("uuid", uuid);
                            ents.add(rss);
                        } else if (typeName.equals("PATH")) {

                            Path path = pair.value().asPath();
                            Iterable<Node> nodes = path.nodes();
                            String keyStr = null;
                            Node start = path.start();

                            for (Node node : nodes) {
                                String uuid = node.id() + "";
                                Map<String, Object> nodemap = node.asMap();
                                for (Entry<String, Object> nodeKV : nodemap.entrySet()) {
                                    String key = nodeKV.getKey();
                                    String value = (String) nodeKV.getValue();
                                    keyStr += key + ",";
                                    if (!keyStr.contains(value)) {
                                        rss.put(key, value);
                                    }
                                }
                                rss.put("uuid", uuid);
                            }
                            ents.add(rss);
                        }
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("222行 GetGraphNode ents：" + ents);
        return ents;
    }

    /**
     * 添加开始节点信息
     *
     * @param startNode
     * @return
     */
    public HashMap<String, Object> GetStartNodeInf(Node startNode) {
        HashMap<String, Object> startrss = new HashMap<String, Object>();
        Node start = startNode;

        startrss.put("uuid", start.id());

        //获取开始节点信息
        Map<String, Object> startMap = start.asMap();


        String startKey = "";
        Object startValue = "";
        for (Entry<String, Object> stratNode : startMap.entrySet()) {
            startKey = stratNode.getKey();
            startValue = stratNode.getValue();
            startrss.put(startKey, startValue);

        }


        return startrss;
    }


    /**
     * 第五章语句后查询
     */
    public HashMap<String, Object> GetGraphAndShip(String cypherSql) {
        HashMap<String, Object> result = new HashMap<>();


        return result;
    }


    /**
     * 查询节点
     *
     * @param cypherSql
     * @return
     */
    public List<HashMap<String, Object>> GetGraphNode1(String cypherSql) {
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();

        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rss = new HashMap<String, Object>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            String uuid = String.valueOf(noe4jNode.id());
                            Map<String, Object> map = noe4jNode.asMap();
                            //遍历节点属性
                            for (Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                rss.put(key, entry.getValue());
                            }
                            rss.put("uuid", uuid);
                            ents.add(rss);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ents;
    }


    public List<HashMap<String, Object>> GetGraphRelationShip(String cypherSql) {
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rss = new HashMap<String, Object>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());
                            String sourceid = String.valueOf(rship.startNodeId());
                            String targetid = String.valueOf(rship.endNodeId());
                            Map<String, Object> map = rship.asMap();
                            for (Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                rss.put(key, entry.getValue());
                            }
                            rss.put("uuid", uuid);
                            rss.put("sourceid", sourceid);
                            rss.put("targetid", targetid);
                            ents.add(rss);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("428行 GetGraphRelation ents：" + ents);
        return ents;
    }

    public List<HashMap<String, Object>> GetGraphItem(String cypherSql) {
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
        List<String> nodeids = new ArrayList<String>();
        List<String> shipids = new ArrayList<String>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    HashMap<String, Object> rss = new HashMap<String, Object>();
                    for (Pair<String, Value> pair : f) {
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            String uuid = String.valueOf(noe4jNode.id());
                            if (!nodeids.contains(uuid)) {
                                Map<String, Object> map = noe4jNode.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rss.put(key, entry.getValue());
                                }
                                rss.put("uuid", uuid);
                            }
                        } else if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());
                            if (!shipids.contains(uuid)) {
                                String sourceid = String.valueOf(rship.startNodeId());
                                String targetid = String.valueOf(rship.endNodeId());
                                Map<String, Object> map = rship.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rss.put(key, entry.getValue());
                                }
                                rss.put("uuid", uuid);
                                rss.put("sourceid", sourceid);
                                rss.put("targetid", targetid);
                            }
                        } else {
                            rss.put(pair.key(), pair.value().toString());
                        }
                    }
                    ents.add(rss);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("328行 GetGraphItem ents：" + ents);
        return ents;
    }

    /*
     * 获取值类型的结果,如count,uuid
     * @return 1 2 3 等数字类型
     */
    public long GetGraphValue(String cypherSql) {
        long val = 0;
        try {
            StatementResult cypherResult = excuteCypherSql(cypherSql);
            if (cypherResult.hasNext()) {
                Record record = cypherResult.next();
                for (Value value : record.values()) {
                    val = value.asLong();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }


    /**
     * 查询节点间路径集合
     *
     * @param cypherSql
     * @return
     */
    public List<String> getPathNodeIds(String cypherSql) {
        List<String> nodeIds = new ArrayList<>();
        List<Integer> pathLengthList = getPathLengthList(cypherSql);

        try {
            StatementResult result = excuteCypherSql(cypherSql);
            List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    a:
                    for (Pair<String, Value> pair : f) {

                        String typeName = pair.value().type().name();

                        if (typeName.equals("PATH")) {
                            Path path = pair.value().asPath();
                            if (path.length() > Collections.min(pathLengthList)) {
                                System.out.println("当前路径大于最小值，故break");
                                break a;
                            }
                        }
                        //
                        if (typeName.equals("NODE")) {
                            Node node = pair.value().asNode();
                            String id = node.id() + "";
                            nodeIds.add(id);
                        }

                    }
                    System.out.println(" 自循环 nodeIds:" + nodeIds);
                }
            }
        } catch (Exception e) {

        }

        return nodeIds;
    }

    public List<Integer> getPathLengthList(String cypherSql) {
        List<Integer> pathLength = new ArrayList<>();
        List<String> nodeIds = new ArrayList<>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
            if (result.hasNext()) {
                List<Record> records = result.list();

                List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
                List<String> uuids = new ArrayList<String>();
                List<String> shipids = new ArrayList<String>();

                List<HashMap<String, Object>> shipNew = new ArrayList<HashMap<String, Object>>();

                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {

                        String typeName = pair.value().type().name();
                        if (typeName.equals("PATH")) {
                            Path path = pair.value().asPath();
                            int length = path.length();
                            pathLength.add(length);
                        }
                    }
                    System.out.println(" 自循环 pathLength:" + pathLength);


                }
            }
        } catch (Exception e) {

        }
        return pathLength;

    }

    /**
     * 查询节点和关系
     *
     * @param cypherSql
     * @return
     */
    public HashMap<String, Object> GetGraphNodeAndShip(String cypherSql) {
        HashMap<String, Object> mo = new HashMap<String, Object>();
        List<String> pathNodeIds = getPathNodeIds(cypherSql);
        List<Integer> pathLengthList = getPathLengthList(cypherSql);
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
            if (result.hasNext()) {
                List<Record> records = result.list();
                List<Integer> pathLength = new ArrayList<>();
                List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
                List<String> uuids = new ArrayList<String>();
                List<String> shipids = new ArrayList<String>();
                List<HashMap<String, Object>> shipNew = new ArrayList<HashMap<String, Object>>();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rships = new HashMap<String, Object>();
                        HashMap<String, Object> rss = new HashMap<String, Object>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NULL")) {
                            continue;
                        } else if (typeName.equals("NODE")) {
                            Node neo4jNode = pair.value().asNode();
                            String id = neo4jNode.id() + "";
                            Map<String, Object> map = neo4jNode.asMap();
                            String uuid = String.valueOf(neo4jNode.id());
                            if (pathNodeIds.contains(id)) {
                                if (!uuids.contains(uuid)) {
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rss.put(key, entry.getValue());
                                    }
                                    rss.put("uuid", uuid);
                                    uuids.add(uuid);
                                }
                                if (rss != null && !rss.isEmpty()) {
                                    ents.add(rss);
                                    rss = new HashMap<>();
                                }
                            }
                        } else if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());

                            if (!shipids.contains(uuid)) {
                                String sourceid = String.valueOf(rship.startNodeId());
                                String targetid = String.valueOf(rship.endNodeId());
                                Map<String, Object> map = rship.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rships.put(key, entry.getValue());
                                }


                                rships.put("uuid", uuid);
                                rships.put("sourceid", sourceid);
                                rships.put("targetid", targetid);
                                if (rships != null && !rships.isEmpty()) {
                                    ships.add(rships);
                                }
                            }

                        } else if (typeName.equals("PATH")) {

                            Path path = pair.value().asPath();
                            Iterable<Node> nodes = path.nodes();
                            Map<String, Object> startNodemap = path.start().asMap();
                            String startNodeuuid = String.valueOf(path.start().id());

                            int length = path.length();


//                            System.out.println("539   length:" + length);

                            if (length <= Collections.min(pathLengthList)) {
                                if (!uuids.contains(startNodeuuid)) {

                                    for (Entry<String, Object> entry : startNodemap.entrySet()) {
                                        String key = entry.getKey();
                                        rss.put(key, entry.getValue());
                                    }
                                    rss.put("color", "#FF2902");
                                    rss.put("uuid", startNodeuuid);
                                    uuids.add(startNodeuuid);


                                    if (rss != null && !rss.isEmpty()) {
                                        ents.add(rss);
                                        rss = new HashMap<>();
                                    }
                                }
                                Map<String, Object> endNodemap = path.end().asMap();
                                String endNodeuuid = String.valueOf(path.end().id());
                                if (!uuids.contains(endNodeuuid)) {
                                    for (Entry<String, Object> entry : endNodemap.entrySet()) {
                                        String key = entry.getKey();
                                        rss.put(key, entry.getValue());

                                    }
                                    rss.put("color", "#5C84FF");
                                    rss.put("uuid", endNodeuuid);
                                    uuids.add(endNodeuuid);

                                    if (rss != null && !rss.isEmpty()) {
                                        ents.add(rss);
                                        rss = new HashMap<>();
                                    }
                                }
                                //获取其他节点信息
                                for (Node node : nodes) {
                                    String uuid = node.id() + "";
                                    if (!uuids.contains(uuid)) {
                                        Set<Entry<String, Object>> entries = node.asMap().entrySet();
                                        for (Entry<String, Object> nodeMap : entries) {
                                            String key = nodeMap.getKey();
                                            rss.put(key, nodeMap.getValue());
                                       /* if ("color".equals(key)){
                                            rss.put(key,"#a5ca34");
                                        }else {

                                        }*/

                                        }
                                        rss.put("color", "#f9bb05");
                                        rss.put("uuid", uuid);
                                        uuids.add(uuid);
                                        if (rss != null && !rss.isEmpty()) {
                                            ents.add(rss);
                                            rss = new HashMap<>();
                                        }
                                    }
                                }
                                System.out.println("599行 获取其他节点信息ents：" + ents);

                                Iterator<Relationship> reships = path.relationships().iterator();
                                String shipStr = "";
                                while (reships.hasNext()) {
                                    Relationship next = reships.next();
                                    String uuid = String.valueOf(next.id());

                                    if (!shipids.contains(uuid)) {
                                        String sourceid = String.valueOf(next.startNodeId());
                                        String targetid = String.valueOf(next.endNodeId());
                                        Map<String, Object> map = next.asMap();
                                        for (Entry<String, Object> entry : map.entrySet()) {
                                            String key = entry.getKey();
                                            rships.put(key, entry.getValue());
                                        }
                                        rships.put("uuid", uuid);
                                        rships.put("sourceid", sourceid);
                                        rships.put("targetid", targetid);
                                        if (rships != null && !rships.isEmpty()) {
//                                        System.out.println("rships" + rships + "------ships" + ships);
                                            ships.add(rships);
                                            System.out.println("shipStr" + ships);
                                            rships = new HashMap<>();

                                        }

                                    }


                                }
                                shipNew.add(rships);

                            }
                        } else if (typeName.contains("LIST")) {
                            Iterable<Value> val = pair.value().values();
                            Value next = val.iterator().next();
                            String type = next.type().name();
                            if (type.equals("RELATIONSHIP")) {
                                Relationship rship = next.asRelationship();
                                String uuid = String.valueOf(rship.id());
                                if (!shipids.contains(uuid)) {
                                    String sourceid = String.valueOf(rship.startNodeId());
                                    String targetid = String.valueOf(rship.endNodeId());
                                    Map<String, Object> map = rship.asMap();
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rships.put(key, entry.getValue());
                                    }
                                    rships.put("uuid", uuid);
                                    rships.put("sourceid", sourceid);
                                    rships.put("targetid", targetid);
                                    if (rships != null && !rships.isEmpty()) {
                                        ships.add(rships);
                                    }
                                }
                            }
                        } else if (typeName.contains("MAP")) {
                            rss.put(pair.key(), pair.value().asMap());
                        } else {
                            rss.put(pair.key(), pair.value().toString());
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
                        }

                    }
                }
                mo.put("node", ents);
                mo.put("relationship", ships);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("最终结果" + mo);
        return mo;
    }


    /**
     * 关系查询最短路径（模糊）
     *
     * @param cypherSql
     * @return
     */
    public HashMap<String, Object> getNodeAndShipFuzzy(String cypherSql) {


        HashMap<String, Object> mo = new HashMap<String, Object>();
        List<String> pathNodeIds = getPathNodeIds(cypherSql);
        List<Integer> pathLengthList = getPathLengthList(cypherSql);
        List<String> startEndNodesList = new ArrayList<>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
            if (result.hasNext()) {
                List<Record> records = result.list();
                List<Integer> pathLength = new ArrayList<>();
                List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
                List<String> uuids = new ArrayList<String>();
                List<String> shipids = new ArrayList<String>();
                List<HashMap<String, Object>> shipNew = new ArrayList<HashMap<String, Object>>();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rships = new HashMap<String, Object>();
                        HashMap<String, Object> rss = new HashMap<String, Object>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NULL")) {
                            continue;
                        } else if (typeName.equals("NODE")) {
                            Node neo4jNode = pair.value().asNode();
                            String id = neo4jNode.id() + "";
                            Map<String, Object> map = neo4jNode.asMap();
                            String uuid = String.valueOf(neo4jNode.id());
                            if (pathNodeIds.contains(id)) {
                                if (!uuids.contains(uuid)) {
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rss.put(key, entry.getValue());
                                    }
                                    rss.put("uuid", uuid);
                                    uuids.add(uuid);
                                }
                                if (rss != null && !rss.isEmpty()) {
                                    ents.add(rss);
                                    rss = new HashMap<>();
                                }
                            }
                        } else if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());

                            if (!shipids.contains(uuid)) {
                                String sourceid = String.valueOf(rship.startNodeId());
                                String targetid = String.valueOf(rship.endNodeId());
                                Map<String, Object> map = rship.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rships.put(key, entry.getValue());
                                }


                                rships.put("uuid", uuid);
                                rships.put("sourceid", sourceid);
                                rships.put("targetid", targetid);
                                if (rships != null && !rships.isEmpty()) {
                                    ships.add(rships);
                                }
                            }

                        } else if (typeName.equals("PATH")) {

                            Path path = pair.value().asPath();
                            Iterable<Node> nodes = path.nodes();
                            Map<String, Object> endNodemap = path.end().asMap();
                            Map<String, Object> startNodemap = path.start().asMap();
                            String startNodeuuid = String.valueOf(path.start().id());
                            //关系长度确定最短  并获取关系中的首尾节点
                            int length = path.length();
                            if (length <= Collections.min(pathLengthList)) {
                                if (!uuids.contains(startNodeuuid)) {
                                    for (Entry<String, Object> entry : startNodemap.entrySet()) {

                                        String key = entry.getKey();
                                        String value = (String) entry.getValue();
                                        rss.put(key, value);
                                    }
                                    rss.put("color", "#FF2902");
                                    rss.put("uuid", startNodeuuid);
                                    uuids.add(startNodeuuid);
                                    if (rss != null && !rss.isEmpty()) {
                                        ents.add(rss);
                                        rss = new HashMap<>();
                                    }
                                }

                                //保证节点添加
                                if (uuids.contains(startNodeuuid)) {
                                    for (Entry<String, Object> entry : startNodemap.entrySet()) {

                                        String key = entry.getKey();
                                        String value = (String) entry.getValue();
                                        //添加首节点
                                        if ("name".equals(key)) {
                                            startEndNodesList.add(value);
                                        }

                                    }
                                }

                                String endNodeuuid = String.valueOf(path.end().id());


                                if (!uuids.contains(endNodeuuid)) {
                                    for (Entry<String, Object> entry : endNodemap.entrySet()) {
                                        String key = entry.getKey();
                                        String value = (String) entry.getValue();
                                        rss.put(key, value);
                                    }
                                    rss.put("color", "#5C84FF");
                                    rss.put("uuid", endNodeuuid);
                                    uuids.add(endNodeuuid);
                                    if (rss != null && !rss.isEmpty()) {
                                        ents.add(rss);
                                        rss = new HashMap<>();
                                    }
                                }

                                //保证尾节点添加
                                if (uuids.contains(endNodeuuid)) {
                                    for (Entry<String, Object> entry : endNodemap.entrySet()) {
                                        String key = entry.getKey();
                                        String value = (String) entry.getValue();
                                        //添加尾节点
                                        if ("name".equals(key)) {
                                            startEndNodesList.add(value);
                                        }
                                    }
                                }

                                //获取其他节点信息
                                for (Node node : nodes) {
                                    String uuid = node.id() + "";
                                    if (!uuids.contains(uuid)) {
                                        Set<Entry<String, Object>> entries = node.asMap().entrySet();
                                        for (Entry<String, Object> nodeMap : entries) {
                                            String key = nodeMap.getKey();
                                            rss.put(key, nodeMap.getValue());
                                       /* if ("color".equals(key)){
                                            rss.put(key,"#a5ca34");
                                        }else {

                                        }*/
                                        }
                                        rss.put("color", "#f9bb05");
                                        rss.put("uuid", uuid);
                                        uuids.add(uuid);
                                        if (rss != null && !rss.isEmpty()) {
                                            ents.add(rss);
                                            rss = new HashMap<>();
                                        }
                                    }
                                }
                                System.out.println("599行 获取其他节点信息ents：" + ents);
                                Iterator<Relationship> reships = path.relationships().iterator();
                                String shipStr = "";
                                while (reships.hasNext()) {
                                    Relationship next = reships.next();
                                    String uuid = String.valueOf(next.id());
                                    if (!shipids.contains(uuid)) {
                                        String sourceid = String.valueOf(next.startNodeId());
                                        String targetid = String.valueOf(next.endNodeId());
                                        Map<String, Object> map = next.asMap();
                                        for (Entry<String, Object> entry : map.entrySet()) {
                                            String key = entry.getKey();
                                            rships.put(key, entry.getValue());
                                        }
                                        rships.put("uuid", uuid);
                                        rships.put("sourceid", sourceid);
                                        rships.put("targetid", targetid);
                                        if (rships != null && !rships.isEmpty()) {
//                                        System.out.println("rships" + rships + "------ships" + ships);
                                            ships.add(rships);
                                            System.out.println("shipStr" + ships);
                                            rships = new HashMap<>();
                                        }
                                    }
                                }
                                shipNew.add(rships);

                            }
                        } else if (typeName.contains("LIST")) {
                            Iterable<Value> val = pair.value().values();
                            Value next = val.iterator().next();
                            String type = next.type().name();
                            if (type.equals("RELATIONSHIP")) {
                                Relationship rship = next.asRelationship();
                                String uuid = String.valueOf(rship.id());
                                if (!shipids.contains(uuid)) {
                                    String sourceid = String.valueOf(rship.startNodeId());
                                    String targetid = String.valueOf(rship.endNodeId());
                                    Map<String, Object> map = rship.asMap();
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rships.put(key, entry.getValue());
                                    }
                                    rships.put("uuid", uuid);
                                    rships.put("sourceid", sourceid);
                                    rships.put("targetid", targetid);
                                    if (rships != null && !rships.isEmpty()) {
                                        ships.add(rships);
                                    }
                                }
                            }
                        } else if (typeName.contains("MAP")) {
                            rss.put(pair.key(), pair.value().asMap());
                        } else {
                            rss.put(pair.key(), pair.value().toString());
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
                        }

                    }
                }
//                System.out.println("neo4j1020startEndNodesList:" + startEndNodesList);
                mo.put("node", ents);
                mo.put("relationship", ships);
                mo.put("startEndNodesList", startEndNodesList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("最终结果" + mo);
        return mo;
    }


    /**
     * 关系查询节点间最小路径（模糊）
     * mo封装  nodes  relationship nodeDetail
     *
     * @param sourceName
     * @param TargetName
     * @return
     */
    public HashMap<String, Object> GetBtnNodeAndShipFuzzy(String sourceName, String TargetName) {
        HashMap<String, Object> mo = new HashMap<String, Object>();

        HashMap<Integer, Object> details = new HashMap<Integer, Object>();
//
        //节点最短路径查询（模糊）
        String cypherSql = String.format("Match (p1),(p2),p = shortestpath((p1)-[*..100]-(p2)) " +
                "where  p1.name contains('%s')  and p2.name contains('%s')  return * limit 100", sourceName, TargetName);


        mo = getNodeAndShipFuzzy(cypherSql);

        List<String> result = (List<String>) mo.get("startEndNodesList");
        List<String> startEndNodesList = ListUtils.removeDuplicate(result);


        details = cyclicQuery(startEndNodesList);

        System.out.println(details.toString());


        mo.put("details", details);


        return mo;
    }


    /**
     * 循环查询 结果返回 Detail
     *
     * @param nameList
     * @return
     */
    public HashMap<Integer, Object> cyclicQuery(List<String> nameList) {
        HashMap<Integer, Object> details = new HashMap<>();
        int n = 0;
        for (String name : nameList) {
            String cypherqlByName = String.format("match (n) where n.name= '%s' return n", name);
            HashMap<String, Object> detailByName = getDetailByName(cypherqlByName);
            int size1 = detailByName.toString().length() - 1;
            String detailByNameStr = detailByName.toString().substring(1, size1);
            String s = detailByNameStr.replaceAll("=", ":");

            details.put(n, s);
            n++;
        }
        return details;
    }


    /**
     * 关系查询节点间最小路径（精确）
     *
     * @param sourceName
     * @param TargetName
     * @return
     */
    public HashMap<String, Object> GetBtnNodeAndShipRSPrecision(String sourceName, String TargetName) {
        HashMap<String, Object> mo = new HashMap<String, Object>();

        HashMap<String, Object> details = new HashMap<String, Object>();
//
        //节点最短路径查询(精确)
        String cypherSql = String.format("Match (p1),(p2),p = shortestpath((p1)-[*..100]-(p2)) " +
                "where  p1.name = '%s'  and p2.name = '%s'  return * limit 100", sourceName, TargetName);


        String cypherqlBysourceName = String.format("match (n) where n.name= '%s' return n", sourceName);
        String cypherqlByTargetName = String.format("match (n) where n.name= '%s' return n", TargetName);

        mo = GetGraphNodeAndShip(cypherSql);
        HashMap<String, Object> detailByName = new HashMap<>();
        HashMap<String, Object> detailBysourceName = getDetailByName(cypherqlBysourceName);
        HashMap<String, Object> detailByTargetName = getDetailByName(cypherqlByTargetName);

        int size1 = detailBysourceName.toString().length() - 1;
        int size2 = detailByTargetName.toString().length() - 1;
        String detailBysourceNameStr = detailBysourceName.toString().substring(1, size1);
        String detailByTargetNameStr = detailByTargetName.toString().substring(1, size2);
//        details.add(detailBysourceName);
//        details.add(detailByTargetName);
        details.put("起点信息", detailBysourceNameStr.replace("=", ":"));
        details.put("终点信息", detailByTargetNameStr.replace("=", ":"));
//        details.add(detailBysourceNameStr);
//        details.add(detailByTargetNameStr);

        mo.put("details", details);


        return mo;
    }

    /**
     * 查询节点和关系
     *
     * @param cypherSql
     * @return
     */
    public HashMap<String, Object> GetGraphNodeAndShip1(String cypherSql) {
        HashMap<String, Object> mo = new HashMap<String, Object>();
        List<String> colorList = new ArrayList<>();
        colorList.add("#47FF31");
        colorList.add("#FF3160");
        colorList.add("#FF5BDD");
        colorList.add("#F9F52E");
//        System.out.println("colorList：" + colorList);
        Collections.shuffle(colorList);
//        System.out.println("打乱后" + colorList);

        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
                List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
                List<String> uuids = new ArrayList<String>();
                List<String> shipids = new ArrayList<String>();
                for (Record recordItem : records) {
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        HashMap<String, Object> rships = new HashMap<String, Object>();
                        HashMap<String, Object> rss = new HashMap<String, Object>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NULL")) {
                            continue;
                        } else if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            Map<String, Object> map = noe4jNode.asMap();
                            String uuid = String.valueOf(noe4jNode.id());
                            if (!uuids.contains(uuid)) {
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rss.put(key, entry.getValue());
                                }
                                rss.put("uuid", uuid);
                                rss.put("color", colorList.get(0));
                                uuids.add(uuid);
                            }
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
                        } else if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            String uuid = String.valueOf(rship.id());
                            if (!shipids.contains(uuid)) {
                                String sourceid = String.valueOf(rship.startNodeId());
                                String targetid = String.valueOf(rship.endNodeId());
                                Map<String, Object> map = rship.asMap();
                                for (Entry<String, Object> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    rships.put(key, entry.getValue());
                                }
                                rships.put("uuid", uuid);
                                rships.put("sourceid", sourceid);
                                rships.put("targetid", targetid);
                                if (rships != null && !rships.isEmpty()) {
                                    ships.add(rships);
                                }
                            }

                        } else if (typeName.equals("PATH")) {
                            Path path = pair.value().asPath();
                            Map<String, Object> startNodemap = path.start().asMap();
                            String startNodeuuid = String.valueOf(path.start().id());
                            if (!uuids.contains(startNodeuuid)) {

                                for (Entry<String, Object> entry : startNodemap.entrySet()) {
                                    String key = entry.getKey();
                                    rss.put(key, entry.getValue());
                                }
                                rss.put("uuid", startNodeuuid);
                                uuids.add(startNodeuuid);
                                if (rss != null && !rss.isEmpty()) {
                                    ents.add(rss);
                                }
                            }
                            Map<String, Object> endNodemap = path.end().asMap();
                            String endNodeuuid = String.valueOf(path.end().id());
                            if (!uuids.contains(endNodeuuid)) {
                                for (Entry<String, Object> entry : endNodemap.entrySet()) {
                                    String key = entry.getKey();
                                    rss.put(key, entry.getValue());
                                }
                                rss.put("uuid", endNodeuuid);
                                rss.put("color", colorList.get(0));
                                uuids.add(endNodeuuid);
                                if (rss != null && !rss.isEmpty()) {
                                    ents.add(rss);
                                }
                            }
                            Iterator<Relationship> reships = path.relationships().iterator();
                            while (reships.hasNext()) {
                                Relationship next = reships.next();
                                String uuid = String.valueOf(next.id());
                                if (!shipids.contains(uuid)) {
                                    String sourceid = String.valueOf(next.startNodeId());
                                    String targetid = String.valueOf(next.endNodeId());
                                    Map<String, Object> map = next.asMap();
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rships.put(key, entry.getValue());
                                    }
                                    rships.put("uuid", uuid);
                                    rships.put("sourceid", sourceid);
                                    rships.put("targetid", targetid);
                                    if (rships != null && !rships.isEmpty()) {
                                        ships.add(rships);
                                    }
                                }
                            }
                        } else if (typeName.contains("LIST")) {
                            Iterable<Value> val = pair.value().values();
                            Value next = val.iterator().next();
                            String type = next.type().name();
                            if (type.equals("RELATIONSHIP")) {
                                Relationship rship = next.asRelationship();
                                String uuid = String.valueOf(rship.id());
                                if (!shipids.contains(uuid)) {
                                    String sourceid = String.valueOf(rship.startNodeId());
                                    String targetid = String.valueOf(rship.endNodeId());
                                    Map<String, Object> map = rship.asMap();
                                    for (Entry<String, Object> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        rships.put(key, entry.getValue());
                                    }
                                    rships.put("uuid", uuid);
                                    rships.put("sourceid", sourceid);
                                    rships.put("targetid", targetid);
                                    if (rships != null && !rships.isEmpty()) {
                                        ships.add(rships);
                                    }
                                }
                            }
                        } else if (typeName.contains("MAP")) {
                            rss.put(pair.key(), pair.value().asMap());
                        } else {
                            rss.put(pair.key(), pair.value().toString());
                            if (rss != null && !rss.isEmpty()) {
                                ents.add(rss);
                            }
                        }

                    }
                }
                mo.put("node", ents);
                mo.put("relationship", ships);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("最终结果" + mo);
        return mo;
    }

    /**
     * 匹配所有类型的节点,可以是节点,关系,数值,路径
     *
     * @param cypherSql
     * @return
     */
    public List<HashMap<String, Object>> GetEntityList(String cypherSql) {
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                List<Record> records = result.list();
                for (Record recordItem : records) {
                    HashMap<String, Object> rss = new HashMap<String, Object>();
                    List<Pair<String, Value>> f = recordItem.fields();
                    for (Pair<String, Value> pair : f) {
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NULL")) {
                            continue;
                        } else if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            Map<String, Object> map = noe4jNode.asMap();
                            for (Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                rss.put(key, entry.getValue());
                            }
                        } else if (typeName.equals("RELATIONSHIP")) {
                            Relationship rship = pair.value().asRelationship();
                            Map<String, Object> map = rship.asMap();
                            for (Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                rss.put(key, entry.getValue());
                            }
                        } else if (typeName.equals("PATH")) {

                        } else if (typeName.contains("LIST")) {
                            rss.put(pair.key(), pair.value().asList());
                        } else if (typeName.contains("MAP")) {
                            rss.put(pair.key(), pair.value().asMap());
                        } else {
                            rss.put(pair.key(), pair.value().toString());
                        }
                    }
                    ents.add(rss);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ents;
    }

    public <T> List<T> GetEntityItemList(String cypherSql, Class<T> type) {
        List<HashMap<String, Object>> ents = GetGraphNode(cypherSql);
        List<T> model = HashMapToObject(ents, type);
        return model;
    }

    public <T> T GetEntityItem(String cypherSql, Class<T> type) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                Record record = result.next();
                for (Value value : record.values()) {
                    if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
                        Node noe4jNode = value.asNode();
                        Map<String, Object> map = noe4jNode.asMap();
                        for (Entry<String, Object> entry : map.entrySet()) {
                            String key = entry.getKey();
                            if (rss.containsKey(key)) {
                                String oldValue = rss.get(key).toString();
                                String newValue = oldValue + "," + entry.getValue();
                                rss.replace(key, newValue);
                            } else {
                                rss.put(key, entry.getValue());
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        T model = HashMapToObjectItem(rss, type);
        return model;
    }

    public HashMap<String, Object> GetEntity(String cypherSql) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                Record record = result.next();
                for (Value value : record.values()) {
                    String t = value.type().name();
                    if (value.type().name().equals("NODE")) {// 结果里面只要类型为节点的值
                        Node noe4jNode = value.asNode();
                        Map<String, Object> map = noe4jNode.asMap();
                        for (Entry<String, Object> entry : map.entrySet()) {
                            String key = entry.getKey();
                            if (rss.containsKey(key)) {
                                String oldValue = rss.get(key).toString();
                                String newValue = oldValue + "," + entry.getValue();
                                rss.replace(key, newValue);
                            } else {
                                rss.put(key, entry.getValue());
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rss;
    }

    public Integer executeScalar(String cypherSql) {
        Integer count = 0;
        try {
            StatementResult result = excuteCypherSql(cypherSql);
            if (result.hasNext()) {
                Record record = result.next();
                for (Value value : record.values()) {
                    String t = value.type().name();
                    if (t.equals("INTEGER")) {
                        count = Integer.valueOf(value.toString());
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public HashMap<String, Object> GetRelevantEntity(String cypherSql) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            StatementResult resultNode = excuteCypherSql(cypherSql);
            if (resultNode.hasNext()) {
                List<Record> records = resultNode.list();
                for (Record recordItem : records) {
                    Map<String, Object> r = recordItem.asMap();
                    System.out.println(JSON.toJSONString(r));
                    String key = r.get("key").toString();
                    if (rss.containsKey(key)) {
                        String oldValue = rss.get(key).toString();
                        String newValue = oldValue + "," + r.get("value");
                        rss.replace(key, newValue);
                    } else {
                        rss.put(key, r.get("value"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rss;
    }


    public String getFilterPropertiesJson(String jsonStr) {
        String propertiesString = jsonStr.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2"); // 去掉key的引号
        return propertiesString;
    }

    public <T> String getkeyvalCyphersql(T obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> sqlList = new ArrayList<String>();
        // 得到类对象
        Class userCla = obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            Class type = f.getType();

            f.setAccessible(true); // 设置些属性是可以访问的
            Object val = new Object();
            try {
                val = f.get(obj);
                if (val == null) {
                    val = "";
                }
                String sql = "";
                String key = f.getName();
                System.out.println("key:" + key + "type:" + type);
                if (val instanceof Integer) {
                    // 得到此属性的值
                    map.put(key, val);// 设置键值
                    sql = "n." + key + "=" + val;
                } else if (val instanceof String[]) {
                    //如果为true则强转成String数组
                    String[] arr = (String[]) val;
                    String v = "";
                    for (int j = 0; j < arr.length; j++) {
                        arr[j] = "'" + arr[j] + "'";
                    }
                    v = String.join(",", arr);
                    sql = "n." + key + "=[" + val + "]";
                } else if (val instanceof List) {
                    //如果为true则强转成String数组
                    List<String> arr = (ArrayList<String>) val;
                    List<String> aa = new ArrayList<String>();
                    String v = "";
                    for (String s : arr) {
                        s = "'" + s + "'";
                        aa.add(s);
                    }
                    v = String.join(",", aa);
                    sql = "n." + key + "=[" + v + "]";
                } else {
                    // 得到此属性的值
                    map.put(key, val);// 设置键值
                    sql = "n." + key + "='" + val + "'";
                }

                sqlList.add(sql);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String finasql = String.join(",", sqlList);
        System.out.println("单个对象的所有键值==反射==" + map.toString());
        return finasql;
    }

    public <T> List<T> HashMapToObject(List<HashMap<String, Object>> maps, Class<T> type) {
        try {
            List<T> list = new ArrayList<T>();
            for (HashMap<String, Object> r : maps) {
                T t = type.newInstance();
                Iterator iter = r.entrySet().iterator();// 该方法获取列名.获取一系列字段名称.例如name,age...
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
                    String key = entry.getKey().toString(); // 从iterator遍历获取key
                    Object value = entry.getValue(); // 从hashmap遍历获取value
                    if ("serialVersionUID".toLowerCase().equals(key.toLowerCase())) continue;
                    Field field = type.getDeclaredField(key);// 获取field对象
                    if (field != null) {
                        field.setAccessible(true);
                        if (field.getType() == int.class || field.getType() == Integer.class) {
                            if (value == null || StringUtil.isBlank(value.toString())) {
                                field.set(t, 0);// 设置值
                            } else {
                                field.set(t, Integer.parseInt(value.toString()));// 设置值
                            }
                        } else if (field.getType() == long.class || field.getType() == Long.class) {
                            if (value == null || StringUtil.isBlank(value.toString())) {
                                field.set(t, 0);// 设置值
                            } else {
                                field.set(t, Long.parseLong(value.toString()));// 设置值
                            }

                        } else {
                            field.set(t, value);// 设置值
                        }
                    }

                }
                list.add(t);
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T HashMapToObjectItem(HashMap<String, Object> map, Class<T> type) {
        try {
            T t = type.newInstance();
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();// 把hashmap转成Iterator再迭代到entry
                String key = entry.getKey().toString(); // 从iterator遍历获取key
                Object value = entry.getValue(); // 从hashmap遍历获取value
                if ("serialVersionUID".toLowerCase().equals(key.toLowerCase())) continue;
                Field field = type.getDeclaredField(key);// 获取field对象
                if (field != null) {
                    field.setAccessible(true);
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        if (value == null || StringUtil.isBlank(value.toString())) {
                            field.set(t, 0);// 设置值
                        } else {
                            field.set(t, Integer.parseInt(value.toString()));// 设置值
                        }
                    } else if (field.getType() == long.class || field.getType() == Long.class) {
                        if (value == null || StringUtil.isBlank(value.toString())) {
                            field.set(t, 0);// 设置值
                        } else {
                            field.set(t, Long.parseLong(value.toString()));// 设置值
                        }

                    } else {
                        field.set(t, value);// 设置值
                    }
                }

            }

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
