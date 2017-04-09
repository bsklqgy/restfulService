package com.bdx.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bdx.entity.Bid;
import com.bdx.entity.Result;
import com.bdx.entity.Result_bid;
import com.bdx.entity.Setting;
import com.bdx.service.ApiService;
import com.bdx.utils.DateUtils;
import com.bdx.utils.HttpURLConnectionUtils;
import com.bdx.utils.JsonUtils;
import org.apache.logging.log4j.Logger;


@Service("apiService")
public class ApiServiceImpl implements ApiService, InitializingBean {

	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private HiloOptimizer investHiloOptimizer;
	
	private String investPrefix = "yyyyMMdd";
	
	private int investMaxLo = 100;

	private static final Logger logger = LogManager.getLogger(ApiServiceImpl.class);
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		investHiloOptimizer = new HiloOptimizer(1, investPrefix, investMaxLo);
	}
	
	@Transactional
	public String generate(int type) {
		if (type == 1) {
			return investHiloOptimizer.generate();
		} 
		return null;
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> getMerchantByAppid(String id) {
		try{
			return jdbcTemplate.queryForMap("select t.appsecret,t.appip from merchant t where t.appid=? ", id);
		} catch (EmptyResultDataAccessException e) {  
			return null;
		}
	}
	
	@Transactional
	public Result invest_success(String appid, String uid, String bid,String iid,Double amount, Date time,String t) {
		Map<String, Object> bid_map = null;
		try { 
			bid_map = jdbcTemplate.queryForMap("select t.id, t.invest_cycle,t.invest_cycle_unit,t.max_invest_amount,t.extra_annualized_rate,t.business_way,a.is_mobile_jump,a.is_web_jump from bid t inner join merchant a on t.m_id = a.id and a.appid=? where t.app_b_id=? and t.status=? ", new Object[] {appid,bid,"启用"});
		}catch (EmptyResultDataAccessException e) {
			insertAppExcLog(appid,"投资回调接口","找不到启用的匹配标的","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(500 ,"服务器内部错误(找不到启用的匹配标的)");
		} 
		if(bid_map.get("is_web_jump").equals(false) && "1".equals(t)){
			insertAppExcLog(appid,"投资回调接口","商户未开通网页端跳转","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(500 ,"服务器内部错误(未开通网页端跳转)");
		}
		if(bid_map.get("is_mobile_jump").equals(false) && "2".equals(t)){
			insertAppExcLog(appid,"投资回调接口","商户未开通移动端跳转","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(500 ,"服务器内部错误(未开通移动端跳转)");
		}
		
		if((bid_map.get("is_web_jump").equals(true) && "1".equals(t)) || (bid_map.get("is_mobile_jump").equals(true) && "2".equals(t))  ){
			try { 
				Long count  =  jdbcTemplate.queryForObject("select count(t.id) from bid_jump t inner join member_appuser a on t.m_id=a.m_id and a.appid=? and a.appuid=?  where t.terminal_type=? ",  new Object[]{appid,uid,t},Long.class);// t.b_id=? and     ,bid_map.get("id")
				if(count==null || count<=0){
					insertAppExcLog(appid,"投资回调接口","用户未有跳转记录","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
					return new Result(500 ,"用户未有跳转记录");
				}
			} catch (EmptyResultDataAccessException e) { 
				insertAppExcLog(appid,"投资回调接口","用户未有跳转记录","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
				return new Result(500 ,"用户未有跳转记录");
			}
		}
		
		Map<String, Object> mid_retime = null;
		try { 
			mid_retime = jdbcTemplate.queryForMap("select t.m_id,t.appuregdate,a.create_date from member_appuser t left join member a on t.m_id = a.id  where t.appid = ? and t.appuid= ? ", new Object[] {appid,uid});  
		} catch (EmptyResultDataAccessException e) {  
			insertAppExcLog(appid,"投资回调接口","找不到匹配用户","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(500 ,"服务器内部错误(找不到匹配用户)");
		}  
		if(mid_retime.get("appuregdate")==null || mid_retime.get("create_date")==null){
			insertAppExcLog(appid,"投资回调接口","用户注册时间为空","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(500 ,"服务器内部错误(用户注册时间为空)");
		}
		
		//合作平台注册时间比北斗星早不返利
		Date regtime1 = (Date) mid_retime.get("appuregdate");
		Date regtime2 = (Date) mid_retime.get("create_date");
		if(regtime1.before(regtime2)){
			insertAppExcLog(appid,"投资回调接口","回调成功，不符合首次注册不计返利","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(200 ,"回调成功，不符合首次注册不计返利");
		}

		//预期利息计算
		BigDecimal extra_profits = null;
		BigDecimal invest_cycle = new BigDecimal(bid_map.get("invest_cycle").toString());
		BigDecimal extra_annualized_rate = new BigDecimal(bid_map.get("extra_annualized_rate").toString());
		BigDecimal max_invest_amount =  new BigDecimal(bid_map.get("max_invest_amount").toString());
		BigDecimal calculateAmount = new BigDecimal(Double.toString(amount));
		if(calculateAmount.compareTo(max_invest_amount) ==1 )calculateAmount = max_invest_amount;
		if("天".equals(bid_map.get("invest_cycle_unit"))){
			extra_profits = (calculateAmount.multiply(invest_cycle).multiply(extra_annualized_rate.divide(BigDecimal.valueOf(36000),5,BigDecimal.ROUND_HALF_EVEN))).setScale(2,RoundingMode.HALF_EVEN);
		}else if("月".equals(bid_map.get("invest_cycle_unit"))){
			extra_profits = (calculateAmount.multiply(invest_cycle).multiply(extra_annualized_rate.divide(BigDecimal.valueOf(1200),5,BigDecimal.ROUND_HALF_EVEN))).setScale(2,RoundingMode.HALF_EVEN);
		}else if("年".equals(bid_map.get("invest_cycle_unit"))){
			extra_profits = (calculateAmount.multiply(invest_cycle).multiply(extra_annualized_rate.divide(BigDecimal.valueOf(100),5,BigDecimal.ROUND_HALF_EVEN))).setScale(2,RoundingMode.HALF_EVEN);
		}
		boolean toinsert = false;
		if("1".equals(bid_map.get("business_way").toString())){
			Long invest_count =  jdbcTemplate.queryForObject("select count(*) from bid_invest t inner join bid a on t.b_id=a.id and a.business_way=1 where t.m_id=? and t.appid=?" ,  new Object[]{mid_retime.get("m_id"),appid},Long.class);  
			//发新手投资奖励
			if(invest_count==0){
				BigDecimal bd=null;
				try {
					String s = jdbcTemplate.queryForObject(" select contents from bas_dicts t where t.nekey=2 and t.nevalue='2' ",String.class);	
					bd =  new BigDecimal(s);
				}catch(EmptyResultDataAccessException e){
				}
				if(bd.compareTo(BigDecimal.ZERO)==1){
					jdbcTemplate.update(" insert into member_income(m_id,amount,create_date,transaction_type,remark) values (?,?,?,?,?) ",new Object[]{mid_retime.get("m_id"),bd,new Date(),"投资奖励","新手独享首投红包"});
				}
				toinsert=true;
			}			
		}else{//TODO 目前其它归为2情况
			toinsert=true;
		}
		if(toinsert){
			int count = jdbcTemplate.update(" insert into bid_invest(b_id,m_id,create_date,invest_date,sn,amount,extra_profits,status,appid,app_i_id)  values(?,?,?,?,?,?,?,?,?,?) ",new Object[]{bid_map.get("id"),mid_retime.get("m_id"),new Date(),time,this.generate(1),amount,extra_profits,"等待返利",appid,iid});
			if(count>0){
				return new Result(200 ,"投资回调成功");
			}
		}else{
			insertAppExcLog(appid,"投资回调接口","回调成功，不符合新手首投规则不计返利","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
			return new Result(200 ,"投资回调成功，不符合新手首投规则不计返利");
		}
		insertAppExcLog(appid,"投资回调接口","回调失败","uid="+uid+"&bid="+bid+"&iid="+iid+"&amount="+amount+"&time="+time.getTime()+"&t="+t);
		return new Result(200 ,"投资回调失败");
	}
	
	@Transactional
	public Result member_match(String appid,String uid,String username,Date regdate) {
		Long mid = null;
		try { 
			mid = jdbcTemplate.queryForObject("select t.id from member t where t.username= ? ", new Object[] {username}, Long.class);  
		} catch (EmptyResultDataAccessException e) {  
			insertAppExcLog(appid,"用户匹配接口","找不到需要匹配的用户","uid="+uid+"&username="+username+"&regdate="+regdate.getTime());
			return new Result(500 ,"服务器内部错误(找不到需要匹配的用户)");
		}
		int count = jdbcTemplate.update(" insert into member_appuser(create_date,m_id,appid,appuid,appuregdate,type)  values(?,?,?,?,?,?) ",new Object[]{new Date(),mid,appid,uid,regdate,2});
		if(count<=0){
			return new Result(200 ,"匹配用户失败");
		}
		return new Result(200 ,"匹配用户成功");
	}
	
	
	@Transactional(readOnly = true)
	public Result member_jump_query(String appid,String uid,String bid,String t) {
		try { 
			String terminal_type= "网页端";
	    	if(t.equals("2")){
	    		terminal_type="移动端";
	    	}
			Date createTime  =  jdbcTemplate.queryForObject("select min(t.create_date) from bid_jump t inner join member_appuser a on t.m_id=a.m_id and a.appid=? and a.appuid=?  where  t.terminal_type=? ",  new Object[]{appid,uid,terminal_type},Date.class);//t.b_id=? and    ,bid
			if(createTime==null){
				insertAppExcLog(appid,"跳转查询接口","查询成功，未有跳转记录","uid="+uid+"&bid="+bid+"&t="+t);
				return new Result(200 ,"查询成功，未有跳转记录");
			}
			return new Result(200 ,"查询成功，获得跳转记录",createTime.getTime());
		} catch (EmptyResultDataAccessException e) {  
			insertAppExcLog(appid,"跳转查询接口","查询成功，未有跳转记录","uid="+uid+"&bid="+bid+"&t="+t);
			return new Result(200 ,"查询成功，未有跳转记录");
		}
	}
	
	@Transactional
	public void fetchBids(){
		List<Map<String, Object>> merchants = jdbcTemplate.queryForList("select t.id,t.appid from merchant t where t.status='启用' ");
		String sql="insert into bid(m_id,create_date,bid_name,bid_description,original_annualized_rate,min_invest_amount,max_invest_amount,invest_cycle,invest_cycle_unit,start_date,end_date,safeguard_way,repay_way,interest_way,total_amount,business_way,url,status,app_b_id,b_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		for(Map<String, Object> m:merchants){
			if(Setting.APP1ID.equals(m.get("appid"))){
				String datastr = "";
				if(Setting.APP1ID.equals(m.get("appid"))){
					datastr = HttpURLConnectionUtils.get("http://211.149.230.247/mapi/index.php?act=beidouxing_get_deal");//http://www.xrkdai.com/mapi/index.php?act=beidouxing_get_deal
				}else if(Setting.APP2ID.equals(m.get("appid"))){
					datastr = HttpURLConnectionUtils.get("http://test1.tourongjia.com/Index/Track?source=bdx&saction=blist");
				}
							
				if(StringUtils.isNotEmpty(datastr)){
					Long m_id =  (Long) m.get("id");
					Date now = new Date();
					Result_bid rb = JsonUtils.toObject(datastr, Result_bid.class);
					List<String> ids = new ArrayList<String>();
					for(Bid b: rb.getDatas()){
						ids.add(b.getApp_b_id());
					}
					List<Map<String, Object>> ids2 =null;
					if(ids!=null && ids.size()>0){
						MapSqlParameterSource  params = new MapSqlParameterSource();
						params.addValue("m_id", m_id);
						params.addValue("ids", ids);
						ids2 = namedParameterJdbcTemplate.queryForList("select t.app_b_id from bid t where t.m_id=:m_id and t.app_b_id in (:ids) ", params);  
					}
					List<String> ids3 = new ArrayList<String>();
					for(Map<String, Object> map: ids2){
						ids3.add((String) map.get("app_b_id"));
					}

					List<Object[]> oList = new ArrayList<Object[]>();
					int i=0;
					for(Bid b: rb.getDatas()){
						if(ids3.contains(b.getApp_b_id())){
							i++;
							continue;
						}
						Date enddate = null;
						//System.out.println(b.getBid_description());
						if("天".equals(b.getInvest_cycle_unit())){
							enddate = org.apache.commons.lang3.time.DateUtils.addDays(b.getStart_date(), b.getInvest_cycle()-1);
						}else if("月".equals(b.getInvest_cycle_unit())){
							enddate = org.apache.commons.lang3.time.DateUtils.addDays(b.getStart_date(), b.getInvest_cycle()*30-1);
						}else if("年".equals(b.getInvest_cycle_unit())){
							enddate = org.apache.commons.lang3.time.DateUtils.addDays(b.getStart_date(), b.getInvest_cycle()*360-1);
						}
						oList.add(new Object[]{m_id,now,b.getBid_name(),b.getBid_description(),b.getOriginal_annualized_rate(),b.getMin_invest_amount(),b.getMax_invest_amount(),b.getInvest_cycle(),b.getInvest_cycle_unit(),b.getStart_date(),enddate,b.getSafeguard_way(),b.getRepay_way(),b.getInterest_way(),b.getTotal_amount(),b.getBusiness_way(),b.getUrl(),"待审核",b.getApp_b_id(),1});
					}
					if(oList!=null && oList.size()>0){
						int exc[] = jdbcTemplate.batchUpdate(sql, oList);
						int j=0;
						for(int a :exc){
							if(a==1)j++;
						}
						logger.info("appid:"+Setting.APP1ID+",读取"+rb.getDatas().size()+"条，数据库已存在"+i+"条，成功执行insert"+j);
					}
				}
			}
		}
	}
	
	private long getLastValue(int type) {
		Long lastValue = jdbcTemplate.queryForObject("select t.last_value from sn t where t.type = ?", new Object[] {type}, Long.class);  
		jdbcTemplate.update(" update sn t set t.last_value=? where t.type=? ",new Object[]{lastValue+1,type});
		return lastValue;
	}
	
	
	/**
	 * 高低位算法
	 */
	private class HiloOptimizer {

		private int type;
		private String prefix;
		private int maxLo;
		private int lo;
		private long hi;
		private long lastValue;

		public HiloOptimizer(int type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix;
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		public synchronized String generate() {
			if (lo > maxLo) {
				lastValue = getLastValue(type);
				lo = lastValue == 0 ? 1 : 0;
				hi = lastValue * (maxLo + 1);
			}
			if(StringUtils.isNotEmpty(prefix)){
				return DateUtils.formatTime(new Date(),prefix) + (hi + lo++);
			}
			return String.valueOf(hi + lo++);
		}
	}
	
	private void insertAppExcLog(String appid,String action,String msg,String params) {
		jdbcTemplate.update(" insert into app_exc_log(create_date,appid,action,msg,params)  values(?,?,?,?,?)  ",new Object[]{new Date(), appid,action, msg, params});
	}
	
	
	//以下小程序用
	@Transactional(readOnly = true)
	public List<Map<String, Object>> findAticleListByPage(Integer pageNumber, Integer pageSize) {
		if(pageNumber==null || pageNumber<=0)pageNumber=1;
		if(pageSize==null)pageSize=0;
		return jdbcTemplate.queryForList("select t.id, date_format(t.create_date,'%Y-%m-%d %H:%i') as create_date_str,t.title,t.show_img,t.article_category from article t where t.is_publication=1  order by t.create_date desc limit ?,?",
						new Object[] {(pageNumber - 1) * pageSize,pageSize });
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> findAticleTopListByPage(Integer pageNumber, Integer pageSize) {
		if(pageNumber==null || pageNumber<=0)pageNumber=1;
		if(pageSize==null)pageSize=0;
		return jdbcTemplate.queryForList("select t.id, date_format(t.create_date,'%Y-%m-%d %H:%i') as create_date_str,t.title,t.show_img,t.article_category from article t where t.is_publication=1 and t.is_top=1  order by t.create_date desc limit ?,?",
						new Object[] {(pageNumber - 1) * pageSize,pageSize });
	}
	
	
	@Transactional(readOnly = true)
	public Map<String, Object> findAticleById(Long id) {
		try {
			return jdbcTemplate.queryForMap("select t.id, date_format(t.create_date,'%Y-%m-%d %H:%i') as create_date_str,t.title,t.summary,t.show_img,t.contents,t.article_category from article t where t.id=? and t.is_publication=1 ",new Object[] {id});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
