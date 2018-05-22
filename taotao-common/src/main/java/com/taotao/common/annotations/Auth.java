package com.taotao.common.annotations;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;

import java.lang.annotation.*;

/**
 * 权限注解。
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth
{
    /**
     * 模块编码
     *
     * @return
     */
    String modelNo() default "";

    /**
     * 方法编码
     *
     * @return
     */
    String actionNo() default "";
}
