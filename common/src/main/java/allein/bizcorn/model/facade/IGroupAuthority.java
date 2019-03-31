package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface IGroupAuthority extends Serializable {
    public String getId();

    public void setId(String id);

    public String getGroupId();

    public void setGroupId(String groupId);

    public String getAuthority();

    public void setAuthority(String authority);

}
