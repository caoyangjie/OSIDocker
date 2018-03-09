/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明： hystrix请求上下文过滤器
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于15:46 2018/3/9
 * @修改说明：
 * @修改日期： 修改于15:46 2018/3/9
 * @版本号： V1.0.0
 */

public class HystrixRequestContextFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.shutdown();
        }
    }

    @Override
    public void destroy() {

    }

}

