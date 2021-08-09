package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthGetTokenReq implements Serializable {
    /**
     *
     */
    private String code;
}
