package authenticators;

import dao.UserClientDAO;
import dao.UserMasterDAO;
import enums.NotificationStatus;
import models.UserClient;
import models.UserMaster;
import org.h2.engine.User;
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

public class UserMasterAuthenticator extends Security.Authenticator {
    @Inject
    private UserMasterDAO userMasterDAO;

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

        Optional<UserMaster> userMasterOptional = userMasterDAO.withToken(token);

        if(userMasterOptional.isPresent()){
            UserMaster userMaster = userMasterOptional.get();

            Instant timeNow = Instant.now();
            Duration duration = Duration.between(userMaster.getTokenAccess().getDateCreated().toInstant(), timeNow);

            //24hrs RENOVAÇÃO DO TOKEN
            if (duration.getSeconds() > GeneralAttr.EXPIRATION_TIME) {
                ctx.args.put("tokenMasterExpired", true);
                return null;
            }

            ctx.args.put("userMaster", userMaster);
            return userMaster.getLogin();
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        if(utils.mapGetValue(ctx.args, "tokenMasterExpired") != null && (boolean)utils.mapGetValue(ctx.args, "tokenMasterExpired")){
            ctx.args.put("tokenMasterExpired", false);
            return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.WARNING,
                    "Sessão expirada!")));
        }
        return utils.badRequest(Json.toJson(utils.notification(NotificationStatus.ERROR,
                "Acesso não autorizado!")));
    }
}
