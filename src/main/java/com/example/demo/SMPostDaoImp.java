package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import FBO.IDBodyPair;
import FBO.SMPost;
import FBO.StoredSMPost;

@Repository
public class SMPostDaoImp implements SMPostDao {
	@Autowired
	private DataSourceTransactionManager transactionManager;

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	@Override
	public void addPost(SMPost post, FileDao fileDao) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		int imageID = fileDao.getImageIDFromURL(post.getStoredPhotoURL());
		try {
			String strSQL = "INSERT INTO SMPost (owner, body, website, image) VALUES (?, ?, ?, ?)";
			jdbcTemplate.update(strSQL, post.getOwner(), post.getBody(), post.getWebsite(), imageID);
			transactionManager.commit(status);
			
		} catch (DataAccessException e) {
			System.out.println("Error in creating account record, rolling back");
			transactionManager.rollback(status);
			e.printStackTrace();
		}
	}
	
	@Override
	public StoredSMPost getPostbyID(int id) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select * FROM SMPOST INNER JOIN uploadedfile ON smpost.image = uploadedfile.id where SMPOST.id = ?";
		ArrayList<SMPost> posts = new ArrayList<SMPost>();
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, id);
		int postID = -1;
		String body = "";
		String url = "";
		String website = "";
		while(rs.next()) {
			//postID = rs.getInt("SMPOST.id");
			body = rs.getString("body");
			url = rs.getString("url");
			website = rs.getString("website");
		}
		System.out.println(website + " url: " + url);
		StoredSMPost post = new StoredSMPost();
		if(!website.equals("") && website != null) {
		if(website.charAt(0) == '1') {
			post.setPostToTwitter(true);
		}
		if(website.charAt(1)== '1') {
			post.setPostToInstagram(true);
		}
		if(website.charAt(2) == '1') {
			post.setPostToFacebook(true);
		}
		}
		post.setWebsite(website);
		post.setPhotos(url);
		post.setBody(body);
		return post;
	}
	
	@Override
	public ArrayList<IDBodyPair> getPostIDs(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select ID, body from SMPost where owner = ?";
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, owner);
		ArrayList<IDBodyPair> pairs = new ArrayList<IDBodyPair>();
		while(rs.next()) {
			IDBodyPair pair = new IDBodyPair();
			pair.setID("" + rs.getInt("ID"));
			pair.setBody(rs.getString("body"));
			pairs.add(pair);
		}
		return pairs;
	}
	
	@Override
	public ArrayList<StoredSMPost> getAll(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select * FROM SMPOST INNER JOIN uploadedfile ON smpost.image = uploadedfile.id where SMPOST.owner = ?";
		ArrayList<StoredSMPost> posts = new ArrayList<StoredSMPost>();
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, owner);
		int postID = 0;
		String body = "";
		String images = "";
		String website = "";
		while(rs.next()) {
			body = rs.getString("body");
			images = rs.getString("url");
			website = rs.getString("website");
			StoredSMPost post = new StoredSMPost();
			if(website.charAt(0) == '1') {
				post.setPostToTwitter(true);
			}
			if(website.charAt(1)== '1') {
				post.setPostToInstagram(true);
			}
			if(website.charAt(2) == '1') {
				post.setPostToFacebook(true);
			}
			post.setPhotos(images);
			post.setBody(body);
			posts.add(post);
		}
		return posts;
	}
}
