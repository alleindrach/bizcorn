package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IProfile;

import java.util.Date;
import java.util.List;

public class Profile  implements IProfile {
    String kindGarden;//幼儿园id
    String face;//头像地址
    String mail;//邮件地址
    List phones;//电话
    String sex;//性别 F=女性，M=男性
    String realname; //真实姓名
    String gender;
    Double lat;//经度
    Double lng;//纬度
    Date brithDay;//出生日期
    String address;//地址
    Long point=0L;//积分
    Date lastvisit;//最近访问时间
    Integer  moodstate= 0;//当前表情
    String inviterId;//邀请人
    String desc;//个人简介
    List<Tag> tags;//标签组

    public String getKindGarden() {
        return kindGarden;
    }

    public void setKindGarden(String kindGarden) {
        this.kindGarden = kindGarden;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List getPhones() {
        return phones;
    }

    public void setPhones(List phones) {
        this.phones = phones;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public Date getBrithDay() {
        return brithDay;
    }

    public void setBrithDay(Date brithDay) {
        this.brithDay = brithDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public Date getLastvisit() {
        return lastvisit;
    }

    public void setLastvisit(Date lastvisit) {
        this.lastvisit = lastvisit;
    }

    public Integer getMoodstate() {
        return moodstate;
    }

    public void setMoodstate(Integer moodstate) {
        this.moodstate = moodstate;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
