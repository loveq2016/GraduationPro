package app.logic.pojo;

/**.
 * 
 * @author SiuJiYung
 * create at 2013-12-4 下午2:58:07
 *</br>
 *地理信息结构体
 *
 */
public class TYLocationInfo {
	
	public String city;
	/**经度**/
	public double longitude;
	/**纬度**/
	public double latitude;
	/**地址**/
	private String locationAddr;
	/**坐标系类型**/
	private ECoordinateSysType coordinateSysType;
	/**
	 * 是否有半径信息
	 */
	private boolean hasRadius;
	/**
	 * 半径范围
	 */
	private float radius;
	/**
	 * gps卫星日期
	 */
	private String gpsDate;
	/**
	 * gps卫星时间
	 */
	private long gpsDateTime;
	
	private long upgradAddrDateTime;
	

	public TYLocationInfo() {
		coordinateSysType = ECoordinateSysType.BaiduCoordinateSys;
	}
	
	public String getGpsDate() {
		return gpsDate;
	}

	public void setGpsDate(String gpsDate) {
		this.gpsDate = gpsDate;
	}
	
	public boolean isHasRadius() {
		return hasRadius;
	}
	public void setHasRadius(boolean hasRadius) {
		this.hasRadius = hasRadius;
	}
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getLocationAddr() {
		return locationAddr;
	}
	public void setLocationAddr(String locationAddr) {
		this.locationAddr = locationAddr;
	}
	public ECoordinateSysType getCoordinateSysType() {
		return coordinateSysType;
	}
	public void setCoordinateSysType(ECoordinateSysType coordinateSysType) {
		this.coordinateSysType = coordinateSysType;
	}

	public long getGpsDateTime() {
		return gpsDateTime;
	}

	public void setGpsDateTime(long gpsDateTime) {
		this.gpsDateTime = gpsDateTime;
	}

	public long getUpgradAddrDateTime() {
		return upgradAddrDateTime;
	}

	public void setUpgradAddrDateTime(long upgradAddrDateTime) {
		this.upgradAddrDateTime = upgradAddrDateTime;
	}
	
}
