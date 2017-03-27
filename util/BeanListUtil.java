package com.saferich.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanListUtil {
    private static final Log log = LogFactory.getLog(BeanListUtil.class);

    public static <T, D> List<D> getList(List<T> tList, Class<D> d) {
        try {
            if (tList != null && tList.size() > 0) {
                List<D> dList = new ArrayList<D>();
                for (T t : tList) {
                    D dObject = BeanUtil.getBean(t, d);
                    dList.add(dObject);
                }
                return dList;
            }
        } catch (Exception e) {
            log.error("BeanListUtil parser error!" + e.getMessage());
        }
        return null;
    }
}
