package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import enums.BankAccountType;
import models.BankAccount;
import models.BankAgency;
import models.UserClient;
import util.Utils;

import javax.inject.Inject;
import java.math.BigDecimal;

public class BankAccountHelper {

    @Inject
    private Utils utils;

    public BankAccount fill(JsonNode json, BankAgency bankAgency, UserClient userClient) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setNumber(utils.getValueFromJson(json, "number"));
        bankAccount.setBalance(BigDecimal.valueOf(utils.getValueDouble(utils.getValueFromJson(json, "balance"))));
        bankAccount.setBankAccountType(BankAccountType.parse(utils.getValueFromJson(json, "bankAccountType")));
        bankAccount.setBankAgency(bankAgency);
        bankAccount.setUserClient(userClient);
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
