package com.appleye.crawler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

public class HarassingPhoneNumberCrawler {
	
	public static void main(String[] args) {
		//创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
        
        File destFile = new File("harassingPhoneNumber.txt");
        
        for(int i=1; i<=6844; i++) {
        	HttpGet httpGet = new HttpGet("http://www.lajidianhua.com/list-8-171091-" + i + ".html");
   		 
   		 try {  
               //执行get请求  
               HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
               //获取响应消息实体  
               HttpEntity entity = httpResponse.getEntity();  
               //判断响应实体是否为空  
               if (entity != null) {   
               	extractPhoneNumberLink(EntityUtils.toString(entity, "utf-8"), destFile);  
               }  
           } catch (IOException e) {  
               e.printStackTrace();  
           } 
        }
	}
	
	public static void extractPhoneNumberLink(String inputHtml, File destFile) {
		Parser parser = Parser.createParser(inputHtml, "utf-8");
		
		try{
			NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
				
				public boolean accept(Node node) {
					if(node instanceof LinkTag) {
						return true;
					}
					return false;
				}
			});
			
			int size  = nodeList.size();
			
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<size; i++) {
				LinkTag linkTag = (LinkTag)nodeList.elementAt(i);
				String link = linkTag.extractLink();
				if(link.indexOf("/view/") !=-1) {
					String linkText = linkTag.getLinkText();
					if (linkText.matches("[\\d|-]{7,}")) {
						if (destFile != null) {
							sb.append(linkTag.getLinkText() + "\r\n");
						}
					}
				}
			}
			
			FileUtils.writeStringToFile(destFile, sb.toString(), "utf-8", true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
