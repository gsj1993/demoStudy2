package com.example.demoStudy2.event.Resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class InitiateEmailResp implements Serializable {
    private  boolean authByQR;
    private String ticketID;
}
