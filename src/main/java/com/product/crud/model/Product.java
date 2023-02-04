package com.product.crud.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Product {

    private String name;
    private String description;
    private String sku;
    private String manufacturer;
    private LocalDateTime date_added;
    private LocalDateTime date_last_updated;
    private Integer qtantity;
    private UUID owner_user_id;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDateTime getDate_added() {
        return date_added;
    }

    public void setDate_added(LocalDateTime date_added) {
        this.date_added = date_added;
    }

    public LocalDateTime getDate_last_updated() {
        return date_last_updated;
    }

    public void setDate_last_updated(LocalDateTime date_last_updated) {
        this.date_last_updated = date_last_updated;
    }

    public Integer getQtantity() {
        return qtantity;
    }

    public void setQtantity(Integer qtantity) {
        this.qtantity = qtantity;
    }

    public UUID getOwner_user_id() {
        return owner_user_id;
    }

    public void setOwner_user_id(UUID owner_user_id) {
        this.owner_user_id = owner_user_id;
    }
}
