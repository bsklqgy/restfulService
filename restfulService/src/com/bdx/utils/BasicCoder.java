package com.bdx.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密基类
 * 
 */
public class BasicCoder
{
   /**
    * MD5(Message Digest algorithm 5，信息摘要算法)
    * 
    * @param data
    *           加密数据
    * @return
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    */
   public byte[] makeMD5(byte[] data) throws NoSuchAlgorithmException
   {
      MessageDigest mdTemp = MessageDigest.getInstance("MD5");
      mdTemp.update(data);
      return mdTemp.digest();
   }

   /**
    * SHA(Secure Hash Algorithm，安全散列算法)
    * 
    * @param data
    *           加密数据
    * @return
    * @throws NoSuchAlgorithmException
    * @throws Exception
    */
   public byte[] makeSHA(byte[] data) throws NoSuchAlgorithmException
   {
      MessageDigest sha = MessageDigest.getInstance("SHA");
      sha.update(data);
      return sha.digest();
   }

   /**
    * HmacSHA1
    * 
    * @param data
    *           加密数据
    * @param key
    *           密钥
    * @return
    * @see Hmac
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeyException
    */
   public byte[] makeHmacSHA1(byte[] data, byte[] key)
         throws NoSuchAlgorithmException, InvalidKeyException
   {
      return makeHmac(Hmac.HmacSHA1, data, key);
   }

   /**
    * HmacMD5
    * 
    * @param data
    *           加密数据
    * @param key
    *           密钥
    * @return
    * @see Hmac
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeyException
    */
   public byte[] makeHmacMD5(byte[] data, byte[] key)
         throws NoSuchAlgorithmException, InvalidKeyException
   {
      return makeHmac(Hmac.HmacMD5, data, key);
   }

   /**
    * Hmac
    * 
    * @param hmac
    *           加密类型
    * @param data
    *           加密数据
    * @param key
    *           密钥
    * @return
    * @see Hmac
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeyException
    */
   public static byte[] makeHmac(Hmac hmac, byte[] data, byte[] key)
         throws NoSuchAlgorithmException, InvalidKeyException
   {
      Mac mac = Mac.getInstance(hmac.toString());
      SecretKeySpec spec = new SecretKeySpec(key, hmac.toString());
      mac.init(spec);
      return mac.doFinal(data);
   }

   /**
    * CRC32，循环冗余校验
    * 
    * @param data
    *           校验数据
    * @return
    */
   public long makeCRC32(byte[] data)
   {
      CRC32 crc32 = new CRC32();
      crc32.update(data);
      return crc32.getValue();
   }

   /**
    * Hmac(Hash Message Authentication Code，散列消息鉴别码)
    */
   public enum Hmac
   {
      HmacMD5, HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512
   }

   /**
    * 初始化HMAC密钥
    * 
    * @return
    * @see Hmac
    * @throws Exception
    */
   public byte[] initMacKey(Hmac hmac) throws Exception
   {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(hmac.toString());
      SecretKey secretKey = keyGenerator.generateKey();
      return secretKey.getEncoded();
   }

   private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6',
         '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

   /**
    * 将字节流按16进制转换成字符串.
    */
   public static String byte2String(byte[] buf)
   {
      int count = buf.length;
      StringBuffer sbuf = new StringBuffer();
      for (int i = 0; i < count; i++)
      {
         byte byte0 = buf[i];
         sbuf.append(hexDigits[byte0 >>> 4 & 0xf]).append(
               hexDigits[byte0 & 0xf]);
      }
      return sbuf.toString().toUpperCase();
   }
   
   @SuppressWarnings("unchecked")
   public static String signHmacRequest(Map<String, ?> params, String secret,Hmac hmac){
       //第一步：检查参数是否已经排序
       String[] keys = params.keySet().toArray(new String[0]);
       Arrays.sort(keys);
       //第二步：把所有参数名和参数值串在一起
       StringBuilder query = new StringBuilder();
       for (String key : keys) {
           Object values = params.get(key);
           if(values instanceof String[]) {
        	   query.append(key);
               for(String value : (String[])values) {
                   query.append(value);
               }
           } else if(values instanceof List) {
        	   query.append(key);
               for(String value : (List<String>)values) {
            	   query.append(value);
               }
           } else {
        	   query.append(key).append(values);
           }
       }
       //System.out.println(query.toString());
       //System.out.println(secret);
       //第三步：使用HmacMD5加密
       byte[] bytes;
		try {
			bytes = makeHmac(hmac, query.toString().getBytes("utf-8"),secret.getBytes("utf-8"));
	        //第四步：把二进制转化为大写的十六进制
	        return BasicCoder.byte2String(bytes);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
       return null;
   }
}