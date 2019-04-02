package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IDevice;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class Device extends User implements IDevice {

    @Indexed(unique = true)
    String key;

    @Indexed
    String ownerId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
