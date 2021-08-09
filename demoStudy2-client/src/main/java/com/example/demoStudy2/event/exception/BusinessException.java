package com.example.demoStudy2.event.exception;

import com.example.demoStudy2.event.message.Message;

public class BusinessException extends Exception {
    private String errorCode;
    private String errorMsg;
    public BusinessException(Message message) {
        this.errorCode = message.getCode();
        this.errorMsg = message.getMsg();
    }

    protected BusinessException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    protected BusinessException(String errorCode, String errorMsg, Exception e) {
        super(e);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    protected boolean canEqual(final Object other) {
        return other instanceof BusinessException;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BusinessException)) {
            return false;
        } else {
            BusinessException other = (BusinessException)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$errorCode = this.getErrorCode();
                Object other$errorCode = other.getErrorCode();
                if (this$errorCode == null) {
                    if (other$errorCode != null) {
                        return false;
                    }
                } else if (!this$errorCode.equals(other$errorCode)) {
                    return false;
                }

                Object this$errorMsg = this.getErrorMsg();
                Object other$errorMsg = other.getErrorMsg();
                if (this$errorMsg == null) {
                    if (other$errorMsg != null) {
                        return false;
                    }
                } else if (!this$errorMsg.equals(other$errorMsg)) {
                    return false;
                }

                return true;
            }
        }
    }
}
