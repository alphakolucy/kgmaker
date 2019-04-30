package com.warmer.kgmaker.dal.impl;

import com.warmer.kgmaker.dal.EncyclopaediaRepository;
import com.warmer.kgmaker.entity.TreeEntity;
import com.warmer.kgmaker.service.EncyclopaediaService;
import com.warmer.kgmaker.util.ListUtils;
import com.warmer.kgmaker.util.Neo4jUtil;
import com.warmer.kgmaker.util.StringUtil;
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
//
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

    @Override
    public String getLabel(String baiKeName) {
        String substringLabel = null;
        try {
            String label = "";

            String cql = String.format("match(n) where n.name = '%s' return labels(n)", baiKeName);


            StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

            System.out.println("statementResult" + statementResult);
            Record next = statementResult.next();


//        if (statementResult.) {
            List<Pair<String, Value>> pairs = next.fields();
            for (Pair<String, Value> pair : pairs) {
                Value value = pair.value();
                label = value + "";
            }
//        }
            int length = label.length();
            substringLabel = "";
            if (!StringUtil.isBlank(label)) {
                substringLabel = label.substring(2, length - 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return substringLabel;
    }


    /**
     * 获取到不需要节点存入集合
     *
     * @param baiKeNameList
     * @param baiKeName
     * @return
     */
    @Override
    public List<String> getRelNodeList(List<String> baiKeNameList, String baiKeName) {
        List<String> list = new ArrayList<>();
        String whereCql = "";
        System.out.println(baiKeNameList);
        System.out.println(baiKeName);
        try {


            String lable = getLabel(baiKeName);

            //无子节点  lable,
            String notNullCql = String.format("MATCH (n:%s)-[r]->() where n.name = '%s' RETURN n", lable, baiKeName);

            Boolean boo = true;

            //查询是否有子节点查询
            StatementResult statementResultNotNull = neo4jUtil.excuteCypherSql(notNullCql);

            List<Record> notNullList = statementResultNotNull.list();

            if (notNullList.size() == 0 & notNullList.isEmpty()) {
                //给定
                boo = false;
                System.out.println("该节点没有子节点 百科中去除");
            }

            //拼接where条件
            for (int i = 0; i < baiKeNameList.size(); i++) {
                if (i < baiKeNameList.size()) {
                    if (i == 0) {
//                    where r.name = '%s' or r.name = '%s' or r.name = '%s' or r.name = '%s'
                        whereCql = String.format("where r.name = '%s'", baiKeNameList.get(i));
                    } else {
                        whereCql += String.format(" or r.name = '%s'", baiKeNameList.get(i));
                    }

                }
            }
            System.out.println("whereCql:" + whereCql);
            String cql = String.format("match()-[r]->(n:脊柱与脊髓损伤) %s return n", whereCql);


            //查询该关系下的节点
            StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

            List<Record> records = statementResult.list();
            System.out.println("records:" + records);

            for (Record record : records) {

                Map<String, Object> recordMap = record.asMap();
                List<Value> values = record.values();


                for (Value value : values) {
                    Node node = value.asNode();
                    Map<String, Object> nodeMap = node.asMap();
                    Set<Map.Entry<String, Object>> entries = nodeMap.entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        String key = entry.getKey();
                        if ("name".equals(key)) {
                            String getValue = entry.getValue() + "";
                            list.add(getValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        List reList = ListUtils.removeDuplicate(list);
        return reList;
    }

    /**
     * 获取节点属性信息
     *
     * @param statementResult
     * @return
     */
    public List<HashMap<String, Object>> getNodeInfoUtil(StatementResult statementResult) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        if (statementResult.hasNext()) {
            List<Record> records = statementResult.list();
            for (Record record : records) {
                List<Pair<String, Value>> pairs = record.fields();
                for (Pair<String, Value> pair : pairs) {
                    String typeName = pair.value().type().name();
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

    @Override
    public List<HashMap<String, Object>> getTreeList(String baiKeName) {
        List<HashMap<String, Object>> treeList = new ArrayList<>();


        //tree实体类
        TreeEntity treeEntity = new TreeEntity();
        String label = getLabel(baiKeName);

        //查询获得该章内容 （该标签下的节点）
//        String treeCql = String.format("MATCH (n:%s) RETURN n", label);
        String treeCql = String.format("match  (a)-[r]-(b) where a.name = '%s'  return *", label);
        StatementResult statementResult = neo4jUtil.excuteCypherSql(treeCql);

        List<Record> records = statementResult.list();

        //获取当前节点id 和label
        HashMap<String, Object> treeIdAndLabel = getTreeIdAndLabel(statementResult);

        // 将子节点存入 childNodeList
        List<String> childNodes = getChildNodes(records);

        for (String childNode : childNodes) {

        }

        return treeList;
    }



    public List<String> getChildNodes(List<Record> records){
        List<String>  childNodeList = new ArrayList<>();
        //将子节点存入 childNodeList
        for (Record record : records) {
            List<Pair<String, Value>> pairs = record.fields();
            List<Value> values = record.values();
            Map<String, Object> map = record.asMap();
            Node secNode = (Node)map.get("b");
            Map<String, Object> secNodemap = secNode.asMap();
            for (Map.Entry<String, Object> secNodes : secNodemap.entrySet()) {
                String key = secNodes.getKey();
                String value = (String) secNodes.getValue();
                if ("name".equals(key)){
                    childNodeList.add(value);
                }
            }
        }
        return childNodeList;
    }
    /**
     * 封装

     * @param statementResult
     * @return
     */
    public HashMap<String, Object> getTree(StatementResult statementResult) {

        //获取id & label
        HashMap<String, Object> treeModel = getTreeIdAndLabel(statementResult);
        //获取children 并添加到 treeIdAndLabel
        List<HashMap<String, Object>> treeChildren = getTreeChildren(statementResult);
        //封装
        treeModel.put("children", treeChildren);
        return treeModel;

    }

    /**
     * 获取当前节点id 和 label
     *
     * @param statementResult
     * @return id: 1,
     * label: '法医临床学',
     * children: [{},{},{}]
     */
    @Override
    public HashMap<String, Object> getTreeIdAndLabel(StatementResult statementResult) {
//        public List<HashMap<String, Object>> getTreeIdAndLabel(String baiKeName) {


        List<HashMap<String, Object>> treeList = new ArrayList<>();
        HashMap<String, Object> treeModel = new HashMap<>();


        List<Record> records = statementResult.list();

        Iterator<Record> iterator = records.iterator();


        for (Record record : records) {
            List<Pair<String, Value>> fields = record.fields();

            //获取当前节点
            Pair<String, Value> startPair = fields.get(0);
            Value value = startPair.value();

            Node startNode = value.asNode();

            //获取id
            String id = startNode.id() + "";
            treeModel.put("id", id);

            Map<String, Object> startNodeMap = startNode.asMap();
            Set<Map.Entry<String, Object>> entries = startNodeMap.entrySet();

            //获取label
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                String label = "";
                if ("name".equals(key)) {
                    label = entry.getValue() + "";
                    treeModel.put("label", label);
                }
            }


        }

//        if (!childrenList.isEmpty()) {
//            treeModel.put("children", childrenList);
//        }
        System.out.println("treeModel: " + treeModel);

//        treeList.add(treeModel);

        //去重 ，留心使用

//        List newTreelist = ListUtils.removeDuplicate(treeList);

////        }


        return treeModel;

    }


    /**
     * 获取下级节点
     *
     * @param statementResult
     * @return
     */
    public List<HashMap<String, Object>> getTreeChildren(StatementResult statementResult) {

        //获取下级节点children
        List<HashMap<String, Object>> childrenList = new ArrayList<>();

        List<Record> records = statementResult.list();

        for (Record record : records) {
            List<Pair<String, Value>> fields = record.fields();
            Pair<String, Value> secPair = fields.get(1);
            Node secNode = secPair.value().asNode();
            Set<Map.Entry<String, Object>> secEntries = secNode.asMap().entrySet();
            for (Map.Entry<String, Object> secEntry : secEntries) {

                String key = secEntry.getKey();
                if ("name".equals(key)) {
                    HashMap<String, Object> treeChildrenModel = new HashMap<>();
                    Object value = secEntry.getValue();


//                    HashMap<String, Object> children = getTreeIdAndLabel(label);
//                    treeChildrenModel.put("children",children);
                }
            }
        }

        return childrenList;
    }


    /**
     * 获取信息并按tree封装 2
     * 封装第一层
     *
     * @param baiKeName
     * @return
     */
    @Override
    public HashMap<String, Object> treeList(String baiKeName) {
        TreeEntity treeEntity = new TreeEntity();

        String labelStr = "";
        String cql = String.format("match  (a)-[r]-(b) where a.name = '%s'  return *", baiKeName);

        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

        List<Record> records = statementResult.list();

        if (StringUtil.isBlank(labelStr)) {

        }


        for (Record record : records) {
            List<Pair<String, Value>> pairs = record.fields();
            for (Pair<String, Value> pair : pairs) {
                String typeName = pair.value().type().name();
                if ("NODE".equals(typeName)) {
                    Node node = pair.value().asNode();
                    Iterable<String> labels = node.labels();
                    for (String label : labels) {
                        labelStr = label;
                    }

//                    labels.

                }


//
//                if ("PATH".equals(typeName)){
//                    Path path = pair.value().asPath();
//                    Node start = path.start();
//                    Iterable<Node> nodes = path.nodes();
//                    for (Node node : nodes) {
//                        Iterable<Value> values = node.values();
//                        System.out.println("path node:" +node);
//                    }
//
//                }

//                if ("RELATIONSHIP".equals(typeName)){
//                    Relationship relationship = pair.value().asRelationship();
//
//
//                }


            }
        }

        return null;
    }

}
