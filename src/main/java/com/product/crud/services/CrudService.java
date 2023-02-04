package com.product.crud.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import com.product.crud.DTO.UserDTO;
import com.product.crud.Exception.DataNotFoundExeception;
import com.product.crud.Exception.UserAuthorizationException;

import com.product.crud.errors.RegistrationStatus;

import com.product.crud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.product.crud.repo.CrudRepo;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Service
@Transactional
public class CrudService implements UserDetailsService {
	private static List<User> users = new ArrayList<>();
	@Autowired
	private CrudRepo repo;
	private PasswordEncoder passwordEncoder;


	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	public UserDTO saveUser(User user) {
		User savedUser = new User();
		user.setPassword(encodePassword(user.getPassword()));
		System.out.println("user.getPassword() : in service" + user.getPassword());
		user.setAccount_created(LocalDateTime.now());
		user.setAccount_updated(LocalDateTime.now());
		savedUser = repo.save(user);

		UserDTO userdto = new UserDTO();
		userdto.setId(savedUser.getId());
		userdto.setUsername(savedUser.getUsername());
		userdto.setFirst_name(savedUser.getFirst_name());
		userdto.setLast_name(savedUser.getLast_name());
		userdto.setAccount_created(savedUser.getAccount_created());
		userdto.setAccount_updated(savedUser.getAccount_updated());
		return userdto;
	}

	private String encodePassword(String password) {
		this.passwordEncoder = new BCryptPasswordEncoder();
		String enCodedPassword = this.passwordEncoder.encode(password);
		return enCodedPassword;
	}

	private String decodePassword(String password) {
		this.passwordEncoder = new BCryptPasswordEncoder();
		String enCodedPassword = this.passwordEncoder.encode(password);
		return enCodedPassword;
	}

	public boolean updateUser(User user, UUID userId) {
		user.setPassword(encodePassword(user.getPassword()));
		System.out.println("Abhishek");
		repo.setUserInfoById(user.getFirst_name(),user.getLast_name(), user.getPassword() ,LocalDateTime.now() ,userId);
		return true;

	}
	

	public Optional<User> fetchUserbyId(UUID id){
		return repo.findById(id);
	}
	public User fetchUserByUserName(String userName)throws DataNotFoundExeception {
		User user = repo.findByUsername(userName);
		return user;
	}

	public User getUserDetailsAuth(UUID id) throws DataNotFoundExeception {
		Optional<User> user = repo.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public boolean isAuthorised(UUID userId,String tokenEnc) throws DataNotFoundExeception, UserAuthorizationException {

		this.passwordEncoder = new BCryptPasswordEncoder();
		User user=getUserDetailsAuth(userId);
		byte[] token = Base64.getDecoder().decode(tokenEnc);
		String decodedStr = new String(token, StandardCharsets.UTF_8);
		String userName = decodedStr.split(":")[0];
		String passWord = decodedStr.split(":")[1];
		System.out.println("Value of Token" + " "+ decodedStr);
		if(!((user.getUsername().equals(userName)) && (passwordEncoder.matches(passWord,user.getPassword())))){
			throw new UserAuthorizationException("Forbidden to access");
		}
		return true;
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(),Collections.emptyList());
	}


	public Boolean isEmailPresent(String username) {
		return repo.isEmailPresent(username) > 0 ? true : false;
	}

	public RegistrationStatus getRegistrationStatus(BindingResult errors) {
		FieldError usernameError = errors.getFieldError("username");
		FieldError passwordError = errors.getFieldError("password");
		FieldError firstnameError = errors.getFieldError("firstName");
		FieldError lastnameError = errors.getFieldError("lastName");
		String firstnameErrorMessage = firstnameError == null ? "-" : firstnameError.getCode();
		String lastnameErrorMessage = lastnameError == null ? "-" : lastnameError.getCode();
		String usernameErrorMessage = usernameError == null ? "-" : usernameError.getCode();
		String passwordErrorMessage = passwordError == null ? "-" : passwordError.getCode();
		return new RegistrationStatus(usernameErrorMessage, passwordErrorMessage,firstnameErrorMessage,lastnameErrorMessage);
	}

	public void isUserVerified() {
	}
}
