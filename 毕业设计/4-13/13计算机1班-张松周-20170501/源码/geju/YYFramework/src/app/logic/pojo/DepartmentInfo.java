package app.logic.pojo;

/**
 * SiuJiYung create at 2016年7月3日 下午3:17:27 部门信息
 */

public class DepartmentInfo {


    private String departmentName;
    private String departmentId;
    private int sort;
    private String org_id;
    private OrgRequestMemberInfo orgRequestMemberInfo;

    // 新增加
    private boolean isShowTitle;
    private boolean isBuilder;
    private boolean isNotDpmAndNotAdmin;

    //  ysf 再次新增
    private boolean isAdmin;
    private String wp_member_info_id;
    public boolean isOpen;
    private boolean isShowTitleView;

    /**
     * 用于隐藏和显示编辑的按钮
     */
    private boolean isSmoothMenuOpen;

    public boolean isSmoothMenuOpen() {
        return isSmoothMenuOpen;
    }

    public void setSmoothMenuOpen(boolean smoothMenuOpen) {
        isSmoothMenuOpen = smoothMenuOpen;
    }

    public boolean isShowTitleView() {
        return isShowTitleView;
    }

    public void setShowTitleView(boolean showTitleView) {
        isShowTitleView = showTitleView;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public DepartmentInfo() {

    }

    public DepartmentInfo(OrgRequestMemberInfo info) {
        this.orgRequestMemberInfo = info;
    }

    public boolean isBuilder() {
        return isBuilder;
    }

    public void setBuilder(boolean isBuilder) {
        this.isBuilder = isBuilder;
    }

    public OrgRequestMemberInfo getOrgRequestMemberInfo() {
        return orgRequestMemberInfo;
    }

    public boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowTitle(boolean isShowTitle) {
        this.isShowTitle = isShowTitle;
    }

    public boolean isNotDpmAndNotAdmin() {
        return isNotDpmAndNotAdmin;
    }

    public void setNotDpmAndNotAdmin(boolean isNotDpmAndNotAdmin) {
        this.isNotDpmAndNotAdmin = isNotDpmAndNotAdmin;
    }

    public String getDpm_name() {
        return departmentName;
    }

    public void setDpm_name(String dpm_name) {
        this.departmentName = dpm_name;
    }

    public String getDpm_id() {
        return departmentId;
    }

    public void setDpm_id(String dpm_id) {
        this.departmentId = dpm_id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

}
