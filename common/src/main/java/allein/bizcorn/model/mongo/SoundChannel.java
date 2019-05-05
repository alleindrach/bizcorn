/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ILog;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Document(collection="SoundChannel")
public class SoundChannel implements Serializable {
    @Id
    private String id;
    @Indexed
    private Integer index ;
    private String  bgPictureId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getBgPictureId() {
        return bgPictureId;
    }

    public void setBgPictureId(String bgPictureId) {
        this.bgPictureId = bgPictureId;
    }
}
