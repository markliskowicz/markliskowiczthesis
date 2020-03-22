package com.example.demo;
import java.io.File;
import java.util.Map;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUploader {
	private static Cloudinary cloudinary;
	
	public CloudinaryUploader() {
		if(cloudinary == null) {
			cloudinary = new Cloudinary(ObjectUtils.asMap(
					  "dau5nr3mn", "my_cloud_name",
					  "873748161588662", "my_api_key",
					  "_5TSC47tD758F_rZ5rwt0031pqo", "my_api_secret"));
		}
	}
	
	public String upload(File file) {
		Map uploadResult;
		try {
		uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
		} catch (Exception e) {
			return "";
		}
		return (String) uploadResult.get("secure_url");
	}
}
