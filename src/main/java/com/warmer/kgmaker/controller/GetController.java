package com.warmer.kgmaker.controller;

import com.warmer.kgmaker.service.IKGGraphService;
import com.warmer.kgmaker.service.IKnowledgegraphService;
import com.warmer.kgmaker.service.impl.IGetService;
import com.warmer.kgmaker.service.impl.KGGraphService;
import com.warmer.kgmaker.util.R;
import com.warmer.kgmaker.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GetController {


    @Autowired
    IGetService getService;


    @GetMapping("/kg/relationsearch")
    public String searchBtnNode(Model model) {
        return "kg/relationSearch";
    }

    @GetMapping("kg/baike")
    public String baike(Model model) {
        return "kg/baike";
    }


    @GetMapping("kg/test")
    public String test(Model model) {
        return "kg/test";
    }

    @GetMapping("kg/cyclopedia")
    public String cyclopedia(Model model) {
        return "showmyself/cyclopedia";
    }


    @GetMapping("kg/bkdetail")
    public String BKDetail(Model model) {
        return "kg/BKDetail";
    }

    @GetMapping("kg/testCrumb")
    public String testCrumb(Model model) {
        return "kg/testCrumb";
    }


//    @GetMapping("kg/bkdetail")
//    public String bkdetail(Model model) {
//        return "kg/bkdetail";
//    }
//
//    @GetMapping("kg/base")
//    public String base(Model model) {
//        return "kg/base";
//    }
//
//    @GetMapping("kg/detail")
//    public String detail(Model model) {
//        return "kg/detail";
//    }


    /**
     * 查询首尾节点detail
     */
    @ResponseBody
    @RequestMapping(value = "/feNodeDetail/{nodeId}/{nodeId2}")
    public R<Map<String, Object>> getNodeDetail(@PathVariable String nodeId, @PathVariable String nodeId2) {
//    @RequestMapping(value = "/feNodeDetail")
//    public R<Map<String, Object>> getNodeDetail(@PathVariable List<Long> ids) {
        R<Map<String, Object>> result = new R<Map<String, Object>>();

//        String[] ids = new String[]{nodeId,nodeId2};

        List<Long> ids = new ArrayList<>();
        try {
//            Map<String, Object> res = new HashMap<String, Object>();
            HashMap<String, Object> getMap = getService.feNodeDetail(ids);
            result.code = 200;
            System.out.println(getMap);
            result.setData(getMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ApiOperation(value = "关系查询（最短路径）")
    @ResponseBody
    @GetMapping(value = "/getIdByName")
    public R<String> getIdByName(String sourceName, String targetName) {
        R<String> result = new R<String>();
        try {
            if (!StringUtil.isBlank(sourceName) && !StringUtil.isBlank(targetName)) {
                List<Long> graphModel = getService.getIdByName(sourceName, targetName);
                String nodeInf = "";
                for (Long aLong : graphModel) {
                    nodeInf += aLong;
                }

                if (graphModel != null) {
                    result.code = 200;
                    result.setData(nodeInf);
                    return result;
                }
            } else {
                System.out.println("sourceName or targetName is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    /**
     * 查询两点最短（关系查询）
     *
     * @param sourceName
     * @param targetName
     * @return
     */
//    @ApiOperation(value = "关系查询（最短路径）")
//    @ResponseBody
//    @GetMapping(value = "/getBtnRelationship/{sourceName}/{targetName}")
//    public R<HashMap<String, Object>> getBtnRelationship(@PathVariable("sourceName") String sourceName, @PathVariable("targetName") String targetName) {
//
//        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
//        try {
//            HashMap<String, Object> graphData = getService.getBtnRelationship(sourceName, targetName);
//            result.code = 200;
//            result.data = graphData;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.code = 500;
//            result.setMsg("服务器错误");
//        }
//
//        return result;
//    }
}
