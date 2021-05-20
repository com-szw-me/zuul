package com.szw.me.zuul.config;

import com.szw.me.zuul.enums.TypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties("zuul.filter.auth-filter")
public class AuthFilterProperties {
    private String filterType = TypeEnum.PRE.getType();
    private Integer filterOrder = 0;
    private Boolean shouldFilter = true;
    private List<String> blackList = new ArrayList<>();
    private List<String> whiteList = new ArrayList<>();
}
