package com.product.crud.Controller;

import com.product.crud.model.Product;
import com.product.crud.model.User;
import com.product.crud.services.CrudService;
import com.product.crud.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    private CrudService service;
    @Autowired
    private ProductService productService;

    @RequestMapping(path = "/v1/product", method = RequestMethod.POST)
    public ResponseEntity<?> createProduct( @RequestBody Product product, HttpServletRequest request) {
        System.out.println("Inside /v1/product");

        try {
//            byte[] token = Base64.getDecoder().decode(request.getHeader("Authorization").split(" ")[1]);
//            String decodedStr = new String(token, StandardCharsets.UTF_8);
//            String userName = decodedStr.split(":")[0];
//            User user = service.fetchUserByUserName(userName);
//            service.isAuthorised(user.getId(),request.getHeader("Authorization").split(" ")[1]);
//            System.out.println("userName is - "+ userName);
//            System.out.println("User is Verified - By Abhishek");

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
            if(productService.ifProductSKUExists(product.getSku())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with SKU Exists");
            }
            if (product.getManufacturer().isBlank() || product.getManufacturer().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Manufacturer Cannot be Empty");
            }
                try {
                    if (product.getQuantity() == null || product.getQuantity() < 1) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
                    }
                }catch(Exception e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
                }
            return new ResponseEntity<Product>( productService.addProduct(product),HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "/v1/product/{productId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable Integer productId , @RequestBody Product product, HttpServletRequest request){
        try{
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
            if(productService.ifProductSKUExists(product.getSku())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with SKU Exists");
            }
            if (product.getManufacturer().isBlank() || product.getManufacturer().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Manufacturer Cannot be Empty");
            }
            try {
                if (product.getQuantity() == null || product.getQuantity() < 1) {
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
        int productCount = productService.findProductById(productId);
        if(productCount==1){
            return new ResponseEntity<String>(productService.deleteProduct(productId),HttpStatus.OK);
        }else {
            return new ResponseEntity<String>("Product with Id Does Not Exist", HttpStatus.BAD_REQUEST);
        }
    }
}
