package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Bank;
import models.BankAgency;
import util.Utils;

import javax.inject.Inject;

public class BankAgencyHelper {

    @Inject
    private Utils utils;

    public BankAgency fill(JsonNode json, Bank bank){
        BankAgency bankAgency = new BankAgency();
        bankAgency.setName(utils.getValueFromJson(json, "name"));
        bankAgency.setCode(utils.getValueFromJson(json, "code"));
        bankAgency.setBank(bank);
        return bankAgency;
    }

    public BankAgency fill(BankAgency bankAgency, JsonNode json, Bank bank){
        bankAgency.setName(utils.getValueFromJson(json, "name"));
        bankAgency.setCode(utils.getValueFromJson(json, "code"));
        bankAgency.setBank(bank);
        return bankAgency;
    }
}
