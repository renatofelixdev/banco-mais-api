package dao;

import com.avaje.ebean.Model;
import models.Bank;

import java.util.List;

public class BankDAO implements ApiDAO {
    private Model.Finder<Long, Bank> bankFinder = new Model.Finder<>(Bank.class);

    @Override
    public List<Bank> all() {
        return bankFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public Bank byId(Long id) {
        return bankFinder.byId(id);
    }
}
