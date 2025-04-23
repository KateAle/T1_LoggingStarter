package logging.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Aspect
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("@annotation(logging.aspect.annotation.LogExecution)")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.debug("Method called: {} with parameters: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(
            pointcut = "@annotation(logging.aspect.annotation.LogException)",
            throwing = "exception"
    )
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        logger.error("Exception in method {}: {} - {}",
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    @Around("@annotation(logging.aspect.annotation.LogExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        logger.debug("Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTime);

        return result;
    }

    @Around("@annotation(logging.aspect.annotation.LogTracking)")
    public Object logTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.trace("TRACE START: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());

        logger.debug("Parameters: {}", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            logger.debug("Result: {}", result);
            return result;
        } catch (Throwable throwable) {
            logger.error("Error: {}", throwable.getMessage());
            throw throwable;
        } finally {
            logger.trace("TRACE END: {}.{}()",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }

    @AfterReturning(
            pointcut = "@annotation(logging.aspect.annotation.HandlingResult)",
            returning = "result"
    )
    public void handleResult(JoinPoint joinPoint, Object result) {
        logger.debug("Processing result of method {}",
                joinPoint.getSignature().getName());

        if (result != null) {
            logger.debug("Result type: {}", result.getClass().getSimpleName());

            if (result instanceof List) {
                logger.debug("Items count: {}", ((List<?>) result).size());
            }
        }
    }
}