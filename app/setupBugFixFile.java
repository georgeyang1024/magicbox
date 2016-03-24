import java.lang.reflect.Constructor;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

public class Test {
    public static void main(String[] args) {
        String buildDir = "";
        String lib = "";

        ClassPool classes = ClassPool.getDefault();
        try {
            classes.appendClassPath(buildDir);
            classes.appendClassPath(lib);

            // 下面的操作比较容易理解,在将需要关联的类的构造方法中插入引用代码
            CtClass c = classes.getCtClass("dodola.hotfix.BugClass");
            CtConstructor constructor = c.getConstructors()[0];
            constructor.insertBefore("System.out.println(dodola.hackdex.AntilazyLoad.class);");
            c.writeFile(buildDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
