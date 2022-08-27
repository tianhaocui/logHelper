package com.logHelper.util;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @program: loghelper
 * @description:
 * @packagename: com.logHelper.util
 * @author: wulingren
 * @date: 2022/08/27 16:33
 **/
public class ObjUtils {
    private static final String OBJECT_PATH ="java.lang.object";
    /**
     * 获取包括所有的属性
     *
     * @param objSource
     * @return
     */
    public static Field[] getAllFields(Object objSource) {
        /*获得当前类的所有属性(private、protected、public)*/
        List<Field> fieldList = new ArrayList<Field>();
        Class tempClass = objSource.getClass();
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass != null && !OBJECT_PATH.equals(tempClass.getName().toLowerCase())) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 深度克隆对象
     *
     */
    @Deprecated
    public static <T>T copy(T objSource) {

        if (null == objSource) {
            return null;
        }
        T clone = ObjectUtils.clone(objSource);
//        Class<?> clazz = objSource.getClass();
//        Object objDes = clazz.newInstance();
//        Field[] fields = getAllFields(objSource);
//        // 循环
//        for (Field field : fields) {
//            field.setAccessible(true);
//            if (field.getModifiers() >= 24) {
//                continue;
//            }
//            try {
//                // 设置字段可见，即可用get方法获取属性值。
//                field.set(objDes, field.get(objSource));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return clone;
    }
}
