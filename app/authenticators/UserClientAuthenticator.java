package authenticators;

import dao.UserClientDAO;
import enums.NotificationStatus;
import models.UserClient;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
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
        token = ctx.request().getHeader("Authorization").toString();

        Optional<UserClient> userClientOptional = userClientDAO.withToken(token);

        if(userClientOptional.isPresent()){
            UserClient userClient = userClientOptional.get();

            Instant timeNow = Instant.now();
            Duration duration = Duration.between(userClient.getTokenAccess().getDateCreated().toInstant(), timeNow);

            //24hrs RENOVAÇÃO DO TOKEN
            if (duration.getSeconds() > 86400) {
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
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Sessão expirada!")));
        }
        return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                "Acesso não autorizado!")));
    }
}
