package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IKid;
import allein.bizcorn.model.facade.IParent;
import allein.bizcorn.model.facade.IUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="Users")
public class Kid extends User implements IKid {

    @Getter
    @Setter
    private List<String> elderNumbers;//可绑定的长辈的电话号码
    @Getter
    @Setter
    private boolean canBind=true;
    @DBRef(lazy=true)
    @Getter
    @Setter
    private User parent;

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
