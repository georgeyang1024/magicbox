package cn.georgeyang.csdnblog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import android.text.TextUtils;
import android.util.Log;

import cn.georgeyang.csdnblog.bean.BlogCategory;
import cn.georgeyang.csdnblog.bean.BlogItem;
import cn.georgeyang.csdnblog.bean.Blogger;
import cn.georgeyang.csdnblog.bean.BloggerDetail;
import cn.georgeyang.csdnblog.bean.BlogComment;
import cn.georgeyang.csdnblog.config.AppConstants;
import cn.georgeyang.csdnblog.config.Blog;

/**
 * 网页解析
 * 
 * @author tangqi
 * @data 2015年8月9日下午08:09:57
 */
@SuppressWarnings("deprecation")
public class JsoupUtil {

	public static boolean contentFirstPage = true;
	public static boolean contentLastPage = true;
	public static boolean multiPages = false;
	private static final String BLOG_URL = "http://blog.csdn.net";

	public static void resetPages() {
		contentFirstPage = true;
		contentLastPage = true;
		multiPages = false;
	}

	/**
	 * 获取博主简易信息
	 * 
	 * @param paramString
	 * @return
	 */
	public static HashMap<String, String> getBloggerItem(String paramString) {
		Document localDocument = Jsoup.parse(paramString);
		Elements localElements = localDocument.getElementsByClass("header");

		String str = "";
		try {
			Element localElement1 = localDocument.getElementsByClass("panel").get(0).select("ul.panel_body.profile").get(0);
			str = localElement1.getElementById("blog_userface").select("a").select("img").attr("src");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			str = "";
		}

		HashMap<String, String> localHashMap = new HashMap<String, String>();
		localHashMap.put("title", localElements.select("h2").text());
		localHashMap.put("description", localElements.select("h3").text());
		localHashMap.put("imgUrl", str);
		return localHashMap;
	}

