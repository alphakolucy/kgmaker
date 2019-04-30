package com.warmer.kgmaker.controller;


import com.warmer.kgmaker.service.EncyclopaediaService;
import com.warmer.kgmaker.util.R;
import com.warmer.kgmaker.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class EncyclopaediaController extends BaseController {

    @Autowired
    private EncyclopaediaService encyclopaediaService;


    /**
     * 百科页面
     *
     * @param model
     * @return
     */
    @GetMapping("kg/baike")
    public String baike(Model model) {
        return "kg/baike";
    }

    /**
     * 获取百科信息（name detail  distinguish  cause  injure)
     *
     * @param baiKeName
     * @return
     */
    @ResponseBody
    @GetMapping(value = "getBaiKeEntity/{baiKeName}")
    public R<Map<String, Object>> getBaiKeEntity(@PathVariable("baiKeName") String baiKeName) {
        R<Map<String, Object>> result = new R<>();
        try {
            if (!StringUtil.isBlank(baiKeName)) {
                HashMap<String, Object> graphModel = encyclopaediaService.getBaiKeEntity(baiKeName);


                if (graphModel != null) {
                    result.code = 200;
                    result.setData(graphModel);
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
}
