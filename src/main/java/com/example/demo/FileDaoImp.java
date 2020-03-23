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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import FBO.UploadedFile;

@Repository
public class FileDaoImp implements FileDao{
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
	public boolean uploadFile(String url, String filename, long owner) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "INSERT INTO uploadedfile (owner, filename, url) VALUES (?, ?, ?)";
		byte[] fileContents;
		//try {
			jdbcTemplate.update(strSQL, owner, filename, url);
			transactionManager.commit(status);
//		} catch (DataAccessException e) {
//			System.out.println("Error in creating file record, rolling back");
//			transactionManager.rollback(status);
//			return false;
//		}
		return true;
	}
	
	@Override
	public UploadedFile downloadFile(int id) {
		UploadedFile file = new UploadedFile();
		UploadedFileMapper mapper = new UploadedFileMapper();
		try {
			file = jdbcTemplate.queryForObject("select * from uploadedfile where id = ?", new Object[] {id}, mapper);
		} catch(DataAccessException e) {
			return null;
		}
		
		return file;
	}
	
	@Override
	public ArrayList<String> getFileNames(int owner){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		String strSQL = "SELECT filename FROM uploadedfile WHERE owner = ?";
		SqlRowSet rs = jdbcTemplate.queryForRowSet(strSQL, new Object[] {owner});
		ArrayList<String> names = new ArrayList<String>();
		while(rs.next()) {
			names.add(rs.getString(1));
		}
		return names;
	}
}
