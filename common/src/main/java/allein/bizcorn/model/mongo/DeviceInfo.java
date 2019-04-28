package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IDeviceInfo;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.mongodb.core.mapping.DBRef;


public class DeviceInfo implements IDeviceInfo{
    @DBRef
    private IUser owner;
    private Integer lightness;
    private Integer loundness;
    private Integer gameSwitch;
    private Integer soundalarm;

    public IUser getOwner() {
        return owner;
    }

    public void setOwner(IUser owner) {
        this.owner = owner;
    }

    public Integer getLightness() {
        return lightness;
    }

    public void setLightness(Integer lightness) {
        this.lightness = lightness;
    }

    public Integer getLoundness() {
        return loundness;
    }

    public void setLoundness(Integer loundness) {
        this.loundness = loundness;
    }

    public Integer getGameSwitch() {
        return gameSwitch;
    }

    public void setGameSwitch(Integer gameSwitch) {
        this.gameSwitch = gameSwitch;
    }

    public Integer getSoundalarm() {
        return soundalarm;
    }

    public void setSoundalarm(Integer soundalarm) {
        this.soundalarm = soundalarm;
    }
}
