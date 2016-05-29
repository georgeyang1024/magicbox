#MagicBox

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

热修复
===
- 代码动态修复
 [原理](https://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=400118620&idx=1&sn=b4fdd5055731290eef12ad0d17f39d4a&scene=1&srcid=1106Imu9ZgwybID13e7y2nEi#wechat_redirect)


Android | version | Status
---|---
Android | 6.0 |	tested
Android | 5.0 |	tested
Android |< 5.0 | tested
YunOs   | unknow | no test

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
task('processWithJavassist') << {
    println '-----------開始往class插入代碼-----------------'

    String classPath = project(':app').buildDir.absolutePath + '/intermediates/classes/debug'//项目编译class所在目录
    String libPath = "$rootDir/transformClasses;$rootDir/transformClasses/buildLib" //AntilazyLoad.class所在目錄
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
```

插件
===
###### 加载插件代码原理: 重写classloder
###### 加载插件View,[参考](https://github.com/jiangyinbin/PluginTheme)



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



gradle知識:

全面文檔
http://tools.android.com/tech-docs/new-build-system/user-guide

資源改名
http://hugozhu.myalert.info/2014/08/03/50-use-gradle-to-customize-apk-build.html


Copyright and Licensing
----

Copyright  [george.yang](http://blog.csdn.net/u010499721) © 2014-2015. All rights reserved.

This library is distributed under an MIT License.