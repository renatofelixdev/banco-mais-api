package api;

import authenticators.UserClientAuthenticator;
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
import play.mvc.Security;
import util.NameEntity;
import util.Utils;
import validators.BankingOperationValidator;

import javax.inject.Inject;
import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class BankingOperationApiController extends Controller {

    @Inject
    private Utils utils;

    @Inject
    private BankingOperationValidator bankingOperationValidator;

    @Inject
    private BankAccountDAO bankAccountDAO;

    @Inject
    private AccountHistoryDAO accountHistoryDAO;

    @Security.Authenticated(UserClientAuthenticator.class)
    public Result bankStatement(){
        JsonNode json = request().body().asJson();

        Notification notification = bankingOperationValidator.bankStatement(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAccount> bankAccount = bankAccountDAO.byNumber(utils.getValueFromJson(json, "account"));
        Result result = validBankAccount(json, bankAccount, "");
        if(result != null) return result;

        Date start = utils.getDateFrom(utils.getValueFromJson(json, "startDate"));
        Date end = utils.getDateFrom(utils.getValueFromJson(json, "endDate"));

        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setDate(new Date());
        accountHistory.setOperation(Operation.BANK_STATEMENT);
        accountHistory.setSource(bankAccount.get());
        accountHistory.setTarget(null);
        accountHistory.setValue(null);
        accountHistory.save();

        return utils.ok(Json.toJson(accountHistoryDAO.byBankAccount(bankAccount.get(), start, end)));
    }


    @Security.Authenticated(UserClientAuthenticator.class)
    public Result deposit(){
        JsonNode json = request().body().asJson();

        Notification notification = bankingOperationValidator.depositOrWithdrawal(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAccount> bankAccountOptional = bankAccountDAO.byNumber(utils.getValueFromJson(json, "account"));
        Result result = validBankAccount(json, bankAccountOptional, "");
        if(result != null) return result;

        BankAccount bankAccount = bankAccountOptional.get();

        Double value = Double.valueOf(utils.getValueFromJson(json, "value"));
        Double balance = bankAccount.getBalance().doubleValue() + value;

        bankAccount.setBalance(BigDecimal.valueOf(balance));
        bankAccount.update();

        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setDate(new Date());
        accountHistory.setOperation(Operation.DEPOSIT_INTO_ACCOUNT);
        accountHistory.setSource(bankAccount);
        accountHistory.setTarget(bankAccount);
        accountHistory.setValue(BigDecimal.valueOf(value));
        accountHistory.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Depósito realizado com sucesso!")));
    }


    @Security.Authenticated(UserClientAuthenticator.class)
    public Result bankTransfer(){
        JsonNode json = request().body().asJson();

        Notification notification = bankingOperationValidator.bankTransfer(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAccount> bankAccountSourceOptional = bankAccountDAO.byNumber(utils.getValueFromJson(json, "account"));
        Result result = validBankAccount(json, bankAccountSourceOptional,"");
        if(result != null) return result;

        Optional<BankAccount> bankAccountTargetOptional = bankAccountDAO.byNumber(utils.getValueFromJson(json, "accountTarget"));
        result = validBankAccount(json, bankAccountTargetOptional, "Target");
        if(result != null) return result;

        BankAccount bankAccountSource = bankAccountSourceOptional.get();
        BankAccount bankAccountTarget = bankAccountTargetOptional.get();


        Double value = Double.valueOf(utils.getValueFromJson(json, "value"));

        if(bankAccountSource.getBalance().doubleValue() < value){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Saldo insuficiente para realizar esta transferência!")));
        }

        Double balanceSource = bankAccountSource.getBalance().doubleValue() - value;
        Double balanceTarget = bankAccountTarget.getBalance().doubleValue() + value;

        bankAccountSource.setBalance(BigDecimal.valueOf(balanceSource));
        bankAccountSource.update();

        bankAccountTarget.setBalance(BigDecimal.valueOf(balanceTarget));
        bankAccountTarget.update();


        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setDate(new Date());
        accountHistory.setOperation(Operation.BANK_TRANSFER);
        accountHistory.setSource(bankAccountSource);
        accountHistory.setTarget(bankAccountTarget);
        accountHistory.setValue(BigDecimal.valueOf(value));
        accountHistory.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Transferência realizada com sucesso!")));
    }


    @Security.Authenticated(UserClientAuthenticator.class)
    public Result accountWithDrawal(){
        JsonNode json = request().body().asJson();

        Notification notification = bankingOperationValidator.depositOrWithdrawal(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAccount> bankAccountOptional = bankAccountDAO.byNumber(utils.getValueFromJson(json, "account"));
        Result result = validBankAccount(json, bankAccountOptional, "");
        if(result != null) return result;

        BankAccount bankAccount = bankAccountOptional.get();

        Double value = Double.valueOf(utils.getValueFromJson(json, "value"));

        if(bankAccount.getBalance().doubleValue() < value){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Saldo insuficiente para realizar este saque!")));
        }

        Double balance = bankAccount.getBalance().doubleValue() - value;

        bankAccount.setBalance(BigDecimal.valueOf(balance));
        bankAccount.update();

        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setDate(new Date());
        accountHistory.setOperation(Operation.ACCOUNT_WITHDRAWAL);
        accountHistory.setSource(bankAccount);
        accountHistory.setTarget(null);
        accountHistory.setValue(BigDecimal.valueOf(value));
        accountHistory.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Saque realizado com sucesso!")));
    }

    private Result validBankAccount(JsonNode json, Optional<BankAccount> bankAccount, String target){

        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        if(!bankAccount.get().getBankAgency().getCode().equals(utils.getValueFromJson(json, "agency"+target))){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Agência não compatível com esta conta!")));
        }

        if(!bankAccount.get().getBankAgency().getBank().getCode().equals(utils.getValueFromJson(json, "bank"+target))){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Banco não compatível com esta agência!")));
        }

        return null;
    }
}
