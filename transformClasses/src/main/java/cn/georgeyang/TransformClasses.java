package cn.georgeyang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

//javassist-3.0.jar，之所以用舊的版本，是因爲舊版的用java1.7編譯的，新版用1.8編譯的會報錯
//http://www.java2s.com/Code/Jar/j/Downloadjavassistjar.htm
public class TransformClasses {
	public static void main(String[] args) {
		try {
			if (args == null || args.length < 3) {
				System.out.println("# args must be:buildDir libDir ignoreList");
			} else {
				System.out.println("@ start transform classes form:" + args[0]);
			}

			String buildDir = args[0];
			String libDir = args[1];
			String ignoreList = args[2];
			String[] ignorePath = ignoreList.split(";");

			buildDir = buildDir.replace("\\\\", "\\");

			ClassPool classes = ClassPool.getDefault();

			classes.appendClassPath(buildDir);
			classes.appendClassPath(libDir);

			loopToInsert(new PrintWriter(System.out), classes,ignorePath, buildDir,new File(buildDir));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loopToInsert(PrintWriter writer, ClassPool classPool,String[] ignorePath,String buildDir, File file) throws Exception {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null)
				for (File subFile : files) {
					loopToInsert(writer, classPool,ignorePath, buildDir, subFile);
				}
		} else {
			String className = buildPackageNameByFilePath(buildDir,file.getAbsolutePath());
			if (className == null || className.equals("")) {
				writer.println("# transform pass:\t" + file.getAbsolutePath());
				return;
			}


			CtClass c = classPool.get(className);

			//過濾類
			for (String iPath : ignorePath) {
				if (iPath.endsWith(".*")) {
					String match = iPath.substring(0, iPath.length()-2);
					if (className.startsWith(match)) {
						writer.println("~ transform ignore by match:\t" + file.getAbsolutePath());
						return;
					}
				}
				if (iPath.startsWith("*.")) {
					String match = iPath.substring(2, iPath.length());
					if (className.endsWith(match)) {
						writer.println("~ transform ignore by match:\t" + file.getAbsolutePath());
						return;
					}
				}
				if (iPath.equalsIgnoreCase(className)) {
					writer.println("~ transform ignore by Class Name:\t" + file.getAbsolutePath());
					return;
				}
				String packageName = c.getPackageName();
				if (iPath.equalsIgnoreCase(packageName)) {
					writer.println("~ transform ignore by Package Name:\t" + file.getAbsolutePath());
					return;
				}
			}

			// 下面的操作比较容易理解,在将需要关联的类的构造方法中插入引用代码
			CtConstructor[] constructors = c.getConstructors();
			if (constructors == null || constructors.length == 0) {
				writer.println(String.format("# transform fail:\t%s",file.getAbsolutePath()));
				return;
			}
			CtConstructor constructor = constructors[0];
			constructor.insertBefore("System.out.println(AntilazyLoad.class);");
			c.writeFile(buildDir);

			writer.println("+ transform success:\t" + file.getAbsolutePath());
		}
		writer.flush();
	}

	private static String buildPackageNameByFilePath(String buildDir,String filePath) {
		if (filePath == null || filePath.equals("")) {
			return "";
		}
		String prePackageName = filePath.substring(buildDir.length() + 1,filePath.length() - 6);
		String packageName = prePackageName.replace('/', '.').replace('\\', '.');
		//跳過資源
		if (packageName.indexOf(".R$")>0) {
			return "";
		} else if (packageName.endsWith(".R")) {
			return "";
		}
		return packageName;
	}
}
