package com.bdx.utils;
import javax.servlet.http.HttpServletRequest;

public final class WebUtils {
	/**
	 * 获取客户端真实IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddrByRequest(HttpServletRequest request) {
		
		String ip = request.getHeader("x-forwarded-for");
		
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
			if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
				ip = request.getRemoteAddr();
			}
		}else {
			String[] ips = ip.split(",");
			if(ips.length > 1) {
			String tempIP = "";
				for(int i=0; i<ips.length; i++) {
					tempIP = ips[i] == null?"":ips[i].trim();
					if(((ip != null) && (ip.trim().length() > 0)&&!"null".equals(ip)) && ! "unknown".equalsIgnoreCase(tempIP)) {
						ip = tempIP;
						break;
					}
				}
			}
		}
		if("0:0:0:0:0:0:0:1".equals(ip)){
			ip = "127.0.0.1";
		}
		return ip;
		
	}

}