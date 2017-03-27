package com.service.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebRequestUtil {
    private static HttpURLConnection c_HttpURLConnection;

    /**
     * 发送webGet请求
     * 
     * @param m_Url 请求Url
     * @param p_Params 请求参数map，（?key=value形式）
     * @return 响应字符串
     * @throws IOException
     */
    public static String get(String m_Url, Map<String, String> p_Params) throws IOException {
        m_Url = setParams(m_Url, p_Params);
        URL m_UrlUtil = new URL(m_Url);
        c_HttpURLConnection = (HttpURLConnection) m_UrlUtil.openConnection();
        setConnectionProperty();
        c_HttpURLConnection.setRequestMethod("GET");
        c_HttpURLConnection.setRequestProperty("Content-Type", "application/json");
        InputStream m_InputStream = c_HttpURLConnection.getInputStream();
        return readResponse(m_InputStream);
    }

    /**
     * 发送webGet请求
     * 
     * @param m_Url 请求Url
     * @param p_Params 请求参数map，（?key=value形式）
     * @return 响应字符串
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static <T> String get(String m_Url, T p_Params)
            throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        m_Url = setParams(m_Url, p_Params);
        URL m_UrlUtil = new URL(m_Url);
        c_HttpURLConnection = (HttpURLConnection) m_UrlUtil.openConnection();
        setConnectionProperty();
        c_HttpURLConnection.setRequestMethod("GET");
        c_HttpURLConnection.setRequestProperty("Content-Type", "application/json");
        InputStream m_InputStream = c_HttpURLConnection.getInputStream();
        return readResponse(m_InputStream);
    }

    /**
     * 发送 webPost请求
     * 
     * @param m_Url 请求Url
     * @param p_Params 请求参数map，（?key=value形式）
     * @param p_Request
     * @return 响应字符串
     * @throws IOException
     */
    public static String post(String m_Url, Map<String, String> p_Params, String p_Request) throws IOException {
        m_Url = setParams(m_Url, p_Params);
        URL m_UrlUtil = new URL(m_Url);
        c_HttpURLConnection = (HttpURLConnection) m_UrlUtil.openConnection();
        setConnectionProperty();
        c_HttpURLConnection.setRequestMethod("POST");
        c_HttpURLConnection.setRequestProperty("Content-Type", "application/json");
        OutputStream m_RequestStream = c_HttpURLConnection.getOutputStream();
        m_RequestStream.write(p_Request.getBytes());
        m_RequestStream.flush();
        m_RequestStream.close();
        InputStream m_ResponseStream = c_HttpURLConnection.getInputStream();
        return readResponse(m_ResponseStream);
    }

    /**
     * 转换Json到实体类
     * 
     * @param p_Json Json字串
     * @param p_Clazz 实体类类型
     * @return 实体类
     */
    public static <T> T convertJson2Obj(String p_Json, Class<T> p_Clazz) {
        ObjectMapper m_Mapper = new ObjectMapper();
        try {
            return m_Mapper.readValue(p_Json, p_Clazz);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置HTTPHeadFields
     */
    private static void setConnectionProperty() {
        c_HttpURLConnection.setDoOutput(true);
        c_HttpURLConnection.setDoInput(true);
        c_HttpURLConnection.setDefaultUseCaches(false);
    }

    /**
     * 读取响应流转成String
     * 
     * @param p_InputStream 响应流
     * @return 响应String
     * @throws IOException
     */
    private static String readResponse(InputStream p_InputStream) throws IOException {
        BufferedReader m_BufferedReader = new BufferedReader(new InputStreamReader(p_InputStream, "UTF-8"));
        String m_ReadLine = null;
        StringBuilder m_StringBuilder = new StringBuilder();
        while ((m_ReadLine = m_BufferedReader.readLine()) != null) {
            m_StringBuilder.append(m_ReadLine);
        }
        return m_StringBuilder.toString();
    }

    /**
     * 设置URL请求参数
     * 
     * @param m_Url URL
     * @param p_Params 参数Map
     * @return
     */
    private static String setParams(String m_Url, Map<String, String> p_Params) {
        if (p_Params != null && !p_Params.isEmpty()) {
            Iterator<String> m_ParamkeyKeyIterator = p_Params.keySet().iterator();
            String m_FirstParamKey = m_ParamkeyKeyIterator.next();
            m_Url += "?" + m_FirstParamKey + "=" + p_Params.get(m_FirstParamKey);
            while (m_ParamkeyKeyIterator.hasNext()) {
                String m_IteratorKey = m_ParamkeyKeyIterator.next();
                m_Url += "&" + m_IteratorKey + "=" + p_Params.get(m_IteratorKey);
            }
        }
        return m_Url;
    }

    /**
     * 设置URL请求参数
     * 
     * @param m_Url URL
     * @param p_Params 参数Map
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static <T> String setParams(String m_Url, T p_Params)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map<String, Object> map = getNameAndValue(p_Params, new HashMap<>(), p_Params.getClass());
        if (map != null && !map.isEmpty()) {
            Iterator<String> m_ParamkeyKeyIterator = map.keySet().iterator();
            String m_FirstParamKey = m_ParamkeyKeyIterator.next();
            m_Url += "?" + m_FirstParamKey + "=" + map.get(m_FirstParamKey);
            while (m_ParamkeyKeyIterator.hasNext()) {
                String m_IteratorKey = m_ParamkeyKeyIterator.next();
                m_Url += "&" + m_IteratorKey + "=" + map.get(m_IteratorKey);
            }
        }
        return m_Url;
    }

    private static <T> Map<String, Object> getNameAndValue(T obj, Map<String, Object> resultMap, Class<?> clazz)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (clazz.getSuperclass() != null) {
            getNameAndValue(obj, resultMap, clazz.getSuperclass());
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isTransient(field.getModifiers())) {// 使用cobertura生成测试报告用到,跳过cobertura添加的跟踪属性
                Method method = null;
                Object value = null;
                String name = field.getName();
                StringBuilder getFieldMethodName = new StringBuilder("get").append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
                method = clazz.getMethod(getFieldMethodName.toString());
                value = method.invoke(obj);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                resultMap.put(name, value);
            }
        }
        return resultMap;
    }
}
