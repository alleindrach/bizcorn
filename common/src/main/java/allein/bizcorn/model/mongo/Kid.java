package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IKid;
import allein.bizcorn.model.facade.IParent;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="Users")
public class Kid extends User implements IKid {


    List<String> elderNumbers;//可绑定的长辈的电话号码

    @DBRef
    private User parent;

    public Kid(){
        this.role=1;//kid
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public List<String> getElderNumbers() {
        return elderNumbers;
    }

    public void setElderNumbers(List<String> elderNumbers) {
        this.elderNumbers = elderNumbers;
    }
}
