package com.example.demoStudy2.globeException;

import com.example.demoStudy2.event.Response;
import com.example.demoStudy2.event.exception.BusinessException;
import com.example.demoStudy2.event.message.BusinessMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ResponseBody
    @ExceptionHandler({BusinessException.class})
    public Response globalException(BusinessException ex) {
        String errorCode = ex.getErrorCode();
        String errorMsg = ex.getErrorMsg();
        return new Response(errorCode, errorMsg);
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public Response globalException(Exception ex) {
        return new Response(BusinessMessage.SYSTEM_EXCEPTION.getCode(),BusinessMessage.SYSTEM_EXCEPTION.getMsg());
    }

}
