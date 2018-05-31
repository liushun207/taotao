package com.taotao.common.exceptions;

import com.taotao.common.pojo.ResponseInstruction;
import com.taotao.common.pojo.ResponseStatus;
import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常捕获处理类
 **/
public class GlobalExceptionResolver implements HandlerExceptionResolver
{
    // region 成员

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);

    // endregion

    /**
     * 异常处理
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        // 记录错误日志
        logger.error(ex.getMessage(), ex);

        ResponseInstruction<String> result = new ResponseInstruction<String>(ResponseStatus.SERVERERROR, ex.getMessage());

        // 发送响应
        ResponseUtils.renderJson(response, JsonUtils.serialize(result));

        return null;
    }
}
