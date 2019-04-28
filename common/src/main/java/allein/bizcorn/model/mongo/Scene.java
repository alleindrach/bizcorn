package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IScene;

public class Scene implements IScene {

    private String title;
    private String img;
    private String snd;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSnd() {
        return snd;
    }

    public void setSnd(String snd) {
        this.snd = snd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
