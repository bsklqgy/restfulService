package com.bdx.entity;

import java.io.Serializable;
import java.util.List;

public class Result_bid implements Serializable{
	
	private static final long serialVersionUID = 2390380457449962249L;
	
	private int code;
	private String msg;
	private List<Bid> datas;
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
	public List<Bid> getDatas() {
		return datas;
	}
	public void setDatas(List<Bid> datas) {
		this.datas = datas;
	}
	
	
	
	
}
