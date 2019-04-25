package com.warmer.kgmaker.dal;


import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;


public interface EncyclopaediaRepository {


    HashMap<String,Object> getBaiKeEntity(String  baiKeName);
}
