package com.bdx.job;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bdx.service.ApiService;

@Component
public class BidJob {
	
	@Resource
	private ApiService apiService;
	
	 @Scheduled(cron="0 0/10 * * * ?")
	 public void fetchBids(){
		 apiService.fetchBids();
	 }

}
