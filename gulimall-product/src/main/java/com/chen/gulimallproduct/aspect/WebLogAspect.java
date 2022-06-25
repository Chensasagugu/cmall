package com.chen.gulimallproduct.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.bouncycastle.util.IPAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author chen
 * @date 2022.05.10 21:03
 */
@Aspect
@Component
public class WebLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * 指定controller包下为切入点
     * 两个..代表所有子目录，最后括号里的两个..代表所有参数
     */
    @Pointcut("execution(* com.chen.gulimallproduct.controller.*.*(..))")
    public void logPointCut(){}

    @Pointcut("execution(* com.chen.gulimallproduct.web.*.*(..))")
    public void logPointCut2(){}
    /**
     * 指定当前执行方法在logPointCut之前执行
     */
    //@Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint)
    {
        System.out.println("在切入点之前执行");
        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        System.out.println("请求地址："+request.getRequestURI().toString());
        System.out.println("HTTP METHOD :" +request.getMethod());

        System.out.println("CLASS_METHOD :" + joinPoint.getSignature().getDeclaringTypeName()+"."
                +joinPoint.getSignature().getName());
        System.out.println("参数："+ Arrays.toString(joinPoint.getArgs()));
    }

    //@AfterReturning(returning = "ret" , pointcut = "logPointCut()")
    public void doAfterReturning(Object ret)
    {
        System.out.println("返回结果");
    }

    @Around("logPointCut2()")
    public Object doAround(ProceedingJoinPoint pjp)
    {
        long startTime = System.currentTimeMillis();
        //System.out.println("执行开始时间："+startTime);
        Object ob = null;
        try {
            ob = pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("执行耗时："+(System.currentTimeMillis()-startTime));
        return ob;
    }

}
