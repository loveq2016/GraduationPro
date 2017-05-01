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

//    private static final String HOST_NAME = "http://120.25.65.109:8080/xhapi"; //218.244.136.153
    private static final String HOST_NAME = "http://geju.gzyueyun.com:8080/xhapi"; //218.244.136.153
//    private static final String HOST_NAME = "http://120.25.65.109:8088/xhapi";

//    private static final String TEST_HOST_NAME = "http://192.168.1.120:8080/xhapi";

    // 用户类接口

    // 获取邀请码
    public static final String GET_CODE = "/xhapi/MemberController/showoInvitationCodeInfo.hn";
    // 登陆
    public static final String LOGIN_URL = "/xhapi/MemberController/login.hn";
    // 发送验证码
    public static final String SEND_VERIFICATION = "/xhapi/MemberController/registerVerification.hn";
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
    public static final String SHOW_PHONEMEMBER_INFO_URL = "/xhapi/MemberController/showPhoneMemberInfo.hn";
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
    // 添加组织联系人
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

    //添加部門的管理員
    public static final String GET_SET_ADMIN = "/xhapi/OrganizationController/updateDepmMemberAdmin.hn";
    // 添加部门
    public static final String ADD_DEPARTMENT = "/xhapi/OrganizationController/addDepartment.hn";
    // 获取部门列表
    public static final String GET_DEPARTMENT_LIST = "/xhapi/OrganizationController/getDepartmentList.hn";
    // 添加部门成员
    public static final String ADD_MEMBER_TO_DPM = "/xhapi/OrganizationController/addMemberToDepm.hn";
    // 获取部门成员列表
    public static final String GET_DEPARTMENT_MEMBER_LIST = "/xhapi/OrganizationController/getDpmMemberList.hn";
    // 移除部门
    public static final String REMOVE_DEPARTMENT = "/xhapi/OrganizationController/removeDepartment.hn";
    // 删除部门成员
    public static final String REMOVE_MEMBER_FROM_DPM = "/xhapi/OrganizationController/removeMemberDepm.hn";
    // 修改部门的信息
    public static final String UPDATA_DPM = "/xhapi/OrganizationController/updateDepartment.hn";
    // 查询签到列表
    public static final String GET_CHECK_IN_LIST_UN = "/xhapi/CheckInController/quaryList.hn";

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

    // 上传图片
    public static final String UPLOAD_IMAGE = "/xhapi/ImageUploadController/uploadImg.hn";

    // 检查更新
    public static final String GET_CHECK_UPDATA_APP = "/xhapi/appController/checkUpdate.hn?app_name=geju_android";

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
    public static final String GET_ORG_LIST_BY_BUILDER = "/xhapi/LiveController/getOrgListByBuilderId.hn";
    //创建直播
    public static final String CREATE_LIVESTREAM = "/xhapi/LiveController/createLive.hn";
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


    //修改群昵称
    public static final String MODIYF_CAHT_ROOM_NAME = "/xhapi/ChatRoomController/updateGroupName.hn";
    //图形验证码接口
    public static final String IMAGE_CODE="/xhapi/ImageCodeController/getImageCode.hn";
}
