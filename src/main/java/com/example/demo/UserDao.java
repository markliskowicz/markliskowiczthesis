package com.example.demo;

import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

	int getID(String owner);

}
