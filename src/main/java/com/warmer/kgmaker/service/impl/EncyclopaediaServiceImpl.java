package com.warmer.kgmaker.service.impl;

import com.warmer.kgmaker.dal.EncyclopaediaRepository;
import com.warmer.kgmaker.service.EncyclopaediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class EncyclopaediaServiceImpl implements EncyclopaediaService {

    @Autowired

    private EncyclopaediaRepository encyclopaediaRepository;


    @Override
    public HashMap<String, Object> getBaiKeEntity(String baiKeName) {
        HashMap<String, Object> map = new HashMap<>();
        List<String> baiKeNameList = new ArrayList<>();

        //添加需要剔除节点的关系
        baiKeNameList.add("法医学鉴定");
        baiKeNameList.add("原因与机制");
        baiKeNameList.add("临床表现");
        baiKeNameList.add("依据");

        //筛选剔除百科节点
        List<String> nodeList = encyclopaediaRepository.getRelNodeList(baiKeNameList, baiKeName);
        if (nodeList.contains(baiKeName)) {
            System.out.println("无当前百科");
            map.put("warning","无当前百科");
            return map;
        }


        List<HashMap<String, Object>> treeList = new ArrayList<>();
        //获取tree 模块信息  =

        List<HashMap<String, Object>> treeMap = encyclopaediaRepository.getTreeList(baiKeName);




//        HashMap<String, Object> treeMap = encyclopaediaRepository.treeList(baiKeName);

//        treeList.add(treeMap);



        HashMap<String, Object> nodeInfo = encyclopaediaRepository.getBaiKeEntity(baiKeName);

        List<HashMap<String, Object>> causesAndMechanisms = encyclopaediaRepository.getCausesAndMechanisms(baiKeName);

        List<HashMap<String, Object>> medicolegalExpertise = encyclopaediaRepository.getMedicolegalExpertise(baiKeName);

        List<HashMap<String, Object>> clinic = encyclopaediaRepository.getClinicalFeature(baiKeName);

        String label = encyclopaediaRepository.getLabel(baiKeName);

        map.put("treeList",treeList);

        map.put("nodeInfo",nodeInfo);

        map.put("causesAndMechanisms", causesAndMechanisms);

        map.put("expertise", medicolegalExpertise);

        map.put("clinic", clinic);

        map.put("label", label);

        return map;

    }
}
