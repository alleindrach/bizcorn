package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ICharacter;

public class Character implements ICharacter{
    public float height;//身高
    public float weight;//体重

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
