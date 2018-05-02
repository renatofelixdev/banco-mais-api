package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserClient;
import util.Utils;

import javax.inject.Inject;

public class UserClientHelper {

    @Inject
    private Utils utils;

    public UserClient fill(JsonNode json){
        UserClient userClient = new UserClient();
        userClient.setName(utils.getValueFromJson(json, "name"));
        userClient.setCpf(utils.getValueFromJson(json, "cpf"));
        userClient.setAddress(utils.getValueFromJson(json, "address"));
        userClient.setPassword(utils.safePassword(utils.getValueFromJson(json, "password")));
        return userClient;
    }

    public UserClient fill(UserClient userClient, JsonNode json){
        userClient.setName(utils.getValueFromJson(json, "name"));
        userClient.setCpf(utils.getValueFromJson(json, "cpf"));
        userClient.setAddress(utils.getValueFromJson(json, "address"));
        userClient.setPassword(utils.safePassword(utils.getValueFromJson(json, "password")));
        return userClient;
    }
}
