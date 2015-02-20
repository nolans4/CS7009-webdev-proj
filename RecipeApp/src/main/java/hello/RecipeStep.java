package hello;

public class RecipeStep implements Comparable<RecipeStep> {
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
	public boolean equals(Object other){
		RecipeStep o = (RecipeStep) other;
		if(o.step == step && o.recipeId == recipeId)
			return true;
		return false;
	}
	
	
}
