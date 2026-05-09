package com.project.expensetrackerapi.services;

import com.project.expensetrackerapi.domain.User;
import com.project.expensetrackerapi.exceptions.EtAuthException;

public interface UserService {
	  User validateUser(String email, String password) throws EtAuthException;

	    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;


}
