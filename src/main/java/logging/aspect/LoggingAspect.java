package logging.aspect;

import logging.config.LoggingProperties;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final LoggingProperties properties;

    public LoggingAspect(LoggingProperties properties) {
        this.properties = properties;
    }

    @Before("@annotation(t1.openSchool.logging.annotation.LogExecution)")
    public void logMethodEntry(JoinPoint joinPoint) {
        if (!properties.isEnabled() || !properties.isLogExecutionEnabled()) return;

        log(properties.getLevel(), "Method called: {} with parameters: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(
            pointcut = "@annotation(t1.openSchool.logging.annotation.LogException)",
            throwing = "ex"
    )
    public void logMethodException(JoinPoint joinPoint, Exception ex) {
        if (!properties.isEnabled() || !properties.isLogExceptionEnabled()) return;

        log(LoggingProperties.Level.ERROR, "Exception in method {}: {} - {}",
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    @Around("@annotation(t1.openSchool.logging.annotation.LogExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled() || !properties.isLogExecutionTimeEnabled()) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log(properties.getLevel(), "Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTime);

        return result;
    }

    @Around("@annotation(t1.openSchool.logging.annotation.LogTracking)")
    public Object logTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled() || !properties.isLogTrackingEnabled()) {
            return joinPoint.proceed();
        }

        log(properties.getLevel(), "TRACE START: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());

        log(LoggingProperties.Level.DEBUG, "Parameters: {}", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log(LoggingProperties.Level.DEBUG, "Result: {}", result);
            return result;
        } catch (Throwable t) {
            log(LoggingProperties.Level.ERROR, "Error: {}", t.getMessage());
            throw t;
        } finally {
            log(properties.getLevel(), "TRACE END: {}.{}()",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }

    @AfterReturning(
            pointcut = "@annotation(t1.openSchool.logging.annotation.HandlingResult)",
            returning = "result"
    )
    public void handleResult(JoinPoint joinPoint, Object result) {
        if (!properties.isEnabled() || !properties.isHandlingResultEnabled()) return;

        log(properties.getLevel(), "Processing result of method {}",
                joinPoint.getSignature().getName());

        if (result != null) {
            log(properties.getLevel(), "Result type: {}", result.getClass().getSimpleName());

            if (result instanceof List) {
                log(properties.getLevel(), "Items count: {}", ((List<?>) result).size());
            }
        }
    }

    private void log(LoggingProperties.Level level, String format, Object... args) {
        if (!properties.isEnabled()) return;

        switch (level) {
            case TRACE -> logger.trace(format, args);
            case DEBUG -> logger.debug(format, args);
            case INFO -> logger.info(format, args);
            case WARN -> logger.warn(format, args);
            case ERROR -> logger.error(format, args);
        }
    }
}
