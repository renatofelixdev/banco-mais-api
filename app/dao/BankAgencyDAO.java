package dao;

import api.ApiController;
import com.avaje.ebean.Model;
import models.BankAgency;

import java.util.List;
import java.util.Optional;

public class BankAgencyDAO implements ApiDAO {

    private Model.Finder<Long, BankAgency> bankAgencyFinder = new Model.Finder<>(BankAgency.class);

    @Override
    public List<BankAgency> all() {
        return bankAgencyFinder.where()
                .eq("removed", false)
                .orderBy("bank.name asc")
                .findList();
    }

    public List<BankAgency> allByBank(Long id){
        return bankAgencyFinder.where()
                .eq("removed", false)
                .eq("bank.id", id)
                .findList();
    }

    @Override
    public Optional<BankAgency> byId(Long id) {
        return Optional.ofNullable(bankAgencyFinder.byId(id));
    }
}
