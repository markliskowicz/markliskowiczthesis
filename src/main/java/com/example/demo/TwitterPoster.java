package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import FBO.SMPost;
import FBO.StoredSMPost;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterPoster {
		
	private Twitter twitter;
	
	private final String CONSUMER_KEY = "CKV1t9z57bi559XQgdSGKemqC";
    private final String CONSUMER_KEY_SECRET = "nSVtX6UTqow8FiwYCmMli1oef32aYl8HAEuiWohavWK8v8b2BV"; 
    
    private RequestToken requestToken;
    private AccessToken accessToken;
    private CloudinaryUploader uploader;
	private static FileDao fileDao;
	
	public TwitterPoster(FileDao fileDao) {
		twitter = new TwitterFactory().getInstance();
		this.fileDao = fileDao;
		uploader = new CloudinaryUploader(fileDao);
	}
	
	public String getAuthenticationURL() {
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		if(requestToken == null) {
			return "";
		}
		return requestToken.getAuthorizationURL();
	}
	
	public void getAccessTokenFromPIN(String pin) {
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	public void setAccessToken(String token, String secret) {
		accessToken = new AccessToken(token, secret);
		twitter.setOAuthAccessToken(accessToken);
	}
	
	public boolean post(SMPost post) {
		String body = post.getBody();
		String url = post.getStoredPhotoURL();
			body = body + " " + url;
		StatusUpdate update = new StatusUpdate(body);
		try {
			Status status = twitter.updateStatus(update);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void restore(StoredSMPost post) {
		String body = post.getBody();
		String url = post.getPhotos();
			body = body + " " + url;
		StatusUpdate update = new StatusUpdate(body);
		try {
			Status status = twitter.updateStatus(update);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
