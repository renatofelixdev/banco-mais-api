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
import java.util.Optional;

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
        Optional<UserClient> userClient = userClientDAO.byId(id);

        Result result = utils.valid(userClient, NameEntity.USER_CLIENT);
        if (result != null) return result;

        return utils.ok(Json.toJson(userClient.get()));
    }

    @Override
    public Result save() {
        JsonNode json = request().body().asJson();

        Notification notification = userClientValidator.hasErrors(json);
        if (notification.getStatus() == NotificationStatus.ERROR) {
            return utils.badRequest(Json.toJson(notification));
        }

        Optional<UserClient> user = userClientDAO.byCpf(utils.getValueFromJson(json, "cpf"));
        if(user.isPresent()){
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Este CPF já está cadastrado!")));
        }

        UserClient userClient = verifyUser(json);
        userClient.save();

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

        Optional<UserClient> userClientOptional = userClientDAO.byId(id);

        Result result = utils.valid(userClientOptional, NameEntity.USER_CLIENT);
        if (result != null) return result;

        Optional<UserClient> user = userClientDAO.byCpf(utils.getValueFromJson(json, "cpf"));
        UserClient finalUserClient = userClientOptional.get();
        if(user.isPresent()){
            if(!user.get().getId().equals(finalUserClient.getId())){
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                        "Este CPF já está cadastrado!")));
            }
        }

        UserClient userClient = userClientOptional.get();
        userClient = userClientHelper.fill(userClient, json);

        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.USER_CLIENT+ " " + userClient.getName() +" salvo com sucesso!")));
    }

    @Override
    public Result delete(Long id) {
        Optional<UserClient> userClientOptional = userClientDAO.byId(id);

        Result result = utils.valid(userClientOptional, NameEntity.USER_CLIENT);
        if (result != null) return result;

        UserClient userClient = userClientOptional.get();
        userClient.setRemoved(true);
        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                NameEntity.USER_CLIENT + ": " + userClient.getName() + " removido(a) com sucesso!")));
    }

    @Override
    public Result alterStatus(Long id) {
        Optional<UserClient> userClientOptional = userClientDAO.byId(id);

        Result result = utils.valid(userClientOptional, NameEntity.USER_CLIENT);
        if (result != null) return result;

        UserClient userClient = userClientOptional.get();
        userClient.setActive(!userClient.isActive());
        userClient.update();

        return utils.ok(Json.toJson(utils.notification(NotificationStatus.SUCCESS,
                "Status alterado com sucesso!")));
    }

    public UserClient verifyUser(JsonNode json){
        Optional<UserClient> userClient = userClientDAO.byCpf(utils.getValueFromJson(json, "cpf"));
        //JAVA 8 OPTIONAL E LAMBDAS
        return userClient
                .map(u -> {
                        userClientHelper.fill(u,json);
                        return u;
                })
                .orElseGet(() -> {
                        return userClientHelper.fill(json);
                });

        /*if(userClient == null){
            userClient = userClientHelper.fill(json);
            userClient.save();
        }else{
            userClient = userClientHelper.fill(userClient, json);
            userClient.update();
        }
        return userClient;*/
    }
}
