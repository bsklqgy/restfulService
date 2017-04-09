package com.bdx.entity;

import java.io.Serializable;

public class Result implements Serializable{

	private static final long serialVersionUID = -3839440342162398072L;
	
	private int code;
	private String msg;
	private Object datas;
	
	public Result() {
	}
	
	public Result(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public Result(int code, String msg, Object datas) {
		super();
		this.code = code;
		this.msg = msg;
		this.datas = datas;
	}
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getDatas() {
		return datas;
	}
	public void setDatas(Object datas) {
		this.datas = datas;
	}
	
	

	
}
