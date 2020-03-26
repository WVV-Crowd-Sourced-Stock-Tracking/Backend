package webService;

import java.util.ArrayList;
import java.util.List;

import tools.DayItem;
import tools.GenericResponse;

public class DayResponse extends GenericResponse {
	List<DayItem> days = new ArrayList<DayItem>();

	public List<DayItem> getDays() {
		return days;
	}

	public void setDays(List<DayItem> days) {
		this.days = days;
	}

}
