package com.loghelper.util;

import com.loghelper.annotation.Hidden;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 脱敏工具类
 * author: cuitianhao
 **/
public class HiddenBeanUtil {

    /**
     * 前三后四
     */
    private static final String PHONE_REGEX = "(?<=\\w{3})\\w(?=\\w{4})";
    /**
     * 前er后二
     */
    private static final String ID_CORD_REGEX = "(?<=\\w{2})\\w(?=\\w{2})";

    /**
     * 账号脱敏
     */
    private static final String ACCOUNT_REGEX = "(?<=\\w)\\w(?=\\w)";
    /**
     * 邮箱脱敏
     */
    private static final String EMAIL_REGEX = "(^\\w)[^@]*(@.*$)";

    private static final String JAVAX = "javax.";
    private static final String JAVA = "java.";
    private static final String STAR = "*";


    /**
     * 获取脱敏后的bean
     *
     * @param javaBean 原本bean
     * @param <T>      T
     * @throws IllegalAccessException exception
     * @return 脱敏后数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T getClone(T javaBean) throws IllegalAccessException {
        T clone = null;
        if (null != javaBean) {
            if (javaBean.getClass().isInterface()) {
                return null;
            }
            //系统类有好多没有无参构造方法
            if (StringUtils.startsWith(javaBean.getClass().getPackage().getName(), JAVAX)
                    || StringUtils.startsWith(javaBean.getClass().getPackage().getName(), JAVA)
                    || StringUtils.startsWith(javaBean.getClass().getName(), JAVAX)
                    || StringUtils.startsWith(javaBean.getClass().getName(), JAVA)) {
                clone = javaBean;
            } else {
                clone = (T) BeanUtils.instantiateClass(javaBean.getClass());
                BeanUtils.copyProperties(javaBean, clone);
            }
            // 定义一个计数器，用于避免重复循环自定义对象类型的字段
            Set<Integer> referenceCounter = new HashSet<>();
            //脱敏
            replace(ObjUtils.getAllFields(clone), clone, referenceCounter);
        }
        return clone;
    }

    /**
     * 对需要脱敏的字段进行转化
     *
     * @param fields           字段
     * @param javaBean         对象
     * @param referenceCounter 计数器
     * @throws IllegalArgumentException 异常
     * @throws IllegalAccessException   异常
     */
    private static void replace(Field[] fields, Object javaBean, Set<Integer> referenceCounter) throws IllegalArgumentException, IllegalAccessException {
        if (null != fields) {
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
                                if (arrayObject != null) {
                                    if (isNotGeneralType(arrayObject.getClass(), arrayObject, referenceCounter)) {
                                        replace(ObjUtils.getAllFields(arrayObject), arrayObject, referenceCounter);
                                    }
                                }
                            }
                        } else if (value instanceof Collection<?>) {
                            Collection<?> c = (Collection<?>) value;
                            for (Object collectionObj : c) {
                                if (collectionObj != null) {
                                    if (isNotGeneralType(collectionObj.getClass(), collectionObj, referenceCounter)) {
                                        replace(ObjUtils.getAllFields(collectionObj), collectionObj, referenceCounter);
                                    }
                                }
                            }
                        } else if (value instanceof Map<?, ?>) {
                            Map<?, ?> m = (Map<?, ?>) value;
                            Set<?> set = m.entrySet();
                            for (Object o : set) {
                                if (o != null) {
                                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                                    Object mapVal = entry.getValue();
                                    if (isNotGeneralType(mapVal.getClass(), mapVal, referenceCounter)) {
                                        replace(ObjUtils.getAllFields(mapVal), mapVal, referenceCounter);
                                    }
                                }
                            }
                        } else if (value instanceof Enum<?>) {
                            continue;
                        }
                        //递归
                        else {
                            if (!type.isPrimitive()
                                    && type.getPackage() != null
                                    && !StringUtils.startsWith(type.getPackage().getName(), JAVAX)
                                    && !StringUtils.startsWith(type.getPackage().getName(), JAVA)
                                    && !StringUtils.startsWith(field.getType().getName(), JAVAX)
                                    && !StringUtils.startsWith(field.getName(), JAVA)
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
     * 排除jdk字段
     *
     * @param clazz            类型
     * @param value            值
     * @param referenceCounter 计数器
     * @return 是否需要脱敏
     */
    private static boolean isNotGeneralType(Class<?> clazz, Object value, Set<Integer> referenceCounter) {
        return !clazz.isPrimitive()
                && clazz.getPackage() != null
                && !clazz.isEnum()
                && !StringUtils.startsWith(clazz.getPackage().getName(), JAVAX)
                && !StringUtils.startsWith(clazz.getPackage().getName(), JAVA)
                && !StringUtils.startsWith(clazz.getName(), JAVAX)
                && !StringUtils.startsWith(clazz.getName(), JAVA)
                && referenceCounter.add(value.hashCode());
    }

    /**
     * 脱敏操作（按照规则转化需要脱敏的字段并设置新值）
     *
     * @param javaBean 对象
     * @param field    字段
     * @param value    值
     * @throws IllegalAccessException 异常
     */
    private static void setNewValueForField(Object javaBean, Field field, Object value) throws IllegalAccessException {
        //处理自身的属性
        Hidden annotation = field.getAnnotation(Hidden.class);
        if (field.getType().equals(String.class) && null != annotation) {
            String valueStr = String.valueOf(value);
            if (StringUtils.isNotBlank(valueStr)) {
                switch (annotation.dataType()) {
                    case ID_CARD: {
                        field.set(javaBean, idCard(valueStr));
                        break;
                    }
                    case EMAIL: {
                        field.set(javaBean, email(valueStr));
                        break;
                    }
                    case PHONE:
                        field.set(javaBean, phone(valueStr));
                        break;
                    case ACCOUNT:
                        field.set(javaBean, account(valueStr));
                        break;
                    case REG:
                    default:
                        field.set(javaBean, reg(valueStr, annotation.regexp()));
                }
            }
        }
    }


    /**
     * 身份证号
     *
     * @param id 身份证号
     * @return 脱敏后的身份证号
     */
    private static String idCard(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        return id.replaceAll(ID_CORD_REGEX, STAR);
    }

    /**
     * 手机号
     *
     * @param num 手机号
     * @return 脱敏后的手机号
     */
    private static String phone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return num.replaceAll(PHONE_REGEX, STAR);
    }

    /**
     * 账号脱敏
     *
     * @param account 账号
     * @return 脱敏后的账号
     */
    private static String account(String account) {
        if (StringUtils.isBlank(account)) {
            return "";
        }
        return account.replaceAll(ACCOUNT_REGEX, STAR);
    }

    /**
     * 自定义正则替换
     *
     * @param data 数据
     * @return 脱敏后的数据
     */
    private static String reg(String data, String reg) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        return data.replaceAll(reg, STAR);
    }

    /**
     * 电子邮箱
     *
     * @param email 电子邮箱
     * @return 脱敏后的邮箱
     */
    private static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return email.replaceAll(EMAIL_REGEX, "$1****$2");
        }
    }

    /**
     * 脱敏
     *
     * @param data 数据
     * @param type 脱敏类型
     * @param reg  reg
     * @return 脱敏后的数据
     */
    public static String replace(String data, Hidden.DataType type, String reg) {
        switch (type) {
            case ID_CARD: {
                return idCard(data);
            }
            case EMAIL: {
                return email(data);
            }
            case PHONE:
                return phone(data);
            case ACCOUNT:
                return account(data);
            case REG:
            default:
                return reg(data, reg);
        }

    }
}


