package com.product.crud.Controller;

import com.google.gson.Gson;
import com.product.crud.Exception.InvalidInputException;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.model.Product;
import com.product.crud.model.ResponseObject;
import com.product.crud.model.User;
import com.product.crud.services.CrudService;
import com.product.crud.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    private CrudService service;
    @Autowired
    private ProductService productService;

    ResponseObject responseObject;
    Map<String,String> responseHashmap;
    @RequestMapping(path = "/v1/product", method = RequestMethod.POST,produces="application/json")
    @ResponseBody
    public ResponseEntity<?> createProduct( @RequestBody Product product, HttpServletRequest request) {
        System.out.println("Inside /v1/product");
//        HttpServletResponse response = new HttpServletResponseWrapper();
        try {
            Long userId = productService.authCredential(request.getHeader("Authorization").split(" ")[1]);
            if(userId == null){
                //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
                throw new UserAuthorizationException("Invalid Username or Password");
            }
            product.setOwner_user_id(userId);
            if (product == null || request == null) {
//                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
                throw new InvalidInputException("Request Body Cannot be Empty");

            }

            if (product.getName().isBlank() || product.getName().isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Name Cannot be Empty");
                throw new Exception("Product Name Cannot be Empty");

            }
            if (product.getDescription().isBlank() || product.getDescription().isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Description Cannot be Empty");
                throw new Exception("Product Description Cannot be Empty");

            }
            if (product.getSku().isBlank() || product.getSku().isEmpty()) {
//                return new ResponseEntity("Product SKU Cannot be Empty",HttpStatus.BAD_REQUEST);
                throw new Exception("Product SKU Cannot be Empty");

            }
            if(productService.ifProductSKUExists(product.getSku())) {
//                return new ResponseEntity<String>( new Gson().toJson("Product with SKU Exists"),HttpStatus.BAD_REQUEST);
                throw new Exception("Product with SKU Exists");
            }
            if (product.getManufacturer().isBlank() || product.getManufacturer().isEmpty()) {
                throw new Exception("Manufacturer Cannot be Null");
            }
                try {
                    if ( product.getQuantity() < 1) {
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
                        throw new Exception("Invalid Product Quantity");
                    }
                }catch(Exception e){
//                    return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
                    throw new Exception("Invalid Product Quantity");

                }
            return new ResponseEntity<Product>( productService.addProduct(product),HttpStatus.CREATED);

        }
        catch(UserAuthorizationException e){
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.UNAUTHORIZED);
        }
        catch(InvalidInputException e){
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.NO_CONTENT);
        }
        catch(Exception e) {
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "/v1/product/{productId}", method = RequestMethod.PUT,produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable Integer productId , @RequestBody Product product, HttpServletRequest request){
        try{
            if(!(productService.isAuthorisedForPut(productId,request.getHeader("Authorization").split(" ")[1], product))){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
            }

            if (product == null || request==null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
            }
            if (product.getName().isBlank() || product.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Name Cannot be Empty");
            }
            if (product.getDescription().isBlank() || product.getDescription().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Description Cannot be Empty");
            }
            if (product.getSku().isBlank() || product.getSku().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product SKU Cannot be Empty");
            }

            if (product.getManufacturer().isBlank() || product.getManufacturer().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Manufacturer Cannot be Empty");
            }
            try {
                if ( product.getQuantity() < 1) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
                }
            }catch(Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
            }

            return new ResponseEntity<String>(productService.updateProduct(productId,product),HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/v1/product/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable Integer productId , @RequestBody Product product, HttpServletRequest request){
        productService.isAuthorisedForGet(productId,request.getHeader("Authorization").split(" ")[1]);
        int productCount = productService.findProductById(productId);
        if(productCount==1){
            return new ResponseEntity<String>(productService.deleteProduct(productId),HttpStatus.OK);

        }else {
            return new ResponseEntity<String>("Product with Id Does Not Exist", HttpStatus.BAD_REQUEST);
        }
    }
}
