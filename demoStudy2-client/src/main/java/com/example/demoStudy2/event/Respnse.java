package com.example.demoStudy2.event;

import com.example.demoStudy2.event.contants.HttpContants;

public class  Respnse <T>{
    private String rejCode;
    private String rejMsg;
    private T t;
    public Respnse(T t){
        this.rejCode= HttpContants.SUCCESS;
        this.rejMsg="success";
        this.t=t;
    }
}
