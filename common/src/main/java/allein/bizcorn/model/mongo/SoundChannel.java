/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ILog;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Document(collection="SoundChannel")
public class SoundChannel implements Serializable {
    @Id
    @Getter
    @Setter
    private String id;
    @Indexed(unique = true)
    @Getter
    @Setter
    private Integer index ;
    @Getter
    @Setter
    private String  img;
    @Getter
    @Setter
    @Indexed(unique = true)
    private String name;
    @Getter
    @Setter
    private String desc;
}
