package com.lm.annotation.inject;

public class NamedParam {

    public String mName;
    public Object mParam;

    public NamedParam(String name, Object param) {
        this.mName = name;
        this.mParam = param;
    }

    public static NamedParam of(String name, Object param) {
        return new NamedParam(name, param);
    }
}
