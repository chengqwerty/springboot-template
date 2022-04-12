package som.make.extend.check;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import som.make.extend.wrapper.ResultBean;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(6)
public class CheckParamsAspect {

    private static final Logger logger = LoggerFactory.getLogger(CheckParamsAspect.class);

    private CheckParamAspect checkParamAspect;

    @Autowired
    public void setCheckParam(CheckParamAspect checkParamAspect) {
        this.checkParamAspect = checkParamAspect;
    }

    @Around("@annotation(checkParams)")
    public Object checkParams(ProceedingJoinPoint joinPoint, CheckParams checkParams) throws Throwable {
        logger.debug("{}.{}开始进行参数校验", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        CheckParam[] checkParams1 = checkParams.value();
        for (CheckParam checkParam: checkParams1) {
            CheckStatus checkStatus = checkParamAspect.checkProcess(joinPoint, checkParam);
            if (checkStatus.getStatus().equals("-1")) {
                logger.error("{}.{}参数错误，{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), checkStatus.getMessage());
                ResultBean resultBean = new ResultBean<>(ResultBean.CHECK_FAIL, checkStatus.getMessage());
                return resultBean;
            } else if (checkStatus.getStatus().equals("1")) {
                if (ObjectUtils.isEmpty(checkStatus.getMessage())) {
                    logger.info("{}.{}参数正确。", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
                } else {
                    logger.info("{}.{}{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), checkStatus.getMessage());
                }
            }
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

}
