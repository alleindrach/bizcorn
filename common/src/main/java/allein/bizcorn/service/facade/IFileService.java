package allein.bizcorn.service.facade;

import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.gate.IFileServiceGate;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;

public interface IFileService extends IFileServiceGate {


    public GridFSFile getFileByName( String fileName)
            throws IOException;

    public GridFSFile getFile(String id) throws IOException;

    public String getFileUrl(String id) ;
/*
@Description:根据多文件上传的结果集，读取文件ID
@Param:
multiUploadResult 文件上传结果集，其形式为
{
[filename]:
    {
        state:0/1
        data:fileurl/fileId
    }
....
}
@Return:
如果文件已入库，则返回id
如果文件是互联网文件，则返回url
@Author:Alleindrach@gmail.com
@Date:2019/5/7
@Time:6:11 PM
*/
    public String getFileID(Result multiUploadResult,String fileName);

}
