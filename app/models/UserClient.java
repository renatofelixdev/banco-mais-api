package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class UserClient extends ModelMaster {
    private String name;
    private String cpf;
    private String address;

    @ManyToMany
    @JoinTable(name="user_client_bank_account", joinColumns=
            {@JoinColumn(name="user_client_id")}, inverseJoinColumns=
            {@JoinColumn(name="bank_account_id")})
    private List<BankAccount> bankAccountList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<BankAccount> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccount> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }
}
