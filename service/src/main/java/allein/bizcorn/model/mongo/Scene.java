package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IScene;

import java.util.List;

public class Scene implements IScene {

    private String title;
    private String imageSource;
    private String soundSource;

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getSoundSource() {
        return soundSource;
    }

    public void setSoundSource(String soundSource) {
        this.soundSource = soundSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
