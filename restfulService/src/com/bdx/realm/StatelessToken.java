package com.bdx.realm;

import java.util.Date;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationToken;

public class StatelessToken implements AuthenticationToken {

	private static final long serialVersionUID = 4565949213211993968L;
	
	private String username;
    private String clientDigest;
	private Date timestamp;
	private Map<String, String[]> params;  
	private String requestIp;
	
    public StatelessToken(String username, String clientDigest,String requestIp,Map<String, String[]> params,Date timestamp) {
        this.username = username;
        this.clientDigest = clientDigest;
        this.requestIp = requestIp;
        this.params = params;
        this.timestamp =timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getClientDigest() {
        return clientDigest;
    }

    public void setClientDigest(String clientDigest) {
        this.clientDigest = clientDigest;
    }

    public Map<String, String[]> getParams() {
		return params;
	}

	public void setParams(Map<String, String[]> params) {
		this.params = params;
	}
	
	

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	@Override
    public Object getPrincipal() {
       return username;
    }

    @Override
    public Object getCredentials() {
        return clientDigest;
    }
}