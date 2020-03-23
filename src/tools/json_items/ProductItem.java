package tools.json_items;

public class ProductItem {
	private int id = 0;
	private String name = "";
	private int availability = 0;
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param availability
	 */
	public ProductItem(int id, String name, int availability) {
		super();
		this.id = id;
		this.name = name;
		this.availability = availability;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}
	
	
}
