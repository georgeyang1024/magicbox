package online.magicbox.desktop;

/**
 * Created by george.yang on 16/3/31.
 */
public class Jni {
    // java.lang.UnsatisfiedLinkError: Shared library "/data/data/online.magicbox.app/files/lib/libJniFunc.so" already opened by ClassLoader 0x22c02620; can't open in ClassLoader 0x22edaba0
    static {
        System.loadLibrary("JniFunc");
    }

    public static native String hello();

    public static native String init();
}
