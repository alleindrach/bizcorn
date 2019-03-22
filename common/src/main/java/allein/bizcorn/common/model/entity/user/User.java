package allein.bizcorn.common.model.entity.user;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String loginPassword;
    private String mobile;
    private String loginPasswordRandom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLoginPasswordRandom() {
        return loginPasswordRandom;
    }

    public void setLoginPasswordRandom(String loginPasswordRandom) {
        this.loginPasswordRandom = loginPasswordRandom;
    }
}