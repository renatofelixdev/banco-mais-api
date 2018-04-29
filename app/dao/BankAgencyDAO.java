package dao;

import api.ApiController;
import com.avaje.ebean.Model;
import models.BankAgency;

import java.util.List;

public class BankAgencyDAO implements ApiDAO {

    private Model.Finder<Long, BankAgency> bankAgencyFinder = new Model.Finder<>(BankAgency.class);

    @Override
    public List<BankAgency> all() {
        return bankAgencyFinder.where()
                .eq("removed", false)
                .findList();
    }

    public List<BankAgency> allByBank(Long id){
        return bankAgencyFinder.where()
                .eq("removed", false)
                .eq("bank.id", id)
                .findList();
    }

    @Override
    public BankAgency byId(Long id) {
        return bankAgencyFinder.byId(id);
    }
}
