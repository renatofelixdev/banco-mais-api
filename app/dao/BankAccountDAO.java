package dao;

import com.avaje.ebean.Model;
import models.BankAccount;

import javax.management.MalformedObjectNameException;
import java.util.List;

public class BankAccountDAO implements ApiDAO {

    private Model.Finder<Long, BankAccount> bankAccountFinder = new Model.Finder<>(BankAccount.class);

    @Override
    public List<BankAccount> all() {
        return bankAccountFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public BankAccount byId(Long id) {
        return bankAccountFinder.byId(id);
    }
}
