package com.warmer.kgmaker.service;

import com.warmer.kgmaker.dal.IGetRepository;
import com.warmer.kgmaker.service.impl.IGetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
public class GetService implements IGetService {


    @Autowired
    IGetRepository getRepository;

    @Override
    public HashMap<String ,Object> getBtnRelationship(String sourceName, String targetName ){

        return getRepository.getBtnRelationship(sourceName,targetName);

    }

    @Override
    public HashMap<String, Object>feNodeDetail(List<Long> ids) {
        return getRepository.feNodeDetail(ids);
    }

    @Override
    public List<Long> getIdByName(String sourceName, String targetName) {
        return  getRepository.getIdByName(sourceName,targetName);
    }


}
