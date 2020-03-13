package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import FBO.UploadedFile;

public class UploadedFileMapper implements RowMapper<UploadedFile>{

	@Override
	public UploadedFile mapRow(ResultSet rs, int rowNum) throws SQLException {
		UploadedFile downloadedFile = new UploadedFile();
		File contents = new File(rs.getString("fileName") + "." + rs.getString("fileType"));
		try {
		contents.createNewFile();
		OutputStream  os = new FileOutputStream(contents); 
		os.write(EncryptionUtil.decrypt(rs.getBytes("contents"))); 
		os.close();
		} catch (IOException i) {
			return null;
		}
		downloadedFile.setContents(contents);
		downloadedFile.setOwner(rs.getLong("owner"));
		return downloadedFile;
	}	
}
