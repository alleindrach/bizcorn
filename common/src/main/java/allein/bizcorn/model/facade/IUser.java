package allein.bizcorn.model.facade;

import allein.bizcorn.model.output.IResultor;

import java.io.Serializable;

public interface IUser extends IResultor {

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
