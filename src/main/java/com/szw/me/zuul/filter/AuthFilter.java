package com.szw.me.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.szw.me.framework.util.JwtUtil;
import com.szw.me.framework.util.ResultVOUtil;
import com.szw.me.framework.vo.ResultVO;
import com.szw.me.zuul.config.AuthFilterProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    AuthFilterProperties tokenFilterProperties;

    @Override
    public String filterType() {
        return tokenFilterProperties.getFilterType();
    }

    @Override
    public int filterOrder() {
        return tokenFilterProperties.getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {
        return tokenFilterProperties.getShouldFilter();
    }

    @Override
    public Object run() throws ZuulException {
        // 获取context
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();

        // 客户端地址
        String remoteHost = request.getRemoteHost();
        // 请求接口地址
        String requestURI = request.getRequestURI();

        // 1. 黑名单拦截
        if (tokenFilterProperties.getBlackList().contains(remoteHost)) {
            context.set("isSuccess", false);
            context.setSendZuulResponse(false);
            context.setResponseBody(ResultVOUtil.error("非法请求").toString());
            context.getResponse().setContentType("application/json; charset=utf-8");
            return null;
        }
        // 2. 白名单放行，非白名单接口校验登陆用户
        if (!tokenFilterProperties.getWhiteList().contains(requestURI)) {
            String token = getToken(request);
            if (StringUtils.isBlank(token)) {
                context.set("isSuccess", false);
                context.setSendZuulResponse(false);
                context.setResponseBody(ResultVOUtil.error("请先登录").toString());
                context.getResponse().setContentType("application/json; charset=utf-8");
                return null;
            }
            ResultVO<Map<String, Object>> vo = JwtUtil.verify(token);
            if (!vo.getCode().equals(ResultVOUtil.CODE_SUCCESS)) {
                context.set("isSuccess", false);
                context.setSendZuulResponse(false);
                context.setResponseBody(vo.toString());
                context.getResponse().setContentType("application/json; charset=utf-8");
            } else {
                Map<String, Object> data = vo.getData();
                context.addZuulRequestHeader("code", (String) data.get("code"));
                context.addZuulRequestHeader("nick", (String) data.get("nick"));
            }
        }
        return null;
    }

    private static final String TOKEN_KEY = "token";

    private String getToken(HttpServletRequest request) {
        return request.getHeader(TOKEN_KEY);
    }
}
