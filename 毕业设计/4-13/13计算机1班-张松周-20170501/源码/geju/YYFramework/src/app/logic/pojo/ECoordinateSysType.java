package app.logic.pojo;

public enum ECoordinateSysType {
	BaiduCoordinateSys("bd0911"),
	BadiMokatuoCoordinateSys("bd09"),
	GuoCeCoordinateSys("gcj02"),
	AppleMapCoordinateSys("applemap"),
	Wgs84("wgs84");
	
	private String strCoor;
	private ECoordinateSysType(String coordinate){
		this.strCoor = coordinate;
	}
	public String toString(){
		return this.strCoor;
	}
}
