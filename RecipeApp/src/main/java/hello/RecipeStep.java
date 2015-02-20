package hello;

public class RecipeStep {
	private final int step; //use this as an id combined with recipeID
	private final long recipeId;
	private final String description;
	
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
	
	
}
