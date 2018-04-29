package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import enums.BankAccountType;
import models.BankAccount;
import models.BankAgency;
import util.Utils;

import javax.inject.Inject;
import java.math.BigDecimal;

public class BankAccountHelper {

    @Inject
    private Utils utils;

    public BankAccount fill(JsonNode json, BankAgency bankAgency) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setNumber(utils.getValueFromJson(json, "number"));
        bankAccount.setBalance(BigDecimal.valueOf(utils.getValueDouble(utils.getValueFromJson(json, "balance"))));
        bankAccount.setBankAccountType(BankAccountType.parse(utils.getValueFromJson(json, "bankAccountType")));
        bankAccount.setBankAgency(bankAgency);
        return bankAccount;
    }

    public BankAccount fill(BankAccount bankAccount, JsonNode json, BankAgency bankAgency) {
        bankAccount.setNumber(utils.getValueFromJson(json, "number"));
        bankAccount.setBalance(BigDecimal.valueOf(utils.getValueDouble(utils.getValueFromJson(json, "balance"))));
        bankAccount.setBankAccountType(BankAccountType.parse(utils.getValueFromJson(json, "bankAccountType")));
        bankAccount.setBankAgency(bankAgency);
        return bankAccount;
    }
}
