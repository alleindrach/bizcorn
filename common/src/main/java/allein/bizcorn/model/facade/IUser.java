package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface IUser extends Serializable {

    public String getId();

    public void setId(String id);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public Integer getEnabled();

    public void setEnabled(Integer enabled);

    public String getMobile() ;

    public void setMobile(String mobile);
}
