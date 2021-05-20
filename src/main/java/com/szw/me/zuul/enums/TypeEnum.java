package com.szw.me.zuul.enums;

import lombok.Getter;

@Getter
public enum TypeEnum {

    PRE("pre"),
    POST("post"),
    ROUT("rout"),
    ERROR("error");

    private String type;

    TypeEnum(String type) {
        this.type = type;
    }

}
