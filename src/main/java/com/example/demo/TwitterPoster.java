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
	
	private final String CONSUMER_KEY = "";
    private final String CONSUMER_KEY_SECRET = ""; 
    
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
		return requestToken.getAuthorizationURL();
	}
	
	public void getAccessTokenFromPIN(String pin) {
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	public boolean post(SMPost post) {
		ArrayList<MultipartFile> images = post.getPhotos();
		long[] mediaIds = new long[images.size()];
		for(int i = 0; i < images.size(); i++) {
			uploader.saveFile(images.get(i), (int)post.getOwner(), fileDao);
			try {
				File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+images.get(i).getOriginalFilename());
				try {
				images.get(i).transferTo(convFile);
				} catch (IOException io) {
					return false;
				}
				UploadedMedia media = twitter.uploadMedia(convFile);
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

	public void restore(StoredSMPost post) {
		String body = post.getBody();
		ArrayList<String> urls = post.getPhotos();
		for(int i = 0; i < urls.size(); i++) {
			body = body + " " + urls.get(i);
		}
		StatusUpdate update = new StatusUpdate(body);
		try {
			Status status = twitter.updateStatus(update);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
