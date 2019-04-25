package com.warmer.kgmaker.service;


import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * @author Administrator
 */

public interface EncyclopaediaService {


    public HashMap<String,Object> getBaiKeEntity(String baiKeName);
}
