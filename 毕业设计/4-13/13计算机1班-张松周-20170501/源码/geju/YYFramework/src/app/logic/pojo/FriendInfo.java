package app.logic.pojo;

import com.sort.sortlistview.SortModel;

public class FriendInfo {

    private String name;
    private String nickName;
    private String picture_url;
    private String phone;
    private String add_friend_id;
    private boolean response;
    private String wp_friends_info_id;
    private String friend_name;
    private boolean success;
    private String requestMessage;
    private String location;
    private String sex;
    private String wp_member_info_id;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    // 好友请求
    private boolean otherRequest;
    private String validation;// 请求消息
    private String request_nickName;// 请求人名称
    private String request_phone;
    private boolean operation_status;
    private String accept_picture_url;
    private String message_id;
    private int isAccess;
    private boolean request_accept;
    private String request_picture_url;

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isRequestMessage() {
        return message_id != null;
    }

    public String getAdd_friend_id() {
        return add_friend_id;
    }

    public void setAdd_friend_id(String add_friend_id) {
        this.add_friend_id = add_friend_id;
    }

    public String getPhone() {
        if (isRequestMessage()) {
            return request_phone;
        }
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getWp_friends_info_id() {
        return wp_friends_info_id;
    }

    public void setWp_friends_info_id(String wp_friends_info_id) {
        this.wp_friends_info_id = wp_friends_info_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public int getIsAccess() {
        return isAccess;
    }

    public void setIsAccess(int isAccess) {
        this.isAccess = isAccess;
    }

    public boolean isRequest_accept() {
        return request_accept;
    }

    public void setRequest_accept(boolean request_accept) {
        this.request_accept = request_accept;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getRequest_nickName() {
        return request_nickName;
    }

    public void setRequest_nickName(String request_nickName) {
        this.request_nickName = request_nickName;
    }

    public String getRequest_phone() {
        return request_phone;
    }

    public void setRequest_phone(String request_phone) {
        this.request_phone = request_phone;
    }

    public boolean isOperation_status() {
        return operation_status;
    }

    public void setOperation_status(boolean operation_status) {
        this.operation_status = operation_status;
    }

    public String getAccept_picture_url() {
        return accept_picture_url;
    }

    public void setAccept_picture_url(String accept_picture_url) {
        this.accept_picture_url = accept_picture_url;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getRequest_picture_url() {
        return request_picture_url;
    }

    public void setRequest_picture_url(String request_picture_url) {
        this.request_picture_url = request_picture_url;
    }

    public boolean isOtherRequest() {
        return otherRequest;
    }

    public void setOtherRequest(boolean otherRequest) {
        this.otherRequest = otherRequest;
    }

}
