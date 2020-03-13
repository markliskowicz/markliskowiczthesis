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

public class InstagramPoster {

	private HttpPost HttpPost = new HttpPost("https://httpbin.org/post");
	
	public boolean post(SMPost post) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("caption", post.getBody()));
        ArrayList<File> images = post.getPhotos();
        for(int i = 0; i < images.size(); i++) {
        urlParameters.add(new BasicNameValuePair("image", images.get(i)));
        }
        HttpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(HttpPost)) {
		return true;
	}
}
