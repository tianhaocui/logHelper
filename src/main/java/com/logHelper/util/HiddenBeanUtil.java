package com.logHelper.util;

import com.logHelper.annotation.Hidden;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @program: loghelper
 * @description:
 * @packagename: com.logHelper.util
 * @author: wulingren
 * @date: 2022/08/27 16:33
 **/
public class HiddenBeanUtil {


    /**
     * 获取脱敏json串(递归引用会导致java.lang.StackOverflowError)
     *
     * @param javaBean
     * @return
     */
    public static <T>T getClone(T javaBean) {
        T clone  = null;
        if (null != javaBean) {
            try {
                if (javaBean.getClass().isInterface()) {
                    return null;
                }


                if (  StringUtils.startsWith(javaBean.getClass().getPackage().getName(), "javax.")
                        || !StringUtils.startsWith(javaBean.getClass().getPackage().getName(), "java.")
                        || !StringUtils.startsWith(javaBean.getClass().getName(), "javax.")
                        || !StringUtils.startsWith(javaBean.getClass().getName(), "java.")){
                    clone =javaBean;
                }else {
                     clone = (T)BeanUtils.instantiateClass(javaBean.getClass());
                    BeanUtils.copyProperties(javaBean,clone );

                }

                /* 定义一个计数器，用于避免重复循环自定义对象类型的字段 */
                Set<Integer> referenceCounter = new HashSet<Integer>();

                /* 对克隆实体进行脱敏操作 */
                replace(ObjUtils.getAllFields(clone), clone, referenceCounter);


            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return clone;
    }

    /**
     * 对需要脱敏的字段进行转化
     *
     * @param fields
     * @param javaBean
     * @param referenceCounter
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static void replace(Field[] fields, Object javaBean, Set<Integer> referenceCounter) throws IllegalArgumentException, IllegalAccessException {
        if (null != fields && fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                if (null != javaBean) {
                    Object value = field.get(javaBean);
                    if (null != value) {
                        Class<?> type = value.getClass();
                        //处理子属性，包括集合中的
                        if (type.isArray()) {
                            int len = Array.getLength(value);
                            for (int i = 0; i < len; i++) {
                                Object arrayObject = Array.get(value, i);
                                if (isNotGeneralType(arrayObject.getClass(), arrayObject, referenceCounter)) {
                                    replace(ObjUtils.getAllFields(arrayObject), arrayObject, referenceCounter);
                                }
                            }
                        } else if (value instanceof Collection<?>) {
                            Collection<?> c = (Collection<?>) value;
                            Iterator<?> it = c.iterator();
                            while (it.hasNext()) {// TODO: 待优化
                                Object collectionObj = it.next();
                                if (isNotGeneralType(collectionObj.getClass(), collectionObj, referenceCounter)) {
                                    replace(ObjUtils.getAllFields(collectionObj), collectionObj, referenceCounter);
                                }
                            }
                        } else if (value instanceof Map<?, ?>) {
                            Map<?, ?> m = (Map<?, ?>) value;
                            Set<?> set = m.entrySet();
                            for (Object o : set) {
                                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                                Object mapVal = entry.getValue();
                                if (isNotGeneralType(mapVal.getClass(), mapVal, referenceCounter)) {
                                    replace(ObjUtils.getAllFields(mapVal), mapVal, referenceCounter);
                                }
                            }
                        } else if (value instanceof Enum<?>) {
                            continue;
                        }

                        /*除基础类型、jdk类型的字段之外，对其他类型的字段进行递归过滤*/
                        else {
                            if (!type.isPrimitive()
                                    && type.getPackage() != null
                                    && !StringUtils.startsWith(type.getPackage().getName(), "javax.")
                                    && !StringUtils.startsWith(type.getPackage().getName(), "java.")
                                    && !StringUtils.startsWith(field.getType().getName(), "javax.")
                                    && !StringUtils.startsWith(field.getName(), "java.")
                                    && referenceCounter.add(value.hashCode())) {
                                replace(ObjUtils.getAllFields(value), value, referenceCounter);
                            }
                        }
                    }

                    //脱敏操作
                    setNewValueForField(javaBean, field, value);

                }
            }
        }
    }

    /**
     * 排除基础类型、jdk类型、枚举类型的字段
     *
     * @param clazz
     * @param value
     * @param referenceCounter
     * @return
     */
    private static boolean isNotGeneralType(Class<?> clazz, Object value, Set<Integer> referenceCounter) {
        return !clazz.isPrimitive()
                && clazz.getPackage() != null
                && !clazz.isEnum()
                && !StringUtils.startsWith(clazz.getPackage().getName(), "javax.")
                && !StringUtils.startsWith(clazz.getPackage().getName(), "java.")
                && !StringUtils.startsWith(clazz.getName(), "javax.")
                && !StringUtils.startsWith(clazz.getName(), "java.")
                && referenceCounter.add(value.hashCode());
    }

    /**
     * 脱敏操作（按照规则转化需要脱敏的字段并设置新值）
     * 目前只支持String类型的字段，如需要其他类型如BigDecimal、Date等类型，可以添加
     *
     * @param javaBean
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    public static void setNewValueForField(Object javaBean, Field field, Object value) throws IllegalAccessException {
        //处理自身的属性
        Hidden annotation = field.getAnnotation(Hidden.class);
        if (field.getType().equals(String.class) && null != annotation) {
            String valueStr = String.valueOf(value);
            if (StringUtils.isNotBlank(valueStr)) {
                switch (annotation.dataType()) {
                    case ID_CARD: {
                        field.set(javaBean, idCardNum(valueStr));
                        break;
                    }
                    case EMAIL: {
                        field.set(javaBean, email(valueStr));
                        break;
                    }
                    case PHONE:
                    default:
                        field.set(javaBean, phone(valueStr));
                }
            }
        }
    }


    /**
     * 【身份证号】显示最后四位，其他隐藏。共计18位或者15位，比如：*************1234
     *
     * @param id
     * @return
     */
    public static String idCardNum(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        String num = StringUtils.right(id, 4);
        return StringUtils.leftPad(num, StringUtils.length(id), "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num
     * @return
     */
    public static String phone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address
     * @param sensitiveSize 敏感信息长度
     * @return
     */
    public static String address(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * 【电子邮箱 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com>
     *
     * @param email
     * @return
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }


}


