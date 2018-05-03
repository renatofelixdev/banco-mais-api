package api;

import authenticators.UserClientAuthenticator;
import authenticators.UserMasterAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import dao.TokenAccessDAO;
import dao.UserClientDAO;
import enums.NotificationStatus;
import helpers.UserClientHelper;
import models.Notification;
import models.TokenAccess;
import models.UserClient;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.h2.engine.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.GeneralAttr;
import util.NameEntity;
import util.OAuthAttr;
import util.Utils;
import validators.UserClientValidator;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UserClientApiController extends Controller implements ApiController {

    @Inject
    private UserClientDAO userClientDAO;

    @Inject
    private TokenAccessDAO tokenAccessDAO;

    @Inject
    private Utils utils;

    @Inject
    private UserClientValidator userClientValidator;

    @Inject
    private UserClientHelper userClientHelper;

    @Override
    @Security.Authenticated(UserMasterAuthenticator.class)
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
    @Security.Authenticated(UserMasterAuthenticator.class)
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
    @Security.Authenticated(UserMasterAuthenticator.class)
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
    @Security.Authenticated(UserMasterAuthenticator.class)
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
    @Security.Authenticated(UserMasterAuthenticator.class)
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
                        u = userClientHelper.fill(u,json);
                        u.update();
                        return u;
                })
                .orElseGet(() -> {
                        UserClient u = userClientHelper.fill(json);
                        u.save();
                        return u;
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

    public Result login(){
        try {
            JsonNode json = request().body().asJson();

            String clientId = utils.getValueFromJson(json, "clientId");
            String clientSecret = utils.getValueFromJson(json, "clientSecret");
            String password = utils.getValueFromJson(json, "password");
            String agency = utils.getValueFromJson(json, "agency");
            String account = utils.getValueFromJson(json, "account");

            if(!clientId.equals(OAuthAttr.CLIENT_ID)){
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                        "Falha na autenticação!")));
            }

            if(!clientSecret.equals(OAuthAttr.CLIENT_SECRET)){
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                        "Falha na autenticação!")));
            }

            Optional<UserClient> userClientOptional = userClientDAO.search(utils.safePassword(password), agency, account);
            if(userClientOptional.isPresent()) {
                OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                String token = oauthIssuerImpl.accessToken();
                UserClient userClient = userClientOptional.get();

                if(userClient.getTokenAccess() == null) {
                    TokenAccess tokenAccess = new TokenAccess(token);
                    tokenAccess.save();
                    userClient.setTokenAccess(tokenAccess);
                    userClient.update();
                }else {
                    Instant timeNow = Instant.now();
                    Duration duration = Duration.between(userClient.getTokenAccess().getDateCreated().toInstant(), timeNow);

                    //24hrs RENOVAÇÃO DO TOKEN
                    if (duration.getSeconds() > GeneralAttr.EXPIRATION_TIME) {
                        TokenAccess tokenAccess = userClient.getTokenAccess();

                        TokenAccess newToken = new TokenAccess(token);
                        newToken.save();
                        userClient.setTokenAccess(newToken);
                        userClient.update();

                        tokenAccess.delete();
                    }
                }

                return utils.ok(Json.toJson(userClient));
            }else{
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                        "Usuário ou senha inválidos!")));
            }

        } catch (OAuthSystemException e) {
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                    "Erro ao tentar autenticar usuário!Tente novamente!")));
        }
    }
}
