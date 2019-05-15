package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ICharacter;
import lombok.Getter;
import lombok.Setter;

public class Character implements ICharacter{
    @Getter
    @Setter
    public float height;//身高
    @Getter
    @Setter
    public float weight;//体重
}
