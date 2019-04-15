package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IAuthority;

import java.io.Serializable;

public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    private String authority;

    public String getAuthority() {
        return authority;
    }

    public Authority setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

}
