package com.tony4men.tony4meninventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProductEntity  implements Serializable {
    private Long productId;
    private String productCode;
    private Long billId;
    private String productName;
    private Long originAmount;
    private Long realAmount;
    private Long image;
    private double price = 350000;
    private Timestamp updateDate;
    public ProductEntity() {
    }

    public ProductEntity(Long productId, String productCode, Long billId, String productName, Long originAmount, Long realAmount, Long image, double price, Timestamp updateDate) {
        this.productId = productId;
        this.productCode = productCode;
        this.billId = billId;
        this.productName = productName;
        this.originAmount = originAmount;
        this.realAmount = realAmount;
        this.image = image;
        this.price = price;
        this.updateDate = updateDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(Long originAmount) {
        this.originAmount = originAmount;
    }

    public Long getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(Long realAmount) {
        this.realAmount = realAmount;
    }

    public Long getImage() {
        return image;
    }

    public void setImage(Long image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }
}
