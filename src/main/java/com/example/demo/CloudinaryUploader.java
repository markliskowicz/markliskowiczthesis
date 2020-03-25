package com.example.demo;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUploader {
	private static Cloudinary cloudinary;
	
	private static FileDao fileDao;
	
	public CloudinaryUploader(FileDao dao) {
		if(cloudinary == null) {
			cloudinary = new Cloudinary(ObjectUtils.asMap(
					  "dau5nr3mn", "dau5nr3mn",
					  "873748161588662", "873748161588662",
					  "_5TSC47tD758F_rZ5rwt0031pqo", "_5TSC47tD758F_rZ5rwt0031pqo"));
		}
		if(fileDao == null) {
			fileDao = dao;
		}
	}

	public String saveFile(MultipartFile file, int owner, FileDao fileDao) {
		Map uploadResult;
		try {
		uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String url = (String) uploadResult.get("secure_url");
		fileDao.uploadFile(url, file.getOriginalFilename(), owner);
		return url;
	}
		
	}
