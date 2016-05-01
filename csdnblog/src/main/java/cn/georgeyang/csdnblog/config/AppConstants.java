package cn.georgeyang.csdnblog.config;

/**
 * 全局常量
 *
 * @author tangqi
 * @data 2015年7月20日下午10:42:07
 */
public class AppConstants {

	/**
	 * CSDN博客基础地址
	 */
	public final static String CSDN_BASE_URL = "http://blog.csdn.net/";

	/**
	 * 预加载数据--消息类型
	 */
	public static final int MSG_PRELOAD_DATA = 1000;

	/**
	 * 博客分类--全部
	 */
	public static final String BLOG_CATEGORY_ALL = "全部";

	/**
	 * 博客类型
	 */
	public class BLOG_ICO_TYPE {
		public static final String BLOG_TYPE_REPOST = "ico ico_type_Repost";
		public static final String BLOG_TYPE_ORIGINAL = "ico ico_type_Original";
		public static final String BLOG_TYPE_TRANSLATED = "ico ico_type_Translated";
	}

	/**
	 * 博客每一项的类型
	 */
	public class DEF_BLOG_ITEM_TYPE {
		public static final int TITLE = 1; // 标题
		public static final int SUMMARY = 2; // 摘要
		public static final int CONTENT = 3; // 内容
		public static final int IMG = 4; // 图片
		public static final int BOLD_TITLE = 5; // 加粗标题
		public static final int CODE = 6; // 代码
	}

	/**
	 * 新闻类型
	 */
	public class DEF_NEWS_TYPE {
		public static final int YEJIE = 1;
		public static final int YIDONG = 2;
		public static final int YANFA = 3;
		public static final int ZAZHI = 4;
		public static final int YUNJISUAN = 5;
	}

	/**
	 * 评论类型
	 */
	public class DEF_COMMENT_TYPE {
		public static final int PARENT = 1;
		public static final int CHILD = 2;
	}

	/**
	 * 操作结果类型
	 */
	public class DEF_RESULT_CODE {
		public static final int ERROR = 1; // 错误
		public static final int NO_DATA = 2;// 无数据
		public static final int REFRESH = 3;// 刷新
		public static final int LOAD = 4; // 加载
		public static final int FIRST = 5;// 第一次加载
	}

	/**
	 * 任务类型
	 */
	public class DEF_TASK_TYPE {
		public static final String FIRST = "first";
		public static final String NOR_FIRST = "not_first";
		public static final String REFRESH = "REFRESH";
		public static final String LOAD = "LOAD";
	}

	/**
	 * 文章类型
	 */
	public class DEF_ARTICLE_TYPE {
		public static final int HOME = 0; // 首页
		public static final int ANDROID = 1; // Android
		public static final int COCOS2DX = 2; // Cocos2dx
		public static final int INTERVIEW = 3; // 面试宝典
		public static final int LUA = 4; // Lua
		public static final int DESIGN_PATTERN = 5; // 设计模式
		public static final int XIAOWU_RECORD = 6; // 记录点滴
		public static final int NETWORK_PROT = 7; // 网络协议
		public static final int GO = 8; // go语言
		public static final int JIAN_ZHAN = 9; // 建站经验
	}
}
