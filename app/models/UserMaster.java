package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class UserMaster extends ModelMaster {
    private String login;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne
    private TokenAccess tokenAccess;

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

    public TokenAccess getTokenAccess() {
        return tokenAccess;
    }

    public void setTokenAccess(TokenAccess tokenAccess) {
        this.tokenAccess = tokenAccess;
    }
}
