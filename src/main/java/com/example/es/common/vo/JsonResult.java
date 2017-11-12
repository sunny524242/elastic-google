package com.example.es.common.vo;

/**
 * 返回model
 * 名称：Model.java<br>
 * 描述：<br>
 * 类型：JAVA<br>
 * 最近修改时间: 2017年5月5日 上午11:20:22 <br>
 * @since  2017年5月5日
 * @authour ChenRenhao
 */
public class JsonResult implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String msg;
	private Long total;
	private Object data;
	
	public JsonResult(Integer code, String msg, Object data, Long total) {
		this.code=code;
		this.msg=msg;
		this.data=data;
		this.total=total;
	}
	
	public JsonResult(Integer code, String msg, Object data) {
		this.code=code;
		this.msg=msg;
		this.data=data;
	}
	
	public JsonResult(Integer code, String msg) {
		this.code=code;
		this.msg=msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	
	
}
