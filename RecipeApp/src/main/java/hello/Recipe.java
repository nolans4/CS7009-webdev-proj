package hello;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Comparable<Recipe> {
    private long id;
    private String title;
    private String description;
    private int preptime;
    private List<Ingredient> ingredients;
    private List<RecipeStep> steps;

    public Recipe(){
    	this.id = -1;
    	this.preptime = 0;
    	this.title ="";
    	this.description = "";
        ingredients = new ArrayList<Ingredient>();
        steps = new ArrayList<RecipeStep>();
    }
    
    public Recipe(long id, String title, String description, int preptime, Ingredient i, RecipeStep s) {
        this.id = id;
        this.preptime = preptime;
        this.description = description;
        this.title = title;
        ingredients = new ArrayList<Ingredient>();
        steps = new ArrayList<RecipeStep>();
        if(i!=null)
        	ingredients.add(i);
        if(s!=null)
        	steps.add(s);
    }
    
    @Override
    public int compareTo(Recipe other){
		if(other.getId()==this.id)
			return 0; //equal
		if(other.getId()<this.id)
			return -1; //less than
		return 1; //greater than
    }
    
    public Ingredient getFirstIngredient(){
    	return ingredients.get(0);
    }
    
    public List<Ingredient> getAllIngredients(){
    	return ingredients;
    }
    
    public RecipeStep getFirstStep(){
    	return steps.get(0);
    }
    
    public List<RecipeStep> getAllSteps(){
    	return steps;
    }
    
    
    public void setId(int id){
    	this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    
    public String getTitle() {
        return title;
    }

	public int getPreptime() {
		return preptime;
	} 
	
	public void addIngredient(Ingredient i){
		ingredients.add(i);
	}
	
	public void addStep(RecipeStep rs){
		steps.add(rs);
	}

	
	public String toString(){
		String result = "{\n\"id\": "+this.id + ",\n\"title\": \""+this.title+"\",\n\"description\": \""+this.description+
				"\",\n\"time\": "+this.preptime+",\n\"ingredients\":\n[";
		for(int i = 0; i<ingredients.size(); i++){
			result+=ingredients.get(i).toString();
			if(i!=ingredients.size()-1)
				result+=",";
			result+="\n";
		}
		result+="\n],\n\"steps\":\n[\n";
		
		for(int i = 0; i<steps.size(); i++){
			result+=steps.get(i).toString();
			if(i!=steps.size()-1)
				result+=",";
			result+="\n";
		}
		result+="\n]\n}";
		
		return result;		
	}
	
	
}
