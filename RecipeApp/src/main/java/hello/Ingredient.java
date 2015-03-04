package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"id"})
public class Ingredient implements Comparable<Ingredient> {
	private final long id;
	private final String name;
	private final String amount;
	
	public Ingredient(){
		this.id = -1;
		this.amount = "";
		this.name ="";
	}
	
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
		Ingredient o = (Ingredient) obj;
		if(this.id == o.id && this.amount.equals(o.amount) && this.name.equals(o.name))
				return true;
		return false;
	}

	@Override
	public int compareTo(Ingredient o) {
		// TODO Auto-generated method stub
		if(id==o.id&&this.amount.equals(o.amount)&&this.name.equals(o.name))
			return 0;
		if(id==o.id&&!this.amount.equals(o.amount))
			return 1;
		return -1;
	}
	
	public String getAmount(){
		return this.amount;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		String result = "";
		if(amount.length()>0){		
			result = "{\n\"name\": \""+name+"\"";
			result+=",\n\"amount\":\""+amount+"\"";
			result+= "\n}";
		}else{
			result ="\""+ name+"\"";		
		}
		return result;//"{\n\"name\": \""+name+"\",\n\"amount\":\""+amount+"\"\n}";
	}
	
}
