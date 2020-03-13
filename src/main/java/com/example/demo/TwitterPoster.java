package com.example.demo;

import java.io.File;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import FBO.SMPost;
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
	
	private final String CONSUMER_KEY = "";
    private final String CONSUMER_KEY_SECRET = ""; 
    
    private RequestToken requestToken;
    private AccessToken accessToken;
	
	public TwitterPoster() {
		twitter = new TwitterFactory().getInstance();
	}
	
	public String getAuthenticationURL() {
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestToken.getAuthorizationURL();
	}
	
	public void getAccessTokenFromPIN(String pin) {
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean post(SMPost post) {
		ArrayList<File> images = post.getPhotos();
		long[] mediaIds = new long[images.size()];
		for(int i = 0; i < images.size(); i++) {
			try {
				UploadedMedia media = twitter.uploadMedia(images.get(i));
				mediaIds[i] = media.getMediaId();
			} catch (TwitterException e) {
				e.printStackTrace();
				return false;
			}
		}
		StatusUpdate update = new StatusUpdate(post.getBody());
        update.setMediaIds(mediaIds);
        try {
			Status status = twitter.updateStatus(update);
		} catch (TwitterException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
