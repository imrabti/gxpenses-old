package com.nuvola.gxpenses.shared.domaine;

import com.nuvola.gxpenses.shared.type.TransactionType;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Transaction extends BaseEntity {

    private String payee;
    @Enumerated
    private TransactionType type;
    private Date date;
    private Double amount;
    private String tags;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Transaction destTransaction;

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Transaction getDestTransaction() {
        return destTransaction;
    }

    public void setDestTransaction(Transaction destTransaction) {
        this.destTransaction = destTransaction;
    }

}
