package study.hitchhiking.utils.response;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author HuaZai
 * @contact who.seek.me@java98k.vip
 *          <ul>
 * @description
 *              <li>消息响应主体类
 *              </ul>
 * @className ResponseData
 * @package com.huazai.b2c.aiyou.controller
 * @createdTime 2018年4月17日 下午4:37:59
 *
 * @version V1.0.0
 */
public class ResponseData extends HashMap<String, Object> {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 17163487151351L;

    /**
     * 默认构造函数
     */
    public ResponseData() {
        put("code", 0);
        put("msg", "success");
    }

    public static ResponseData success() {

        return new ResponseData();
    }

    public static ResponseData success(final Map<String, Object> map) {
        ResponseData responseData = new ResponseData();

        responseData.putAll(map);
        return responseData;
    }

    public static ResponseData success(final String msg) {
        ResponseData responseData = new ResponseData();

        responseData.put("msg", msg);
        return responseData;
    }

    /**
     * 设置响应数据体，可以同时设置多个
     */
    @Override
    public ResponseData put(final String key, final Object value) {
        super.put(key, value);
        // 当前实例
        return this;
    }

    public static ResponseData failed() {
        return failed(1, "未知的系统异常，请联系运维人员");
    }

    public static ResponseData failed(final String msg) {

        return failed(2, msg);
    }

    public static ResponseData failed(final int code, final String msg) {
        ResponseData responseData = new ResponseData();

        responseData.put("code", code);
        responseData.put("msg", msg);
        return responseData;
    }
}