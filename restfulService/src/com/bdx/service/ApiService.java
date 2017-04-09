package com.bdx.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bdx.entity.Result;

public interface ApiService {
	
	Map<String, Object> getMerchantByAppid(String id);
	
	Result invest_success(String appid, String uid, String bid,String iid,Double amount, Date time,String t);
	
	Result member_match(String appid,String uid,String username, Date regdate);
	
	Result member_jump_query(String appid,String uid,String bid,String t);
	
	void fetchBids();
	
	String generate(int type);
	
	List<Map<String, Object>> findAticleListByPage(Integer pageNumber, Integer pageSize);
	
	List<Map<String, Object>> findAticleTopListByPage(Integer pageNumber, Integer pageSize);
	
	Map<String, Object> findAticleById(Long id);

}
