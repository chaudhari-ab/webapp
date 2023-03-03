package com.product.crud.Controller;

import com.product.crud.Exception.InvalidInputException;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.model.Image;
import com.product.crud.model.Product;
import com.product.crud.model.ResponseObject;
import com.product.crud.services.ProductService;
import com.product.crud.services.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private ProductService productService;

    @RequestMapping(path = "/v1/product/{product_id}/image", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile( @PathVariable("product_id") Integer product_id,@RequestParam(value="file")MultipartFile file, HttpServletRequest request){

        try {
            if (!(productService.isAuthorisedForPut(product_id, request.getHeader("Authorization").split(" ")[1], null))) {
                throw new InvalidInputException("Invalid Username or Password");
            }
            return new ResponseEntity<Image>( storageService.uploadImage(file,product_id),HttpStatus.CREATED);


        }catch(InvalidInputException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.BAD_REQUEST);
            response.setResponseMessage(e.getMessage());
            if(e.getMessage().matches("Invalid Username or Password")) return new ResponseEntity<ResponseObject>(response,HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<ResponseObject>(response,HttpStatus.BAD_REQUEST);
        }catch(UserAuthorizationException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.FORBIDDEN);
            response.setResponseMessage(e.getMessage());
            return new ResponseEntity<ResponseObject>(response,HttpStatus.FORBIDDEN);
        }

    }

    @RequestMapping(path = "/v1/product/download/{imageName}", method = RequestMethod.GET)
    public ResponseEntity<?> downloadImage(@PathVariable String imageName){


        byte[] data = storageService.downloadImage(imageName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok().contentLength(data.length)
                .header("Content-type","application/octet-stream")
                .header("Content-disposition","attachment;filename=\""+imageName+"\"")
                .body(resource);
    }

    @RequestMapping(path = "/v1/product/{product_id}/image/{image_id}", method = RequestMethod.GET)
    public ResponseEntity<?> getImageDetails(@PathVariable Integer product_id,Integer image_id,HttpServletRequest request){
        try {
            if (!(productService.isAuthorisedForPut(product_id, request.getHeader("Authorization").split(" ")[1], null))) {
                throw new InvalidInputException("Invalid Username or Password");
            }
            Image image = storageService.getImage(image_id);
            if(image!=null){
                return new ResponseEntity<Image>( image,HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
            }

        }catch(InvalidInputException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.BAD_REQUEST);
            response.setResponseMessage(e.getMessage());
            if(e.getMessage().matches("Invalid Username or Password")) return new ResponseEntity<ResponseObject>(response,HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<ResponseObject>(response,HttpStatus.BAD_REQUEST);
        }catch(UserAuthorizationException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.FORBIDDEN);
            response.setResponseMessage(e.getMessage());
            return new ResponseEntity<ResponseObject>(response,HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(path = "/v1/product/{product_id}/image", method = RequestMethod.GET)
    public ResponseEntity<?> getImageList(@PathVariable Integer product_id,HttpServletRequest request){
        try {
            if (!(productService.isAuthorisedForPut(product_id, request.getHeader("Authorization").split(" ")[1], null))) {
                throw new InvalidInputException("Invalid Username or Password");
            }
            List<Image> imageList = storageService.getAllImages(product_id);
            if(imageList!=null && !imageList.isEmpty()){
                return new ResponseEntity<List<Image>>( imageList,HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
            }

        }catch(InvalidInputException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.BAD_REQUEST);
            response.setResponseMessage(e.getMessage());
            if(e.getMessage().matches("Invalid Username or Password")) return new ResponseEntity<ResponseObject>(response,HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<ResponseObject>(response,HttpStatus.BAD_REQUEST);
        }catch(UserAuthorizationException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.FORBIDDEN);
            response.setResponseMessage(e.getMessage());
            return new ResponseEntity<ResponseObject>(response,HttpStatus.FORBIDDEN);
        }
    }




    @RequestMapping(path = "/v1/product/{product_id}/image/{image_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteImage(@PathVariable Integer image_id,Integer product_id,HttpServletRequest request){
        try {
            if (!(productService.isAuthorisedForPut(product_id, request.getHeader("Authorization").split(" ")[1], null))) {
                throw new InvalidInputException("Invalid Username or Password");
            }
            String deleteResponse=storageService.deleteImage(image_id);
            if(deleteResponse.equals("Image Not Found"))  return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<String>( "Image Deleted Successfully",HttpStatus.NO_CONTENT);
        }catch(InvalidInputException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.BAD_REQUEST);
            response.setResponseMessage(e.getMessage());
            if(e.getMessage().matches("Invalid Username or Password")) return new ResponseEntity<ResponseObject>(response,HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<ResponseObject>(response,HttpStatus.BAD_REQUEST);
        }catch(UserAuthorizationException e){
            ResponseObject response = new ResponseObject();
            response.setHttpStatusCode(HttpStatus.FORBIDDEN);
            response.setResponseMessage(e.getMessage());
            return new ResponseEntity<ResponseObject>(response,HttpStatus.FORBIDDEN);
        }
    }
}
