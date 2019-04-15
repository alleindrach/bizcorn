package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IStory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Story")
public class Story implements IStory {
    @Id
    String id;

}
