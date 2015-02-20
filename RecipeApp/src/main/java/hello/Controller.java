package hello;

//import java.util.concurrent.atomic.AtomicLong;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.core.RowMapper;


@RestController
public class Controller {
	private SimpleDriverDataSource dataSource;
	
	public void setDataSource(SimpleDriverDataSource dataSource){
		this.dataSource = dataSource;	
	}
	
	public List<Recipe> condenseRecipes(List<Recipe> result){
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	
    	Collections.sort(result);
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)==1){
    			newList.add(curr);
    		}else{
    			endOfList.addIngredient(curr.getFirstIngredient());
    		}	
    	}
    	 return newList;			
	}
  //  private static final String template = "Hello, %s!";
 //   private final AtomicLong counter = new AtomicLong();

    //set up some dummy data ?? 
    /*
     * Function should retrieve a list of recipe objects from the data base using ID
     * http://localhost:8080/recipe?id=2
     * 
     * Need to also build up list of ingredients and steps that are associated with that recipe ID
     */
    @RequestMapping("/recipe")
    public String getRecipeById(@RequestParam(value="id", defaultValue="0") final long id) {
    	//Get recipe
    	 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	 List<Recipe> result = jdbcTemplate.query("select * from recipe_with_ingredients where recipe_id = ?",
    	   new PreparedStatementSetter() {
             public void setValues(PreparedStatement ps) throws SQLException {
                 ps.setLong(1, id);
             }
    	   },
             new RowMapper<Recipe>() {
                 @Override
                 public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                 	 Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),rs.getString("amount"));             	 
                     return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                             rs.getString("description"), rs.getInt("cooking_time"),i,null);
             }
           });
    	
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	result.sort(null);//this will only be necessary when getting ALL recipes
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)==1){
    			newList.add(curr);
    		}else{
    			endOfList.addIngredient(curr.getFirstIngredient());
    		}	
    	}
    	 
    	 
     	ObjectMapper mapper = new ObjectMapper(); // create once, reuse
     	mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
     	mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
     	mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
     	
     	StringWriter s = new StringWriter();
    	String json = "";
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	try {
			//mapper.writeValue(s, results);
    		json = ow.writeValueAsString(result);
 		} catch (JsonGenerationException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (JsonMappingException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
     	return newList.get(0).toString();   	 
    	    	
    }
    
    @RequestMapping("/allrecipe")
    public String allRecipes(){
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	
        System.out.println("Querying for all recipes");
        List<Recipe> results = jdbcTemplate.query(
                "select * from recipe_with_ingredients",
                new RowMapper<Recipe>() {
                    @Override
                    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),rs.getString("amount"));
                    	
                        return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                                rs.getString("description"), rs.getInt("cooking_time"),i,null);
                    }
                });    	
    	List<Recipe> newList = condenseRecipes(results);
    	
    	return newList.toString();
    }
    
    @RequestMapping("/")
    public String initial(){ 	
    	return new String("Recipe application!");
    }
    
    @RequestMapping(value ="/sendRecipe", method = RequestMethod.POST,headers ={"Accept=application/plain-text"})
    @ResponseBody
    public ResponseEntity<String> sendNewRecipe(@RequestBody String recipe){
    	//System.out.println("Taking "+recipe);
    	ObjectMapper mapper = new ObjectMapper(); // create once, reuse
    	mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    	mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    	mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
    	
    	Recipe r=null;
		try {
			r = mapper.readValue(recipe, Recipe.class);
			//System.out.println("Recipe "+r.toString());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//will need to parse the ingredients and steps in too

		
    	
    	return new ResponseEntity<String>(recipe, HttpStatus.OK);
    }
    
    
}