package com.example.demoStudy2.baseFunction.impl;
import com.example.demoStudy2.IamSmartInstance;
import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.dao.UserInfoMapper;
import com.example.demoStudy2.event.Req.*;
import com.example.demoStudy2.event.Resp.*;
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

    /**
     * 根据扫码返回的code码获取accessToken与openID
     * @param req
     * @return
     */
    @Override
    public AuthGetTokenResp authGetToken(AuthGetTokenReq req) {
        return iamSmartInstance.authGetToken(req.getCode());
    }

    @Override
    public InitiateResp initiateRequest(InitiateReq req) {
        return iamSmartInstance.initiateRequest(req.getAccessToken(),req.getOpenID(),req.getSource()
                ,req.getRedirectURI(),req.getProfileFields());
    }

    @Override
    public InitiateEmailResp initiateEmailRequest(InitiateEmailReq req) {
        return iamSmartInstance.initiateEmailRequest(req.getAccessToken(),req.getOpenID(),req.getSource()
                ,req.getRedirectURI(),req.getFormName(),req.getFormNum(),req.getFormDesc(),req.getEMEFields());
    }

    @Override
    public InitiateSignResp initiateSignRequest(InitiateSignReq req) {
        return iamSmartInstance.initiateSignRequest(req.getAccessToken(),req.getOpenID(),req.getSource(),req.getRedirectURI(),req.getHashCode(),req.getSigAlgo(),req.getHKICHash(),req.getDepartment(),req.getServiceName(),req.getDocumentName());
    }

    @Override
    public InitiatePdfSignResp initiatePdfSignRequest(InitiatePdfSignReq req) {
        return iamSmartInstance.initiatePdfSignRequest(req.getAccessToken(),req.getOpenID(),req.getSource(),req.getRedirectURI(),req.getDocDigest(),req.getHKICHash(),req.getDepartment(),req.getServiceName(),req.getDocumentName());
    }

}
