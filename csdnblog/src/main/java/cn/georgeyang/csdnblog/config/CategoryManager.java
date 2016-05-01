package cn.georgeyang.csdnblog.config;

/**
 * 分类管理
 * 
 * @author tangqi
 * @data 2015年8月19日下午9:53:12
 */

public class CategoryManager {

	/**
	 * 博客分类
	 * 
	 * @author Frank
	 *
	 */
	public class CategoryName {
		public static final String MOBILE = "移动开发";
		public static final String WEB = "WEB前端";
		public static final String ENTERPRISE = "架构设计";
		public static final String CODE = "编程语言";
		public static final String WWW = "互联网";
		public static final String DATABASE = "数据库";
		public static final String SYSTEM = "系统运维";
		public static final String CLOUD = "云计算";
		public static final String SOFTWARE = "研发管理";
	}

    public static String[] categoryNames = { CategoryName.MOBILE, CategoryName.WEB, CategoryName.ENTERPRISE, CategoryName.CODE,
            CategoryName.WWW, CategoryName.DATABASE, CategoryName.SYSTEM, CategoryName.CLOUD, CategoryName.SOFTWARE };


    public static String[] urls = { CategoryUrl.MOBILE, CategoryUrl.WEB, CategoryUrl.ENTERPRISE, CategoryUrl.CODE, CategoryUrl.WWW,
            CategoryUrl.DATABASE, CategoryUrl.SYSTEM, CategoryUrl.CLOUD, CategoryUrl.SOFTWARE, };

	/**
	 * 博客分类URL
	 * 
	 * @author Frank
	 *
	 */

	public class CategoryUrl {

		/**
		 * 移动开发
		 */
		public final static String MOBILE = "http://blog.csdn.net/mobile/experts.html";

		/**
		 * WEB前端
		 */
		public final static String WEB = "http://blog.csdn.net/web/experts.html";

		/**
		 * 架构设计
		 */
		public final static String ENTERPRISE = "http://blog.csdn.net/enterprise/experts.html";

		/**
		 * 编程语言
		 */
		public final static String CODE = "http://blog.csdn.net/code/experts.html";

		/**
		 * 互联网
		 */
		public final static String WWW = "http://blog.csdn.net/www/experts.html";

		/**
		 * 数据库
		 */
		public final static String DATABASE = "http://blog.csdn.net/database/experts.html";

		/**
		 * 系统运维
		 */
		public final static String SYSTEM = "http://blog.csdn.net/system/experts.html";

		/**
		 * 云计算
		 */
		public final static String CLOUD = "http://blog.csdn.net/cloud/experts.html";

		/**
		 * 研发管理
		 */
		public final static String SOFTWARE = "http://blog.csdn.net/software/experts.html";
	}

}
