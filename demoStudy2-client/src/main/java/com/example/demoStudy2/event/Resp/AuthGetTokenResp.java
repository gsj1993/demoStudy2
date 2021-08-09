package com.example.demoStudy2.event.Resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthGetTokenResp implements Serializable {
    private String accessToken;
    private String tokenType;
    private String issueAt;
    private String expressed;
    private String milliseconds;
    private long expiresIn;
    private String openID;
    private long lastModifiedDate;
    private String userType;
    private String scope;
}
