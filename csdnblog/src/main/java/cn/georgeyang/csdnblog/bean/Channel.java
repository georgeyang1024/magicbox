package cn.georgeyang.csdnblog.bean;


/**
 * 频道分类
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:21:54
 */

public class Channel extends BaseEntity {

	private static final long serialVersionUID = 3205931841537722040L;

	/**
	 * 频道名
	 */
//	@Column(column = "channelName")
	private String channelName;

	/**
	 * 频道图标地址
	 */
//	@Column(column = "imgUrl")
	private String imgUrl;

	/**
	 * 频道图标ID（本地）
	 */
//	@Column(column = "imgResourceId")
	private int imgResourceId;

	/**
	 * 资源地址
	 */
//	@Column(column = "url")
	private String url;

	/**
	 * 更新时间
	 */
//	@Column(column = "updateTime")
	private long updateTime;

	/**
	 * 保留
	 */
//	@Column(column = "reserve")
	private String reserve;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getImgResourceId() {
		return imgResourceId;
	}

	public void setImgResourceId(int imgResourceId) {
		this.imgResourceId = imgResourceId;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public String getUrl() {
		return url;
	}

//	public void setUrl(String url) {
//		this.url = url;
//	}

}
