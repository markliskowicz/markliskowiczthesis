package com.example.demo;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import FBO.IDBodyPair;
import FBO.SMPost;
import FBO.StoredSMPost;

@Repository
public interface SMPostDao {

	void addPost(SMPost post, FileDao fileDao);

	StoredSMPost getPostbyID(int id);

	ArrayList<IDBodyPair> getPostIDs(int owner);

	ArrayList<StoredSMPost> getAll(int owner);

}
