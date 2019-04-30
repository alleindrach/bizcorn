package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IProfile;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection="Users")
public class User  implements  IUser {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;
    @Indexed
    private String mobile;

    private Integer enabled;

    private IProfile profile;

    Set<Authority> Authorities;

    private Date createDate;

    @DBRef
    private User curPartner;

    protected  Role role=Role.ADULT;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

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

    public IProfile getProfile() {
        return profile;
    }

    public void setProfile(IProfile profile) {
        this.profile = profile;
    }

    public Set<Authority> getAuthorities() {
        return Authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        Authorities = authorities;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getCurPartner() {
        return curPartner;
    }

    public void setCurPartner(User curPartner) {
        this.curPartner = curPartner;
    }

}
