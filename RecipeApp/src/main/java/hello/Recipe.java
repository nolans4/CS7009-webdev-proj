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
    private List<Long> imageids;
    private String addedby;
    private float match;

    public boolean contains;

    public Recipe(){
    	this.id = -1;
    	this.time = "";
    	this.title ="";
    	this.description = "";
        this.ingredients = new ArrayList<Ingredient>();
        this.steps = new ArrayList<RecipeStep>();
        this.imageids = new ArrayList<Long>();
        this.addedby = "";
        match = -1;
        contains = false;

    }
    
    public Recipe(long id, String title, String description, String time, Ingredient i, RecipeStep s, String addedby, Long image_id) {
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
        if(image_id>=0)
        	imageids.add(image_id);
        
        this.imageids = new ArrayList<Long>();
        this.addedby = addedby;
        match =-1;
        contains = false;

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
	
	public void setAddedby(String ab){
		this.addedby = ab;
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
    
    public List<Long> getImageids(){
    	return imageids;
    }
    
    public void addImageId(Long id){
    	imageids.add(id);
    }
    
    public Long getFirstImageId(){
    	return imageids.get(0);
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
    
    public String getAddedby(){
    	return addedby;
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
		
		if(imageids.size()>0){
			result+="\n],\n\"imageids\":\n[\n";
			
			for(int i = 0; i<imageids.size(); i++){
				
				result+=imageids.get(i);
				if(i!=steps.size()-1)
					result+=",";
				result+="\n";
			}
		}
		result+="\n],\n\"addedby\":\""+addedby+"\"\n}";
		
		return result;		
	}

	public void addImageid(Long next_iid) {
		this.imageids.add(next_iid);		
	}

}
