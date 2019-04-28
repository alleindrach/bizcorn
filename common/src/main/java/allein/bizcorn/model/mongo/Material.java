package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IMaterial;

public class Material  implements IMaterial{

    private String type;
    private String fileId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
