package app.logic.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * SiuJiYung create at 2016年6月24日 下午3:33:15 名片
 */

public class CardInfo implements Serializable {
    private List<String> bc_tel;
    private List<String> bc_cellPhone;
    private List<String> bc_im;
    private List<String> bc_orgName;
    private List<String> bc_email;
    private List<String> bc_QQ;
    private String bc_title;
    private String bc_tag;
    private String bc_name;
    private String bc_pic_url;
    private String bc_id;
    private int dafaultImg;

    public int getDafaultImg() {
        return dafaultImg;
    }

    public void setDafaultImg(int dafaultImg) {
        this.dafaultImg = dafaultImg;
    }

    public List<String> getBc_QQ() {
        return bc_QQ;
    }

    public void setBc_QQ(List<String> bc_QQ) {
        this.bc_QQ = bc_QQ;
    }

    public List<String> getBc_tel() {
        return bc_tel;
    }

    public void setBc_tel(List<String> bc_tel) {
        this.bc_tel = bc_tel;
    }

    public List<String> getBc_cellPhone() {
        return bc_cellPhone;
    }

    public void setBc_cellPhone(List<String> bc_cellPhone) {
        this.bc_cellPhone = bc_cellPhone;
    }

    public List<String> getBc_im() {
        return bc_im;
    }

    public void setBc_im(List<String> bc_im) {
        this.bc_im = bc_im;
    }

    public List<String> getBc_orgName() {
        return bc_orgName;
    }

    public void setBc_orgName(List<String> bc_orgName) {
        this.bc_orgName = bc_orgName;
    }

    public List<String> getBc_email() {
        return bc_email;
    }

    public void setBc_email(List<String> bc_email) {
        this.bc_email = bc_email;
    }

    public String getBc_title() {
        return bc_title;
    }

    public void setBc_title(String bc_title) {
        this.bc_title = bc_title;
    }

    public String getBc_tag() {
        return bc_tag;
    }

    public void setBc_tag(String bc_tag) {
        this.bc_tag = bc_tag;
    }

    public String getBc_name() {
        return bc_name;
    }

    public void setBc_name(String bc_name) {
        this.bc_name = bc_name;
    }

    public String getBc_pic_url() {
        return bc_pic_url;
    }

    public void setBc_pic_url(String bc_pic_url) {
        this.bc_pic_url = bc_pic_url;
    }

    public String getBc_id() {
        return bc_id;
    }

    public void setBc_id(String bc_id) {
        this.bc_id = bc_id;
    }
}
