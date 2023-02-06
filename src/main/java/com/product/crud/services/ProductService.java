package com.product.crud.services;

import com.product.crud.model.Product;
import com.product.crud.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    public Product addProduct(Product product){

        product.setDate_added(LocalDateTime.now());
        product.setDate_last_updated(LocalDateTime.now());
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
}
