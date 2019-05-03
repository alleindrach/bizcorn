package allein.bizcorn.service.facade;

import allein.bizcorn.service.facade.gate.IFileServiceGate;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;

public interface IFileService extends IFileServiceGate {


    public GridFSFile getFileByName( String fileName)
            throws IOException;

    public GridFSFile getFile(String id) throws IOException;

}
