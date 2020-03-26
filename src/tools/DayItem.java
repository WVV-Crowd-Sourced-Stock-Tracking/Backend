package tools;

public class DayItem {
	private int day_id = 0;
	private String day_name = "";
	private String day_name_short = "";
	public int getDay_id() {
		return day_id;
	}
	public void setDay_id(int day_id) {
		this.day_id = day_id;
	}
	public String getDay_name() {
		return day_name;
	}
	public void setDay_name(String day_name) {
		this.day_name = day_name;
	}
	public String getDay_name_short() {
		return day_name_short;
	}
	public void setDay_name_short(String day_name_short) {
		this.day_name_short = day_name_short;
	}
	public DayItem(int day_id, String day_name, String day_name_short) {
		super();
		this.day_id = day_id;
		this.day_name = day_name;
		this.day_name_short = day_name_short;
	}
	
}
