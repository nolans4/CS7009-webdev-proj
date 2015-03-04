package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"recipeId"})
public class RecipeStep implements Comparable<RecipeStep> {
	private final int step; //use this as an id combined with recipeID
	private final long recipeId;
	private final String description;
	
	public RecipeStep(){
		this.recipeId = -1;
		this.step = 0;
		this.description = "";
	}
	
	public RecipeStep(long recipeId, int step, String description){
		this.recipeId = recipeId;
		this.step = step;
		this.description = description;	
	}

	public int getStep() {
		return step;
	}

	public long getRecipeId() {
		return recipeId;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString(){
		return "{\n\"step\": "+step+",\n\"description\": \""+this.description+"\"}";
	}

	@Override
	public int compareTo(RecipeStep o) {
		// TODO Auto-generated method stub
		if(recipeId==o.recipeId&&step==o.step)
			return 0;
		if(recipeId==o.recipeId&&step>o.step)
			return 1;
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecipeStep other = (RecipeStep) obj;
		if (recipeId != other.recipeId)
			return false;
		if (step != other.step)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
		result = prime * result + step;
		return result;
	}
	
}
