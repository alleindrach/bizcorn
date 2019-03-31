package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface IGroupMember extends Serializable {
    public String getId();

    public void setId(String id) ;

    public String getGroupId();

    public void setGroupId(String groupId);

}
