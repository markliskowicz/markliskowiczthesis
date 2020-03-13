package FBO;

import java.io.File;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class SMPost {
	private long owner;
	private String body;
	private String website;
	private ArrayList<File> photos;
	
	private boolean postToTwitter;
	private boolean postToFacebook;
	private boolean postToInstagram;
	
	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public ArrayList<File> getPhotos() {
		return photos;
	}
	public void setPhotos(ArrayList<File> photos) {
		this.photos = photos;
	}
	public boolean isPostToTwitter() {
		return postToTwitter;
	}
	public void setPostToTwitter(boolean postToTwitter) {
		this.postToTwitter = postToTwitter;
	}
	public boolean isPostToFacebook() {
		return postToFacebook;
	}
	public void setPostToFacebook(boolean postToFacebook) {
		this.postToFacebook = postToFacebook;
	}
	public boolean isPostToInstagram() {
		return postToInstagram;
	}
	public void setPostToInstagram(boolean postToInstagram) {
		this.postToInstagram = postToInstagram;
	}
	
	public void addFile(File file) {
		photos.add(file);
	}
}
