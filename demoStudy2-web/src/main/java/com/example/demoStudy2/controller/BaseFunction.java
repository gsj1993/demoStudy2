package com.example.demoStudy2.controller;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.event.Req.AuthGetTokenReq;
import com.example.demoStudy2.event.Req.LoginReq;
import com.example.demoStudy2.event.Response;
import com.example.demoStudy2.template.CoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("baseFunction")
public class BaseFunction {
    @Resource
    private CoreTemplate coreTemplate;
    @Resource
    private BaseFunctionBusiness baseFunctionBusiness;


    @PostMapping("/login")
    @ResponseBody
    public Response login(@RequestBody LoginReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.login(req));
                }
        );
    }

    /**
     * 使用authCode请求accessToken和openID
     * @param req
     * @return
     */
    @PostMapping("/authGetToken")
    @ResponseBody
    public Response authGetToken(@RequestBody AuthGetTokenReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.authGetToken(req));
        });
    }
}
