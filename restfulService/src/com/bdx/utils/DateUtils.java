package com.bdx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils {
	
	public static final long ONE_DAY_MILLS = 3600000 * 24; 
	
	/**
	 * 格式化时间
	 * @param time
	 * @return
	 */
	public static String formatTime(Date time) {
		if(time == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(time);
	}
	
	/**
	 * 格式化日期
	 * @return
	 */
	public static String formatDate(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	/**
	 * 格式化时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String formatTime(Date time, String format) {
		if(time == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
	
	public static int getDaysBetween(Date startDate, Date endDate) {  
        return (int) ((endDate.getTime() - startDate.getTime()) / ONE_DAY_MILLS);  
    } 

}
