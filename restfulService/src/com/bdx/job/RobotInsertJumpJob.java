package com.bdx.job;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.bdx.utils.DateUtils;

@Component
public class RobotInsertJumpJob {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	 @Scheduled(cron="0 0/30 8-19 * * ?")
	 public void doTask() throws ParseException{
		 List<Map<String,Object>> list = jdbcTemplate.queryForList("select t.id from bid t where t.status='启用' ");
		 Random r = new Random();
		 for(Map<String,Object> a:list){
			 if(!r.nextBoolean())continue;
			 Long c = jdbcTemplate.queryForObject("select count(distinct(a.mobile)) as c from bid_jump_robot a where a.b_id=? ", new Object[]{a.get("id")}, Long.class); 
			 if(c<150){
				 Date now = new Date();
				 DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 Date randomDate = randomDate(fmt.parse(DateUtils.formatDate(now)+" 08:00:00"), now);  
				 jdbcTemplate.update("insert into bid_jump_robot(b_id,mobile,create_date) values (?,?,?) ",new Object[]{a.get("id"),getTel(),randomDate});
			 }
		 }
		 

	 }

	 private static String[] telFirst="134、135、136、137、138、139、147、150、151、152、157、158、159、182、187、188、130、131、132、155、156、185、186、133、153、180、189".split("、");  
	    private static String getTel() {  
	        int index=getNum(0,telFirst.length-1);  
	        String first=telFirst[index];  
	        String second=String.valueOf(getNum(1,888)+10000).substring(1);  
	        String third=String.valueOf(getNum(1,9100)+10000).substring(1);  
	        return first+second+third;  
	    }
	    public static int getNum(int start,int end) {  
	        return (int)(Math.random()*(end-start+1)+start);  
	    } 	    

	    private static Date randomDate(Date start, Date end) {  
	        try {  
	            if (start.getTime() >= end.getTime()) {  
	                return null;  
	            }  
	            long date = random(start.getTime(), end.getTime());  
	            return new Date(date);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	  
	    private static long random(long begin, long end) {  
	        long rtn = begin + (long) (Math.random() * (end - begin));  
	        if (rtn == begin || rtn == end) {  
	            return random(begin, end);  
	        }  
	        return rtn;  
	    }  
	    
	    
	    public static void main(String[] args) throws ParseException {
	    	Random r = new Random();
			for(int i=0;i<100;i++){
				 boolean b = r.nextBoolean();
				 System.out.println("b : " + b);
			}
		}
}
