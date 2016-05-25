package online.magicbox.lib;

/**
 * 供fragment跳轉時使用
 * Created by george.yang on 2016-3-30.
 */
public class PluginConfig {
    //do not change line...
    public static final String NONE = "";//没有动画
    public static final String System = "Sytem";//动画跟随系统
    public static final String LeftInRightOut = "LeftInRightOut";//左进右出
    public static final String BottomInTopOut = "BottomInTopOut";//上进，往下出
    public static final String TopOut = "TopOut";//上进，往下出
    public static final String ZoomShow = "ZoomShow";//fragment默认缩放
    public static final String AlphaShow = "Alpha";//透明顯示

//    //重要，如果配置的版本号和上线版本的版本号没对应，就是去加载配置的版本号的就apk的class
//    public static final String pluginVersion = "6";//配置插件版本
}
