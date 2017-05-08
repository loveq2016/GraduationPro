package app.config.http;

/**
 * SiuJiYung create at 2016-6-1 下午3:07:56
 */

public class HttpConfig {

    public static String getUrl(String path) {
        if (isTest) {
            return TEST_HOST_NAME + path;
        }
        return HOST_NAME + path;
    }

    private static final boolean isTest = false;

    private static final String TEST_HOST_NAME = "http://192.168.1.70:8080/xhapi";

    private static final String HOST_NAME = "https://geju.gzyueyun.com/xhapi";
//    private static final String HOST_NAME = "http://120.25.65.109:8088/xhapi"; //218.244.136.153

//    private static final String TEST_HOST_NAME = "http://192.168.1.120:8080/xhapi";  http://120.25.65.109:8080/xhapi/xhapi/appController/checkUpdate.hn?app_name=geju_android

    // 用户类接口
    // 直播分享
    public static final String SHARE_LIVE = "https://geju.gzyueyun.com/web/geju-app/#/wechat-share/live/";
    // 公告分享
    public static final String SHARE_NOTICE = "https://geju.gzyueyun.com/web/geju-app/#/wechat-share/notice/";
    // 组织分享
    public static final String SHARE_ORG = "https://geju.gzyueyun.com/web/geju-app/#/wechat-share/org/";
    //统计跳转地址
    public static final String JOIN_NOTICE_CENTER = "https://geju.gzyueyun.com/web/geju-app/#/notice-center/";
    //服务条款
    public static final String SERVICE_TERM = "https://geju.gzyueyun.com/web/geju-app/#/service-terms";

    // 获取邀请码
    public static final String GET_CODE = "/xhapi/MemberController/showoInvitationCodeInfo.hn";
    // 登陆
    public static final String LOGIN_URL = "/xhapi/MemberController/login.hn";
    // 发送验证码
    public static final String SEND_VERIFICATION = "/xhapi/MemberController/registerVerificationV2.hn";
    // 注册
    public static final String REGISTER = "/xhapi/MemberController/generatePassword.hn";
    // 判断验证码
    public static final String REGISTER_ISEFFECTIVE = "/xhapi/MemberController/registerIsEffective.hn";
    // 修改密码
    public static final String CHANGE_PSW_URL = "/xhapi/MemberController/updateMemberPassword.hn";
    // 修改昵称
    public static final String UPDATE_MEMBER_NAME = "/xhapi/MemberController/updateMemberNameInfo.hn";

    public static final String UPDATE_USER_PROPERTY = "/xhapi/MemberController/updateMemberListInfo.hn";
    // 根据电话号码返回用户信息
    public static final String SHOW_PHONEMEMBER_INFO_URL = "/xhapi/friends/friendRequestOnId.hn";
    // 显示客户本人的的详细信息
    public static final String SHOW_MEMBER_LIST_INFO_URL = "/xhapi/MemberController/showOneMemberListInfo.hn";
    // 修改用户信息
    public static final String UPDATE_MEMBER_LIST_INFO_URL = "/xhapi/MemberController/updateMemberListInfo.hn";
    // 添加用户信息
    public static final String ADD_MEMBER_LIST_INFO_URL = "/xhapi/MemberController/addMemberListInfo.hn";
    // 交易详细信息
    public static final String SHOW_MEMBER_TRANSACTION_URL = "/xhapi/MemberController/showMemberTransaction.hn";
    // 忘记密码
    public static final String FORGET_MEMBER_PASSWORD_URL = "/xhapi/MemberController/forgetMemberPassword.hn";
    // 修改密码
    public static final String UPDATE_MEMBER_PASSWORD_URL = "/xhapi/MemberController/updateMemberPassword.hn";
    // 查询行业范围或业务范围
    public static final String GET_INDUSTRY_OR_SCOPE = "/xhapi/MemberController/getIndustryOrScope.hn";
    // 操作用户头像信息
    public static final String OPERATING_MEMBER_PICTURE_URL = "/xhapi/MemberController/OperatingMemberPicture.hn";
    // 环信im
    // 显示聊天方的信息
    public static final String GET_CHATUSER_INFO = "/xhapi/ChatController/getChatUserInfo.hn";

