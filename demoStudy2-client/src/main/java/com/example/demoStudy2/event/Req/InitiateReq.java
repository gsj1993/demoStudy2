package com.example.demoStudy2.event.Req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InitiateReq implements Serializable {
    //private String businessID;

    private String accessToken;

    private String openID;
    /**
     * 请求发起来源，可见 ITE_iAM_Smart_API_specification_v1.2.2-1.pdf Appendix B
     */
    private String source;
    /**
     * 回调url
     */
    private String redirectURI;

    //private String state;

    /**
     * 文件字段  idNo enName chName birthDate gender
     */
    private List<String> profileFields;

}
