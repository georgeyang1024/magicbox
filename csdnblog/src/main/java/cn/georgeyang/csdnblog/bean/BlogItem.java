package cn.georgeyang.csdnblog.bean;


/**
 * 博客实体类
 * 
 * @author tangqi
 * @data 2015年8月6日下午10:28:30
 */
public class BlogItem extends BaseEntity {


//	@Column(column = "title")
	private String title; // 标题

//	@Column(column = "link")
	private String link; // 文章链接

//	@Column(column = "date")
	private String date; // 博客发布时间

//	@Column(column = "imgLink")
	private String imgLink; // 图片链接

//	@Column(column = "content")
	private String content; // 文章内容

//	@Column(column = "msg")
	private String msg; // 消息

//	@Column(column = "category")
	private String category; // 博客分类

//	@Column(column = "viewTime")
	private String viewTime;

//	@Column(column = "isTop")
	private int topFlag;// 是否置顶

//	@Column(column = "icoType")
	private String icoType;// 文章类型

//	@Column(column = "updateTime")
	private long updateTime;// 更新时间

//	@Column(column = "reserve")
	private String reserve;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getViewTime() {
		return viewTime;
	}

	public void setViewTime(String viewTime) {
		this.viewTime = viewTime;
	}

	public int getTopFlag() {
		return topFlag;
	}

	public void setTopFlag(int topFlag) {
		this.topFlag = topFlag;
	}

	public String getIcoType() {
		return icoType;
	}

	public void setIcoType(String icoType) {
		this.icoType = icoType;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
