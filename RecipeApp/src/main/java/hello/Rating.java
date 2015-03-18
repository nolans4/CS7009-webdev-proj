package hello;

public class Rating {
	private long recipeId;
	private String addedBy;
	private String description;
	private double rating;
	
	public Rating(long recipeId, String addedBy, String description, double rating){
		this.recipeId = recipeId;
		this.addedBy = addedBy;
		this.description = description;
		this.rating = rating;		
	}
	
	
	public long getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(long recipeId) {
		this.recipeId = recipeId;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}	
	
}
