package me.kts.boardexample.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ServiceAspect {
    //    @Around("@annotation(ServiceLogging)")
    @Around("execution(* me.kts.boardexample.service.*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        log.info(joinPoint.getSignature().getDeclaringTypeName() + "의 " + joinPoint.getSignature().getName()
                + " 메소드 실행 시간 : " + (System.currentTimeMillis() - begin) + "ms");
        return proceed;
    }
}
