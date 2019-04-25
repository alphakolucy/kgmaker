package com.warmer.kgmaker.service.impl;

import com.warmer.kgmaker.dal.EncyclopaediaRepository;
import com.warmer.kgmaker.service.EncyclopaediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class EncyclopaediaServiceImpl implements EncyclopaediaService {

    @Autowired

    private EncyclopaediaRepository encyclopaediaRepository;


    @Override
    public HashMap<String, Object> getBaiKeEntity(String baiKeName) {
        HashMap<String, Object> map = encyclopaediaRepository.getBaiKeEntity(baiKeName);

        return map;
    }
}
