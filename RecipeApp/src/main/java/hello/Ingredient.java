package hello;

public class Ingredient {
	private final long id;
	private final String name;
	private final String amount;
	
	public Ingredient(long id, String name, String amount){
		this.id = id;
		this.name = name;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		return "{\n\"name\": \""+name+"\",\n\"amount\":\""+amount+"\"\n}";
	}
	
}
