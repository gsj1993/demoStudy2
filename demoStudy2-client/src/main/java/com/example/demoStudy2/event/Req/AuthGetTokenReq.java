package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthGetTokenReq implements Serializable {
    /**
     *扫码或者app认证返回的code码，
     */
    private String code;
}
