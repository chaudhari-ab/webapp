package com.product.crud.Controller;

import com.product.crud.Exception.DataNotFoundExeception;
import com.product.crud.Exception.InvalidInputException;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.Exception.UserExistException;
import com.product.crud.Validation.UserValidator;
import com.product.crud.errors.RegistrationStatus;
import com.product.crud.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.product.crud.services.CrudService;

import java.util.UUID;

@RestController
public class CrudRestController {

	@Autowired
	private CrudService service;

	@Autowired
	private UserValidator userValidator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	@RequestMapping(path = "/v1/user", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult errors) {
		RegistrationStatus registrationStatus;
		System.out.println("user.getPassword() Controller :" + user.getPassword());
		System.out.println(user);

		if(errors.hasErrors()) {
			registrationStatus = service.getRegistrationStatus(errors);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationStatus);
		}else {
			return ResponseEntity.status(HttpStatus.CREATED).body(service.saveUser(user));
		}



	}

	@RequestMapping(path = "/v1/user/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable UUID userId ,  @RequestBody User user,BindingResult errors, HttpServletRequest request ) {
		RegistrationStatus registrationStatus;
		if(user==null) return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
		try {
			if(userId.toString().isBlank()||userId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid User Id");
			}

			if(errors.hasErrors()) {
				registrationStatus = service.getRegistrationStatus(errors);
				System.out.println("Raghav");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationStatus);
			}else{
				service.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
				System.out.println("Khanna");
				service.updateUser(user,userId);
				return ResponseEntity.status(HttpStatus.CREATED).body("User Updated");
			}


 
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
