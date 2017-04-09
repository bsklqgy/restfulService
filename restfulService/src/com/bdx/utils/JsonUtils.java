package com.bdx.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

public final class JsonUtils
{
  private static ObjectMapper mapper = new ObjectMapper();
  
  public static String toJson(Object value)
  {
    try{
      return mapper.writeValueAsString(value);
    }catch (Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public static void toJson(HttpServletResponse response, String contentType, Object value)
  {
    Assert.notNull(response);
    Assert.hasText(contentType);
    try{
      response.setContentType(contentType);
      mapper.writeValue(response.getWriter(), value);
    }catch (Exception e){
      e.printStackTrace();
    }
  }
  
  public static void toJson(HttpServletResponse response, Object value)
  {
    Assert.notNull(response);
    PrintWriter pw = null;
    try{
      pw = response.getWriter();
      mapper.writeValue(pw, value);
      pw.flush();
    }catch (Exception e){
      e.printStackTrace();
    }finally
    {
      IOUtils.closeQuietly(pw);
    }
  }
  
  public static <T> T toObject(String json, Class<T> valueType)
  {
    Assert.hasText(json);
    Assert.notNull(valueType);
    try{
      return mapper.readValue(json, valueType);
    }catch (Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> T toObject(String json, TypeReference<?> typeReference)
  {
    Assert.hasText(json);
    Assert.notNull(typeReference);
    try
    {
      return mapper.readValue(json, typeReference);
    }catch (Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> T toObject(String json, JavaType javaType)
  {
    Assert.hasText(json);
    Assert.notNull(javaType);
    try{
      return mapper.readValue(json, javaType);
    }catch (Exception e){
      e.printStackTrace();
    }
    return null;
  }
}
