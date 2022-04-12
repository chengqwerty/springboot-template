package som.make.extend.check;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import som.make.extend.wrapper.ResultBean;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@Order(6)
public class CheckParamAspect {

    private static final Logger logger = LoggerFactory.getLogger(CheckParamAspect.class);

    /**
     * 参数校验切面方法
     * @param joinPoint
     * @param checkParam
     * @return
     * @throws Throwable
     */
    @Around("@annotation(checkParam)")
    public Object checkParam(ProceedingJoinPoint joinPoint, CheckParam checkParam) throws Throwable {
        logger.debug("开始进行参数校验");
        Object[] objects = joinPoint.getArgs();
        CheckParamDefine checkParamDefine = new CheckParamDefine();
        checkParamDefine.parseAnnotation(checkParam);
        CheckStatus checkStatus = checkProcess(joinPoint, checkParam);
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
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return null;
    }

    /**
     * 参数校验方法
     * @param joinPoint
     * @param checkParam
     * @return
     * @throws IllegalAccessException
     */
    public CheckStatus checkProcess(ProceedingJoinPoint joinPoint, CheckParam checkParam) throws IllegalAccessException {
        Object[] objects = joinPoint.getArgs();
        CheckParamDefine checkParamDefine = new CheckParamDefine();
        checkParamDefine.parseAnnotation(checkParam);
        CheckStatus checkStatus = null;
        if ((checkParamDefine.getNotNull() == null || checkParamDefine.getNotNull().size() == 0) && (checkParamDefine.getNotNull() == null || checkParamDefine.getNotNull().size() == 0)) {
            logger.warn("注解的参数是空的，将跳过参数校验。");
            checkStatus = new CheckStatus("1");
            checkStatus.setMessage("注解的参数是空的，将跳过参数校验。");
        } else  {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request.getMethod().equals(RequestMethod.GET)) {
                checkStatus = checkRequestParams(joinPoint, checkParamDefine, request);
            } else if (request.getMethod().equals(RequestMethod.POST.name())) {
                checkStatus = checkRequestBody(joinPoint, checkParamDefine);
            }
        }
        return checkStatus;
    }

    /**
     * 对get方法的参数校验
     * @param joinPoint
     * @param checkParamDefine
     * @param request
     * @return
     */
    private CheckStatus checkRequestParams(JoinPoint joinPoint, CheckParamDefine checkParamDefine, HttpServletRequest request) {
        // 检查requestParam
        CheckStatus checkStatus = new CheckStatus("1");
        Map<String, String[]> paramsHeadMap = request.getParameterMap();
        if (null != paramsHeadMap && paramsHeadMap.keySet().size() > 0) {
            checkCycle(checkParamDefine, paramsHeadMap, checkStatus);
        } else {
            checkStatus.setStatus("-1");
            checkStatus.setMessage("这个请求没有参数。");
        }
        return checkStatus;
    }

    /**
     * post 方法的参数校验
     * @param joinPoint
     * @param checkParamDefine
     * @return
     * @throws IllegalAccessException
     */
    private CheckStatus checkRequestBody(JoinPoint joinPoint, CheckParamDefine checkParamDefine) throws IllegalAccessException {
        CheckStatus checkStatus = new CheckStatus("1");
        // 检查requestBody
        Object[] args = joinPoint.getArgs();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 参数注解，1维是参数，2维是注解
        Annotation[][] annotations = method.getParameterAnnotations();
        // 开始处理参数
        for (int i = 0; i < annotations.length; i++) {
            Object paramsVO = args[i];
            // 获取注解数组
            Annotation[] paramAnn = annotations[i];
            // 参数为空，直接下一个参数
            if (paramsVO == null || paramAnn.length == 0) {
                continue;
            }
            boolean isBodyOb = Arrays.stream(paramAnn).anyMatch(it -> it.annotationType().equals(RequestBody.class));
            if (isBodyOb) {
                //从接收封装的对象
                Map<String, Object> paramsBodyMap = new HashMap<>();
                if (ClassTypeUtil.isPackClass(paramsVO) || ClassTypeUtil.isBaseClass(paramsVO)) {
                    logger.error("{}.{}参数校验失败，此注解只接收 Map 或 Object 对象", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
                    checkStatus.setStatus("2");
                    return checkStatus;
                } else {
                    if (paramsVO instanceof List<?> && ((List<?>) paramsVO).size() > 0) {
                        logger.info("{}.{}参数校验终端，此注解不校验list属性，list集合不为空", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
                        checkStatus.setStatus("2");
                        return checkStatus;
                    }
                    // 使用Map接收参数
                    if (paramsVO instanceof Map) {
                        paramsBodyMap = (Map<String, Object>) paramsVO;
                    } else {
                        //使用对象接收参数,获取对象的全部属性
                        Field[] fields = ObjectUtil.getSupperClassProperties(paramsVO, new Field[0]);
                        for (Field field : fields) {
                            field.setAccessible(true);
                            paramsBodyMap.put(field.getName(), field.get(paramsVO));
                        }
                    }
                    checkCycle(checkParamDefine, paramsBodyMap, checkStatus);
                    return checkStatus;
                }
            }
        }
        checkStatus.setStatus("-1");
        checkStatus.setMessage("未知情况，可能是方法没有参数");
        return checkStatus;
    }

    /**
     * 循环判断每一组规则
     * @param checkParamDefine
     * @param paramMap
     * @param checkStatus
     * @return
     */
    private CheckStatus checkCycle(CheckParamDefine checkParamDefine, Map paramMap, CheckStatus checkStatus) {
        // 遍历集合,判断notNull
        if (checkParamDefine.getNotNull() != null) {
            for (CheckParamDefine.CheckGroup checkGroup : checkParamDefine.getNotNull()) {
                for (CheckParamDefine.CheckSingle checkSingle: checkGroup.getCheckSingleList()) {
                    if (ObjectUtils.isEmpty(paramMap.get(checkSingle.getName()))) {
                        checkStatus.setResult("-1", "notNull", checkSingle.getName(), checkSingle.getMessage());
                        return checkStatus;
                    }
                }
            }
        }
        // 遍历集合,判断notAllNull
        if (checkParamDefine.getNotAllNull() != null) {
            boolean status = false;
            for (CheckParamDefine.CheckGroup checkGroup : checkParamDefine.getNotAllNull()) {
                boolean groupStatus = true;
                for (CheckParamDefine.CheckSingle checkSingle: checkGroup.getCheckSingleList()) {
                    if (ObjectUtils.isEmpty(paramMap.get(checkSingle.getName()))) {
                        groupStatus = false;
                        break;
                    }
                }
                // 有一个group通过，说明整个规则就通过了
                if (groupStatus) {
                    status = true;
                    break;
                }
            }
            if (!status) {
                checkStatus.setResult("-1", "notAllNull", null, "请求中的参数不符合要求，请检查。");
            }
        }
        return checkStatus;
    }

}
