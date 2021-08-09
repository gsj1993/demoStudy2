package com.example.demoStudy2.baseFunction;

import com.example.demoStudy2.event.Req.AuthGetTokenReq;
import com.example.demoStudy2.event.Req.LoginReq;
import com.example.demoStudy2.event.Resp.AuthGetTokenResp;
import com.example.demoStudy2.model.UserInfo;

public interface BaseFunctionBusiness {
    UserInfo login(LoginReq req);

    AuthGetTokenResp authGetToken(AuthGetTokenReq Req);
}
