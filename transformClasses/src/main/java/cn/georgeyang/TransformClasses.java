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
import javassist.CtField;
import javassist.NotFoundException;
import javassist.runtime.Cflow;

//javassist-3.0.jar，之所以用舊的版本，是因爲舊版的用java1.7編譯的，新版用1.8編譯的會報錯
//http://www.java2s.com/Code/Jar/j/Downloadjavassistjar.htm
//https://github.com/nuptboyzhb/AndroidPluginFramework/tree/master/%E7%AC%AC%E4%BA%94%E8%AF%BE-%E5%8A%A8%E6%80%81%E5%90%AF%E5%8A%A8%E6%8F%92%E4%BB%B6%E4%B8%AD%E7%9A%84Activity
public class TransformClasses {
	public static void main(String[] args) {
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(System.out);

			if (args == null || args.length < 3) {
				printer.println("# args must be:buildDir libDir ignoreList");
			} else {
				printer.println("@ start transform classes form:" + args[0]);
			}

			String buildContext = args[0];
			String libContext = args[1];
			String ignoreContext = args[2];

			buildContext = buildContext.replace("\\\\", "\\");
			libContext = libContext.replace("\\\\", "\\");

			String[] libPaths = libContext.split(";");
			String[] buildPaths = buildContext.split(";");
			String[] ignorePaths = ignoreContext.split(";");

			ClassPool classes = ClassPool.getDefault();

			for (String libPath:libPaths) {
				classes.appendClassPath(libPath);
			}
			for (String buildPath:buildPaths) {
				classes.appendClassPath(buildPath);
			}

			for (String buildPath:buildPaths) {
				loopToInsert(printer, classes,ignorePaths, buildPath,new File(buildPath));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				printer.close();
			} catch (Exception e2) {

			}
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

			//修改資源
			if (className.indexOf(".R$")>0 || className.endsWith(".R")) {
				c.stopPruning(true);

				writer.println("# transform resoure:\t" + file.getAbsolutePath());
				CtField[] fields = c.getFields();
				if (fields!=null) {
					for (CtField field:fields) {
						try {
							String name = field.getName();
							int value = (Integer)field.getConstantValue();
							int packageId = ((value >> 24) & 255);
							int resourceType = ((value >> 16) & 255);
							int resourcesSeq = value & 0xffff;

							packageId = 120;
							value = (packageId << 24) + (resourceType <<16)  + resourcesSeq ;
							c.removeField(field);
//							byte[] by = new byte[1];
//							by[0]=123;
//							field.setAttribute(name, by);
							String newFieldCode = String.format("public static final int %s = %s;",new Object[]{name,String.valueOf(value)});

							CtField.make(newFieldCode, c);
							writer.println("# newFieldCode:\t" +newFieldCode);

							c.writeFile();
							c.defrost();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				writer.flush();
				return;
			}

			//過濾類
			for (String iPath : ignorePath) {
				if (iPath.endsWith(".*")) {
					String match = iPath.substring(0, iPath.length()-2);
					if (className.startsWith(match)) {
						writer.println(String.format("~ transform ignore by match:\t %s - %s" ,new Object[]{iPath,file.getAbsolutePath()}));
						return;
					}
				}
				if (iPath.startsWith("*.")) {
					String match = iPath.substring(2, iPath.length());
					if (className.endsWith(match)) {
						writer.println(String.format("~ transform ignore by match:\t %s - %s" ,new Object[]{iPath,file.getAbsolutePath()}));
						return;
					}
				}
				if (iPath.equalsIgnoreCase(className)) {
					writer.println(String.format("~ transform ignore by Class Name:\t %s - %s" ,new Object[]{className,file.getAbsolutePath()}));
					return;
				}
				String packageName = c.getPackageName();
				if (iPath.equalsIgnoreCase(packageName)) {
					writer.println(String.format("~ transform ignore by Package Name:\t %s - %s" ,new Object[]{packageName,file.getAbsolutePath()}));
					return;
				}
			}

			// 下面的操作比较容易理解,在将需要关联的类的构造方法中插入引用代码
			CtConstructor[] constructors = c.getConstructors();
			if (constructors == null || constructors.length == 0) {
				writer.println(String.format("# transform fail:\t %s - %s",new Object[]{"no constructors",file.getAbsolutePath()}));
				return;
			}

			try {
				CtConstructor constructor = constructors[0];
				constructor.insertBefore("System.out.println(AntilazyLoad.class);");
				c.writeFile();
				writer.println("+ transform success:\t" + file.getAbsolutePath());
			} catch (Exception e2) {
				writer.println(String.format("# transform fail:\t %s - %s",new Object[]{e2.getLocalizedMessage(),file.getAbsolutePath()}));
			}
		}
		writer.flush();
	}

	private static String buildPackageNameByFilePath(String buildDir,String filePath) {
		try {
			if (filePath == null || filePath.equals("")) {
				return "";
			}
			String prePackageName = filePath.substring(buildDir.length() + 1,filePath.length() - 6);
			String packageName = prePackageName.replace('/', '.').replace('\\', '.');
			return packageName;
		} catch (Exception e) {

		}
		return "";
	}
}
