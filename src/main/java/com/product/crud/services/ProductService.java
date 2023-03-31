package com.product.crud.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.product.crud.Exception.UserAuthorizationException;
import com.product.crud.model.Product;
import com.product.crud.model.User;
import com.product.crud.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    CrudService service;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3client;

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
    public boolean ifOtherProductWithSKUExists(String sku, Integer productId){
        Product productBySku = productRepo.getProductbySku(sku);
        System.out.println(productBySku);
        if(productBySku==null){
            return false;
        }
        if(productId==productBySku.getId()){
            return false;
        }
        return true;
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

    public boolean isAuthorisedForPut(int productId,String tokenEnc, Product productsRequest)  throws UserAuthorizationException {
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
            throw new UserAuthorizationException("Forbidden");
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
    public boolean isAuthorisedForGet(int productId, String tokenEnc) throws UserAuthorizationException {

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
            throw new UserAuthorizationException("Forbidden");
        }
        return true;
    }

    public String getUserIdFromToken(String tokenEnc){
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);
        String userName = decodedStr.split(":")[0];
        return userName;
    }


    public Product getProductbyId(Integer productId){
        Product productFromDb = productRepo.getProductbyId(productId);
        return productFromDb;
    }
    public String updateProductwithPatch(Integer productId, Product product) {

        Product productFromDb = productRepo.getProductbyId(productId);
        if(productFromDb!=null){
        if(product.getQuantity()!=null){
            if(product.getQuantity()< 0 || product.getQuantity() > 99) return "Invalid Product Quantity";
            productFromDb.setQuantity(product.getQuantity());
        }
        if(product.getName()!=null){
            if(product.getName().isBlank()) return "Invalid Product Name";
            productFromDb.setName(product.getName());
        }
        if(product.getManufacturer()!=null){
            if(product.getManufacturer().isBlank()) return "Invalid Product Manufacturer";
            productFromDb.setManufacturer(product.getManufacturer());
        }
        if(product.getDescription()!=null){
            if(product.getDescription().isBlank()) return "Invalid Product Description";
            productFromDb.setDescription(product.getDescription());
        }
        if(product.getSku()!=null){
            if(ifOtherProductWithSKUExists(product.getSku(),productId)){
                return "Another Product with SKU Exists";
            }
            productFromDb.setSku(product.getSku());
        }
        productRepo.save(productFromDb);
        return "Product Updated";
        }else{
            return "Product with the ID does not Exists";
        }
    }

    public void deleteImageByProductId(Integer productId)  throws Exception {
        // TODO Auto-generated method stub
        String path = String.format("%s",  productId);
        String folderName = path;
        System.out.println(path);
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(folderName);
        ListObjectsV2Result objects = s3client.listObjectsV2(listObjectsRequest);
        List<S3ObjectSummary> summaries = objects.getObjectSummaries();
        System.out.println(objects+" "+summaries);
        for (S3ObjectSummary summary : summaries) {
            System.out.println(summary.getKey());
            s3client.deleteObject(bucketName, summary.getKey());
        }
        s3client.deleteObject(bucketName, folderName);
    }

}
