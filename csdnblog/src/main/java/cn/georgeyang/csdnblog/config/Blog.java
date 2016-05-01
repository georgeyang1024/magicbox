package cn.georgeyang.csdnblog.config;

import cn.georgeyang.csdnblog.bean.BaseEntity;

@Deprecated
public class Blog extends BaseEntity {
	private static final long serialVersionUID = -8366599113596257949L;

//	@Column(column = "title")
	private String title;
	
//	@Column(column = "content")
	private String content;
	
//	@Column(column = "summary")
	private String summary;
	
//	@Column(column = "imgLink")
	private String imgLink;
	
//	@Column(column = "link")
	private String link;
	
//	@Column(column = "state")
	private int state;
	
//	@Column(column = "commentCount")
	private String commentCount;
	
//	@Column(column = "reserve")
	private String reserve;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
}
