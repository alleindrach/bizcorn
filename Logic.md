##业务
* 接口
    * 注册 /register
       - 参数：
         - mobile 电话
         - mobilecaptcha 验证码
         - username 用户名
         - password 密码
    * 获取图片验证码
        /common/capthca.jpg?v=timestamp
        返回的response会带有一个captchaKey的cookie，下次验证图片验证码时需要携带
        一个图形验证码只能使用一次，不管是否通过验证，都将从cache中删除。
    * 获取短信验证码
       /common/mobile/captcha
       参数：
       mobile:电话号码
       captcha:之前获取的图片验证码。
    * 登录
        /login
        参数: username 用户名，也可以是电话号码
        password:密码
        captcha:非必须，只有超过3次验证错误时才需要，当登录返回CAPTCH_INVALID（000202）时，才需要进行图形验证码验证。
        返回：Result
         当state=1时为成功，data字段为sessionid（cookie为其BASE64编码），
         state=0为失败，具体异常代码见reason字段，
    * 重置密码
        /password/reset
        参数：
            mobile:注册手机
            mobileCaptcha:手机验证码
            password:新的密码.
    * 绑定
        /user/bind/{mac}
        参数：地址中的mac为需要绑定的设备的mac地址（去掉:间隔符）
          * 用户绑定小童
            * 如果小童是初次绑定kid.owner==null，则设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
            * 如果kid.owner!=null,且user in kid.ownerGroup ，设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
            * 如果kid.owner!=null,且user not in kid.ownerGroup ，绑定失败。
          * 小童绑定小童
            * 设定双方小童的curPartner=对方。
    * 小童设备注册：
       /kid/register/{mac}
          参数：mac mac地址
          权限：管理员 HasRole('ADMIN')
    
    * 声音变变变频道背景图片设置
        /story/sound/channel/{index}
        参数：
            index:频道号
            file:频道背景图片，注意，file控件的name字段必须为"file"    
        返回：
            result:state=1表明成功，data为file id
            state =0 表明失败，reason为异常原因。
    * 声音变变变频道背景获取
        /story/sound/channels
        参数:无
        返回： 
            result:state=1表明成功，data为频道信息数组
                   state =0 表明失败，reason为异常原因。
    * 声音变变变消息获取
        /story/sound/{id}
        参数：id为消息id
        返回：result.data 为消息实体，
            {id: message.id,snd:file.id}
    * 声音变变变阅读回执
        /story/sound/{id}/copy
        参数：id为消息id
        返回：result
    * 声音变变变消息获取
        /story/sound/fetch
        参数:
            pageIndex:起始页面
            pageSize:页面长度
            type: =0 全部消息
                  =1 未读消息
        返回：
            result.data => {count:xxx,msgs:[xxx]}
    
            
* websocket
    * 连接地址:[service]/websocket 注意，这里是service服务器的地址，目前无法使用feign进行转发。
    * 服务器消息订阅地址: /topic/message
    * 消息发送地址: /center/message
    * 消息格式:
        {action:actionname, parameters:{...}}
    * 消息定义:
        声音变变变消息到达:
        {action:"sound-message",parameters:{id="",snd:"xxx" }}
              
* 业务逻辑
    1.  注册
        普通用户注册需要提供用户名及手机号码，并需要验证手机验证码，用户名不能全部为数字
        小童设备由后台统一注册，其用户名为mac地址，密码为mac地址。
    2.  登录
        普通用户使用用户名/手机号码、密码登录，在错误次数超过一定限制（3次）时，需要进行图形验证码登录。
        小童设备凭mac地址自动登录,不需要验证码       
    3.  重置密码
        普通用户需要使用手机号码、手机验证码、新密码进行设置。
        小童设备不支持。
    4.  绑定
        * 用户绑定小童
                    * 如果小童是初次绑定kid.owner==null，则设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
                    * 如果kid.owner!=null,且user in kid.ownerGroup ，设定小童的kid.owner=user，kid.curPartner=user，user.curPartner=kid
                    * 如果kid.owner!=null,且user not in kid.ownerGroup ，绑定失败。
        * 小童绑定小童
                    * 设定双方小童的curPartner=对方。   
    5.  小童设定
        在用户绑定小童设备后，可以对绑定的小童设备进行设定。设定包括 
        * 昵称
        * 可绑定的电话号码
        * 图片
        * 是否可以开放给其他小童进行绑定
        * 其他
    6.  声音变变变
    
        6.1.    声音变变变的频道设置
            由管理员进行频道背景动画的设置。
            
        6.2.    声音变变变的录制及上传
            用户录制音频后，上传，服务器收到上传文件后,服务器将文件入库，并产生新的soundMessage入库，通知其curPartner有消息到达，对方再从服务器获取音频文件。
        
        6.3.    对方阅读及标记
            接收方收到消息，并查看后，调用回执接口标记该消息已经被取阅。
       
        6.4.    离线消息处理
            用户上线后，通过未读消息获取接口获取未读消息，提示用户阅读。
        
        6.5.    我的消息
            服务器保存1周内的消息，由后台进行进行清理
        
        6.6.    资料库
            暂时不提供资料库
            
    7.  超级变变变
        7.1 超级变变变作品上传
            需要提供频道及作品的音频、图片
            服务器收到文件及相关信息后，文件入库，生成作品数据，通知其curPartner 有超级变变变作品到达
        7.2 对方取阅及标记
            对方收到消息后，下载作品，加载到相应的频道，发送回执消息到发起者。
        7.3 互动消息
            每次甩动或者切换作品页面，发送消息到curPartner，对方收到消息后进行同步切换。
        7.4 我的作品
            服务器保存1周内的作品，由后台进行清理
        7.5 资料库
            用户可以选择发布，附带标签，发布后的资料（经审核？）放入到公共资料库里，供其他用户检索
            
    8.  分享爱
        
            
       
* 用户
    角色：role:=0 成人/家长,:=1 小童,
       
    * 当前对话对象[curPartner]，通过绑定过程确定
    
* Kid 小童，特殊用户，
    * 小童注册通过接口
    /kid/register
    * 小童登录
    /login/username=mac&password=mac
    * 小童的owner 在初次绑定时设定，不允许再次修改。
    * 小童有8个可绑定的所有用户电话号码，由owner设定，和owner构成ownerGroup，只有在ownerGroup中的**用户**，才能进行绑定。
    * 小童和小童间的绑定，不受ownerGroup的限制。
    * 当小童的ownerGroup用户发起对话请求时，小童的当前对话对象自动切换至ownerGroup对象。

    
* 设定小童可绑定属性
/user/canbind/{kidmac}
    * 首先确认user必须是kid的owner或者ownergroup，否则返回错误。
    * 设定kid.canBindByOtherkids 
* 消息发送默认发向当前的curPartner，如果当前没有curPartner,消息发送失败。


    
    
    