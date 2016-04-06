package cn.georgeyang.network;

/**
 * Created by george.yang on 2016/1/7.
 */
public class Resp<T> {
    public T data;
    public int code;
    public boolean success;
    public Exception error;
}
