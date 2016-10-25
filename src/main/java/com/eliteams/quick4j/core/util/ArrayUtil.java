package com.eliteams.quick4j.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zya on 16/10/25.
 */
public class ArrayUtil {
    /**
     * 获得数组对象包含的元素数量
     *
     * @param array
     *            数组
     * @return 数组大小，如果array为null，则返回0
     */
    public static int length(Object[] array) {

        return (array == null ? 0 : array.length);
    }

    /**
     * 判断数组对象是否为null或空
     *
     * @param array
     *            数组
     * @return 是否为null或空
     */
    public static boolean isEmpty(Object[] array) {

        return (array == null || array.length == 0);
    }

    /**
     * 判断数组对象是否不为null或空
     *
     * @param array
     *            数组
     * @return 是否不为null或空
     */
    public static boolean isNotEmpty(Object[] array) {

        return (array != null && array.length > 0);
    }

    /**
     * 判断数组是否包含指定对象
     *
     * @param array
     *            数组
     * @param target
     *            对象
     * @return 是否包含
     */
    public static boolean contains(Object[] array, Object target) {

        if (isNotEmpty(array)) {
            for (Object obj : array) {
                if ((target == null && obj == null) || (target != null && target.equals(obj))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断数组是否包含指定对象集中任何一个对象
     *
     * @param array
     *            数组
     * @param targets
     *            对象集
     * @return 是否包含
     */
    public static boolean containsAny(Object[] array, Object... targets) {

        if (isNotEmpty(array) && isNotEmpty(targets)) {
            for (Object target : targets) {
                for (Object obj : array) {
                    if ((target == null && obj == null) || (target != null && target.equals(obj))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 判断数组是否包含指定对象集中任何一个对象
     *
     * @param array
     *            数组
     * @param targets
     *            对象集
     * @return 是否包含
     */
    public static boolean containsAll(Object[] array, Object... targets) {

        if (isNotEmpty(array) && isNotEmpty(targets)) {
            for (Object target : targets) {
                boolean isContains = false;
                for (Object obj : array) {
                    if ((target == null && obj == null) || (target != null && target.equals(obj))) {
                        isContains = true;
                        break;
                    }
                }

                if (!isContains) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 根据指定下标从给定的数组中获得数据
     *
     * @param <T>
     *            元素类型
     * @param array
     *            数组
     * @param index
     *            下标
     * @return 元素
     */
    public static <T> T getElement(T[] array, int index) {

        return getElement(array, index, null);
    }

    /**
     * 根据指定下标从给定的数组中获得数据，数组为空或下标超限时返回默认元素
     *
     * @param <T>
     *            元素类型
     * @param array
     *            数组
     * @param index
     *            下标
     * @param defElement
     *            默认元素
     * @return 元素
     */
    public static <T> T getElement(T[] array, int index, T defElement) {

        if (array == null || index < 0 || index >= array.length) {
            return defElement;
        }

        return array[index];
    }

    /**
     * 在根据给定下标从数组中取出指定数量的元素
     *
     * @param <T>
     * @param array
     *            数组
     * @param startIndex
     *            下标
     * @param length
     *            长度
     * @return 取出的元素数组
     */
    public static <T> T[] getElements(T[] array, int startIndex, int length) {

        if (array == null) {
            return null;

        } else if (array.length == 0 || startIndex >= array.length || length <= 0) {
            return newArray(array, 0);
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (startIndex + length > array.length) {
            length = array.length - startIndex;
        }

        T[] result = newArray(array, length);
        System.arraycopy(array, startIndex, (Object[]) result, 0, length);

        return result;
    }

    /**
     * 根据给定长度将数组分割成若干子数组
     *
     * @param array
     *            数组
     * @param length
     *            分割长度. 如果小于等于0, 则默认为array数组的长度.
     * @return 二维数组，包含分割后的子数组(源数组的复制), 按下标顺序排列.
     */
    public static <T> T[][] split(T[] array, int length) {

        return split(array, length, false);
    }

    /**
     * 根据给定长度将数组分割成若干子数组
     *
     * @param array
     *            数组
     * @param length
     *            分割长度. 如果小于等于0, 则默认为array数组的长度.
     * @param isKeepSameLength
     *            是否保持每个子数组的长度一致. 如果为true, 最后一个子数组的长度等于length, 多出空位填充null.
     *            如果为false, 则最后一个子数组的长度小于等于length.
     * @return 二维数组，包含分割后的子数组(源数组的复制), 按下标顺序排列.
     */
    public static <T> T[][] split(T[] array, int length, boolean isKeepSameLength) {

        if (array == null) {
            return null;

        } else if (array.length == 0) {
            return newArray(array, new int[] { 1, 0 });
        }

        if (length <= 0) {
            length = array.length;
        }

        T[][] result = newArray(array, new int[] { (int) Math.ceil((double) array.length / length), 0 });
        for (int ai = 0, ri = 0, riLen = length; ai < array.length; ai += length, ri++) {
            if (array.length - ai < length) {
                riLen = array.length - ai;
            }

            result[ri] = newArray(array, isKeepSameLength ? length : riLen);
            System.arraycopy(array, ai, (Object[]) result[ri], 0, riLen);
        }

        return result;
    }

    /**
     * 利用反射构建一维数组
     *
     * @param reference
     *            参考对象
     * @param dimension
     *            维度.
     * @return 数组对象. 如果参考对象为null或创建发生异常则返回null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newArray(Object reference, int dimension) {

        return (T) newArray(reference, new int[] { dimension });
    }

    /**
     * 利用反射构建数组
     *
     * @param reference
     *            参考对象
     * @param dimensions
     *            维度. 例如 new int[]{5}是长度为5的一维数组, new int[]{5, 5}是二维数组
     * @return 数组对象. 如果参考对象为null或创建发生异常则返回null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newArray(Object reference, int[] dimensions) {

        Class<?> clazz = null;
        if (reference instanceof Class<?>) {
            clazz = (Class<?>) reference;

        } else if (reference != null) {
            Class<?> refType = reference.getClass();
            if (refType.isArray()) {
                // ���referenceΪ����, ��ȡ��Ԫ������
                clazz = refType.getComponentType();

            } else {
                clazz = refType;
            }
        }

        if (clazz != null) {
            try {
                return (T) Array.newInstance(clazz, dimensions);
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * 将多个对象打造成数组
     *
     * @param objs
     *            零到多个对象
     * @return 数组. 如果objs是无指定类型的null, 则返回null.
     */
    public static <T> T[] asArray(T... objs) {

        return objs;
    }

    /**
     * 创建列表对象
     *
     * @param objs
     *            多个对象
     * @return 列表对象
     */
    public static <T> List<T> asList(T... objs) {

        if (objs == null) {
            return null;
        }

        List<T> list = new ArrayList<T>();
        for (T obj : objs) {
            list.add(obj);
        }

        return list;
    }

    /**
     * 创建集合对象
     *
     * @param objs
     *            多个对象
     * @return 集合对象
     */
    public static <T> Set<T> asSet(T... objs) {

        if (objs == null) {
            return null;
        }

        Set<T> set = new LinkedHashSet<T>();
        for (T obj : objs) {
            set.add(obj);
        }

        return set;
    }

    /**
     * 尝试将二维数组转换成Map, 其包含的一维数组第一个元素为key, 第二个元素为value. 如果包含的一维数组为空或元素少于2个,
     * 则被会被忽略.
     *
     * @param objs
     *            二维数组对象. 如果包含的一维数组为空或元素少于2个, 则被会被忽略
     * @return 键值对
     */
    public static <T> Map<T, T> asMap(T[][] objs) {

        return asMultiTypeMap(objs);
    }

    /**
     * 尝试将二维数组转换成Map, 其包含的一维数组第一个元素为key, 第二个元素为value. 如果包含的一维数组为空或元素少于2个,
     * 则被会被忽略. T类型必须是K, V类型的子类型, 不然在对Map进行操作时可能会出现运行时错误.
     *
     * @param objs
     *            二维数组对象. 如果包含的一维数组为空或元素少于2个, 则被会被忽略.
     * @return 键值对
     */
    @SuppressWarnings("unchecked")
    public static <K, V, T> Map<K, V> asMultiTypeMap(T[][] objs) {

        if (isEmpty(objs)) {
            return Collections.emptyMap();
        }

        Map<K, V> map = new LinkedHashMap<K, V>();
        for (T[] obj : objs) {
            if (obj != null && obj.length >= 2) {
                map.put((K) obj[0], (V) obj[1]);
            }
        }

        return map;
    }

    /**
     * 尝试将多个传入参数转换成Map, 奇数位的元素为key, 偶数位的元素为value. 如果为奇数个对象,
     * 则会自动添加一个null作为最后一个对象.
     *
     * @param objs
     *            包含任意数量对象. 如果为奇数个对象, 则会自动添加一个null作为最后一个对象.
     * @return 键值对
     */
    public static <T> Map<T, T> asMap(T... objs) {

        return asMultiTypeMap(objs);
    }

    /**
     * 尝试将多个传入参数转换成Map, 奇数位的元素为key, 偶数位的元素为value. 如果为奇数个对象,
     * 则会自动添加一个null作为最后一个对象. T类型必须是K, V类型的子类型, 不然在对Map进行操作时可能会出现运行时错误.
     *
     * @param objs
     *            包含任意数量对象. 如果为奇数个对象, 则会自动添加一个null作为最后一个对象.
     * @return 键值对
     */
    @SuppressWarnings("unchecked")
    public static <K, V, T> Map<K, V> asMultiTypeMap(T... objs) {

        if (isEmpty(objs)) {
            return Collections.emptyMap();
        }

        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < objs.length;) {
            map.put((K) objs[i++], (V) getElement(objs, i++));
        }

        return map;
    }

    /**
     * 提取数组中符合匹配条件的元素
     *
     * @param array
     *            原数组
     * @param matcher
     *            匹配器
     * @return 包含符合条件的元素数组. 返回null, 如果array为null或无元素或者matcher为null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] match(T[] array, Matcher<T> matcher) {

        if (matcher == null) {
            return null;
        }

        return matchAll(array, matcher);
    }

    /**
     * 提取数组中符合任何一个匹配条件的元素
     *
     * @param array
     *            原数组
     * @param matchers
     *            匹配器
     * @return 包含符合条件的元素数组. 返回null, 如果array为null或无元素或者无匹配器
     */
    public static <T> T[] matchAny(T[] array, Matcher<T>... matchers) {

        return match(array, matchers, false);
    }

    /**
     * 提取数组中符合全部匹配条件的元素
     *
     * @param array
     *            原数组
     * @param matchers
     *            匹配器
     * @return 包含符合条件的元素数组. 返回null, 如果array为null或无元素或者无匹配器
     */
    public static <T> T[] matchAll(T[] array, Matcher<T>... matchers) {

        return match(array, matchers, true);
    }

    /**
     * 提取数组中符合匹配条件的元素
     *
     * @param array
     *            原数组
     * @param matchers
     *            匹配器数组
     * @param isMatchAll
     *            是否满足全部条件才提取. 如果为false, 则只需满足任意条件就提取
     * @return 包含符合条件的元素数组. 返回null, 如果array为null或无元素或者匹配器数组为空或无元素
     */
    private static <T> T[] match(T[] array, Matcher<T>[] matchers, boolean isMatchAll) {

        if (isEmpty(array) || isEmpty(matchers)) {
            return null;
        }

        List<T> matched = new ArrayList<T>();
        for (T obj : array) {
            if (MatchHelper.match(obj, matchers, isMatchAll)) {
                matched.add(obj);
            }
        }

        T[] result = newArray(array, new int[] { matched.size() });
        return matched.toArray(result);
    }

    private ArrayUtil() {

    }
}
