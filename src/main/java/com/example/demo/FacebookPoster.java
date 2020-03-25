package com.example.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.multipart.MultipartFile;

import FBO.SMPost;
import FBO.StoredSMPost;

public class FacebookPoster {
	
	static private String appID;
	static private String secretID;
	private String accessToken;
	private HttpPost HttpPost = new HttpPost("https://httpbin.org/post");
	private CloudinaryUploader uploader;
	private static FileDao fileDao;
	
	public FacebookPoster(FileDao fileDao) {
		this.fileDao = fileDao;
		uploader = new CloudinaryUploader(fileDao);
	}
	
	public boolean post(SMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
        
        String url = post.getStoredPhotoURL();
        	urlParameters.add(new BasicNameValuePair("image", url));
        	urlParameters.add(new BasicNameValuePair("body", post.getBody()));
        try {
        	HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        	CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost);
             } catch (Exception e) {
		return false;
             }
        return true;
	}

	public boolean restore(StoredSMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
        
        String url = post.getImage();
        	urlParameters.add(new BasicNameValuePair("image", url));
        	urlParameters.add(new BasicNameValuePair("body", post.getBody()));
        try {
        	HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        	CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost);
             } catch (Exception e) {
		return false;
             }
        return true;
	}

	public void setAccessToken(String token) {
		accessToken = token;
		
	}
}
