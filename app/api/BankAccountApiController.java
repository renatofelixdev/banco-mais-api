package api;

import com.fasterxml.jackson.databind.JsonNode;
import dao.BankAccountDAO;
import dao.BankAgencyDAO;
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
import play.mvc.Result;
import util.NameEntity;
import util.Utils;
import validators.BankAccountValidator;
import validators.UserClientValidator;

import javax.inject.Inject;

public class BankAccountApiController extends Controller implements ApiController {

    @Inject
    private BankAccountDAO bankAccountDAO;

    @Inject
    private BankAgencyDAO bankAgencyDAO;

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

    @Override
    public Result all() {
        return utils.ok(Json.toJson(bankAccountDAO.all()));
    }

    @Override
    public Result byId(Long id) {
        BankAccount bankAccount = bankAccountDAO.byId(id);

        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        return utils.ok(Json.toJson(bankAccount));
    }

    @Override
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        UserClient userClient = userClientHelper.fill(json);
        userClient.save();

        utils.putValue(json, "userClient", userClient.getId()+"");

        notification = bankAccountValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        BankAgency bankAgency = bankAgencyDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bankAgency")));

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountHelper.fill(json, bankAgency);
        bankAccount.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT +" cadastrado(a) com sucesso!")));
    }

    @Override
    public Result update(Long id) {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        UserClient userClient = userClientHelper.fill(json);
        //TODO VALIDAR CADASTRO
        userClient.save();

        utils.putValue(json, "userClient", userClient.getId()+"");

        notification = bankAccountValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        BankAgency bankAgency = bankAgencyDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bankAgency")));

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAccount bankAccount = bankAccountDAO.byId(id);
        result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        bankAccount = bankAccountHelper.fill(bankAccount, json, bankAgency);
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT +" atualizado(a) com sucesso!")));
    }

    @Override
    public Result delete(Long id) {
        BankAccount bankAccount = bankAccountDAO.byId(id);
        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        bankAccount.setRemoved(true);
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_ACCOUNT + ": " + bankAccount.getNumber() + " removido(a) com sucesso!")));
    }

    @Override
    public Result alterStatus(Long id) {
        BankAccount bankAccount = bankAccountDAO.byId(id);
        Result result = utils.valid(bankAccount, NameEntity.BANK_ACCOUNT);
        if (result != null) return result;

        bankAccount.setActive(!bankAccount.isActive());
        bankAccount.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }
}
