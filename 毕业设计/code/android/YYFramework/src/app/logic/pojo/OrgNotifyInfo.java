package app.logic.pojo;

/**
 * Created by apple on 17/4/28.
 */

public class OrgNotifyInfo {

    private String readStatus;
    private int detail_type_id;
    private boolean operation_status;
    private String operation_result;
    private String message_id;
    private int operation_type;
    private String createDate;
    private String detail_description;
    private int message_type_id;
    private String wp_member_info_id;
    private String descriptions;
    private String org_logo_url;
    private String org_id;

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }


    public boolean isOperation_status() {
        return operation_status;
    }

    public void setOperation_status(boolean operation_status) {
        this.operation_status = operation_status;
    }

    public String getOperation_result() {
        return operation_result;
    }

    public void setOperation_result(String operation_result) {
        this.operation_result = operation_result;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDetail_description() {
        return detail_description;
    }

    public void setDetail_description(String detail_description) {
        this.detail_description = detail_description;
    }


    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getOrg_logo_url() {
        return org_logo_url;
    }

    public void setOrg_logo_url(String org_logo_url) {
        this.org_logo_url = org_logo_url;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public int getDetail_type_id() {
        return detail_type_id;
    }

    public void setDetail_type_id(int detail_type_id) {
        this.detail_type_id = detail_type_id;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public int getMessage_type_id() {
        return message_type_id;
    }

    public void setMessage_type_id(int message_type_id) {
        this.message_type_id = message_type_id;
    }
}
