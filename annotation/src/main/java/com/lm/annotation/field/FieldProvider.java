package com.lm.annotation.field;

import java.util.Set;

public interface FieldProvider {
    <T> T get(String fieldName);

    <T> T get(Class<T> tClass);

    Set<Object> allFields();
}
