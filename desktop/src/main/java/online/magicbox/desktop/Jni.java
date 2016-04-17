package online.magicbox.desktop;

/**
 * Created by george.yang on 16/3/31.
 */
public class Jni {
    static {
        System.loadLibrary("JniFunc");
    }

    public static native String hello();

    public static native String init();
}
