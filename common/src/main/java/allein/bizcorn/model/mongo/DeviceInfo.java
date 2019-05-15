package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IDeviceInfo;
import allein.bizcorn.model.facade.IUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;


public class DeviceInfo implements IDeviceInfo{
    @DBRef
    @Getter
    @Setter
    private User owner;
    @Getter
    @Setter
    private Integer lightness;
    @Getter
    @Setter
    private Integer loundness;
    @Getter
    @Setter
    private Integer gameSwitch;
    @Getter
    @Setter
    private Integer soundalarm;

}
