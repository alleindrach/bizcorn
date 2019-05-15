package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IScene;
import lombok.Getter;
import lombok.Setter;

public class Scene implements IScene {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String img;
    @Getter
    @Setter
    private String snd;

}
