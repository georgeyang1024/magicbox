/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package cn.georgeyang.csdnblog.bean;

import java.util.Comparator;

/**
 * 评论列表-排序
 * 
 * @author tangqi
 * @date 2015年8月4日下午4:09:58
 */

public class CommentComparator implements Comparator<BlogComment> {

	@Override
	public int compare(BlogComment arg0, BlogComment arg1) {
		// TODO Auto-generated method stub
		if (arg0.getParentId().equals(arg1.getCommentId())) {
			return 1;
		} else if (arg0.getCommentId().equals(arg1.getParentId())) {
			return -1;
		}
		return 0;
	}

}
