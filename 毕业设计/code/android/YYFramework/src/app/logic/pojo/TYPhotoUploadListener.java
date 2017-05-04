package app.logic.pojo;

public interface TYPhotoUploadListener {

	public void nextTask(TYPhotoInfo taskInfo);
	
	public void taskStatusChange(TYPhotoInfo taskInfo);
	
	public void didAddNewTask(TYPhotoInfo taskInfo);
	
	public void didAllTaskFinish();
	
	public void didRemoveTask(TYPhotoInfo taskInfo);
		
}
