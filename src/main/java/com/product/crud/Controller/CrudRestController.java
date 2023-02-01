package com.product.crud.Controller;

import com.product.crud.Exception.DataNotFoundExeception;
import com.product.crud.Exception.InvalidInputException;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.Exception.UserExistException;
import com.product.crud.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.product.crud.services.CrudService;

import java.util.UUID;

@RestController
public class CrudRestController {
	
	@Autowired
	private CrudService service;

	@RequestMapping(path = "/v1/user", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user) {
		System.out.println("user.getPassword() Controller :" + user.getPassword());
		System.out.println(user);
		try {
			//Deleted If
			return ResponseEntity.status(HttpStatus.CREATED).body(service.saveUser(user));
		} catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}


	}

	@RequestMapping(path = "/v1/user/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable UUID userId , @RequestBody User user, HttpServletRequest request ) {
		if(user==null) return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
		try {
			if(userId.toString().isBlank()||userId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid User Id");
			}
			service.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
			service.updateUser(user,userId);
			return ResponseEntity.status(HttpStatus.CREATED).body("User Updated");

		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		catch (UserAuthorizationException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
		catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
		catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}



	}

	@RequestMapping(path = "/v1/user/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> fetchProductByID(@PathVariable UUID userId, HttpServletRequest request) {
		try {
			if(userId.toString().isBlank()||userId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid User Id");
			}
			service.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
			return ResponseEntity.status(HttpStatus.OK).body(service.fetchUserbyId(userId));
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		catch (UserAuthorizationException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
		catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
		catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/healthz", method = RequestMethod.GET)
	public void healthZ() {
	}
}
