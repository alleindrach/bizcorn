package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ILog;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.List;

public class Log implements ILog {
    @Id
    private String id;
    @Indexed
    private String userId ;
    @Indexed
    private Date date;
    private Character character;
    private String message;
    private String picture;
    private String voice;
    private List<Tag> tags;
}
