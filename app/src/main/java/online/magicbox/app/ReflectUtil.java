package online.magicbox.app;



/**
 * 反射工具
 * Created by george.yang on 2015/9/18.
 */
public class ReflectUtil {
    public static final Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return null;
        }
    }

}
