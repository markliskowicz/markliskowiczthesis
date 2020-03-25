package com.example.demo;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import FBO.FilenameURLPair;
import FBO.UploadedFile;

public interface FileDao {

	UploadedFile downloadFile(int id);

	ArrayList<FilenameURLPair> getFileNamesAndURLs(int owner);

	boolean uploadFile(String url, String filename, long owner);

	int getImageIDFromURL(String storedPhotoURL);

}
