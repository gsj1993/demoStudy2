package com.example.demoStudy2.controller;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.event.Req.*;
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

    /**
     * 文件请求
     * @param req
     * @return
     */
    @PostMapping("/initiateRequest")
    @ResponseBody
    public Response initiateRequest(@RequestBody InitiateReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.initiateRequest(req));
        });
    }

    /**
     * 邮件预请求
     * @param req
     * @return
     */
    @PostMapping("/initiateEmailRequest")
    @ResponseBody
    public Response initiateEmailRequest(@RequestBody InitiateEmailReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.initiateEmailRequest(req));
        });
    }

    /**
     * 签名预请求
     * @param req
     * @return
     */
    @PostMapping("/initiateSignRequest")
    @ResponseBody
    public Response initiateSignRequest(@RequestBody InitiateSignReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.initiateSignRequest(req));
        });
    }

    /**
     * pdf签名预请求
     * @param req
     * @return
     */
    @PostMapping("/initiatePdfSignRequest")
    @ResponseBody
    public Response initiatePdfSignRequest(@RequestBody InitiatePdfSignReq req){
        return coreTemplate.query(()-> {
            return new Response(baseFunctionBusiness.initiatePdfSignRequest(req));
        });
    }

}
