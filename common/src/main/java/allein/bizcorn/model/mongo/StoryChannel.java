/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection="StoryChannel")
@CompoundIndexes({
        @CompoundIndex(name = "story_channel_index", def = "{'index', 1, 'currentKid': 1}")

})
public class StoryChannel implements Serializable {
    @Id
    private String id;
    @Indexed
    private Integer index ;
    @DBRef
    private  Story currentStory;
    @DBRef
    private Kid currentKid;

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

    public Story getCurrentStory() {
        return currentStory;
    }

    public void setCurrentStory(Story currentStory) {
        this.currentStory = currentStory;
    }

    public Kid getCurrentKid() {
        return currentKid;
    }

    public void setCurrentKid(Kid currentKid) {
        this.currentKid = currentKid;
    }
}
