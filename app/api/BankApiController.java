package api;

import com.fasterxml.jackson.databind.JsonNode;
import dao.BankDAO;
import enums.NotificationStatus;
import helpers.BankHelper;
import models.Bank;
import models.Notification;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.NameEntity;
import util.Utils;
import validators.BankValidator;

import javax.inject.Inject;
import java.util.Optional;

public class BankApiController extends Controller implements ApiController {

    @Inject
    private BankDAO bankDAO;

    @Inject
    private Utils utils;

    @Inject
    private BankValidator bankValidator;

    @Inject
    private BankHelper bankHelper;

    @Override
    public Result all() {
        return utils.ok(Json.toJson(bankDAO.all()));
    }

    @Override
    public Result byId(Long id) {
        Optional<Bank> bank = bankDAO.byId(id);

        Result result = utils.valid(bank, NameEntity.BANK);
        if(result != null) return result;

        return utils.ok(Json.toJson(bank));
    }

    @Override
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = bankValidator.hasErrors(json);
        if(notification.getStatus() == NotificationStatus.ERROR){
            return utils.badRequest(Json.toJson(notification));
        }

        Bank bank = bankHelper.fill(json);
        bank.save();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK + ": " +bank.getName() + " cadastrado(a) com sucesso!")));
    }

    @Override
    public Result update(Long id) {
        JsonNode json = request().body().asJson();

        Notification notification = bankValidator.hasErrors(json);
        if(notification.getStatus() == NotificationStatus.ERROR){
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<Bank> bankOptional = bankDAO.byId(id);

        Result result = utils.valid(bankOptional, NameEntity.BANK);
        if(result != null) return result;

        Bank bank = bankOptional.get();
        bank = bankHelper.fill(bank, json);
        bank.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK + ": " +bank.getName() + " atualizado(a) com sucesso!")));
    }

    @Override
    public Result delete(Long id) {
        Optional<Bank> bankOptional = bankDAO.byId(id);

        Result result = utils.valid(bankOptional, NameEntity.BANK);
        if(result != null) return result;

        Bank bank = bankOptional.get();
        bank.setRemoved(true);
        bank.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.BANK + ": " +bank.getName() + " removido(a) com sucesso!")));
    }

    @Override
    public Result alterStatus(Long id) {
        Optional<Bank> bankOptional = bankDAO.byId(id);

        Result result = utils.valid(bankOptional, NameEntity.BANK);
        if(result != null) return result;

        Bank bank = bankOptional.get();
        bank.setActive(!bank.isActive());
        bank.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }
}
