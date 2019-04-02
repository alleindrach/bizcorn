package allein.bizcorn.model.facade;

import java.io.Serializable;

public interface ITag extends Serializable {
    public String getValue() ;

    public void setValue(String value) ;

    public String getCatalog();

    public void setCatalog(String catalog);
}
