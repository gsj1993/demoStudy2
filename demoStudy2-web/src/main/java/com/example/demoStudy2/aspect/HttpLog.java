package com.example.demoStudy2.aspect;


import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.core.ApplicationContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Order(1)
@Aspect
@Slf4j
@Component
public class HttpLog  {

    public HttpLog(){}
    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    @Pointcut("execution(public * com.example.demoStudy2.controller.*.*(..))")
    public void webLogPointcut() {

    }


    @Around("webLogPointcut()")
    public Object around(ProceedingJoinPoint  proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info("url:{}", request.getRequestURI());
        // 打印 Http method
        log.info("HTTP Method:{}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method:{}.{}", proceedingJoinPoint.getSignature().getDeclaringTypeName(), proceedingJoinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP :{}", request.getRemoteAddr());
        // 打印请求入参
        log.info("Request Args:{}", new Gson().toJson(proceedingJoinPoint.getArgs()));
        Object ret = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        log.info(ret.toString());
        return ret;
    }

}
