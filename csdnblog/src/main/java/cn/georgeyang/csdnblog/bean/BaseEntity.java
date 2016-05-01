package cn.georgeyang.csdnblog.bean;

import java.io.Serializable;

import cn.georgeyang.database.Model;


/**
 * 实体类--基类
 *
 * @author tangqi
 * @data 2015年8月01日下午10:42:07
 */
public abstract class BaseEntity extends Model implements Serializable {

	private static final long serialVersionUID = 4995176180527325406L;
	
//	@Column(column = "id")
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
