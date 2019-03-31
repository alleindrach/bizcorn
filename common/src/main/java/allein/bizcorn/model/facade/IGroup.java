package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface IGroup extends Serializable{
    public String getId();

    public void setId(String id);

    public String getGroupName();

    public void setGroupName(String groupName);

}
