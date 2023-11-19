package com.teamProj.utils;

import java.io.Serializable;

public class HttpResult<T> implements Serializable {
    private Boolean success;
    private Integer code;
    private T info;
    private String message;

    private HttpResult() {
        this.code = 200;
        this.success = true;
    }

    private HttpResult(T obj, String message) {
        this.code = 200;
        this.info = obj;
        this.message = message;
        this.success = true;
    }

    private HttpResult(ResultCodeEnum resultCode) {
        this.success = false;
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    private HttpResult(ResultCodeEnum resultCode, String message) {
        this.success = false;
        this.code = resultCode.getCode();
        this.message = message;
    }

    public static <T> HttpResult<T> success() {
        return new HttpResult<T>();
    }

    public static <T> HttpResult<T> success(T data, String message) {
        return new HttpResult<T>(data, message);
    }

    public static <T> HttpResult<T> failure(ResultCodeEnum resultCode) {
        return new HttpResult<T>(resultCode);
    }

    public static <T> HttpResult<T> failure(ResultCodeEnum resultCode, String message) {
        return new HttpResult<T>(resultCode);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "success=" + success +
                ", code=" + code +
                ", data=" + info +
                ", message='" + message + '\'' +
                '}';
    }
}
