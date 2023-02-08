package com.product.crud.Controller;

import com.google.gson.Gson;
import com.product.crud.model.Product;
import com.product.crud.model.ResponseObject;
import com.product.crud.model.User;
import com.product.crud.services.CrudService;
import com.product.crud.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
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
        responseObject = new ResponseObject();
        try {

            responseHashmap = new HashMap<>();
            Long userId = productService.authCredential(request.getHeader("Authorization").split(" ")[1]);
            if(userId == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
//                responseObject.setHttpStatusCode(HttpStatus.UNAUTHORIZED);
//                responseObject.setResponseMessage("Invalid Username or Password");
//                return responseObject;
//                responseHashmap.put("HttpStatusMessage",""+HttpStatus.UNAUTHORIZED.value());
//                responseHashmap.put("HttpStatusMessage",HttpStatus.UNAUTHORIZED.toString());
//                responseHashmap.put("response Message","Invalid Username or Password");
//                return responseHashmap;
            }
            product.setOwner_user_id(userId);
            if (product == null || request==null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request Body Cannot be Empty");
//                responseHashmap.put("HttpStatusMessage",""+HttpStatus.NO_CONTENT.value());
//                responseHashmap.put("HttpStatusMessage",HttpStatus.NO_CONTENT.toString());
//                responseHashmap.put("response Message","Request Body Cannot be Empty");
//                return responseHashmap;
            }

            if (product.getName().isBlank() || product.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Name Cannot be Empty");
//                responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                responseObject.setResponseMessage("Product Name Cannot be Empty");
//                return null;
            }
            if (product.getDescription().isBlank() || product.getDescription().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Description Cannot be Empty");
//                responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                responseObject.setResponseMessage("Product Description Cannot be Empty");
////                return responseObject;
            }
            if (product.getSku().isBlank() || product.getSku().isEmpty()) {
                return new ResponseEntity("Product SKU Cannot be Empty",HttpStatus.BAD_REQUEST);
//                responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                responseObject.setResponseMessage("Product SKU Cannot be Empty");
////                return responseObject;
            }
            if(productService.ifProductSKUExists(product.getSku())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with SKU Exists");
                return new ResponseEntity<String>( new Gson().toJson("Product with SKU Exists"),HttpStatus.BAD_REQUEST);
//                responseHashmap.put("HttpStatusMessage",""+HttpStatus.BAD_REQUEST.value());
//                responseHashmap.put("HttpStatusMessage",HttpStatus.BAD_REQUEST.toString());
//                responseHashmap.put("response Message","Product with SKU Exists");
//                return responseHashmap;
            }
            if (product.getManufacturer().isBlank() || product.getManufacturer().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product Manufacturer Cannot be Empty");
//                responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                responseObject.setResponseMessage("Product Manufacturer Cannot be Empty");
////                return responseObject;
            }
                try {
                    if ( product.getQuantity() < 1) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Product Quantity ");
//                        responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                        responseObject.setResponseMessage("Invalid Product Quantity");
////                        return responseObject;
                    }
                }catch(Exception e){
                    return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
//                    responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//                    responseObject.setResponseMessage(e.getMessage());
////                    return responseObject;
                }
            return new ResponseEntity<Product>( productService.addProduct(product),HttpStatus.CREATED);
//            responseHashmap.put("HttpStatusMessage",""+HttpStatus.CREATED.value());
//            responseHashmap.put("HttpStatusMessage",productService.addProduct(product).toString());
//            responseHashmap.put("response Message","Product Added");
//            return responseHashmap;
        }
        catch (Exception e) {
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
//            responseObject.setHttpStatusCode(HttpStatus.BAD_REQUEST);
//            responseObject.setResponseMessage(e.getMessage());
//            return null;
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
//            if(productService.ifProductSKUExists(product.getSku())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with SKU Exists");
//            }
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
