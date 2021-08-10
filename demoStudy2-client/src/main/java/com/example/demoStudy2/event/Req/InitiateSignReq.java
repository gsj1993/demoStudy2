package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;

@Data
public class InitiateSignReq implements Serializable {
    private String businessID;
    private String accessToken;
    private String openID;
    private String source;
    private String redirectURI;
    private String state;
    private String hashCode;
    private String sigAlgo;
    private String HKICHash;
    private String department;
    private String serviceName;
    private String documentName;
}
