package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import FBO.UploadedFile;

public class FileDao {
	@Autowired
	private DataSourceTransactionManager transactionManager;

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	public boolean uploadFile(MultipartFile imageFile, long owner) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "INSERT INTO file (owner, filename, contents, filetype) VALUES (?, ?, ?)";
		byte[] fileContents;
		try {
		fileContents = imageFile.getBytes();
		fileContents = EncryptionUtil.encrypt(fileContents);
		} catch (IOException i) {
			return false;
		}
		try {
			jdbcTemplate.update(strSQL, owner, fileContents, imageFile.getName(), imageFile.getContentType());
		} catch (DataAccessException e) {
			System.out.println("Error in creating account record, rolling back");
			transactionManager.rollback(status);
			return false;
		}
		return true;
	}
	
	public UploadedFile downloadFile(int id) {
		UploadedFile file = new UploadedFile();
		UploadedFileMapper mapper = new UploadedFileMapper();
		try {
			file = jdbcTemplate.queryForObject("select * from file where id = ?", new Object[] {id}, mapper);
		} catch(DataAccessException e) {
			return null;
		}
		
		return file;
	}
	
	public ArrayList<String> getFileNames(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "SELECT filename WHERE owner = ?";
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, new Object[] {owner});
		ArrayList<String> names = new ArrayList<String>();
		while(rs.next()) {
			names.add(rs.getString(1));
		}
		return names;
	}
}
