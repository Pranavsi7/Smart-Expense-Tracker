package com.project.expensetrackerapi.domain;

import java.io.Serializable;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 2L;

    private Integer transactionId;
    private Integer categoryId;
    private Integer userId;
    private Double  amount;
    private String  note;
    private Long    transactionDate;

    public Transaction() {}

    public Transaction(Integer transactionId, Integer categoryId, Integer userId,
                       Double amount, String note, Long transactionDate) {
        this.transactionId   = transactionId;
        this.categoryId      = categoryId;
        this.userId          = userId;
        this.amount          = amount;
        this.note            = note;
        this.transactionDate = transactionDate;
    }

    public Integer getTransactionId()             { return transactionId; }
    public void    setTransactionId(Integer v)    { this.transactionId = v; }
    public Integer getCategoryId()                { return categoryId; }
    public void    setCategoryId(Integer v)       { this.categoryId = v; }
    public Integer getUserId()                    { return userId; }
    public void    setUserId(Integer v)           { this.userId = v; }
    public Double  getAmount()                    { return amount; }
    public void    setAmount(Double v)            { this.amount = v; }
    public String  getNote()                      { return note; }
    public void    setNote(String v)              { this.note = v; }
    public Long    getTransactionDate()           { return transactionDate; }
    public void    setTransactionDate(Long v)     { this.transactionDate = v; }
}
