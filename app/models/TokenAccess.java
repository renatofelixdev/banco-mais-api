package models;

import javax.persistence.Entity;

@Entity
public class TokenAccess extends ModelMaster {

    private String token;

    public TokenAccess(){}

    public TokenAccess(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
