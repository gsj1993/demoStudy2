package com.example.demoStudy2.event.message;

public enum  BusinessMessage implements Message{

    SYSTEM_EXCEPTION("99999","系统异常");
    private String code;
    private String msg;

    BusinessMessage(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }


}
