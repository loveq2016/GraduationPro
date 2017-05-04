package app.logic.activity.live;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class IsOnLiveOrgInfo {

    private String org_id ;
    private String org_name ;
    private int status ;  //组织直播状态  0 ：正在直播   1：未直播 ；（默认正在直播）
    private String create_time ;
    private String room_id ;
    private String plug_id ;
    private String org_builder_id ;
    private String org_builder_name ;
    private String org_logo_url ;
    private String live_id ;
    private String friend_name;
    private String start_time;

    private String live_title;
    private String live_cover;

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getPlug_id() {
        return plug_id;
    }

    public void setPlug_id(String plug_id) {
        this.plug_id = plug_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }



    public String getOrg_logo_url() {
        return org_logo_url;
    }

    public void setOrg_logo_url(String org_logo_url) {
        this.org_logo_url = org_logo_url;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getOrg_builder_name() {
        return org_builder_name;
    }

    public void setOrg_builder_name(String org_builder_name) {
        this.org_builder_name = org_builder_name;
    }

    public String getOrg_builder_id() {
        return org_builder_id;
    }

    public void setOrg_builder_id(String org_builder_id) {
        this.org_builder_id = org_builder_id;
    }

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }

    public String getLive_title() {
        return live_title;
    }

    public void setLive_title(String live_title) {
        this.live_title = live_title;
    }

    public String getLive_cover() {
        return live_cover;
    }

    public void setLive_cover(String live_cover) {
        this.live_cover = live_cover;
    }
}
