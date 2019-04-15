package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IScene;

import java.util.List;

public class Scene implements IScene {
    private List<Material> materialList;
    private String title;
    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
