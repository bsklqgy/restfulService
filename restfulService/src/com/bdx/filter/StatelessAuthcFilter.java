package com.bdx.filter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

import com.bdx.entity.Result;
import com.bdx.entity.Setting;
import com.bdx.realm.StatelessToken;
import com.bdx.utils.JsonUtils;
import com.bdx.utils.WebUtils;

public class StatelessAuthcFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String username = servletRequest.getParameter(Setting.PARAM_USERNAME);
        String clientDigest = servletRequest.getParameter(Setting.PARAM_DIGEST);
        String timestamp = servletRequest.getParameter(Setting.PARAM_TIMESTAMP);
        if(clientDigest==null || username==null || timestamp==null){
        	onLoginFail(servletResponse,"参数不规范");
        	return false;
        }
        if(timestamp.length()==10){
        	timestamp +="000";//JAVA时间戳长度是13位，如：1294890876859  PHP时间戳长度是10位， 如：1294890859 
        }
        Date d = new Date(); 
        try { 
            long tslong = Long.parseLong(timestamp); 
            Timestamp ts = new Timestamp(tslong);
            d = ts; 
        } catch (Exception e) { 
            e.printStackTrace(); 
            onLoginFail(servletResponse,"ts转换错误");
            return false;
        }
        Date now = new Date();
        if(now.after(DateUtils.addMinutes(d,Setting.EXPIRED_MINUTE))){
        	onLoginFail(servletResponse,"ts过期");
        	return false;
        }

		Map<String, String[]> params = new HashMap<String, String[]>(servletRequest.getParameterMap()); 
        params.remove(Setting.PARAM_DIGEST);
        
        //生成无状态Token
        StatelessToken token = new StatelessToken(username,clientDigest,WebUtils.getIpAddrByRequest((HttpServletRequest) servletRequest),params, d);

        try {
            //委托给Realm进行登录
            getSubject(servletRequest, servletResponse).login(token);
        } catch (Exception e) {
            e.printStackTrace();
            onLoginFail(servletResponse,"通过失败"); //登录失败
            return false;
        }
        return true;
    }

    //登录失败时默认返回401状态码
    private void onLoginFail(ServletResponse response,String msg) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write( JsonUtils.toJson(new Result(HttpServletResponse.SC_UNAUTHORIZED,"unauthorized:"+msg)));
    }
}