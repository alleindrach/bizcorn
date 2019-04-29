##业务逻辑
* 用户
    * 注册 /user/register
       - 参数：
         - mobile 电话
         - mobilecaptcha 验证码
         - username 用户名
         - password 密码
    
    * 当前对话对象[curPartner]，通过绑定过程确定
    
* 设备，特殊用户，
    * 设备注册通过接口
    /device/register
    参数：mac mac地址
    权限：管理员 HasRole('Admin')
    
    * 凭mac地址自动登录,不需要验证码
    /user/login/username=mac&password=mac
    * 设备的owner 在初次绑定时设定，不允许再次修改。
    * 设备有8个可绑定的所有用户电话号码，由owner设定，和owner构成ownerGroup，只有在ownerGroup中的**用户**，才能进行绑定。
    * 设备和设备间的绑定，不受ownerGroup的限制。
    * 当设备的ownerGroup用户发起对话请求时，设备的当前对话对象自动切换至ownerGroup对象。

* 绑定
/user/bind/{devicemac}
  * 用户绑定设备
    * 如果设备是初次绑定device.owner==null，则设定设备的device.owner=user，device.curPartner=user，user.curPartner=device
    * 如果device.owner!=null,且user in device.ownerGroup ，设定设备的device.owner=user，device.curPartner=user，user.curPartner=device
    * 如果device.owner!=null,且user not in device.ownerGroup ，绑定失败。
  * 设备绑定设备
    * 设定双方设备的curPartner=对方。
    
* 设定设备可绑定属性
/user/canbind/{devicemac}
    * 首先确认user必须是device的owner或者ownergroup，否则返回错误。
    * 设定device.canBindByOtherDevices 
* 消息发送默认发向当前的curPartner，如果当前没有curPartner,消息发送失败。


    
    
    