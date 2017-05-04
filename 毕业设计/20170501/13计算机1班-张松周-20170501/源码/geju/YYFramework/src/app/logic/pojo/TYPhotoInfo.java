package app.logic.pojo;

import java.io.Serializable;

/**
 * 
 * @author SiuJiYung create at 2013-12-2 下午6:44:47 </br> 照片结构体
 */
public class TYPhotoInfo implements Serializable{

	private static final long serialVersionUID = 7966858576119253078L;
	private int photoUploadInterfaceVersion;
	private String CorrectiveBusinessKey;//整改key
	private String RectifyBusinessKey;
	private String fileTitle;
	private String FileTypeIDs;
	private int creatorId;
	private String BusinessKey;
	/** 经度 **/
	private double longgitude;
	/** 纬度 **/
	private double latitude;
	/** 逻辑站点ID **/
	private String LStationID;
	/** 照片id **/
	private long photoId;

	/** 所属表单id **/
	private int noteId;
	/** 照片远程路径 **/
	private String remoteURL;
	/** 照片本地路径 **/
	private String localURL;
	/** 上传状态 **/
	private int uploadStatus;
	/** 照片md5 **/
	private String hashCode;
	/** 拍照日期 **/
	private String createTime;
	/**
	 * 完成情况
	 */
	private String OtherInfo;
	/**
	 * 质量情况
	 */
	private String QAInfo;
	/**
	 * 黑名单标记
	 * @return
	 */
	private int blackTag;
	
	private boolean isLocalPhoto;
	
	private String FileId;
	
	private String FileID;
	
	private int ZhuanYeID;
	//关键工序
	private String guanjianWork;
	
	/**数据库ID*/
	private int id;
	/**备注**/
	private String tagInfo;
	
	public String getCorrectiveBusinessKey() {
		return (CorrectiveBusinessKey==null?RectifyBusinessKey:CorrectiveBusinessKey);
	}

	public void setCorrectiveBusinessKey(String correctiveBusinessKey) {
		RectifyBusinessKey = correctiveBusinessKey;
		CorrectiveBusinessKey = correctiveBusinessKey;
	}

	public TYPhotoInfo(){
		blackTag = 0;
		isLocalPhoto = true;
		photoUploadInterfaceVersion = 0;
	}
	
	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

//	public String getRemoteURL() {
//		String _fid = TextUtils.isEmpty(FileId)?FileID:FileId;
//		if (TextUtils.isEmpty(_fid)) {
//			return null;
//		}
//		String url = TYHttpRequestManager.createRequestURL(TYInterfaceList.STR_DISPLAY_IMAGE);
//		return url + "?fileId="+_fid;
//	}

//	public void setRemoteURL(String remoteURL) {
//		this.remoteURL = remoteURL;
//	}

	public String getLocalURL() {
		return localURL;
	}

	public void setLocalURL(String localURL) {
		this.localURL = localURL;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
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

	public String getBusinessKey() {
		return BusinessKey;
	}

	public void setBusinessKey(String businessKey) {
		BusinessKey = businessKey;
	}

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public double getLonggitude() {
		return longgitude;
	}

	public void setLonggitude(double longgitude) {
		this.longgitude = longgitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public String getStationID() {
		return LStationID;
	}

	public void setLStationID(String lStationID) {
		LStationID = lStationID;
	}
	
	public String getFileId() {
		String _t = FileId == null?FileID:FileId;
		return _t;
	}

	public void setFileId(String fileId) {
		FileId = fileId;
		FileID = fileId;
	}

	public boolean isLocalPhoto() {
		isLocalPhoto = (FileId == null && FileID == null);
		return isLocalPhoto;
	}

	public void setLocalPhoto(boolean isLocalPhoto) {
		this.isLocalPhoto = isLocalPhoto;
	}

	public int getBlackTag() {
		return blackTag;
	}

	public void setBlackTag(int blackTag) {
		this.blackTag = blackTag;
	}

	public String getQAInfo() {
		return QAInfo;
	}

	public void setQAInfo(String qAInfo) {
		QAInfo = qAInfo;
	}

	public String getGuanjianWork() {
		return guanjianWork;
	}

	public void setGuanjianWork(String guanjianWork) {
		this.guanjianWork = guanjianWork;
	}

	public String getFileTypeID() {
		return FileTypeIDs;
	}

	public void setFileTypeID(String FileTypeIDs) {
		this.FileTypeIDs = FileTypeIDs;
	}

	public String getFileTypeIDs() {
		return FileTypeIDs;
	}

	public void setFileTypeIDs(String fileTypeIDs) {
		FileTypeIDs = fileTypeIDs;
	}

	public String getOtherInfo() {
		return OtherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		OtherInfo = otherInfo;
	}

	public int getZhuanYeID() {
		return ZhuanYeID;
	}

	public void setZhuanYeID(int zhuanYeID) {
		ZhuanYeID = zhuanYeID;
	}

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTagInfo() {
		return tagInfo;
	}

	public void setTagInfo(String tagInfo) {
		this.tagInfo = tagInfo;
	}

	public int getPhotoUploadInterfaceVersion() {
		return photoUploadInterfaceVersion;
	}

	public void setPhotoUploadInterfaceVersion(int photoUploadInterfaceVersion) {
		this.photoUploadInterfaceVersion = photoUploadInterfaceVersion;
	}
	
}
