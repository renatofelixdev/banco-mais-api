package api;

import authenticators.UserClientAuthenticator;
import authenticators.UserMasterAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import dao.BankAccountDAO;
import dao.BankAgencyDAO;
import dao.UserClientDAO;
import enums.BankAccountType;
import enums.NotificationStatus;
import helpers.BankAccountHelper;
import helpers.UserClientHelper;
import models.BankAccount;
import models.BankAgency;
import models.Notification;
import models.UserClient;
import org.h2.engine.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.NameEntity;
import util.Utils;
import validators.BankAccountValidator;
import validators.UserClientValidator;

import javax.inject.Inject;
import java.util.Optional;

public class BankAccountApiController extends Controller implements ApiController {

    @Inject
    private BankAccountDAO bankAccountDAO;

    @Inject
    private BankAgencyDAO bankAgencyDAO;

    @Inject
    private UserClientDAO userClientDAO;

    @Inject
    private Utils utils;

    @Inject
    private BankAccountValidator bankAccountValidator;

    @Inject
    private UserClientValidator userClientValidator;

    @Inject
    private BankAccountHelper bankAccountHelper;

    @Inject
    private UserClientHelper userClientHelper;

    @Inject
    private UserClientApiController userClientApiController;

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result all() {
        return utils.ok(Json.toJson(bankAccountDAO.all()));
    }


    @Security.Authenticated(UserClientAuthenticator.class)
    public Result byUserClient(){
        UserClient userClient = (UserClient) utils.mapGetValue(Http.Context.current().args, "userClient");

        Result result = utils.valid(Optional.ofNullable(userClient), NameEntity.USER_CLIENT);
        if (result != null) return result;

        return utils.ok(Json.toJson(userClient.getBankAccountList()));

    }

    public Result byBankAgency(Long id){

        Optional<BankAgency> bankAgency = bankAgencyDAO.byId(id);

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        return utils.ok(Json.toJson(bankAccountDAO.allByAgency(id)));

    }

    public Result getTypes(){
        return utils.ok(Json.toJson(BankAccountType.toMap()));
    }

    @Override
    public Result byId(Long id) {
        Optional<BankAccount> bankAccount = bankAccountDAO.byId(id);

        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        return utils.ok(Json.toJson(bankAccount));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        UserClient userClient = userClientApiController.verifyUser(json);


        utils.putValue(json, "userClient", userClient.getId()+"");

        notification = bankAccountValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAgency> bankAgency = bankAgencyDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bankAgency")));

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountHelper.fill(json, bankAgency.get(), userClient);
        bankAccount.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT +" cadastrado(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result update(Long id) {
        JsonNode json = request().body().asJson();

        Notification notification = bankAccountValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<BankAgency> bankAgency = bankAgencyDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bankAgency")));

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        Optional<BankAccount> bankAccountOptional = bankAccountDAO.byId(id);
        result = utils.valid(bankAccountOptional, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountOptional.get();
        bankAccount = bankAccountHelper.fill(bankAccount, json, bankAgency.get());
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT +" atualizado(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result delete(Long id) {
        Optional<BankAccount> bankAccountOptional = bankAccountDAO.byId(id);
        Result result = utils.valid(bankAccountOptional, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountOptional.get();
        bankAccount.setRemoved(true);
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT + ": " + bankAccount.getNumber() + " removido(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result alterStatus(Long id) {
        Optional<BankAccount> bankAccountOptional = bankAccountDAO.byId(id);
        Result result = utils.valid(bankAccountOptional, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountOptional.get();
        bankAccount.setActive(!bankAccount.isActive());
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }


}
