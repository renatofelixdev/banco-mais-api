package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Bank;
import util.Utils;

import javax.inject.Inject;

public class BankHelper {

    @Inject
    private Utils utils;

    public Bank fill(JsonNode json){
        Bank bank = new Bank();
        bank.setName(utils.getValueFromJson(json, "name"));
        bank.setCode(utils.getValueFromJson(json, "code"));
        return bank;
    }

    public Bank fill(Bank bank, JsonNode json){
        bank.setName(utils.getValueFromJson(json, "name"));
        bank.setCode(utils.getValueFromJson(json, "code"));
        return bank;
    }
}
