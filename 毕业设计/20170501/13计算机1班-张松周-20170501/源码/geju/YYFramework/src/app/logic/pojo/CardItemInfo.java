package app.logic.pojo;

/*
 * GZYY    2016-12-27  下午3:46:47
 * author: zsz
 */

public class CardItemInfo {

    private String title;
    private String content;

    /**
     * 1 为电话； 2为内容 ； 3为公司信息； 4为职业 5为图片 的类型
     */
    private int type;

    private int inputType;
    private String pictureUrl;
    private boolean enableShowLine = false;
    private boolean edtStatus;


    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public boolean isEdtStatus() {
        return edtStatus;
    }

    public void setEdtStatus(boolean edtStatus) {
        this.edtStatus = edtStatus;
    }

    public boolean getEnableShowLine() {
        return enableShowLine;
    }

    public void setEnableShowLine(boolean enableShowLine) {
        this.enableShowLine = enableShowLine;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
