package hello;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe implements Comparable<Recipe> {
    private long id;
    private String title;
    private String description;
    private String time;
    private List<Ingredient> ingredients;
    private List<RecipeStep> steps;
    private float match;
    private String added_by;
    public boolean contains;

    public Recipe(){
    	this.id = -1;
    	this.time = "";
    	this.title ="";
    	this.description = "";
        this.ingredients = new ArrayList<Ingredient>();
        this.steps = new ArrayList<RecipeStep>();
        match = -1;
        contains = false;
        added_by = "";
    }
    
    public Recipe(long id, String title, String description, String time, Ingredient i, RecipeStep s) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.title = title;
        ingredients = new ArrayList<Ingredient>();
        steps = new ArrayList<RecipeStep>();
        if(i!=null)
        	ingredients.add(i);
        if(s!=null)
        	steps.add(s);
        match =-1;
        contains = false;
        added_by = "";
    }
    
    @Override
    public int compareTo(Recipe other){
		if(other.getId()==this.id)
			return 0; //equal
		if(other.getId()<this.id)
			return -1; //less than
		return 1; //greater than
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Recipe other = (Recipe) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public void setAddedBy(String ab){
		this.added_by = ab;
	}

	public Ingredient getFirstIngredient(){
    	return ingredients.get(0);
    }
    
    public List<Ingredient> getIngredients(){
    	return ingredients;
    }
    
    public RecipeStep getFirstStep(){
    	return steps.get(0);
    }
    
    public List<RecipeStep> getSteps(){
    	return steps;
    }
    
    
    public void setId(Long recipe_id){
    	this.id = recipe_id;
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

    public void setMatch(float match){
		this.match=match;
	}
    
    public String getAddedBy(){
    	return added_by;
    }
    
	public String getTime() {
		return time;
	} 
	
	public void addIngredient(Ingredient i){
		ingredients.add(i);
	}
	
	public void addStep(RecipeStep rs){
		steps.add(rs);
	}
	
	public String numMatch(){
		return ",\n\"match\": "+this.match;	
	}

	
	public String toString(){
		String result = "{\n\"id\": "+this.id + ",\n\"title\": \""+this.title+"\",\n\"description\": \""+this.description+
				"\",\n\"time\": \""+this.time+"\"";
		if(contains)
			result+=",\n\"contains\":\n[";
		else
			result+=",\n\"ingredients\":\n[";
		for(int i = 0; i<ingredients.size(); i++){
			result+=ingredients.get(i).toString();
			if(i!=ingredients.size()-1)
				result+=",";
			result+="\n";
		}
		result+="]";
		
		if(steps.size()==0){
			if(match>-1){
				result+=numMatch();
			}
			result+="}";
			return result;
		}
		
		result+=",\n\"steps\":\n[\n";
		
		for(int i = 0; i<steps.size(); i++){
			result+=steps.get(i).toString();
			if(i!=steps.size()-1)
				result+=",";
			result+="\n";
		}
		result+="\n],\n\"addedby\":\""+added_by+"\"\n}";
		
		return result;		
	}

}
