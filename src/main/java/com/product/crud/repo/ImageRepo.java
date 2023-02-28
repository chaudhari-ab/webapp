package com.product.crud.repo;

import com.product.crud.model.Image;
import com.product.crud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepo extends JpaRepository<Image,Integer> {

    @Query("SELECT i FROM Image i WHERE i.image_id = :image_id")
    Image getImagebyId(@Param("image_id") Integer image_id);

    @Query("SELECT i FROM Image i WHERE i.product_id = :product_id")
    List<Image> getAllImages(@Param("product_id") Integer product_id);
}
