package com.bdx.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bdx.entity.Result;
import com.bdx.service.ApiService;
import com.bdx.utils.DateEditor;
import com.bdx.utils.HtmlCleanEditor;

@RestController
@RequestMapping("/api")
public class ServiceController {
	
	private  static final Integer ARTICLE_PAGESIZE = 8;
	
	@Resource
	private ApiService apiService;

	/**
	 * 投资回调
	 * @param appid
	 * @param uid
	 * @param bid
	 * @param iid
	 * @param amount
	 * @param time
	 * @return
	 */
    @RequestMapping("/invest/success")
    public Result invest_success(String appid,String uid,String bid,String iid,Double amount, String time, String t) {
    	if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(bid) || StringUtils.isEmpty(iid) || StringUtils.isEmpty(time) || amount==null){
    		 return new Result(500,"参数有误") ;
    	}
    	if(time.length()==10){
    		time +="000";//JAVA时间戳长度是13位，如：1294890876859  PHP时间戳长度是10位， 如：1294890859 
        }
        Date d = new Date(); 
        try { 
        	long tslong = Long.parseLong(time); 
            Timestamp ts = new Timestamp(tslong);
            d = ts; 
        } catch (Exception e) { 
            e.printStackTrace(); 
            return new Result(500,"服务器内部异常（time格式转换错误）") ;
        }
        if(StringUtils.isEmpty(t)){
    		t="1";
    	}
    	if(!t.equals("1") && !t.equals("2")){
    		return new Result(500,"参数有误") ;
    	}
        return apiService.invest_success(appid, uid, bid, iid, amount, d, t) ;
    }
    
    /**
     * 用户匹配
     * @param appid
     * @param uid
     * @param username
     * @param regtime
     * @return
     */
    @RequestMapping("/member/match")
    public Result member_match(String appid,String uid,String username,String regtime) {
    	if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(username) || StringUtils.isEmpty(regtime)){
   		 	return new Result(500,"参数有误") ;
    	}
    	if(regtime.length()==10){
    		regtime +="000";//JAVA时间戳长度是13位，如：1294890876859  PHP时间戳长度是10位， 如：1294890859 
        }
        Date d = new Date(); 
        try { 
        	long tslong = Long.parseLong(regtime); 
            Timestamp ts = new Timestamp(tslong);
            d = ts; 
        } catch (Exception e) { 
            e.printStackTrace(); 
            return new Result(500,"服务器内部异常（regtime格式转换错误）") ;
        }
        return apiService.member_match(appid, uid, username,d) ;
    }
    
    
    /**
     * 跳转查询
     * @param appid
     * @param uid
     * @param t
     * @return
     */
    @RequestMapping("/member/jump/query")
    public Result member_jump_query(String appid,String uid,String bid,String t) {
    	if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(bid)){
   		 	return new Result(500,"参数有误");
    	}
    	if(StringUtils.isEmpty(t)){
    		t="1";
    	}
    	if(!t.equals("1") && !t.equals("2")){
    		return new Result(500,"参数有误");
    	}
        return apiService.member_jump_query(appid, uid, bid, t) ;
    }
    
    
    @RequestMapping("/article/list")
    public List<Map<String, Object>> article_list(Integer pn) {
    	List<Map<String,Object>> alist = apiService.findAticleListByPage(pn, ARTICLE_PAGESIZE);
    	return alist;
    }
    
    @RequestMapping("/article/top/list")
    public List<Map<String, Object>> article_top_list(Integer pn) {
    	List<Map<String,Object>> alist = apiService.findAticleTopListByPage(pn, ARTICLE_PAGESIZE);
    	return alist;
    }
    
    @RequestMapping("/article/detail")
    public Map<String, Object> article_detail(Long id) {
    	Map<String,Object> a = apiService.findAticleById(id);
    	return a;
    }
    
    
    @RequestMapping("/sn/bytype/query")
    public String  sn_bytype_query(Integer t) {
    	if(t==null)return null;
        return apiService.generate(t);
    }
    
    
    @RequestMapping("/error_401")
	public Result error_401(){ 
    	return new Result(401,"无效的或者错误的认证");
	}
    
    @RequestMapping("/error_403")
	public Result error_403(){ 
    	return new Result(403,"禁止访问");
	}
    
    @RequestMapping("/error_404")
	public Result error_404(){ 
    	return new Result(404,"该路径不可用");
	}
    
    @RequestMapping("/error_500")
	public Result error_500(){ 
    	return new Result(500,"服务器内部异常") ;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new HtmlCleanEditor(true, true));
		binder.registerCustomEditor(Date.class, new DateEditor(true));
	}
}