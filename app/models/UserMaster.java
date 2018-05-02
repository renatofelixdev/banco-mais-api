package models;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class UserMaster extends ModelMaster {
    private String login;
    private String password;

    @Transient
    private String token;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
