package tools;

public class PeriodItem {
	private String close_name = "";	
	private String close_name_short = "";	
	private int close_day_id = 0;
	private String close_time = "";			//format is hh:mm
	private String open_name = "";	
	private String open_name_short = "";	
	private int open_day_id = 0;
	private String open_time = "";			//format is hh:mm
	public int getClose_day_id() {
		return close_day_id;
	}
	public void setClose_day_id(int close_day_id) {
		this.close_day_id = close_day_id;
	}
	public String getClose_time() {
		return close_time;
	}
	public void setClose_time(String close_time) {
		this.close_time = close_time;
	}
	public int getOpen_day_id() {
		return open_day_id;
	}
	public void setOpen_day_id(int open_day_id) {
		this.open_day_id = open_day_id;
	}
	public String getOpen_time() {
		return open_time;
	}
	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
	public String getClose_name() {
		return close_name;
	}
	public void setClose_name(String close_name) {
		this.close_name = close_name;
	}
	public String getClose_name_short() {
		return close_name_short;
	}
	public void setClose_name_short(String close_name_short) {
		this.close_name_short = close_name_short;
	}
	public String getOpen_name() {
		return open_name;
	}
	public void setOpen_name(String open_name) {
		this.open_name = open_name;
	}
	public String getOpen_name_short() {
		return open_name_short;
	}
	public void setOpen_name_short(String open_name_short) {
		this.open_name_short = open_name_short;
	}
}
