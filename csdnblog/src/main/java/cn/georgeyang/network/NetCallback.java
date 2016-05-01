package cn.georgeyang.network;

/**
 * 回调接口
 *
 * @author ping 2014-4-9 下午10:32:42
 */
public interface NetCallback<T> {
    void onSuccess(String flag, T object);
    void onFail(String flag, int code, Exception e);
}