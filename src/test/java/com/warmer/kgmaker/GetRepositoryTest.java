package com.warmer.kgmaker;

import com.warmer.kgmaker.dal.impl.GetRepository;
import com.warmer.kgmaker.util.Neo4jUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GetRepositoryTest {

    @Autowired
    Neo4jUtil neo4jUtil;  //= new Neo4jUtil();


    GetRepository getRepository = new GetRepository();

    @Test
    public void getBtnRe() {
//

        String sourceName = "脊髓损伤";

        String targetName = "运动功能障碍";

        HashMap<String, Object> btnRelationship = getRepository.getBtnRelationship(sourceName, targetName);


        System.out.println("测试输出" + btnRelationship);
    }

    @Test
    public void testExcuteCypher() {

//        String sourceName = "脊髓损伤";
//
//        String targetName = "运动功能障碍"; ,sourceName,targetName

        String cql = String.format("Match (p1),(p2),p = shortestpath((p1)-[*..100]-(p2)) where p1.name = '脊髓损伤' and p2.name = '运动功能障碍' return nodes(p) limit 20 ");

        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);

        System.out.println(statementResult);

        System.out.println(statementResult.list());


    }


    @Test
    public void testGetGraphNode() {

//        String sourceName = "脊髓损伤";
//
//        String targetName = "运动功能障碍"; ,sourceName,targetName
        String str = "";
        String cql = String.format("Match (p1),(p2),p = shortestpath((p1)-[*..100]-(p2)) where p1.name = '脊髓损伤' and p2.name = '运动功能障碍' return * limit 20 ");

//        List<HashMap<String, Object>> statementResult = neo4jUtil.GetGraphNode(cql);

        StatementResult statementResult = neo4jUtil.excuteCypherSql(cql);
        List<String> keys = statementResult.keys();
        Record next = statementResult.next();
//        System.out.println("next:" + next);
        System.out.println("keys" + keys);

        List<Value> values = next.values();


        for (Value value : values) {
            //判断  type
            if ("PATH".equals(value.type().name())) {
                Iterable<Relationship> relationships = value.asPath().relationships();
                System.out.println("TYPE(Iterable):" + relationships);
                Iterator<Relationship> iterator = relationships.iterator();
                System.out.println("TYPE(Iterator)");
                Relationship next1 = iterator.next();
                String type = next1.type();
                System.out.println("Relationship:" + next1);
                System.out.println(type);
            }
            if ("NODE".equals(value.type().name())) {
                Node node = value.asNode();
                System.out.println("NODE:" + node);
                Map<String, Object> map = node.asMap();
                for (Map.Entry<String, Object> objectEntry : map.entrySet()) {
                    System.out.println("object:"+objectEntry);
                }

                Iterable<String> labels = node.labels();
//                System.out.println(labels);
                Iterator<String> iterator = labels.iterator();
                String next1 = iterator.next();
                System.out.println("nextString"+next1);


            }


        }
        System.out.println(values);


        List<Record> listRecord = statementResult.list();

        List<String> ids = new ArrayList<>();
//        System.out.println("statementResult"+statementResult);
//        System.out.println("statementResultList"+listRecord);
        Set<Map.Entry<String, Object>> entries = next.asMap().entrySet();

        HashMap<String, Object> idlist = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries) {

//            System.out.println("entry:" + entry);
//            System.out.println("entry.getValue():" + entry.getValue());

            String[] split = entry.getValue().toString().split(",");
            System.out.println(split);
            for (String s : split) {
                if (s != null && !"".equals(s)) {
                    for (int i = 0; i < s.length(); i++) {
                        if (s.charAt(i) >= 48 && s.charAt(i) <= 57) {
                            str += s.charAt(i);

                        }
                    }
//                    System.out.println(str);
                    ids.add(str + ",");

                }
                System.out.println(ids);
            }


            if (statementResult.hasNext()) {
                System.out.println(statementResult);

                System.out.println("查询结果" + statementResult.list());
                System.out.println("statementResult.keys():" + statementResult.keys());
                System.out.println(statementResult.list().size());
//            for (Map.Entry<String, Object> stringObjectEntry : statementResult.list().get(0).asMap().entrySet()) {
//                System.out.println("list的value的属性："+stringObjectEntry);
//                System.out.println(stringObjectEntry.getValue());
//            }
//            System.out.println("statementResult.peek():"+statementResult.peek());


                List<Record> list = statementResult.list();
                System.out.println("List" + list);
                int n = 0;
                System.out.println("----" + n + "-----");
                if (!list.isEmpty()) {
                    for (Record record : list) {
                        System.out.println("----" + n + "-----");
                        n++;
                        System.out.println("record.values():" + record.values() + " 第" + n + "次");
                        for (Map.Entry<String, Object> stringObjectEntry : record.asMap().entrySet()) {
                            System.out.println("stringObjectEntry.getKey():" + stringObjectEntry.getKey());
                            System.out.println("stringObjectEntry.getValue():" + stringObjectEntry.getValue());
                        }
                    }
                }
            }
        }
    }
}
