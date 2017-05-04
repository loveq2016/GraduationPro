package app.tianyue.activity.note.jianli;

public class TYNormPhotoInfo {
	private String busniessKey;
	private String remoteURL;//照片路径
	private String hashCode;	
	private String createTime;//拍照时间
	private String tagInfo;//备注信息
	/** 上传状态 **/
	private int uploadStatus;
	/**
	 * 黑名单标记
	 * @return
	 */
	private int blackTag;
	/** 照片本地路径 **/
	private String localURL;
	
	public String getLocalURL() {
		return localURL;
	}
	public void setLocalURL(String localURL) {
		this.localURL = localURL;
	}
	public int getBlackTag() {
		return blackTag;
	}
	public void setBlackTag(int blackTag) {
		this.blackTag = blackTag;
	}
	public boolean isLocalPhoto() {
		return isLocalPhoto;
	}
	public void setLocalPhoto(boolean isLocalPhoto) {
		this.isLocalPhoto = isLocalPhoto;
	}
	private boolean isLocalPhoto;
	
	public TYNormPhotoInfo(){
		blackTag = 0;
		isLocalPhoto = true;
	}
	public int getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getBusniessKey() {
		return busniessKey;
	}
	public void setBusniessKey(String busniessKey) {
		this.busniessKey = busniessKey;
	}
	public String getRemoteURL() {
		return remoteURL;
	}
	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}
	public String getHashCode() {
		return hashCode;
	}
	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTagInfo() {
		return tagInfo;
	}
	public void setTagInfo(String tagInfo) {
		this.tagInfo = tagInfo;
	}
}
