package com.product.crud.repo;

import com.product.crud.model.Product;
import com.product.crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    @Query("SELECT count(sku) FROM Product WHERE sku=:sku")
    int checkSKUExists(@Param("sku") String sku);

    @Modifying
    @Query("update Product p set p.name = ?1, p.description = ?2, p.sku = ?3, p.manufacturer=?4, date_last_updated = ?5, p.quantity=?6 where p.id = ?7")
    public void updateProductById(String name,  String description , String sku, String manufacturer,  LocalDateTime date_last_updated, Integer quantity, Integer id);

//    Product findProductbyId(Integer id);

    @Query("SELECT count(id) FROM Product WHERE id=:id")
    int findProductbyId(@Param("id") Integer id);
}
