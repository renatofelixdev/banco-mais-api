package dao;

import com.avaje.ebean.Model;
import models.TokenAccess;

import javax.security.auth.callback.TextOutputCallback;
import java.util.Optional;

public class TokenAccessDAO {
    private Model.Finder<Long, TokenAccess> tokenAccessFinder = new Model.Finder<>(TokenAccess.class);

    public Optional<TokenAccess> byToken(String token){
        return Optional.ofNullable(tokenAccessFinder.where().eq("token", token).findUnique());
    }
}
