package dao;

import com.avaje.ebean.Model;
import models.UserClient;

import java.util.List;

public class UserClientDAO implements ApiDAO {
    private Model.Finder<Long, UserClient> userClientFinder = new Model.Finder<>(UserClient.class);

    @Override
    public List<UserClient> all() {
        return userClientFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public UserClient byId(Long id) {
        return userClientFinder.byId(id);
    }
}
