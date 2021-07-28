package com.example.demoStudy2.baseFunction.impl;

import com.example.demoStudy2.baseFunction.BaseFunctionBusiness;
import com.example.demoStudy2.dao.UserInfoMapper;
import com.example.demoStudy2.event.Req.LoginReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
public class BaseFunctionBusinessImpl implements BaseFunctionBusiness {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public String login(LoginReq req) {
        List<Map<String, Object>> list=userInfoMapper.queryUserInfo();
        return "success2"+req.getInfo()+list.toString();
    }
}
