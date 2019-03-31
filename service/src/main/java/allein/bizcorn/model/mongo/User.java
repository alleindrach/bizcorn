package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection="Users")
public class User  implements  IUser {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Id
    private String id;

    /**
     *
     */
    @Indexed(unique = true)
    private String username;

    /**
     *
     */
    private String password;
    @Indexed
    private String mobile;
    /**
     *
     */
    private Integer enabled;

    Set<Authority> Authorities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Set<Authority> getAuthoritys() {
        return Authorities;
    }

    public void setAuthoritys(Set<Authority> authoritys) {
        Authorities = authoritys;
    }
}
