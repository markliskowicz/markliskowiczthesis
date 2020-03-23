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
	public void addPost(SMPost post) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		try {
			String strSQL = "INSERT INTO SMPost (owner, body, website) VALUES (?, ?, ?)";
			jdbcTemplate.update(strSQL, post.getOwner(), post.getBody(), post.getWebsite());
			transactionManager.commit(status);
			
		} catch (DataAccessException e) {
			System.out.println("Error in creating account record, rolling back");
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	@Override
	public StoredSMPost getPostbyID(int id) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select * FROM SMPOST INNER JOIN filefrompost ON postID = id (INNER JOIN file ON file.id = fileID) where SMPOST.id = ?";
		ArrayList<SMPost> posts = new ArrayList<SMPost>();
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, id);
		int postID = -1;
		String body = "";
		//File image;
		ArrayList<String> images = new ArrayList<String>();
		while(rs.next()) {
			if(postID != rs.getInt("SMPOST.id")) {
			postID = rs.getInt("SMPOST.id");
			body = rs.getString("body");
			} 
			String imageUrl = rs.getString("hosturl");
			images.add(imageUrl);
//			File contents = new File(rs.getString("fileName") + "." + rs.getString("fileType"));
//			try {
//			contents.createNewFile();
//			OutputStream  os = new FileOutputStream(contents); 
//			os.write(EncryptionUtil.decrypt(rs.getString("contents").getBytes())); 
//			os.close();
//			} catch (IOException i) {
//				return null;
//			}
//			images.add(contents);
		}
		StoredSMPost post = new StoredSMPost();
		post.setPhotos(images);
		post.setBody(body);
		return post;
	}
	
	@Override
	public ArrayList<String> getPostIDs(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select ID from SMPost where owner = ?";
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, owner);
		ArrayList<String> IDs = new ArrayList<String>();
		while(rs.next()) {
			IDs.add("" + rs.getInt("ID"));
		}
		return IDs;
	}
	
	@Override
	public ArrayList<StoredSMPost> getAll(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "Select * FROM SMPOST INNER JOIN filefrompost ON postID = id (INNER JOIN file ON file.id = fileID) where SMPOST.owner = ?";
		ArrayList<StoredSMPost> posts = new ArrayList<StoredSMPost>();
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, owner);
		int postID = -1;
		String body = "";
		//File image;
		ArrayList<String> images = new ArrayList<String>();
		while(rs.next()) {
		while(rs.next() && postID != rs.getInt("SMPOST.id")) {
			if(postID != rs.getInt("SMPOST.id")) {
			postID = rs.getInt("SMPOST.id");
			body = rs.getString("body");
			} 
			String imageurl = rs.getString("hosturl");
//			File contents = new File(rs.getString("fileName") + "." + rs.getString("fileType"));
//			try {
//			contents.createNewFile();
//			OutputStream  os = new FileOutputStream(contents); 
//			os.write(EncryptionUtil.decrypt(rs.getString("contents").getBytes())); 
//			os.close();
//			} catch (IOException i) {
//				return null;
//			}
			images.add(imageurl);
		}
		StoredSMPost post = new StoredSMPost();
		post.setPhotos(images);
		post.setBody(body);
		posts.add(post);
		}
		return posts;
	}
}
