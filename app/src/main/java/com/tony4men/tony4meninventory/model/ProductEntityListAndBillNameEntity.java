package com.tony4men.tony4meninventory.model;

import java.util.List;

public class ProductEntityListAndBillNameEntity {
    List<ProductEntity> entities;
    String billName;

    public ProductEntityListAndBillNameEntity() {
    }

    public ProductEntityListAndBillNameEntity(List<ProductEntity> entities, String billName) {
        this.entities = entities;
        this.billName = billName;
    }

    public List<ProductEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<ProductEntity> entities) {
        this.entities = entities;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }
}
