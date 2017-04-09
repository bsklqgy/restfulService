package com.bdx.realm;

import java.util.Map;
import javax.annotation.Resource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import com.bdx.service.ApiService;
import com.bdx.utils.BasicCoder;
import com.bdx.utils.BasicCoder.Hmac;

public class StatelessRealm extends AuthorizingRealm {
	
	@Resource
	private ApiService apiService;
	
	public boolean supports(AuthenticationToken token) {
        //仅支持StatelessToken类型的Token
        return token instanceof StatelessToken;
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //根据用户名查找角色，请根据需求实现
//        String username = (String) principals.getPrimaryPrincipal();
//        SimpleAuthorizationInfo authorizationInfo =  new SimpleAuthorizationInfo();
//        authorizationInfo.addRole("admin");
        return new SimpleAuthorizationInfo();
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        StatelessToken statelessToken = (StatelessToken) token;
        Map<String, Object> user = apiService.getMerchantByAppid(statelessToken.getUsername());
		if(user == null || !user.get("appip").equals(statelessToken.getRequestIp())){
			throw new IncorrectCredentialsException("请求来源ip："+statelessToken.getRequestIp());
		}

        //在服务器端生成客户端参数消息摘要
        String serverDigest = BasicCoder.signHmacRequest(statelessToken.getParams(),user.get("appsecret").toString(),Hmac.HmacMD5);

       // System.out.println("客户端："+statelessToken.getClientDigest()+"服务器："+serverDigest+"比较"+serverDigest.equals(statelessToken.getClientDigest()));
        
        //然后进行客户端消息摘要和服务器端消息摘要的匹配
        return new SimpleAuthenticationInfo(
        		statelessToken.getUsername(),
                serverDigest,
                getName());
    }
    
  
     

}