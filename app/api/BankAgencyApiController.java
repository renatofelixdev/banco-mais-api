package api;

import authenticators.UserMasterAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import dao.BankAgencyDAO;
import dao.BankDAO;
import enums.NotificationStatus;
import helpers.BankAgencyHelper;
import models.Bank;
import models.BankAgency;
import models.Notification;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.NameEntity;
import util.Utils;
import validators.BankAgencyValidator;

import javax.inject.Inject;
import java.util.Optional;

public class BankAgencyApiController extends Controller implements ApiController {

    @Inject
    private BankDAO bankDAO;

    @Inject
    private BankAgencyDAO bankAgencyDAO;

    @Inject
    private Utils utils;

    @Inject
    private BankAgencyValidator bankAgencyValidator;

    @Inject
    private BankAgencyHelper bankAgencyHelper;

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result all() {
        return utils.ok(Json.toJson(bankAgencyDAO.all()));
    }


    public Result byBank(Long id) {
        Optional<Bank> bank = bankDAO.byId(id);

        Result result = utils.valid(bank, NameEntity.BANK);
        if (result != null) return result;

        return utils.ok(Json.toJson(bankAgencyDAO.allByBank(id)));
    }

    @Override
    public Result byId(Long id) {
        Optional<BankAgency> bankAgency = bankAgencyDAO.byId(id);

        Result result = utils.valid(bankAgency, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        return utils.ok(Json.toJson(bankAgency));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = bankAgencyValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<Bank> bank = bankDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bank")));

        Result result = utils.valid(bank, NameEntity.BANK);
        if (result != null) return result;

        BankAgency bankAgency = bankAgencyHelper.fill(json, bank.get());
        bankAgency.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_AGENCY + ": " + bankAgency.getName() + " cadastrado(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result update(Long id) {
        JsonNode json = request().body().asJson();

        Notification notification = bankAgencyValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<Bank> bank = bankDAO.byId(Long.valueOf(utils.getValueFromJson(json, "bank")));

        Result result = utils.valid(bank, NameEntity.BANK);
        if (result != null) return result;

        Optional<BankAgency> bankAgencyOptional = bankAgencyDAO.byId(id);

        result = utils.valid(bankAgencyOptional, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAgency bankAgency = bankAgencyOptional.get();
        bankAgency = bankAgencyHelper.fill(bankAgency, json, bank.get());
        bankAgency.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_AGENCY + ": " + bankAgency.getName() + " atualizado(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result delete(Long id) {
        Optional<BankAgency> bankAgencyOptional = bankAgencyDAO.byId(id);

        Result result = utils.valid(bankAgencyOptional, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAgency bankAgency = bankAgencyOptional.get();
        bankAgency.setRemoved(true);
        bankAgency.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK_AGENCY + ": " + bankAgency.getName() + " removido(a) com sucesso!")));
    }

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
    public Result alterStatus(Long id) {
        Optional<BankAgency> bankAgencyOptional = bankAgencyDAO.byId(id);

        Result result = utils.valid(bankAgencyOptional, NameEntity.BANK_AGENCY);
        if (result != null) return result;

        BankAgency bankAgency = bankAgencyOptional.get();
        bankAgency.setActive(!bankAgency.isActive());
        bankAgency.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }
}
