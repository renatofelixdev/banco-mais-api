package api;

import com.fasterxml.jackson.databind.JsonNode;
import dao.AccountHistoryDAO;
import dao.BankAccountDAO;
import enums.NotificationStatus;
import enums.Operation;
import models.AccountHistory;
import models.BankAccount;
import models.Notification;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.NameEntity;
import util.Utils;
import validators.BankingOperationValidator;

import javax.inject.Inject;
import java.util.Date;

public class BankingOperationApiController extends Controller {

    @Inject
    private Utils utils;

    @Inject
    private BankingOperationValidator bankingOperationValidator;

    @Inject
    private BankAccountDAO bankAccountDAO;

    @Inject
    private AccountHistoryDAO accountHistoryDAO;

    public Result bankStatement(){
        JsonNode json = request().body().asJson();

        Notification notification = bankingOperationValidator.bankStatement(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        BankAccount bankAccount = bankAccountDAO.byNumber(utils.getValueFromJson(json, "account"));

        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        if(!bankAccount.getBankAgency().getCode().equals(utils.getValueFromJson(json, "agency"))){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Agência não compatível com esta conta!")));
        }

        if(!bankAccount.getBankAgency().getBank().getCode().equals(utils.getValueFromJson(json, "bank"))){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Banco não compatível com esta agência!")));
        }

        Date start = utils.getDateFrom(utils.getValueFromJson(json, "startDate"));
        Date end = utils.getDateFrom(utils.getValueFromJson(json, "endDate"));

        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setDate(new Date());
        accountHistory.setOperation(Operation.BANK_STATEMENT);
        accountHistory.setSource(bankAccount);
        accountHistory.setTarget(null);
        accountHistory.setValue(null);
        accountHistory.save();

        return utils.ok(Json.toJson(accountHistoryDAO.byBankAccount(bankAccount, start, end)));
    }

    public Result deposit(){
        JsonNode json = request().body().asJson();
        return null;
    }

    public Result bankTransfer(){
        JsonNode json = request().body().asJson();
        return null;
    }

    public Result accountWithDrawal(){
        JsonNode json = request().body().asJson();
        return null;
    }
}
