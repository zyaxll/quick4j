package com.eliteams.quick4j.core.util;

/**
 * Created by zya on 16/10/25.
 */
public class MatchHelper {
    /**
     * 匹配单个元素
     *
     * @param obj
     *            待匹配值
     * @param matchers
     *            匹配器数组
     * @param isMatchAll
     *            是否满足全部条件才算匹配. 如果为false, 则只需满足任意条件就认为是匹配
     * @return 是否匹配
     */
    public static <T> boolean match(T obj, Matcher<T>[] matchers, boolean isMatchAll) {

        if (ArrayUtil.isEmpty(matchers)) {
            return false;
        }

        boolean isMatched = false;
        for (Matcher<T> matcher : matchers) {
            if (matcher == null) {
                continue;
            }

            isMatched = matcher.matches(obj);
            if (isMatched) {
                if (!isMatchAll) {
                    return true;
                }
            } else if (isMatchAll) {
                return false;
            }
        }

        return isMatched;
    }
}
