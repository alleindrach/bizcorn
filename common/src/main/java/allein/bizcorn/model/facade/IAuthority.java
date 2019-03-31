package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface IAuthority extends Serializable {
    public String getId();

    public void setId(String id);

    public String getUserId();

    public void setUserId(String userId) ;

    public String getAuthority();

    public void setAuthority(String authority);

}
