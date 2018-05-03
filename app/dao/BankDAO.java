package dao;

import com.avaje.ebean.Model;
import models.Bank;

import java.util.List;
import java.util.Optional;

public class BankDAO implements ApiDAO {
    private Model.Finder<Long, Bank> bankFinder = new Model.Finder<>(Bank.class);

    @Override
    public List<Bank> all() {
        return bankFinder.where()
                .eq("removed", false)
                .orderBy("name asc")
                .findList();
    }

    @Override
    public Optional<Bank> byId(Long id) {
        return Optional.ofNullable(bankFinder.byId(id));
    }
}
