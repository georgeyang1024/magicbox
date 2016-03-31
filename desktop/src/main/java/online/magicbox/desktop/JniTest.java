package online.magicbox.desktop;

/**
 * Created by george.yang on 16/3/31.
 */
public class JniTest {
    static {
        System.loadLibrary("JniTest");
    }

    public static native String hello();
}
