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
					"cloud_name", "dau5nr3mn",
					  "api_key", "873748161588662",
					  "api_secret", "_5TSC47tD758F_rZ5rwt0031pqo"));
		}
		if(fileDao == null) {
			fileDao = dao;
		}
	}

	public String saveFile(MultipartFile file, int owner, FileDao fileDao) {
		Map uploadResult;
		try {
			File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
			try {
			file.transferTo(convFile);
			} catch (IOException io) {
				io.printStackTrace();
				return null;
			}
		uploadResult = cloudinary.uploader().upload(convFile, ObjectUtils.emptyMap());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String url = (String) uploadResult.get("secure_url");
		fileDao.uploadFile(url, file.getOriginalFilename(), owner);
		return url;
	}
		
}