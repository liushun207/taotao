package com.taotao.interceptor;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * T登录认证的拦截器.
 */
public class LoginInterceptor implements HandlerInterceptor
{
    @Autowired
    private UserService userService;

    /**
     * 执行Handler方法之前执行
     *
     * @param:
     * @param request
     * @param response
     * @param handler
     * @Description:
     * 用于身份认证、身份授权
     * 比如身份认证，如果认证通过表示当前用户没有登陆，需要此方法拦截不再向下执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //获取请求的url
        //String url = request.getRequestURI();

        //在Handler执行之前处理
        //判断用户是否登录
        //从cookie中取token
        String token = CookieUtils.getCookieValue(request,  userService.getTokenName());

        //根据token换取用户信息，调用sso系统的接口。
        TbUser user = userService.getTbUserByToken(token);

        //取不到用户信息
        if (null == user)
        {
            //跳转到登录页面，把用户请求的url作为参数传递给登录页面。
            response.sendRedirect(userService.getSSOBaseUrl() + userService.getSSOPageLogin()
                    + "?redirect=" + request.getRequestURL());
            //返回false
            return false;
        }

        //取到用户信息，放行
        //把用户信息放入Request
        request.setAttribute("user", user);
        //返回值决定handler是否执行。true：执行，false：不执行。

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

    private String getBaseURL(HttpServletRequest request)
    {
        String url = request.getScheme()
                + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath()
                + request.getRequestURI();
        return url;
    }
}
