package com.bdx.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpURLConnectionUtils {
	
	private static final Logger logger = LogManager.getLogger(HttpURLConnectionUtils.class);
	
	public static String get(String targetURL){
		try {
			URL targetUrl = new URL(targetURL);
	
			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("Accept", "application/json");

			if (httpConnection.getResponseCode() != 200) {
				logger.error("url:"+targetURL+" HTTP error code:"+httpConnection.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "+ httpConnection.getResponseCode());
			}
	 
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
	 
			StringBuffer buffer = new StringBuffer();
			String output;
			while ((output = responseBuffer.readLine()) != null) {
				buffer.append(output);
			}
			//System.out.println("Output from Server:"+buffer.toString());
			httpConnection.disconnect();
			return buffer.toString();
		}catch (MalformedURLException me) {
		  	me.printStackTrace();
		  	logger.error("url:"+targetURL+" error:"+me.getMessage());
		}catch (IOException e) {
		  	e.printStackTrace();
		  	logger.error("url:"+targetURL+" error:"+e.getMessage());
		}
		return null;
	}  
	
	
	public static String post(String targetURL,String input){
		try {
			URL targetUrl = new URL(targetURL);
	
			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "application/json");
	
			if(StringUtils.isNotEmpty(input)){
				OutputStream outputStream = httpConnection.getOutputStream();
				outputStream.write(input.getBytes());
				outputStream.flush();
			}

			if (httpConnection.getResponseCode() != 200) {
				logger.error("url:"+targetURL+" HTTP error code:"+httpConnection.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "+ httpConnection.getResponseCode());
			}
	 
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
	 
			StringBuffer buffer = new StringBuffer();
			String output;
			while ((output = responseBuffer.readLine()) != null) {
				buffer.append(output);
			}
			//System.out.println("Output from Server:"+buffer.toString());
			httpConnection.disconnect();
			return buffer.toString();
		}catch (MalformedURLException me) {
		  	me.printStackTrace();
		  	logger.error("url:"+targetURL+" error:"+me.getMessage());
		}catch (IOException e) {
		  	e.printStackTrace();
		  	logger.error("url:"+targetURL+" error:"+e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		get("http://127.0.0.1:8888/x/api/invest/success?amount=100&appid=bdx415e987a&bid=1079&iid=10000&time=1461993570&timestamp=1461993570&uid=43407&sign=BADD6918BC709188D5095A772E379CEF");

	}
}
