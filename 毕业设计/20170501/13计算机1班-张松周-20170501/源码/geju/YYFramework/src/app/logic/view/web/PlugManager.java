package app.logic.view.web;

public class PlugManager {
	
	private static PlugManager plugManager = null;
	//插件根目录，所有的插件均解压到这个目录下。
	private static final String PLUG_ROOT_PATH = "/TYPlug/";
	
	public static PlugManager getShareInstance(){
		if (plugManager == null) {
			plugManager = new PlugManager();
		}
		return plugManager;
	}
	
	private PlugManager(){
		
	}
	
	
}