	/**
	 * 获取博客列表
	 * 
	 * @param
	 * @param str
	 * @return
	 */
	public static List<BlogItem> getBlogItemList(String category, String str, List<BlogCategory> blogCategoryList) {
		List<BlogItem> list = new ArrayList<BlogItem>();
		Document doc = Jsoup.parse(str);
		Elements blogList = doc.getElementsByClass("article_item");

		for (Element blogItem : blogList) {
			BlogItem item = new BlogItem();
			String title = blogItem.select("h1").text();

			String icoType = blogItem.getElementsByClass("ico").get(0).className();
			if (title.contains("置顶")) {
				item.setTopFlag(1);
			}
			String description = blogItem.select("div.article_description").text();
			String msg = blogItem.select("div.article_manage").text();
			String date = blogItem.getElementsByClass("article_manage").get(0).text();
			String link = BLOG_URL + blogItem.select("h1").select("a").attr("href");

			item.setTitle(title);
			item.setMsg(msg);
			item.setContent(description);
			item.setDate(date);
			item.setLink(link);
			item.setCategory(category);
			item.setIcoType(icoType);

			// 没有图片
			item.setImgLink(null);
			list.add(item);
		}

		// 获取博客分类
		Elements panelElements = doc.getElementsByClass("panel");
		for (Element panelElement : panelElements) {
			try {
				String panelHead = panelElement.select("ul.panel_head").get(0).text();
				if ("文章分类".equals(panelHead)) {
					Element panelBodyElement = panelElement.select("ul.panel_body").get(0);
					Elements typeElements = panelBodyElement.select("li");

					if (typeElements != null) {
						// 若发现新的分类，清除以前的分类
						blogCategoryList.clear();
						BlogCategory allBlogCategory = new BlogCategory();
						allBlogCategory.setName("全部");
						blogCategoryList.add(0, allBlogCategory);

						for (Element typeElement : typeElements) {
							BlogCategory blogCategory = new BlogCategory();
							String name = typeElement.select("a").text().trim().replace("【", "").replace("】", "");
							String link = typeElement.select("a").attr("href");
							blogCategory.setName(name.trim());
							blogCategory.setLink(link.trim());

							blogCategoryList.add(blogCategory);
						}

					}
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

		return list;
	}

	/**
	 * 获取热门博客列表
	 * 
	 * @param category
	 * @param str
	 * @return
	 */
	public static List<BlogItem> getHotBlogList(String category, String str) {
		List<BlogItem> list = new ArrayList<BlogItem>();
		Document doc = Jsoup.parse(str);
		Elements blogList = doc.getElementsByClass("blog_list");

		for (Element blogItem : blogList) {
			BlogItem item = new BlogItem();
			String title = blogItem.select("h1").text();

			String description = blogItem.select("dd").text();
			String date = blogItem.getElementsByClass("about_info").get(0).text();
			String link = blogItem.select("h1").select("a").attr("href");

			item.setTitle(title);
			item.setContent(description);
			item.setDate(date);
			item.setLink(link);
			item.setCategory(category);
			item.setIcoType(AppConstants.BLOG_ICO_TYPE.BLOG_TYPE_ORIGINAL);

			// 没有图片
			item.setImgLink(null);
			list.add(item);
		}

		return list;

	}

	/**
	 * 获取博客详情内容
	 * 
	 * @param paramString
	 * @return
	 */
	public static String getContent(String paramString) {
		if (TextUtils.isEmpty(paramString)) {
			return null;
		}

		Element localElement1 = Jsoup.parse(paramString).getElementsByClass("details").get(0);

		// 获取详情
		localElement1.select("script").remove();
		if (localElement1.getElementById("digg") != null) {
			localElement1.getElementById("digg").remove();
		}
		if (localElement1.getElementsByClass("tag2box") != null) {
			localElement1.getElementsByClass("tag2box").remove();
		}
		localElement1.getElementsByClass("article_manage").remove();
		localElement1.getElementsByTag("h1").tagName("h2");
		Iterator<?> localIterator = localElement1.select("pre[name=code]").iterator();
		while (true) {
			if (!localIterator.hasNext())
				return localElement1.toString();
			Element localElement2 = (Element) localIterator.next();
			localElement2.attr("class", "brush: java; gutter: false;");
			Log.i("CSNDBlog_JsoupUtil", "codeNode.text()" + localElement2.text());
		}
	}

	/**
	 * 获取博客详情内容
	 * 
	 * @param paramString
	 * @return
	 */
	public static String getTitle(String paramString) {
		if (TextUtils.isEmpty(paramString)) {
			return null;
		}

		Element localElement1 = Jsoup.parse(paramString).getElementsByClass("details").get(0);
		Element titleElement = localElement1.getElementsByClass("article_title").get(0);
		try {
			return titleElement.select("h1").get(0).select("span").get(0).select("a").get(0).text();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取博主列表(各大分类)
	 * 
	 * @param category
	 * @param str
	 * @return
	 */
	public static List<Blogger> getBloggerList(String category, String str) {
		List<Blogger> list = new ArrayList<Blogger>();
		Document doc = Jsoup.parse(str);
		Elements bloggerList = doc.getElementsByClass("list_3");

		Elements ulElements = bloggerList.select("ul");
		for (Element ulElement : ulElements) {
			Elements liElements = ulElement.select("li");
			for (Element element : liElements) {
				Blogger blogger = new Blogger();
				Element bloggerElement = element.select("dl").get(0).select("dt").get(0).select("a").get(0);
				String url = bloggerElement.attr("href");

				Element imgElement = bloggerElement.select("img").get(0);
				String title = imgElement.attr("alt");
				String imgUrl = imgElement.attr("src");

				blogger.setUserId(url.replace(AppConstants.CSDN_BASE_URL, ""));
				blogger.setLink(url);
				blogger.setTitle(title);
				blogger.setImgUrl(imgUrl);
				blogger.setType(category);
				blogger.setCategory(category);
				list.add(blogger);
			}
		}

		return list;
	}

	/**
	 * 获取博文评论列表
	 * 
	 * @param str
	 *            json字符串
	 * @return
	 */
	public static List<BlogComment> getBlogCommentList(String str, int pageIndex, int pageSize) {
		List<BlogComment> list = new ArrayList<BlogComment>();
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			int index = 0;
			int len = jsonArray.length();

			// 如果评论数大于20
			if (len > 20) {
				index = (pageIndex * pageSize) - 20;
			}

			if (len < pageSize && pageIndex > 1) {
				return list;
			}

			if ((pageIndex * pageSize) < len) {
				len = pageIndex * pageSize;
			}

			for (int i = index; i < len; i++) {
				JSONObject item = jsonArray.getJSONObject(i);
				String commentId = item.getString("CommentId");
				String content = item.getString("Content");
				String username = item.getString("UserName");
				String parentId = item.getString("ParentId");
				String postTime = item.getString("PostTime");
				String userface = item.getString("Userface");

				BlogComment comment = new BlogComment();
				comment.setCommentId(commentId);
				comment.setContent(content);
				comment.setUsername(username);
				comment.setParentId(parentId);
				comment.setPostTime(postTime);
				comment.setUserface(userface);

				if (parentId.equals("0")) {
					// 如果parentId为0的话，表示它是评论的topic
					comment.setType(AppConstants.DEF_COMMENT_TYPE.PARENT);
				} else {
					comment.setType(AppConstants.DEF_COMMENT_TYPE.CHILD);
				}
				list.add(comment);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得博主个人详情资料
	 * 
	 * @param str
	 * @return
	 */
	public static BloggerDetail getBloggerInfo(String str) {

		if (str == null) {
			return null;
		}
		Document doc = Jsoup.parse(str);
		Element profile = doc.getElementsByClass("panel").get(0);

		Element profileBody = profile.select("ul.panel_body.profile").get(0);
		Element userface = profileBody.getElementById("blog_userface");
		String userfaceLink = userface.select("a").select("img").attr("src"); // 得到头像链接
		String username = userface.getElementsByTag("a").get(1).text(); // 用户名

		Element blog_rank = profileBody.getElementById("blog_rank");
		Element blog_statistics = profileBody.getElementById("blog_statistics");

		Elements rankLi = blog_rank.select("li");
		StringBuilder sb = new StringBuilder();
		String rankStr = "";
		for (Element rank : rankLi) {
			sb.append(rank.text()).append("|");
		}
		rankStr = sb.toString();

		String statistics = "";
		StringBuilder sb2 = new StringBuilder();
		Elements blogLi = blog_statistics.select("li");
		for (Element info : blogLi) {
			sb2.append(info.text()).append("|");
		}
		statistics = sb2.toString();

		BloggerDetail blogger = new BloggerDetail();

		blogger.setUserface(userfaceLink);
		blogger.setUsername(username);
		blogger.setRank(rankStr);
		blogger.setStatistics(statistics);

		return blogger;
	}

	/**
	 * 扒取传入url地址的博客详细内容
	 * 
	 * @param url
	 * @param str
	 * @return
	 */
	@Deprecated
	public static List<Blog> getDetail(String url, String str) {
		List<Blog> list = new ArrayList<Blog>();

		// 获取文档内容
		Document doc = Jsoup.parse(str);

		// 这里可以优化，取出所有details的元素即可，不用区分这么细

		// 获取class="details"的元素
		Element detail = doc.getElementsByClass("details").get(0);
		detail.select("script").remove(); // 删除每个匹配元素的DOM。

		// 获取标题
		Element title = detail.getElementsByClass("article_title").get(0);
		Blog blogTitle = new Blog();
		blogTitle.setState(AppConstants.DEF_BLOG_ITEM_TYPE.TITLE); // 设置状态
		blogTitle.setContent(ToDBC(title.text())); // 设置标题内容

		// 获取文章内容
		Element content = detail.select("div.article_content").get(0);

		// 获取所有标签为<a的元素
		Elements as = detail.getElementsByTag("a");
		for (int b = 0; b < as.size(); b++) {
			Element blockquote = as.get(b);
			// 改变这个元素的标记。例如,<span>转换为<div> 如el.tagName("div");。
			blockquote.tagName("bold"); // 转为粗体
		}

		Elements ss = detail.getElementsByTag("strong");
		for (int b = 0; b < ss.size(); b++) {
			Element blockquote = ss.get(b);
			blockquote.tagName("bold");
		}

		// 获取所有标签为<p的元素
		Elements ps = detail.getElementsByTag("p");
		for (int b = 0; b < ps.size(); b++) {
			Element blockquote = ps.get(b);
			blockquote.tagName("body");
		}

		// 获取所有引用元素
		Elements blockquotes = detail.getElementsByTag("blockquote");
		for (int b = 0; b < blockquotes.size(); b++) {
			Element blockquote = blockquotes.get(b);
			blockquote.tagName("body");
		}

		// 获取所有标签为<ul的元素
		Elements uls = detail.getElementsByTag("ul");
		for (int b = 0; b < uls.size(); b++) {
			Element blockquote = uls.get(b);
			blockquote.tagName("body");
		}

		// 找出粗体
		Elements bs = detail.getElementsByTag("b");
		for (int b = 0; b < bs.size(); b++) {
			Element bold = bs.get(b);
			bold.tagName("bold");
		}

		// 遍历博客内容中的所有元素
		for (int j = 0; j < content.children().size(); j++) {
			Element c = content.child(j); // 获取每个元素

			// 抽取出图片
			if (c.select("img").size() > 0) {
				Elements imgs = c.getElementsByTag("img");
				System.out.println("img");
				for (Element img : imgs) {
					if (!img.attr("src").equals("")) {
						Blog blogImgs = new Blog();
						// 大图链接
						if (!img.parent().attr("href").equals("")) {
							blogImgs.setImgLink(img.parent().attr("href"));
							System.out.println("href=" + img.parent().attr("href"));
							if (img.parent().parent().tagName().equals("p")) {
								// img.parent().parent().remove();
							}
							img.parent().remove();
						}
						blogImgs.setContent(img.attr("src"));
						blogImgs.setImgLink(img.attr("src"));
						System.out.println(blogImgs.getContent());
						blogImgs.setState(AppConstants.DEF_BLOG_ITEM_TYPE.IMG);
						list.add(blogImgs);
					}
				}
			}
			c.select("img").remove();

			// 获取博客内容
			Blog blogContent = new Blog();
			blogContent.setState(AppConstants.DEF_BLOG_ITEM_TYPE.CONTENT);

			if (c.text().equals("")) {
				continue;
			} else if (c.children().size() == 1) {
				if (c.child(0).tagName().equals("bold") || c.child(0).tagName().equals("span")) {
					if (c.ownText().equals("")) {
						// 小标题，咖啡色
						blogContent.setState(AppConstants.DEF_BLOG_ITEM_TYPE.BOLD_TITLE);
					}
				}
			}

			// 代码
			if (c.select("pre").attr("name").equals("code")) {
				blogContent.setState(AppConstants.DEF_BLOG_ITEM_TYPE.CODE);
				blogContent.setContent(ToDBC(c.outerHtml()));
			} else if ((c.select("pre").attr("class").equals("prettyprint"))) {
				blogContent.setState(AppConstants.DEF_BLOG_ITEM_TYPE.CODE);
				blogContent.setContent(ToDBC(c.outerHtml()));
			} else {
				blogContent.setContent(ToDBC(c.outerHtml()));
			}
			list.add(blogContent);
		}

		return list;
	}

	/**
	 * 半角转换为全角 全角---指一个字符占用两个标准字符位置。 半角---指一字符占用一个标准的字符位置。
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

}