    // 显示好友列表
    public static final String SHOW_FRIENDS_INFO = "/xhapi/friends/list.hn";
    // 显示好友列表
    public static final String SHOW_FRIEND_LIST_INFO_URL = "/xhapi/friends/list.hn";
    // 添加好友
    public static final String ADD_FRIEND_LIST_INFO_URL = "/xhapi/friends/friendRequest.hn";
    // 删除好友信息
    public static final String DELETE_FRIEND_LIST_INFO_URL = "/xhapi/friends/delete.hn";
    // 确认添加好友
    public static final String CONFIRM_FRIEND_LIST_INFO_URL = "/xhapi/friends/friendConfirm.hn";
    // 更改好友备注
    public static final String UPDATA_FRIEND_NAME = "/xhapi/friends/updateFriendName.hn";
    // 获取好友申请列表
    public static final String GET_LIST_FRIEND_MESSAGE = "/xhapi/friends/listFriendMessage.hn";
    // 删除好友请求
    public static final String DELE_REQUEST_FRIEND_MESSAGE = "/xhapi/friends/deleteFriendRequestMessage.hn";
    // 通讯录查询好友关系
    public static final String CHECK_CONTACT_URL = "/xhapi/friends/contactCheck.hn";
    // 根据电话号码或昵称搜索用户列表
    public static final String SEARCH_RESULT = "/xhapi/MemberController/showMemberInfo.hn";

    // ---------------------接口转向member_id by 2017-04-21------------------------start
    // 添加好友
    public static final String ADD_FRIEND_BY_ID_INFO_URL = "/xhapi/friends/friendRequestOnId.hn";
    // 删除好友信息
    public static final String DELETE_FRIEND_BY_ID_INFO_URL = "/xhapi/friends/deleteOnId.hn";

    // 注册
    public static final String REGISTER_BY_ID = "/xhapi/MemberController/registerOnId.hn";
    // 创建聊天室
    public static final String CREATE_CHAT_ROOM_BY_ID = "/xhapi/ChatRoomController/createOnId.hn";
    // 添加聊天室成员
    public static final String ADD_MEMBER_TO_CHAT_ROOM_BY_ID = "/xhapi/ChatRoomController/addOnId.hn";
    // 移除聊天室成员
    public static final String REMOVE_MEMBER_FROM_CHAT_ROOM_BY_ID = "/xhapi/ChatRoomController/removeOnId.hn";

    // ---------------------接口转向member_id by 2017-04-21------------------------end

    // 聊天
    // 获取会话列表
    public static final String GET_CHAT_LIST = "/xhapi/ChatController/showDialogueInfo.hn";
    // 添加一个会话
    public static final String ADD_CHAT = "/xhapi/ChatController/addDialogueInfo.hn";
    // 删除一个会话
    public static final String REMOVE_CHAT = "/xhapi/ChatController/deleteDialogueInfo.hn";
    // 修改好友昵称
    public static final String MODIFY_NICK_SUB = "/xhapi/device/updateDeviceChildName.hn";
    // 获取用户信息
    public static final String GET_USER_INFO = "/xhapi/MemberController/showOneMemberListInfo.hn";

    // 聊天室
    // 获取聊天室列表
    public static final String GET_CHAT_ROOM_LIST = "/xhapi/ChatRoomController/list.hn";
    // 创建聊天室
    public static final String CREATE_CHAT_ROOM = "/xhapi/ChatRoomController/create.hn";
    // 解散聊天室
    public static final String REMOVE_CHAT_ROOM = "/xhapi/ChatRoomController/removeChatRoom.hn";
    // 获取聊天室信息
    public static final String GET_CHAT_ROOM_INFO = "/xhapi/ChatRoomController/getInfo.hn";
    // 添加聊天室成员
    public static final String ADD_MEMBER_TO_CHAT_ROOM = "/xhapi/ChatRoomController/add.hn";
    // 移除聊天室成员
    public static final String REMOVE_MEMBER_FROM_CHAT_ROOM = "/xhapi/ChatRoomController/remove.hn";
    //将群组注册到消息列表
    public static final String REGISTE_CHAT_GROUP_TO_MESSAGELIST = "/xhapi/ChatRoomController/isDisplayChatRoom.hn";


    // 名片
    // 获取名片列表
    public static final String GET_MY_CAR_LIST = "/xhapi/BusinessCardController/list.hn";
    // 获取详细信息
    public static final String GET_CARD_INFO = "/xhapi/BusinessCardController/getInfo.hn";
    // 添加名片
    public static final String ADD_CARD_INFO = "/xhapi/BusinessCardController/add.hn";
    // 修改名片
    public static final String MODIFY_CARD_INFO = "/xhapi/BusinessCardController/modify.hn";
    // 删除名片
    public static final String REMOVE_CARD_INFO = "/xhapi/BusinessCardController/remove.hn";
    // 搜索名片
    public static final String SEARCH_CARD_INFO = "/xhapi/BusinessCardController/search.hn";

