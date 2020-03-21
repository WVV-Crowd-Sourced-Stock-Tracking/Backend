package webService;

import tools.GenericResponse;

public class HelloResponse extends GenericResponse {
	private String text = "";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
