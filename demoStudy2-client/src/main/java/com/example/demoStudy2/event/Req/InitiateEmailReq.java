package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InitiateEmailReq implements Serializable {
    private String businessID;
    private String accessToken;
    private String openID;
    private String source;
    private String redirectURI;
    private String state;
    private String formName;
    private String formNum;
    private String formDesc;
    private List<String> eMEFields;
}
