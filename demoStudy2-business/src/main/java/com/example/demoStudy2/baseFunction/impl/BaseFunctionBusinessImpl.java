package com.example.demoStudy2.baseFunction.impl;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.event.Req.LoginReq;
import org.springframework.stereotype.Component;



@Component
public class BaseFunctionBusinessImpl implements BaseFunctionBusiness {


    @Override
    public String login(LoginReq req) {
        return "success2"+req.getInfo();
    }
}
