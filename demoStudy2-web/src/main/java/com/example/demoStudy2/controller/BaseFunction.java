package com.example.demoStudy2.controller;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.event.Req.LoginReq;
import com.example.demoStudy2.event.Respnse;
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
    public Respnse login(@RequestBody LoginReq req){
        return coreTemplate.query(()-> {
            return new Respnse(baseFunctionBusiness.login(req));
                }
        );
    }
}