    // 组织
    // 获取组织列表
    public static final String GET_ORG_LIST = "/xhapi/AssociationController/getMyAssociations.hn";
    // 获取组织信息
    public static final String GET_ORG_INFO = "/xhapi/AssociationController/getInfo.hn";
    // 删除组织信息
    public static final String DELETE_ORG_INFO = "/xhapi/AssociationController/deleteAsso.hn";
    // 搜索组织列表
    public static final String SEARCH_ORG_LIST = "/xhapi/AssociationController/search.hn";
    // 搜索组织列表
    public static final String SEARCH_ORG_LIST_NEW = "/xhapi/AssociationController/searchByOwner.hn";
    // 申请入会
    public static final String JOIN_ORG = "/xhapi/AssociationController/requestJoin.hn";
    // 同意/拒绝入会申请
    public static final String REQUEST_JOIN_ORG = "/xhapi/AssociationController/responseRequest.hn";
    // 创建组织
    public static final String CREATE_ORG = "/xhapi/AssociationController/create.hn";
    public static final String CREATE_ORG_NEW = "/xhapi/AssociationController/newCreate.hn";
    // 退出组织
    public static final String EXIT_ORG = "/xhapi/AssociationController/exit.hn";
    // 添加分组联系人
    public static final String ADD_PERSON_ORG = "/xhapi/AssociationController/addPerson.hn";
    // 获取申请入会列表
    public static final String GET_JOINREQUEST = "/xhapi/OrganizationController/getJoinRequest.hn";
    // 清空申请列表
    public static final String GET_EMPTY_APPLY_LIST = "/xhapi/OrganizationController/deleteJoinRequest.hn";
    // 获取组织所有成员
    public static final String GET_ORG_ALL_NAME = "/xhapi/AssociationController/getAssociationMemberList.hn";
    // 删除组织成员
    public static final String DELECT_ORG_MEMBER = "/xhapi/AssociationController/exit.hn";
    // 修改组织logo
    public static final String REPLACE_ORG_LOGO = "/xhapi/AssociationController/updateAssociation.hn";
    // 修改组织信息
    public static final String UPDATAORG_INFO =  "/xhapi/AssociationController/updateAssociationInfo.hn";
    // 获取所用组织公告未读数量
    public static final String ORG_UNREAD_COUNT = "/xhapi/MessageController/getUnreadNumber.hn";
    // public static final String ADD_DEPARTMENT =
    // 获取所用组织入会申请列表
    public static final String ORG_JOIN_REQURST = "/xhapi/MessageController/getApplyUnprocessedNumber.hn";
    // 获取十个随机会员
    public static final String GET_RECOMMEND_MEM = "/xhapi/MemberController/getRandomRecommendMember.hn";
    // 协会邀请同意或拒绝
    public static final String MODIFY_ORG_INVITE = "/xhapi/RecommendAndInvitationController/inviteConfirm.hn";
    // 协会邀请
    public static final String MODIFY_ORG_INVITE_MEM = "/xhapi/RecommendAndInvitationController/inviteMember.hn";
    // 协会邀请清空
    public static final String MODIFY_ORG_INVITE_CLEAR = "/xhapi/RecommendAndInvitationController/clearMessage.hn";
    // 推荐协会
    public static final String RECOMMEND_ASSOCIATION = "/xhapi/RecommendAndInvitationController/recommendAssociation.hn";

    //添加部門的管理員
    public static final String GET_SET_ADMIN = "/xhapi/OrganizationController/updateDepmMemberAdmin.hn";
    // 添加分组
    public static final String ADD_DEPARTMENT = "/xhapi/OrganizationController/addDepartment.hn";
    // 获取分组列表
    public static final String GET_DEPARTMENT_LIST = "/xhapi/OrganizationController/getDepartmentList.hn";
    // 添加分组成员
    public static final String ADD_MEMBER_TO_DPM = "/xhapi/OrganizationController/addMemberToDepm.hn";
    // 获取分组成员列表
    public static final String GET_DEPARTMENT_MEMBER_LIST = "/xhapi/OrganizationController/getDpmMemberList.hn";
    // 移除分组
    public static final String REMOVE_DEPARTMENT = "/xhapi/OrganizationController/removeDepartment.hn";
    // 删除分组成员
    public static final String REMOVE_MEMBER_FROM_DPM = "/xhapi/OrganizationController/removeMemberDepm.hn";
    // 修改分组的信息
    public static final String UPDATA_DPM = "/xhapi/OrganizationController/updateDepartment.hn";
    // 查询签到列表
    public static final String GET_CHECK_IN_LIST_UN = "/xhapi/CheckInController/quaryList.hn";
    // 查询系统消息列表
    public static final String GET_NOTIFY_LIST= "/xhapi/MessageController/listMessage.hn";
    // 查询系统未读消息数
    public static final String GET_NOTIFY_UNREAD_COUNT= "/xhapi/RecommendAndInvitationController/getUnreadCount.hn";

