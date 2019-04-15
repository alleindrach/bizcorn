package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IKid;
import allein.bizcorn.model.facade.IParent;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection="Users")
public class Kid extends User implements IKid {

    @Indexed(unique = true)
    String key;

    @DBRef
    List<IParent> parents;

    @DBRef
    private IUser owner;

    public List<IParent> getParents() {
        return parents;
    }

    public void setParents(List<IParent> parents) {
        this.parents = parents;
    }

    public IUser getOwner() {
        return owner;
    }

    public void setOwner(IUser owner) {
        this.owner = owner;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
