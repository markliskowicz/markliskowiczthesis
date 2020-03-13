package FBO;

import java.io.File;

public class UploadedFile {
	private long owner;
	private File contents;
	
	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	public File getContents() {
		return contents;
	}
	public void setContents(File contents) {
		this.contents = contents;
	}	
}