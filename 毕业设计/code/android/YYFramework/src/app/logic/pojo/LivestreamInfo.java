package app.logic.pojo;

/**
 * Created by GZYY on 17/2/15.
 */

public class LivestreamInfo {
    private String org_name;
    private String org_id;
    private int status;
    private String create_time;
    private String org_builder_name;
    private String org_builder_id;
    private String live_id;
    private String org_logo_url;

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
}
