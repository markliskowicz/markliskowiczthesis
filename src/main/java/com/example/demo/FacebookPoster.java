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

import FBO.SMPost;

public class FacebookPoster {
	
	static String appID;
	static String secretID;

	private HttpPost HttpPost = new HttpPost("https://httpbin.org/post");
	private CloudinaryUploader uploader = new CloudinaryUploader();
	
	public boolean post(SMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
        ArrayList<File> images = post.getPhotos();
        String url = "";
        for(int i = 0; i < images.size(); i++) {
        	url = uploader.upload(images.get(i));
        	urlParameters.add(new BasicNameValuePair("image", url));
        }
        

        try {
        	HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        	CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost);
             } catch (Exception e) {
		return false;
             }
        return true;
	}
}
