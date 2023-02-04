package com.product.crud.Controller;

import com.product.crud.Exception.DataNotFoundExeception;
import com.product.crud.Exception.InvalidInputException;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.Exception.UserExistException;
import com.product.crud.Validation.UserValidator;
import com.product.crud.errors.RegistrationStatus;
import com.product.crud.model.Product;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
	public ResponseEntity<?> createUser( @RequestBody User user, HttpServletRequest request) {

		User fromDBuser;
		try {
			if (user == null || request==null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
			}
			if (user.getUsername().isBlank() || user.getUsername().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Name Cannot be Empty");
			}
			if (!user.getUsername().matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email Format");
			}
			System.out.println(user.getPassword());
			if (!(user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Password Format");
			}

			fromDBuser = service.fetchUserByUserName(user.getUsername());
			if(fromDBuser==null){
				return ResponseEntity.status(HttpStatus.CREATED).body(service.saveUser(user));
			}else{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already Exists");
			}

		}catch(Exception e){
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@RequestMapping(path = "/v1/user/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable UUID userId ,  @RequestBody User user,BindingResult errors, HttpServletRequest request ) {
		RegistrationStatus registrationStatus;
		if(user==null) return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
		try {
			if (user == null || request==null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
			}
			if(user.getUsername()!=null){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Name Cannot be Entered");
			}

			System.out.println(user.getPassword());
			if (!(user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Password Format");
			}

			service.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
			service.updateUser(user,userId);
			return ResponseEntity.status(HttpStatus.CREATED).body("User Updated");

		}
		catch (UserAuthorizationException e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
		catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
		catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
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

	@RequestMapping(path = "/v1/product", method = RequestMethod.POST)
	public ResponseEntity<?> createProduct(@Valid @RequestBody Product product,  HttpServletRequest request) {

		System.out.println("Inside /v1/product");
		try {


			byte[] token = Base64.getDecoder().decode(request.getHeader("Authorization").split(" ")[1]);
			String decodedStr = new String(token, StandardCharsets.UTF_8);
			String userName = decodedStr.split(":")[0];

			User user = service.fetchUserByUserName(userName);

			service.isAuthorised(user.getId(),request.getHeader("Authorization").split(" ")[1]);

			System.out.println("User is Verified - By Abhishek");
			return new ResponseEntity<String>( "User is Verified",HttpStatus.OK);

		} catch (DataNotFoundExeception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( "User Not Found",HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/healthz", method = RequestMethod.GET)
	public void healthZ() {
	}
}
