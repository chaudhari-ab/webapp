package com.product.crud.services;

import com.product.crud.model.Product;
import com.product.crud.model.User;
import com.product.crud.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    CrudService service;

    public Product addProduct(Product product){

        product.setDate_added(LocalDateTime.now());
        product.setDate_last_updated(LocalDateTime.now());
        int qt = product.getQuantity();
        System.out.println("quantity " + qt);
        return productRepo.save(product);
    }

    public boolean ifProductSKUExists(String sku){
       return productRepo.checkSKUExists(sku)!=0?true:false;
    }

    public String updateProduct(Integer id,Product product){
        productRepo.updateProductById(product.getName(),product.getDescription(),product.getSku(),product.getManufacturer(),LocalDateTime.now(),product.getQuantity(),id);
        return "Product Updated";
    }

    public int findProductById(Integer id){
         return productRepo.findProductbyId(id);
    }
    public String deleteProduct(Integer id){
        productRepo.deleteById(id);
        return "Product Deleted";
    }

    public Long authCredential(String tokenEnc)  {
        try {
            byte[] token = Base64.getDecoder().decode(tokenEnc);
            String decodedStr = new String(token, StandardCharsets.UTF_8);
            String userName = decodedStr.split(":")[0];
            String passWord = decodedStr.split(":")[1];
            User user = service.fetchUserByUserName(userName);
            if (!(PassEncoder().matches(passWord, user.getPassword()))) {
                return null;
            }
            return user.getId();
        }catch(Exception e){
            return null;
        }
    }

    public BCryptPasswordEncoder PassEncoder() {
        return new BCryptPasswordEncoder();
    }

    public boolean isAuthorisedForPut(int productId,String tokenEnc, Product productsRequest)  {
        Product product1 = getUserDetailsAuth(productId);
        Long userId ;
        if(product1!=null) {
            userId = product1.getOwner_user_id();
        }else{
            return false;
        }
        Optional<User> user = service.fetchUserbyId(userId);
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);
        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);
        if(!(user.get().getUsername().equals(userName)) || !(PassEncoder().matches(passWord,user.get().getPassword()))){
            return false;
        }
        return true;
    }

    public Product getUserDetailsAuth(int productId) {
        Optional<Product> product = productRepo.findById(productId);
        if (product.isPresent()) {
            return product.get();
        }
        return null;
    }
    public boolean isAuthorisedForGet(int productId, String tokenEnc) {

        Product product1 = getUserDetailsAuth(productId);
        Long userId ;
        if(product1!=null) {
            userId = product1.getOwner_user_id();
        }else{
            return false;
        }
        Optional<User> user = service.fetchUserbyId(userId);
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);
        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);
        if(!(user.get().getUsername().equals(userName)) || !(PassEncoder().matches(passWord,user.get().getPassword()))){
            return false;
        }
        return true;
    }

}
