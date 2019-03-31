package allein.bizcorn.model.facade;

import java.io.Serializable;
import java.util.Date;

public interface IPersistentLogin extends Serializable {
    public String getId();

    public void setId(String id);

    public String getUserId();

    public void setUserId(String userId) ;

    public String getSeries();

    public void setSeries(String series);

    public String getToken();

    public void setToken(String token);

    public Date getLastUsed();

    public void setLastUsed(Date lastUsed);

}
