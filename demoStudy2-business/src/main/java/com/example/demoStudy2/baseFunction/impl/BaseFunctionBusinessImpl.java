package com.example.demoStudy2.baseFunction.impl;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.dao.UserInfoMapper;
import com.example.demoStudy2.event.Req.LoginReq;
import com.example.demoStudy2.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class BaseFunctionBusinessImpl implements BaseFunctionBusiness {
    private Logger logger = LoggerFactory.getLogger(BaseFunctionBusinessImpl.class);
    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public String login(LoginReq req) {
        long start=System.currentTimeMillis();
        UserInfo userInfo=  userInfoMapper.selectByPrimaryKey(1);
        long end=System.currentTimeMillis();
        logger.info(""+(end-start));
        return "success2"+req.getInfo()+userInfo.toString();
    }
}
