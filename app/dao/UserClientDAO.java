package dao;

import com.avaje.ebean.Model;
import models.UserClient;

import java.util.List;
import java.util.Optional;

public class UserClientDAO implements ApiDAO {
    private Model.Finder<Long, UserClient> userClientFinder = new Model.Finder<>(UserClient.class);

    @Override
    public List<UserClient> all() {
        return userClientFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public Optional<UserClient> byId(Long id) {
        return Optional.ofNullable(userClientFinder.byId(id));
    }

    public Optional<UserClient> byCpf(String cpf) {
        return Optional.ofNullable(userClientFinder.where()
                .eq("removed", false)
                .eq("cpf", cpf)
                .findUnique());
    }

    public Optional<UserClient> search(String password, String agency, String account) {
        return  Optional.ofNullable(userClientFinder.where()
                    .eq("removed", false)
                    .eq("password", password)
                    .eq("bankAccountList.number", account)
                    .eq("bankAccountList.bankAgency.code", agency)
                    .findUnique());
    }
}
