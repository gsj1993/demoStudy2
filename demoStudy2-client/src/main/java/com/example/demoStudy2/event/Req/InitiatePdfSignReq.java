package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;
@Data
public class InitiatePdfSignReq implements Serializable { ;
    private String accessToken;
    private String openID;
    private String source;
    private String redirectURI;
    private String docDigest;
    private String HKICHash;
    private String department;
    private String serviceName;
    private String documentName;
}
