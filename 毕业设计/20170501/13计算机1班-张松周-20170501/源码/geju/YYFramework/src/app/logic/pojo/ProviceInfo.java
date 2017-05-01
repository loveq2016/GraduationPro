package app.logic.pojo;

/*
 * GZYY    2016-8-18  上午11:40:56
 */

public class ProviceInfo {

	private String name;
	private String[] cities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getCities() {
		return cities;
	}

	public void setCities(String[] cities) {
		this.cities = cities;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;

	}

}
