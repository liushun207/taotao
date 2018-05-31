package com.taotao.interceptor;

import com.taotao.common.pojo.RequestInstruction;
import com.taotao.common.pojo.ResponseInstruction;
import com.taotao.common.pojo.ResponseStatus;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 参数验证拦截器
 */
public class ValidationInterceptor implements MethodInterceptor
{
    // region 成员

    private static Logger logger = Logger.getLogger(ValidationInterceptor.class);
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    // endregion

    // region 私有方法

    /**
     * 验证参数
     *
     * @param <T> the type parameter
     * @param t   the t
     * @return 错误结果
     */
    private static <T> List<String> validate(T t)
    {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        List<String> messageList = new ArrayList<>();

        for (ConstraintViolation<T> constraintViolation : constraintViolations)
        {
            String paramterName = constraintViolation.getPropertyPath().toString();

            messageList.add("参数：`" + paramterName + "`值传入错误，" + constraintViolation.getMessage());
        }
        return messageList;
    }

    // endregion

    // region 重写方法

    /**
     * 环绕通知
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        // 获取方法
        Method method = invocation.getMethod();

        // 获取请求参数
        Object[] arguments = invocation.getArguments();

        try
        {
            // 有参数的进行判断
            if (arguments != null && arguments.length > 0)
            {
                for (Object argument : arguments)
                {
                    if(argument instanceof RequestInstruction)
                    {
                        argument = ((RequestInstruction) argument).getParams();
                    }

                    // 验证参数
                    List<String> errors = validate(argument);

                    // 如果有错误，固定返回类型
                    if (errors != null && !errors.isEmpty())
                    {
                        ResponseInstruction<String> response = new ResponseInstruction<String>(ResponseStatus.VALIDATIONERROR, errors.iterator().next());

                        return response;
                    }
                }
            }
        }
        catch (Exception e)
        {
            // 记录日志
            logger.error(e.getMessage());

            ResponseInstruction<String> response = new ResponseInstruction<String>(ResponseStatus.SERVERERROR, "方法参数校验失败！");
            return response;
        }

        // 继续执行
        return invocation.proceed();
    }

    // endregion
}
