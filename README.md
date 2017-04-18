# MagicBox

一款热修复和插件技术集一身app，[下载地址](http://apk.hiapk.com/appinfo/online.magicbox.app)

演示效果

![weiget Effect](./gif/SCR_20160417_225627.gif)

- 目前里面有10款免安装的插件软件:

1.手电筒

2.wifi连接

3.wifi密码查看

4.火车票余票查询

5.计算器

6.俄罗斯方块

7.csdn阅读

8.拨号声识别

9.hash-e

10.二维码


特点
===
- 依赖devlibrary开发插件，写出的app既可以独立运行，也可以作为插件运行


热修复
===
- 代码动态修复
 [原理](https://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=400118620&idx=1&sn=b4fdd5055731290eef12ad0d17f39d4a&scene=1&srcid=1106Imu9ZgwybID13e7y2nEi#wechat_redirect)


# Android Support

| Android version        | Status           | 
| ------------- |:-------------:| 
| Android 6.0      | tested | 
| Android 5.0     | tested      |   
| Android < 5.0 | tested     |   


详情请看:online.magicbox.bugfix.BundlePathLoader

- 防止CLASS_ISPREVERIFIED，补丁制作

原理请看[这篇文字](https://github.com/dodola/HotFix)，这篇文章介绍的“实现javassist动态代码注入”使用Groovy开发的demo中代码注入，要一个个填写注入，本人写了一个transformClasses.jar,实现批量代码注入，用法如下：

```
java -jar transformClasses.jar classPath libPath ignore
classPath:项目编译class所在目录
libPath:项目运行的依赖class路径，包括AntilazyLoad.class所在目录(注入代码必须用到)
ignore:忽略注入代码的类,如: *.APP   packageName.*  packageName.className
以上参数均可用;表示多个


//在build.gradle插入以下代码
//gradle1.4以下使用这段
task('processWithJavassist') << {
    println '-----------開始往class插入代碼-----------------'

    String classPath = project(':app').buildDir.absolutePath + '/intermediates/classes/debug'//项目编译class所在目录
    String libPath = "$rootDir/transformClasses;$rootDir/transformClasses/androidClass" //AntilazyLoad.class及android.jar解压后的class所在目录
    String ignore = "*.App;*.BuildConfig;online.magicbox.bugfix.*;online.magicbox.app.R.*;cn.jpush.*"
    println classPath
    println libPath
    println ignore

    javaexec {
        classpath "$rootDir/transformClasses/transformClasses.jar"
        main = 'cn.georgeyang.TransformClasses'
        args classPath,libPath,ignore
    }
}

//gradle1.5及以上使用这段:
gradle.taskGraph.beforeTask { Task task ->
    println "beforeTask:" + task.name + "," + task.group + "," + task.getProject().name
        if (task.name.equals("preBuild") && task.getProject().name.equals("app")) {
            String rootPath = project.rootDir.absolutePath
            String jarPath = rootPath + "/transformClasses/transformClasses.jar"
            String classPath = project.buildDir.absolutePath + '/intermediates/classes/debug'//项目编译class所在目录
            String libPath = rootPath + "/transformClasses;" +  rootPath+ "/transformClasses/androidClass" //AntilazyLoad.class及android.jar解压后的class所在目录
            String ignore = "*.App;*.BuildConfig;online.magicbox.bugfix.*;online.magicbox.app.R.*;cn.jpush.*"
            project.javaexec {
                classpath jarPath
                main = 'cn.georgeyang.TransformClasses'
                args classPath,libPath,ignore
            }
    }
}

//gradle1.5及以上，自定义gradlePlugIn写法:
apply plugin: CodeInsert
class CodeInsert implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.afterEvaluate {
            project.android.applicationVariants.each { variant ->
                def dexTaskName = "transformClassesWithDexFor${variant.name.capitalize()}"
                def dexTask = project.tasks.findByName(dexTaskName)
                if (dexTask) {
                    String rootPath = project.rootDir.absolutePath
                    String jarPath = rootPath + "/transformClasses/transformClasses.jar"
                    String classPath = project.buildDir.absolutePath + '/intermediates/classes/debug'//项目编译class所在目录
                    String libPath = rootPath + "/transformClasses;" +  rootPath+ "/transformClasses/androidClass" //AntilazyLoad.class及android.jar解压后的class所在目录
                    String ignore = "*.App;*.BuildConfig;online.magicbox.bugfix.*;online.magicbox.app.R.*;cn.jpush.*"
                    project.javaexec {
                        classpath jarPath
                        main = 'cn.georgeyang.TransformClasses'
                        args classPath,libPath,ignore
                    }
                }
            }
        }
    }
}
```
> 进过transformClasses.jar处理后，如何知道哪些类可以热修复？

查看gradle console，会有如下结果

```
成功插入AntilazyLoad代码的会显示:
+ transform success:	***.class

失败会显示:
# transform fail:	 fail reasion - ***.class

R文件不能热修复，所以被忽略:
# ingroe resoure:   ***.class

指定忽略的会显示:
~ transform ignore by match:  ignoreType - ***.class
```
只有transform success的类才能热更新



插件
===
###### 加载插件代码原理: [重写classloder](http://www.trinea.cn/android/android-plugin/)
###### 加载插件View实现:[参考](https://github.com/jiangyinbin/PluginTheme)



插件开发依赖devlibrary项目

- 定义一个Slice(类似activity的类，用于pluginActivity加载)

```
public class MainSlice extends Slice {
...
}
```

加载这个Slice:

```
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PluginActivity.init("cn.georgeyang.flashlight","magicbox","2");//包名，定义的scheme，版本号
        Intent intent = PluginActivity.buildIntent(this,MainSlice.class);
        startActivity(intent);

        finish();
    }
}
```

- 插件中要使用fragment

```
public class FileFragment extends PluginFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getPluginLayoutInflater().inflate(R.layout.fragment_file,null);
    }
    //getPluginContext().getResources().getStringArray(R.array.item}
```

- 调用其他插件的图片选择器

```
Intent picker = PluginActivity.buildIntent(getActivity(),"online.magicbox.desktop","ImageSelectorSlice","1");
picker.putExtra("select_count_mode",0);
getActivity().startActivityForResult(picker,100);
```

- Slice中的权限申请(fragment也一样)：

```
requestPermission(123,Manifest.permission.CAMERA);

    @Override
    public void onPermissionGiven(int code,String permission) {
        super.onPermissionGiven(code,permission);
        if (code == 123) {
            Intent scan = PluginActivity.buildIntent(getActivity(), CaptureSlice2.class);
            getActivity().startActivityForResult(scan, 50);
        }
    }
```

> 本项目中插件的缺点：

- 目前没有写service,receiver的支持
- Slice继承的是Context，而不是activity，很多方法要谨慎使用
- 大量使用反射调用app宿主程序，效率减低

参考链接
---

CLASS_ISPREVERIFIED的问题:

https://github.com/dodola/HotFix

https://github.com/bunnyblue/DroidFix

Javassist 使用:
https://www.ibm.com/developerworks/cn/java/j-dyn0916/

gradle commend-line:

http://stackoverflow.com/questions/29289200/in-gradle-exec-task-commandline-searching-environment-but-not-working-directory

http://blog.csdn.net/innost/article/details/48228651


使用方法:
将app-build-outputsd的app-debug.apk提取classes.dex文件，将这个文件压缩，生成一个只有代码没有资源的zip包，这个包便是补丁包，

同一个项目下生成的补丁，兼容不同的签名，debug生成的补丁也可以给release版本打补丁。

[自定义gradle插件，让AndHotFix支持gradle1.5+](https://github.com/Livyli/AndHotFix)

[自定义gradle插件](http://unclechen.github.io/2015/11/17/%E8%87%AA%E5%AE%9A%E4%B9%89Android-Gradle%E6%8F%92%E4%BB%B6/)

gradle知識:

全面文檔
http://tools.android.com/tech-docs/new-build-system/user-guide

資源改名
http://hugozhu.myalert.info/2014/08/03/50-use-gradle-to-customize-apk-build.html


Copyright and Licensing
----

Copyright  [george.yang](http://blog.csdn.net/u010499721) © 2014-2015. All rights reserved.

This library is distributed under an MIT License.