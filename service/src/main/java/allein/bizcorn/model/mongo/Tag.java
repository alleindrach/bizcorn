package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.ITag;
//标签
public class Tag implements ITag {
    private String value;
    private String catalog;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
