package com.example.demo;



import javax.validation.Valid;

public interface AccountDao {
	public @Valid void createAccount(@Valid Account account);	
}