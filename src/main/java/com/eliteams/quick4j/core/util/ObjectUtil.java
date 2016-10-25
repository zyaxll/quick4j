package com.eliteams.quick4j.core.util;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zya on 16/10/25.
 */
public class ObjectUtil {
    /**
     * 比较两个对象的对应属性名的属性值是否相同(非宽松模式)，不指定属性名则比较所有属性。
     * 如果任一对象为空，或比较全属性且属性数量不一致，或指定属性不存在或属性值不一致，则返回不相同，不然返回相同
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @param propNames
     *            属性名集合
     * @return 是否相同
     */
    public static boolean isSame(Object obj1, Object obj2, String... propNames) {

        return isSame(obj1, obj2, false, propNames);
    }

    /**
     * 比较两个对象的对应属性名的属性值是否相同，不指定属性名则比较所有属性。
     * 宽松模式：如果两个对象不全为空，或指定属性值不一致，则返回不相同，否则返回相同(允许属性不存在或不一致)。
     * 非宽松模式：如果任一对象为空，或比较全属性且属性数量不一致，或指定属性不存在或属性值不一致，则返回不相同，否则返回相同。
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @param isLenient
     *            是否为宽松模式
     * @param propNames
     *            属性名集合
     * @return 是否相同
     */
    public static boolean isSame(Object obj1, Object obj2, boolean isLenient, String... propNames) {

        try {
            return isSameImpl(obj1, obj2, isLenient, propNames);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 比较两个对象的对应属性名的属性值是否相同，不指定属性名则比较所有属性。
     * 宽松模式：如果两个对象不全为空，或指定属性值不一致，则返回不相同，否则返回相同(允许属性不存在或不一致)。
     * 非宽松模式：如果任一对象为空，或比较全属性且属性数量不一致，或指定属性不存在或属性值不一致，则返回不相同，否则返回相同。
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @param isLenient
     *            是否为宽松模式
     * @param propNames
     *            属性名集合
     * @return 是否相同
     */
    private static boolean isSameImpl(Object obj1, Object obj2, boolean isLenient, String... propNames)
            throws Exception {

        if (obj1 == obj2) {
			/*
			 * 宽松模式下，引用相同则相同
			 * 非宽松模式下，如果对象引用相同且都不为空且比较所有属性，则返回相同，否则即使引用相同且不为空也必须验证属性是否存在
			 */
            if (isLenient || (obj1 != null && ArrayUtil.isEmpty(propNames))) {
                return true;
            }
        }

        // 任意一个为null则不相同
        if (obj1 == null || obj2 == null) {
            return false;
        }

        // 打造对象1的属性名与属性表述信息的映射关系
        Map<String, PropertyDescriptor> propDefMap1 = new HashMap<String, PropertyDescriptor>();
        for (PropertyDescriptor pd : Introspector.getBeanInfo(obj1.getClass()).getPropertyDescriptors()) {
            if (!"class".equals(pd.getName())) {
                propDefMap1.put(pd.getName(), pd);
            }
        }

        // 如果对象2与对象1的类型相同，则使用相同的映射关系
        Map<String, PropertyDescriptor> propDefMap2 = propDefMap1;
        if (!obj1.getClass().equals(obj2.getClass())) {
            // 不同则解析
            propDefMap2 = new HashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor pd : Introspector.getBeanInfo(obj2.getClass()).getPropertyDescriptors()) {
                if (!"class".equals(pd.getName())) {
                    propDefMap2.put(pd.getName(), pd);
                }
            }
        }

        // 如果未指定属性名，则比较所有属性(选取任何一个对象的属性映射作为参考)
        if (ArrayUtil.isEmpty(propNames)) {
            // 比较所有属性时，如果是非宽松模式则属性数量必须一致
            if (!isLenient && (propDefMap1.size() != propDefMap2.size())) {
                return false;
            }

            propNames = propDefMap1.keySet().toArray(new String[propDefMap1.size()]);
        }

        // 遍历所有属性，比较属性值
        for (String propName : propNames) {
            PropertyDescriptor pd1 = propDefMap1.get(propName);
            PropertyDescriptor pd2 = propDefMap2.get(propName);

            if (pd1 == null || pd2 == null) {
                // 宽松模式则继续判断下一个字段
                if (isLenient) {
                    continue;
                }

                return false;
            }

            // 如果不一致则返回不相同
            if (isNotEqual(pd1.getReadMethod().invoke(obj1), pd2.getReadMethod().invoke(obj2))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断两个对象是否不相等(equals方法)，如果全部为null，则根据返回isEqualIfAllNull值
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @param isEqualIfAllNull
     *            全部为null是否认为是相等
     * @return 是否不相等
     */
    public static boolean isNotEqual(Object obj1, Object obj2, boolean isEqualIfAllNull) {

        if (obj1 == null || obj2 == null) {
            return (obj1 == obj2 ? !isEqualIfAllNull : true);
        }

        return !obj1.equals(obj2);
    }

    /**
     * 判断两个对象是否不相等(equals方法)，如果全部为null，则返回false。
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @return 是否不相等
     */
    public static boolean isNotEqual(Object obj1, Object obj2) {

        return isNotEqual(obj1, obj2, true);
    }

    /**
     * 判断两个对象是否相等(equals方法)，如果全部为null，则根据返回isEqualIfAllNull值
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @param isEqualIfAllNull
     *            全部为null是否认为是相等
     * @return 是否相等
     */
    public static boolean isEqual(Object obj1, Object obj2, boolean isEqualIfAllNull) {

        if (obj1 == null || obj2 == null) {
            return (obj1 == obj2 ? isEqualIfAllNull : false);
        }

        return obj1.equals(obj2);
    }

    /**
     * 判断两个对象是否相等(equals方法)，如果全部为null，则返回true。
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @return 是否相等
     */
    public static boolean isEqual(Object obj1, Object obj2) {

        return isEqual(obj1, obj2, true);
    }

    /**
     * 调用指定对象的toString方法，如果对象为null则返回null
     *
     * @param obj
     *            对象
     * @return toString字符串
     */
    public static String toString(Object obj) {

        return (obj != null ? obj.toString() : null);
    }

    private ObjectUtil() {

    }

    public static boolean isEmpty(Object obj) {
        boolean result = true;
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            result = (obj.toString().trim().length() == 0) || obj.toString().trim().equals("null");
        } else if (obj instanceof Collection) {
            result = ((Collection<?>) obj).size() == 0;
        } else {
            result = ((obj == null) || (obj.toString().trim().length() < 1)) ? true : false;
        }
        return result;
    }

    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    public static List<Map<String, Object>> getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap<String, Object>();
            infoMap.put("type", fields[i].getType().toString());
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    public static Object[] getFiledValues(Object o) {
        String[] fieldNames = getFiledName(o);
        Object[] value = new Object[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            value[i] = getFieldValueByName(fieldNames[i], o);
        }
        return value;
    }

    /**
     * copy 一个新的与source同类型的对象出来,并把proNames中指定的field从原对象复制到新对象中 方法用途: <br>
     * 实现步骤: <br>
     *
     * @param source
     * @param propNames
     * @return
     */
    public static Object partCopyNew(Object source, String... propNames) {
        try {
            Class c = source.getClass();
            Object ret = c.newInstance();
            for (String prop : propNames) {
                Field field = getField(c, prop);
                if (field == null) {
                    throw new RuntimeException(c.getName() + " does not have a fied : " + prop);
                }
                field.setAccessible(true);
                field.set(ret, field.get(source));
            }

            return ret;
        } catch (Exception e) {
            throw new RuntimeException("ObjectUtil.partCopyNew exception", e);
        }
    }

    private static Field getField(Class c, String prop) throws NoSuchFieldException, SecurityException {
        Field field = null;

        try {
            field = c.getDeclaredField(prop);
        } catch (NoSuchFieldException e) {
            Class superC = c.getSuperclass();
            if (superC != null) {
                return getField(superC, prop);
            }
        }

        return field;
    }
}
