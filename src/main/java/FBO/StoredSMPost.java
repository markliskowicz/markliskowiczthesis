package FBO;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class StoredSMPost {
	private long owner;
	private String body;
	private String website;
	private String image;
	

	private boolean postToTwitter = false;
	private boolean postToFacebook = false;
	private boolean postToInstagram= false;
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
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
	public String getPhotos() {
		return image;
	}
	public void setPhotos(String photos) {
		this.image = photos;
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
}
