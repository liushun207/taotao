package com.taotao.interceptor;

import com.taotao.common.annotations.Auth;
import com.taotao.common.pojo.ResponseInstruction;
import com.taotao.common.pojo.ResponseStatus;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.common.utils.ResponseUtils;
import com.taotao.pojo.TbUser;
import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限拦截器
 **/
public class AuthInterceptor implements HandlerInterceptor
{
    /**
     * 执行Handler方法之前执行
     *
     * @param:
     * @param request
     * @param response
     * @param handler
     * @Description:
     * 用于身份认证
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 不是 `HandlerMethod` 类型 直接放行
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }

        // 获取权限注解
        HandlerMethod method = (HandlerMethod) handler;
        Auth auth = method.getMethodAnnotation(Auth.class);

        // 没有注解
        if (auth == null)
        {
            return true;
        }

        String modelNo = auth.modelNo();
        String actionNo = auth.actionNo();

        //List<String> auths = new ArrayList<>();//模拟从缓存或者从数据库中查询出对应用户的权限
        //if (!auths.contains(authId))
        //{
        //    throw new Exception("权限不足");
        //}

        //ResponseInstruction<String> result = new ResponseInstruction<String>(ResponseStatus.UNAUTHORIZED, null);

        // 发送响应
        //ResponseUtils.renderJson(response, JsonUtils.serialize(result));

        return true;
    }

    /**
     * 进入Handler方法之后，返回modelAndView之前执行
     *
     * @param:
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @Description:
     * 应用场景从modelAndView出发：将公用的模型数据(比如菜单导航)在这里
     * 传到视图，也可以在这里统一指定视图
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        //System.out.println("HandlerInterceptor1......postHandle");
    }

    /**
     * 执行Handler完成执行此方法
     *
     * @param:
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @Description:
     * 应用场景：统一异常处理，统一日志处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        //System.out.println("HandlerInterceptor1......afterHandle");
    }
}
