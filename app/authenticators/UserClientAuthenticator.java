package authenticators;

import dao.UserClientDAO;
import enums.NotificationStatus;
import models.UserClient;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.GeneralAttr;
import util.Utils;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UserClientAuthenticator extends Security.Authenticator {
    @Inject
    private UserClientDAO userClientDAO;

    @Inject
    private Utils utils;

    @Override
    public String getUsername(Http.Context ctx) {
        String token = "";
        try {
            token = ctx.request().getHeader("Authorization").toString();
        }catch (Exception e){
            return null;
        }

        Optional<UserClient> userClientOptional = userClientDAO.withToken(token);

        if(userClientOptional.isPresent()){
            UserClient userClient = userClientOptional.get();

            Instant timeNow = Instant.now();
            Duration duration = Duration.between(userClient.getTokenAccess().getDateCreated().toInstant(), timeNow);

            //24hrs RENOVAÇÃO DO TOKEN
            if (duration.getSeconds() > GeneralAttr.EXPIRATION_TIME) {
                ctx.args.put("tokenExpired", true);
                return null;
            }

            ctx.args.put("userClient", userClient);
            return userClient.getName();
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        if(utils.mapGetValue(ctx.args, "tokenExpired") != null && (boolean)utils.mapGetValue(ctx.args, "tokenExpired")){
            ctx.args.put("tokenExpired", false);
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Sessão expirada!")));
        }
        return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                "Acesso não autorizado!")));
    }
}