    // 签到
    public static final String CHECK_IN = "/xhapi/CheckInController/checkin.hn";
    // 查询
    public static final String GET_CHECK_IN_LIST = "/xhapi/CheckInController/quary.hn";

    // 公告明细
    public static final String GET_MESSAGE_DETAIL = "/xhapi/MessageController/getInfo.hn";
    // 公告列表
    public static final String GET_MESSAGE_LIST = "/xhapi/MessageController/getlist.hn";
    // 发布公告
    public static final String POST_MESSAGE = "/xhapi/MessageController/post.hn";
    // 删除公告
    public static final String REMOVE_MSG = "/xhapi//MessageController/removeMsg.hn";
    // 添加公告点赞，参与，举报
    public static final String Add_MSG_EXTENTION = "/xhapi/MessageController/addMsgExtentionInfo.hn";

    // 上传图片
    public static final String UPLOAD_IMAGE = "/xhapi/ImageUploadController/uploadImg.hn";
    // 上传图片返回url
    public static final String UPLOAD_IMAGE_URL = "/xhapi/ImageUploadController/uploadImgReUrl.hn";

    // 检查更新
    public static final String GET_CHECK_UPDATA_APP = "/xhapi/appController/checkUpdate.hn?app_name=geju_android_v2";

    // 提交问题
    public static final String POST_CONTENT = "/xhapi/AssociationController/addHelpInfo.hn";

    // 检索数据
    public static final String SEARCH_DATAS = "/xhapi/AssociationController/searchRelative.hn";


    /**
     * 直播
     */
    //获取正在直播列表
    public static final String GET_LIVESTREAM_LIST = "/xhapi/LiveController/liveList.hn";
    //获取用户创建的组织
    public static final String GET_ORG_LIST_BY_BUILDER = "/xhapi/LiveController/getOrgList.hn";
    //创建直播
    public static final String CREATE_LIVESTREAM = "/xhapi/LiveController/openLive.hn";
    //关闭直播
    public static final String COLSE_LIVESTREAM = "/xhapi/LiveController/closeLive.hn";
    //直播状态监听
    public static final String LIEV_STATE_LISTER = "/xhapi/LiveController/listen.hn";
    //查询是否组织成员
    public static final String IS_THE_ORG_MEMBER = "/xhapi/LiveController/isTheOrgMember.hn";
    //获取所有直播列表
    public static final String GET_LIVESTREAM_ALLLIST = "/xhapi/LiveController/allLiveList.hn";
    //获取直播页面的轮播图
    public static final String GET_LIVE_CAROUSEL_IMG = "/xhapi/LiveController/carousel.hn";
    //设置直播封面和直播标题
    public static final String SET_LIVE_INFO = "/xhapi/LiveController/setLiveInfo.hn";
    //成员进入或退出直播间
    public static final String LIVE_ENTER_EXIT = "/xhapi/LiveController/saveOrUpdateAudience.hn";
    //获取所有正在观看直播的观众
    public static final String GET_LIVE_AUDIENCE = "/xhapi/LiveController/getAudience.hn";

    //修改群昵称
    public static final String MODIYF_CAHT_ROOM_NAME = "/xhapi/ChatRoomController/updateGroupName.hn";
    //图形验证码接口
    public static final String IMAGE_CODE="/xhapi/ImageCodeController/getImageCode.hn";

    //微信登录
    public static final String WEIXING_SIGN_IN = "/xhapi/MemberController/loginByWxOnId.hn";
    //微信注册
    public static final String WEIXING_REGISTER = "/xhapi/MemberController/registerWithWxDirectlyOnId.hn";
    //微信绑定（用户登录操作）
    public static final String WEIXING_BINBING_USER = "/xhapi/MemberController/bindWxByAuth.hn";
    //微信绑定（绑定界面操作）
    public static final String WEIXING_BINBING_BINBING = "/xhapi/MemberController/bindWxByUser.hn";
    //微信绑定并登录
    public static final String WEIXING_BINBING_AND_SIGN_IN = "/xhapi/MemberController/registerWithWx.hn";
    //微信解除绑定
    public static final String WEIXING_RELIEVE_BINBING = "/xhapi/MemberController/unbindWx.hn";
    //微信绑定手机
    public static final String WEIXING_BUIBING_PHONE = "/xhapi/MemberController/bindPhone.hn";

    //检查token有效性
    public static final String CHECK_TOKEN = "/xhapi/MemberController/isValidToken.hn";

}
