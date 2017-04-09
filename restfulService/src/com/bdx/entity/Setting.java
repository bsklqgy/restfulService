package com.bdx.entity;

import java.io.Serializable;

public class Setting implements Serializable{
	
	private static final long serialVersionUID = -818902881140828477L;
	
	/**参数用户身份名*/
    public static final String PARAM_USERNAME = "appid";
    /**参数消息摘要名*/
	public static final String PARAM_DIGEST = "sign";
	/**参数时间戳名*/
	public static final String PARAM_TIMESTAMP = "timestamp";
	/**本次访问token有效期限(分钟)*/
	public static final int EXPIRED_MINUTE = 10;
	
	/** 日期格式配比 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd",
			"yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };
	
	/** 向日葵贷 */
	public static final String APP1ID = "bdx415e987a";
	
	/**投融家*/
	public static final String APP2ID = "bdx2m9cihs8";
	
	
}
