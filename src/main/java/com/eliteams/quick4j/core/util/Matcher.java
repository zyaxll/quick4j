package com.eliteams.quick4j.core.util;

/**
 * Created by zya on 16/10/25.
 */
public interface Matcher<T> {
    /**
     * 判断输入条件是否匹配
     *
     * @param t
     *            输入条件
     * @return 是否满足
     */
    public boolean matches(T t);
}
