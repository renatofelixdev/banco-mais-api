package models;

import com.avaje.ebean.Model;
import enums.BankAccountType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class BankAccount extends ModelMaster {

    private String number;

    @Column(precision=10, scale=2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private BankAccountType bankAccountType;

    @ManyToOne
    private BankAgency bankAgency;

    @OneToOne
    private UserClient userClient;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(BankAccountType bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public BankAgency getBankAgency() {
        return bankAgency;
    }

    public void setBankAgency(BankAgency bankAgency) {
        this.bankAgency = bankAgency;
    }

    public UserClient getUserClient() {
        return userClient;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
