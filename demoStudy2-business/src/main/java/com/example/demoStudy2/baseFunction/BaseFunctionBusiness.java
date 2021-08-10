package com.example.demoStudy2.baseFunction;

import com.example.demoStudy2.event.Req.*;
import com.example.demoStudy2.event.Resp.*;
import com.example.demoStudy2.model.UserInfo;

public interface BaseFunctionBusiness {
    UserInfo login(LoginReq req);

    /**
     * 根据扫码返回的code码获取accessToken与openID
     * @param Req
     * @return
     */
    AuthGetTokenResp authGetToken(AuthGetTokenReq Req);

    InitiateResp initiateRequest(InitiateReq req);

    InitiateEmailResp initiateEmailRequest(InitiateEmailReq req);

    InitiateSignResp initiateSignRequest(InitiateSignReq req);

    InitiatePdfSignResp initiatePdfSignRequest(InitiatePdfSignReq req);



}
