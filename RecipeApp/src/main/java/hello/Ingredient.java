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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		String result = "{\n\"name\": \""+name+"\"";
		if(amount.length()>0)
			result+=",\n\"amount\":\""+amount+"\"";
		result+= "\n}";
		return result;//"{\n\"name\": \""+name+"\",\n\"amount\":\""+amount+"\"\n}";
	}
	
}
