package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IStory;
import org.springframework.data.annotation.Id;

public class Story implements IStory {
    @Id
    String id;

}
