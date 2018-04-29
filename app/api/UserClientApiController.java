package api;

import com.fasterxml.jackson.databind.JsonNode;
import dao.UserClientDAO;
import enums.NotificationStatus;
import helpers.UserClientHelper;
import models.Notification;
import models.UserClient;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.NameEntity;
import util.Utils;
import validators.UserClientValidator;

import javax.inject.Inject;

public class UserClientApiController extends Controller implements ApiController {

    @Inject
    private UserClientDAO userClientDAO;

    @Inject
    private Utils utils;

    @Inject
    private UserClientValidator userClientValidator;

    @Inject
    private UserClientHelper userClientHelper;

    @Override
    public Result all() {
        return utils.ok(Json.toJson(userClientDAO.all()));
    }

    @Override
    public Result byId(Long id) {
        UserClient userClient = userClientDAO.byId(id);

        Result result = utils.valid(userClient, NameEntity.USER_CLIENT);
        if (result != null) return result;

        return utils.ok(Json.toJson(userClient));
    }

    @Override
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        UserClient userClient = verifyUser(json);

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.USER_CLIENT+ " " + userClient.getName() +" salvo com sucesso!")));
    }

    @Override
    public Result update(Long id) {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        UserClient userClient = userClientDAO.byId(id);

        Result result = utils.valid(userClient, NameEntity.USER_CLIENT);
        if (result != null) return result;

        UserClient user = userClientDAO.byCpf(utils.getValueFromJson(json, "cpf"));
        if(user != null && !user.getId().equals(userClient.getId()))
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Este CPF já está cadastrado!")));

        userClient = userClientHelper.fill(userClient, json);

        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.USER_CLIENT+ " " + userClient.getName() +" salvo com sucesso!")));
    }

    @Override
    public Result delete(Long id) {
        UserClient userClient = userClientDAO.byId(id);

        Result result = utils.valid(userClient, NameEntity.USER_CLIENT);
        if (result != null) return result;

        userClient.setRemoved(true);
        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.USER_CLIENT + ": " + userClient.getName() + " removido(a) com sucesso!")));
    }

    @Override
    public Result alterStatus(Long id) {
        UserClient userClient = userClientDAO.byId(id);

        Result result = utils.valid(userClient, NameEntity.USER_CLIENT);
        if (result != null) return result;

        userClient.setActive(!userClient.isActive());
        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }

    public UserClient verifyUser(JsonNode json){
        UserClient userClient = userClientDAO.byCpf(utils.getValueFromJson(json, "cpf"));
        if(userClient == null){
            userClient = userClientHelper.fill(json);
            userClient.save();
        }else{
            userClient = userClientHelper.fill(userClient, json);
            userClient.update();
        }
        return userClient;
    }
}
