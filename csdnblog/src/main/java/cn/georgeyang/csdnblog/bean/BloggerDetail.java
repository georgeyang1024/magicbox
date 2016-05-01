package cn.georgeyang.csdnblog.bean;

/**
 * 博主详情
 * 
 * @author tangqi
 * @data 2015年8月8日上午10:02:57
 */

public class BloggerDetail extends BaseEntity {

	private static final long serialVersionUID = -6906903716539249845L;

	private String userface;
	private String username;
	private String rank;
	private String statistics;

	public String getUserface() {
		return userface;
	}

	public void setUserface(String userface) {
		this.userface = userface;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getStatistics() {
		return statistics;
	}

	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}

}
