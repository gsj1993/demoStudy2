package com.example.demoStudy2.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class HttpLog  {

    public HttpLog(){}
    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    //@Pointcut("execution(public * cn.van.log.aop.controller.*.*(..))")
    @Pointcut("execution(public * com.example.demoStudy2.controller.*.*(..))")
    public void webLogPointcut() {

    }

    @Before("webLogPointcut()")
    public void doBefore(JoinPoint joinPoint){
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("请求方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info(parameterMap.toString());
    }



    @Around("webLogPointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object ret = proceedingJoinPoint.proceed();
        log.info(ret.toString());
        return ret;
    }

}
