package com.saferich.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saferich.core.annotation.FieldMapping;

public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 实体类的转换
     * 
     * @param t 源目标对象
     * @param d 需要转换成的目的class
     * @return
     */
    public static <T, D> D getBean(T t, Class<D> d) {
        try {
            if (t != null) {
                // 获取T里声明的字段信息
                Field[] tFields = t.getClass().getDeclaredFields();
                // 按照空的够着函数生成对象
                D dObject = d.newInstance();
                // 获取D里声明的字段信息
                Field[] fields = d.getDeclaredFields();
                // 循环赋值
                for (Field field : fields) {
                    // 判断是否包含需要转换的字段信息
                    if (containField(tFields, field)) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        // 是否有字段注解
                        FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
                        if (fieldMapping != null) {
                            // 获取注解对应的字段信息
                            fieldName = fieldMapping.value();
                        }
                        // bean的反射取值、赋值
                        PropertyDescriptor pd = new PropertyDescriptor(fieldName, t.getClass());
                        Method method = pd.getReadMethod();
                        Object retVal = method.invoke(t);
                        pd = new PropertyDescriptor(field.getName(), d);
                        method = pd.getWriteMethod();
                        method.invoke(dObject, retVal);
                    } else {
                        logger.warn("no field {} found in class {} ", field.getName(), t.getClass().getName());
                    }
                }
                return dObject;
            }
        } catch (Exception e) {
            logger.error("BeanUtil parser error：{}", e.getMessage());
        }
        return null;
    }

    private static boolean containField(Field[] fields, Field field) {
        if (fields.length > 0) {
            FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
            String fieldName = field.getName();
            if (fieldMapping != null) {
                fieldName = fieldMapping.value();
            }
            for (Field newField : fields) {
                if (fieldName.equals(newField.getName()) && field.getType().equals(newField.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
