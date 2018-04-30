package dao;

import com.avaje.ebean.Model;
import models.BankAccount;

import javax.management.MalformedObjectNameException;
import java.util.List;
import java.util.Optional;

public class BankAccountDAO implements ApiDAO {

    private Model.Finder<Long, BankAccount> bankAccountFinder = new Model.Finder<>(BankAccount.class);

    @Override
    public List<BankAccount> all() {
        return bankAccountFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public Optional<BankAccount> byId(Long id) {
        return Optional.ofNullable(bankAccountFinder.byId(id));
    }

    public List<BankAccount> allByAgency(Long id) {
        return bankAccountFinder.where()
                .eq("removed", false)
                .eq("bankAgency.id", id)
                .findList();
    }

    public Optional<BankAccount> byNumber(String account) {
        return Optional.ofNullable(bankAccountFinder.where()
                .eq("removed", false)
                .eq("number", account)
                .findUnique());
    }
}
