package com.saferich.core.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.saferich.core.annotation.FieldMapping;

/**
 * 反射比较对象值工具类
 *
 * @author Steven
 * @date 2016.7.20
 */
public class CompareObjectUtil {
    public static <T> Boolean Compare(T tSource, T tCompareObj)
            throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<String> equalPrint = new ArrayList<>();
        List<String> differPrint = new ArrayList<>();
        List<String> sourceNullPrint = new ArrayList<>();
        // 分别读取两个对象的属性列表
        Field[] sourceFieldList = tSource.getClass().getDeclaredFields();
        Field[] compareFieldList = tCompareObj.getClass().getDeclaredFields();
        for (Field sourceField : sourceFieldList) {
            String sourceFieldName = sourceField.getName();
            if ("serialVersionUID".equals(sourceFieldName)) {
                continue;
            }
            if (containField(compareFieldList, sourceField)) {
                sourceField.setAccessible(true);
                // 是否有字段注解
                FieldMapping fieldMapping = sourceField.getAnnotation(FieldMapping.class);
                if (fieldMapping != null) {
                    // 获取注解对应的字段信息
                    sourceFieldName = fieldMapping.value();
                }
                PropertyDescriptor pDescriptor = new PropertyDescriptor(sourceFieldName, tSource.getClass());
                Method ReadMethod = pDescriptor.getReadMethod();
                Object sourceValue = ReadMethod.invoke(tSource);
                Object compareValue = ReadMethod.invoke(tCompareObj);
                if (sourceValue != null) {
                    if (sourceValue.getClass().getName().contains("java")) {
                        if (!sourceValue.equals(compareValue)) {
                            differPrint.add("\r\n!!!different in \"" + sourceFieldName + "\": " + sourceValue + " != \"" + compareValue + "\"!!!\r\n");
                        } else {
                            equalPrint.add("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + sourceFieldName + ": \""
                                    + sourceValue + "\" == \"" + compareValue + "\""
                                    + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        }
                    } else {
                        Compare(sourceValue, compareValue);
                    }
                } else {
                    sourceNullPrint.add("-------------------------------------------------------------------------------------" + sourceFieldName + " is null"
                            + "-------------------------------------------------------------------------------------");
                }
            }
        }
        for (String singleEqualPrint : equalPrint) {
            System.out.println(singleEqualPrint);
        }
        for (String singleSourceNullPrint : sourceNullPrint) {
            System.out.println(singleSourceNullPrint);
        }
        for (String singleDifferPrint : differPrint) {
            System.out.println(singleDifferPrint);
        }
        return true;
    }

    public static <T> void compareList(List<T> source, List<T> compareList)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
        for (int i = 0; i < source.size(); i++) {
            Compare(source.get(i), compareList.get(i));
        }
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
