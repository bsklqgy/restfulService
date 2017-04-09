package com.bdx.entity;

import java.io.Serializable;
import java.util.Date;

public class Bid implements Serializable{

	private static final long serialVersionUID = 3516675968471853339L;
	
	public String app_b_id;
	public String bid_name;
	public String bid_description;
	public double original_annualized_rate;
	public double min_invest_amount;
	public double max_invest_amount;
	public int invest_cycle;
	public String invest_cycle_unit;
	public Date start_date;
	public String safeguard_way;
	public String repay_way ;
	public String interest_way;
	public double total_amount;
	public int  business_way ;
	public String url;
	public String getApp_b_id() {
		return app_b_id;
	}
	public void setApp_b_id(String app_b_id) {
		this.app_b_id = app_b_id;
	}
	public String getBid_name() {
		return bid_name;
	}
	public void setBid_name(String bid_name) {
		this.bid_name = bid_name;
	}
	public String getBid_description() {
		return bid_description;
	}
	public void setBid_description(String bid_description) {
		this.bid_description = bid_description;
	}
	public double getOriginal_annualized_rate() {
		return original_annualized_rate;
	}
	public void setOriginal_annualized_rate(double original_annualized_rate) {
		this.original_annualized_rate = original_annualized_rate;
	}
	public double getMin_invest_amount() {
		return min_invest_amount;
	}
	public void setMin_invest_amount(double min_invest_amount) {
		this.min_invest_amount = min_invest_amount;
	}
	public int getInvest_cycle() {
		return invest_cycle;
	}
	public void setInvest_cycle(int invest_cycle) {
		this.invest_cycle = invest_cycle;
	}
	public String getInvest_cycle_unit() {
		return invest_cycle_unit;
	}
	public void setInvest_cycle_unit(String invest_cycle_unit) {
		this.invest_cycle_unit = invest_cycle_unit;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public String getSafeguard_way() {
		return safeguard_way;
	}
	public void setSafeguard_way(String safeguard_way) {
		this.safeguard_way = safeguard_way;
	}
	public String getRepay_way() {
		return repay_way;
	}
	public void setRepay_way(String repay_way) {
		this.repay_way = repay_way;
	}
	public String getInterest_way() {
		return interest_way;
	}
	public void setInterest_way(String interest_way) {
		this.interest_way = interest_way;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public int getBusiness_way() {
		return business_way;
	}
	public void setBusiness_way(int business_way) {
		this.business_way = business_way;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public double getMax_invest_amount() {
		return max_invest_amount;
	}
	public void setMax_invest_amount(double max_invest_amount) {
		this.max_invest_amount = max_invest_amount;
	}
	
	
	
}
