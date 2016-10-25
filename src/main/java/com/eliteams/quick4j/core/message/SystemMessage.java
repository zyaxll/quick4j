package com.eliteams.quick4j.core.message;

import com.eliteams.quick4j.core.util.JSONUtil;

/**
 * Created by zya on 16/10/25.
 */
public class SystemMessage<T> {
    /*
	 * 状态，>=0表示成功，<0表示失败。默认为成功。
	 */
    private int status = 0;
    /*
     * 消息
     */
    private String message = "操作成功";
    /*
     * 用户数据
     */
    private T data;

    /**
     *
     * 方法用途: 把对象转换为json格式字符串<br>
     * 实现步骤: <br>
     *
     * @return String
     */
    public String toString() {
        return JSONUtil.toJSON(this);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
