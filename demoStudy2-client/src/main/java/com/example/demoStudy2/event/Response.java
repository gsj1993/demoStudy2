package com.example.demoStudy2.event;

import com.example.demoStudy2.event.contants.HttpContants;
import lombok.Data;

@Data
public class  Response <T>{
    private String rejCode;
    private String rejMsg;
    private T body;
    public Response(T body){
        this.rejCode= HttpContants.SUCCESS;
        this.rejMsg="success";
        this.body=body;
    }

    public Response(String rejCode ,String rejMsg){
        this.rejCode= rejCode;
        this.rejMsg=rejMsg;
        this.body=null;
    }
}
