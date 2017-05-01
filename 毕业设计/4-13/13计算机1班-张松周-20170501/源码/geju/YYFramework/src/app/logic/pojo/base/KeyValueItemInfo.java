package app.logic.pojo.base;

public class KeyValueItemInfo<TName,TID> {

	private TName ItemName;
	
	private TID ItemID;

	public TName getItemName() {
		return ItemName;
	}

	public void setItemName(TName itemName) {
		ItemName = itemName;
	}

	public TID getItemID() {
		return ItemID;
	}

	public void setItemID(TID itemID) {
		ItemID = itemID;
	}
	
	public String toString(){
		if (ItemName instanceof String) {
			return (String)ItemName;
		}
		return ItemName.toString();
	}
}
