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

public class InstagramPoster {

	private HttpPost HttpPost;
	private CloudinaryUploader uploader;
	private static FileDao fileDao;
	
	private String accessToken;
	
	public InstagramPoster(FileDao fileDao) {
		this.fileDao = fileDao;
		uploader = new CloudinaryUploader(fileDao);
	}
	
	public void setAccessToken(String token) {
		accessToken = token;
		HttpPost = new HttpPost("https://graph.facebook.com/"+ accessToken + "/media");
	}
	public boolean post(SMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
       
        String url = post.getStoredPhotoURL();
        urlParameters.add(new BasicNameValuePair("image", url));
        

        try {
        	HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        	CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost);
             } catch (Exception e) {
		return false;
             }
        return true;
	}
	public void restore(StoredSMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
       
        String url = post.getImage();
        urlParameters.add(new BasicNameValuePair("image", url));
        

        try {
        	HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        	CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost);
             } catch (Exception e) {
		e.printStackTrace();
             }
		
	}
}

