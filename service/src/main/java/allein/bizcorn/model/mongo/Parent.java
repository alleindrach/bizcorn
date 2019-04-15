package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IKid;
import allein.bizcorn.model.facade.IParent;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection="Users")
public class Parent extends User implements IParent {

    @DBRef
    private List<IKid> kids;

}
