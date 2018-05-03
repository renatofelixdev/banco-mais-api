package api;

import com.fasterxml.jackson.databind.JsonNode;
import dao.UserMasterDAO;
import enums.NotificationStatus;
import models.TokenAccess;
import models.UserClient;
import models.UserMaster;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.h2.engine.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.GeneralAttr;
import util.OAuthAttr;
import util.Utils;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UserMasterApiController extends Controller {

    @Inject
    private UserMasterDAO userMasterDAO;

    @Inject
    private Utils utils;

    public Result create(String login, String password){
        if(login != null && !login.isEmpty() && password != null && !password.isEmpty()){
            UserMaster userMaster = new UserMaster();
            userMaster.setLogin(login);
            userMaster.setPassword(utils.safePassword(password));
            userMaster.save();
            return ok("Cadastrado com sucesso!");
        }
        return badRequest("Erro ao tentar cadastrar!");
    }

    public Result login(){
        try {
            JsonNode json = request().body().asJson();

            String clientId = utils.getValueFromJson(json, "clientId");
            String clientSecret = utils.getValueFromJson(json, "clientSecret");
            String password = utils.getValueFromJson(json, "password");
            String login = utils.getValueFromJson(json, "login");

            if(!clientId.equals(OAuthAttr.CLIENT_ID)){
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                        "Falha na autenticação!")));
            }

            if(!clientSecret.equals(OAuthAttr.CLIENT_SECRET)){
                return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                        "Falha na autenticação!")));
            }

            Optional<UserMaster> userMasterOptional = userMasterDAO.withLoginAndPassword(login, utils.safePassword(password));
            if(userMasterOptional.isPresent()) {
                OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                String token = oauthIssuerImpl.accessToken();
                UserMaster userMaster = userMasterOptional.get();

                if(userMaster.getTokenAccess() == null) {
                    TokenAccess tokenAccess = new TokenAccess(token);
                    tokenAccess.save();
                    userMaster.setTokenAccess(tokenAccess);
                    userMaster.update();
                }else {
                    Instant timeNow = Instant.now();
                    Duration duration = Duration.between(userMaster.getTokenAccess().getDateCreated().toInstant(), timeNow);

                    //24hrs RENOVAÇÃO DO TOKEN
                    if (duration.getSeconds() > GeneralAttr.EXPIRATION_TIME) {
                        TokenAccess tokenAccess = userMaster.getTokenAccess();

                        TokenAccess newToken = new TokenAccess(token);
                        newToken.save();
                        userMaster.setTokenAccess(newToken);
                        userMaster.update();

                        tokenAccess.delete();
                    }
                }

                return utils.ok(Json.toJson(userMaster));
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
