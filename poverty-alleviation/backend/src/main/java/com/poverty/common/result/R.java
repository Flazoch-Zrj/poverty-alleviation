package com.poverty.common.result;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 统一返回结果封装
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;
    private long timestamp;

    private R() {
        this.timestamp = Instant.now().getEpochSecond();
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return ok("操作成功", data);
    }

    public static <T> R<T> ok(String message, T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> R<T> fail(String message) {
        return fail(400, message);
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> R<T> error(String message) {
        return error(500, message);
    }

    public static <T> R<T> error(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }
}
