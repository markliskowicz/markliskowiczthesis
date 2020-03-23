package com.example.demo;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import FBO.SMPost;
import FBO.StoredSMPost;

@Repository
public interface SMPostDao {

	void addPost(SMPost post);

	StoredSMPost getPostbyID(int id);

	ArrayList<String> getPostIDs(int owner);

	ArrayList<StoredSMPost> getAll(int owner);

}
