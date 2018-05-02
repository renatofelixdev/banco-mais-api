package dao;

import com.avaje.ebean.Model;
import models.UserClient;
import models.UserMaster;

import java.util.Optional;

public class UserMasterDAO {

    private Model.Finder<Long, UserMaster> userMasterFinder = new Model.Finder<>(UserMaster.class);

    public Optional<UserMaster> withLoginAndPassword(String login, String password){
        return Optional.ofNullable(userMasterFinder.where()
        .eq("removed", false)
        .eq("login", login)
        .eq("password", password)
        .findUnique());
    }

    public Optional<UserMaster> withToken(String token) {
        return Optional.ofNullable(userMasterFinder.where()
        .eq("removed", false)
        .eq("tokenAccess.token", token)
        .findUnique());
    }
}
