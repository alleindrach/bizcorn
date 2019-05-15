package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

public class Profile  implements IProfile {
    @Getter
    @Setter
    String kindGarden;//幼儿园id
    @Getter
    @Setter
    String face;//头像地址
    @Getter
    @Setter
    String mail;//邮件地址
    @Getter
    @Setter
    List phones;//电话
    @Getter
    @Setter
    String sex;//性别 F=女性，M=男性
    @Getter
    @Setter
    String realname; //真实姓名
    @Getter
    @Setter
    String gender;
    @Getter
    @Setter
    Double lat;//经度
    @Getter
    @Setter
    Double lng;//纬度
    @Getter
    @Setter
    Date brithDay;//出生日期
    @Getter
    @Setter
    String address;//地址
    @Getter
    @Setter
    Long point=0L;//积分
    @Getter
    @Setter
    Date lastvisit;//最近访问时间
    @Getter
    @Setter
    Integer  moodstate= 0;//当前表情
    @Getter
    @Setter
    String inviterId;//邀请人
    @Getter
    @Setter
    String desc;//个人简介
    @Getter
    @Setter
    List<String> tags;//标签组

}
