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
    private boolean canBind=true;
    @DBRef
    private User parent;

    public Kid(){
        this.role=Role.KID;//kid
    }

    public boolean isCanBind() {
        return canBind;
    }

    public void setCanBind(boolean canBind) {
        this.canBind = canBind;
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

    public boolean isValidElder(String phoneNumber){
        if(phoneNumber==null ||phoneNumber.isEmpty() )
            return false;
        if(this.elderNumbers==null || this.elderNumbers.size()<=0)
        {
            if(phoneNumber.compareToIgnoreCase(this.parent.getMobile())==0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }else{
            if(phoneNumber.compareToIgnoreCase(parent.getMobile())==0)
                return true;
            for(String number:this.elderNumbers){
                if(number.compareToIgnoreCase(phoneNumber)==0){
                    return true;
                }
            }
        }

        return false;
    }
}
