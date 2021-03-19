package com.tony4men.tony4meninventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class BillEntity implements Serializable {
    private Long billId;
    private String billName;
    private Timestamp updateDate;
    private String billType;
    private String description;
    private Long productTotal;

    public BillEntity(Long billId, String billName, Timestamp updateDate, String billType, String description, Long productTotal) {
        this.billId = billId;
        this.billName = billName;
        this.updateDate = updateDate;
        this.billType = billType;
        this.description = description;
        this.productTotal = productTotal;
    }

    public Long getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(Long productTotal) {
        this.productTotal = productTotal;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BillEntity() {
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
