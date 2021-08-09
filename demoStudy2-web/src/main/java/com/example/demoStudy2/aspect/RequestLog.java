package com.example.demoStudy2.aspect;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Map;

public class RequestLog implements Serializable {

    @JSONField(
            ordinal = 1
    )
    private String requestUri;
    @JSONField(
            ordinal = 2
    )
    private String requestIp;
    @JSONField(
            ordinal = 3
    )
    private Map<String, String> requestHeaders;
    @JSONField(
            ordinal = 4
    )
    private Map<String, String[]> requestParameters;
    @JSONField(
            ordinal = 5
    )
    private Object requestBody;
    @JSONField(
            ordinal = 6
    )
    private Map<String, Object> requestAttributes;
    public RequestLog() {
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String[]> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String[]> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, Object> getRequestAttributes() {
        return requestAttributes;
    }

    public void setRequestAttributes(Map<String, Object> requestAttributes) {
        this.requestAttributes = requestAttributes;
    }
}
