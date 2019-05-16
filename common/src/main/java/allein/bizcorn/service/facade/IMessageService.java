package allein.bizcorn.service.facade;

public interface IMessageService {

    /**
     *
     * @param phoneNumber  短信接收人手机号码
     * @param content 短信内容
     */

    String sendMsg(String phoneNumber,String content);
}
