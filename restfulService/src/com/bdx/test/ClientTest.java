package com.bdx.test;

import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith; 
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.bdx.entity.Setting;
import com.bdx.service.ApiService;
import com.bdx.utils.BasicCoder;
import com.bdx.utils.BasicCoder.Hmac;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/applicationContext.xml"})
//@Transactional
public class ClientTest{
	
	@Resource
	private ApiService apiService;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	 @Test
	 public void test(){

		 	String username = "bdx415e987a";
	        String appsecret = "8deb7199-7545-41f1-94c4-4cfb57a2ebac";
	        
	        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
	        params.add(Setting.PARAM_USERNAME, username);
	        params.add("uid", "43407");
	        params.add("bid", "1125");
	        params.add("iid", "11373");
	        params.add("amount","100");
	        params.add(Setting.PARAM_TIMESTAMP,"1463989201159");//new Date().getTime()
	        params.add("time","1463989201159");
	        System.out.println(new Date().getTime());
	        

	        String sign = BasicCoder.signHmacRequest(params,appsecret,Hmac.HmacMD5);
	        params.add(Setting.PARAM_DIGEST, sign);

	        
	        String url = UriComponentsBuilder
	                .fromHttpUrl("http://127.0.0.1/x/api/invest/success")
	                .queryParams(params).build().toUriString();
	        System.out.println(url);
	        @SuppressWarnings("rawtypes")
			ResponseEntity responseEntity = restTemplate.getForEntity(url, String.class);
	        System.out.println(responseEntity.getBody());

	 }
	 
	 
	 @Test
	 public void test2(){

		 	String username = "bdx415e987a";
	        String appsecret = "8deb7199-7545-41f1-94c4-4cfb57a2ebac";
	        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
	        params.add(Setting.PARAM_USERNAME, username);
	        params.add("bid", "1079");
	        params.add("t", "2");
	        params.add("t", "2");
	        params.add(Setting.PARAM_TIMESTAMP,"1462268374755");
	        

	        String sign = BasicCoder.signHmacRequest(params,appsecret,Hmac.HmacMD5);
	        params.add(Setting.PARAM_DIGEST, sign);

	        
	        String url = UriComponentsBuilder
	                .fromHttpUrl("http://127.0.0.1/x/api/member/match")
	                .queryParams(params).build().toUriString();
	        System.out.println(url);
	        @SuppressWarnings("rawtypes")
			ResponseEntity responseEntity = restTemplate.getForEntity(url, String.class);
	        System.out.println(responseEntity.getBody());

	 }
	 
	 @Test
	 public void test3(){

		 apiService.fetchBids();

	 }
	 
	 @Test
	 public void test4(){

		 	String username = "bdx415e987a";
	        String appsecret = "8deb7199-7545-41f1-94c4-4cfb57a2ebac";
	        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
	        params.add(Setting.PARAM_USERNAME, username);
	        params.add("bid", "1079");
	        params.add("uid", "43407");
	        params.add("t", "2");
	        params.add(Setting.PARAM_TIMESTAMP,"1463735838100");
	        System.out.println(new Date().getTime());

	        String sign = BasicCoder.signHmacRequest(params,appsecret,Hmac.HmacMD5);
	        params.add(Setting.PARAM_DIGEST, sign);

	        
	        String url = UriComponentsBuilder
	                .fromHttpUrl("http://127.0.0.1/x/api/member/jump/query")
	                .queryParams(params).build().toUriString();
	        System.out.println(url);
	        @SuppressWarnings("rawtypes")
			ResponseEntity responseEntity = restTemplate.getForEntity(url, String.class);
	        System.out.println(responseEntity.getBody());

	 }
}