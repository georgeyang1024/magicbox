package cn.georgeyang.csdnblog.bean;


/**
 * 博主简介
 * 
 * @author tangqi
 * @data 2015年7月8日
 */

public class Blogger extends BaseEntity {

	private static final long serialVersionUID = 6569781303855823679L;

	/**
	 * 博主ID
	 */
//	@Column(column = "userId")
	private String userId;

	/**
	 * 博主名字
	 */
//	@Column(column = "title")
	private String title;

	/**
	 * 博主描述
	 */
//	@Column(column = "description")
	private String description;

	/**
	 * 博主头像地址
	 */
//	@Column(column = "imgUrl")
	private String imgUrl;

	/**
	 * 博主博客链接
	 */
//	@Column(column = "link")
	private String link;

	/**
	 * 博主类型（小分类）
	 */
//	@Column(column = "type")
	private String type;

	/**
	 * 博主类别（大分类）
	 */
//	@Column(column = "category")
	private String category;

	/**
	 * 是否最新添加
	 */
//	@Column(column = "isNew")
	private int isNew;

	/**
	 * 是否置顶
	 */
//	@Column(column = "isTop")
	private int isTop;

	/**
	 * 博主更新时间
	 */
//	@Column(column = "updateTime")
	private long updateTime;

	/**
	 * 保留字段
	 */
//	@Column(column = "reserve")
	private String reserve;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getIsTop() {
		return isTop;
	}

	public void setIsTop(int isTop) {
		this.isTop = isTop;
	}

}