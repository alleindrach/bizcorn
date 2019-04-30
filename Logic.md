##业务逻辑
* 用户
    角色：role:=0 成人/家长,:=1 小童,
    * 注册 /register
       - 参数：
         - mobile 电话
         - mobilecaptcha 验证码
         - username 用户名
         - password 密码
    
    * 当前对话对象[curPartner]，通过绑定过程确定
    
* Kid 小童，特殊用户，
    * 小童注册通过接口
    /kid/register
    参数：mac mac地址
    权限：管理员 HasRole('ADMIN')
    
    * 凭mac地址自动登录,不需要验证码
    /login/username=mac&password=mac
    * 小童的owner 在初次绑定时设定，不允许再次修改。
    * 小童有8个可绑定的所有用户电话号码，由owner设定，和owner构成ownerGroup，只有在ownerGroup中的**用户**，才能进行绑定。
    * 小童和小童间的绑定，不受ownerGroup的限制。
    * 当小童的ownerGroup用户发起对话请求时，小童的当前对话对象自动切换至ownerGroup对象。

* 绑定
/user/bind/{kidmac}
  * 用户绑定小童
    * 如果小童是初次绑定kid.owner==null，则设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
    * 如果kid.owner!=null,且user in kid.ownerGroup ，设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
    * 如果kid.owner!=null,且user not in kid.ownerGroup ，绑定失败。
  * 小童绑定小童
    * 设定双方小童的curPartner=对方。
    
* 设定小童可绑定属性
/user/canbind/{kidmac}
    * 首先确认user必须是kid的owner或者ownergroup，否则返回错误。
    * 设定kid.canBindByOtherkids 
* 消息发送默认发向当前的curPartner，如果当前没有curPartner,消息发送失败。


    
    
    