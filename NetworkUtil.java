package com.kmlinkd.joinyou.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class NetworkUtil {
	/** 
     * Logger for this class 
     */  
    private static Logger logger = (Logger) LoggerFactory.getLogger(NetworkUtil.class);  
  
    /** 
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址; 
     *  
     * @param request 
     * @return 
     * @throws IOException 
     */  
    public final static String getIpAddress(HttpServletRequest request) throws IOException {  
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  
  
        String ip = request.getHeader("X-Forwarded-For");  
        if (logger.isInfoEnabled()) {  
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);  
        }  
  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("Proxy-Client-IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getRemoteAddr(); 
                if (ip.equals("0:0:0:0:0:0:0:1"))
                	ip = "127.0.0.1";
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);  
                }  
            }  
        } else if (ip.length() > 15) {  
            String[] ips = ip.split(",");  
            for (int index = 0; index < ips.length; index++) {  
                String strIp = (String) ips[index];  
                if (!("unknown".equalsIgnoreCase(strIp))) {  
                    ip = strIp;  
                    break;  
                }  
            }  
        }  
        return ip;  
    }  
    public static String POST_RAS(String url, Map<String, String> p)
            throws Exception
    {
        List nvps = new ArrayList();
        Iterator iter = p.entrySet().iterator();
        String result = "";
        String params = "";
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            nvps.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
            if (index != 0) {
                params = params + "&";
            }
            params = params + (String)entry.getKey() + "=" + (String)entry.getValue();
            index++;
        }

        if (params.length() > 0) {
            url = url + "?" + params;
        }
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        UrlEncodedFormEntity ue = new UrlEncodedFormEntity(nvps, "UTF-8");//设置post请求的参数body
        post.setEntity(ue);
        result = sendPost(client,post);
        return result;
    }

    public static String sendPost(HttpClient client, HttpPost post){
        String result = "";
        try {
            HttpResponse res = client.execute(post);
            if (!res.getStatusLine().toString().toLowerCase().contains("ok")) {
                throw new Exception("ULR请求错误：" + res.getStatusLine());
            }
            HttpEntity entity = res.getEntity();
            result = EntityUtils.toString(entity, "utf8");
            EntityUtils.consume(entity);//关闭httpentity的流
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            post.releaseConnection();
        }
        return  result;
    }
}
