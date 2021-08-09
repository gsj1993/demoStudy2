package com.example.demoStudy2.baseFunction.impl;
import com.example.demoStudy2.IamSmartInstance;
import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.dao.UserInfoMapper;
import com.example.demoStudy2.event.Req.AuthGetTokenReq;
import com.example.demoStudy2.event.Req.LoginReq;
import com.example.demoStudy2.event.Resp.AuthGetTokenResp;
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
    @Resource
    private IamSmartInstance iamSmartInstance;
    @Override
    public UserInfo login(LoginReq req) {
        long start=System.currentTimeMillis();
        UserInfo userInfo= userInfoMapper.selectByPrimaryKey(1);
        long end=System.currentTimeMillis();
        logger.info(""+(end-start));
        return userInfo;
    }

    @Override
    public AuthGetTokenResp authGetToken(AuthGetTokenReq req) {
        return iamSmartInstance.authGetToken(req.getCode());
    }
}
