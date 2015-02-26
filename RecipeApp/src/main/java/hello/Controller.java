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
import com.fasterxml.jackson.databind.DeserializationFeature;
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
	
	public List<Recipe> condenseRecipesIngredients(List<Recipe> result){
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	
    	//Collections.sort(result);
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)==-1){
    			newList.add(curr);
    		}else{
    			endOfList.addIngredient(curr.getFirstIngredient());
    		}	
    	}
    	 return newList;			
	}
	
	public List<Recipe> condenseFullRecipe(List<Recipe> result){
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	
    	//Collections.sort(result);
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)==1){
    			newList.add(curr);
    		}else{
    			Ingredient next_i = curr.getFirstIngredient();
    			RecipeStep next_s = curr.getFirstStep();
    			if(!endOfList.getIngredients().contains(next_i)){
    				endOfList.addIngredient(next_i);
    				
    			}
    			if(!endOfList.getSteps().contains(next_s)){
    				endOfList.addStep(next_s);
    			}
    		}	
    	}
    	 return newList;			
	}	
	
  //  private static final String template = "Hello, %s!";
 //   private final AtomicLong counter = new AtomicLong();
    /*
     * Function should retrieve a list of recipe objects from the data base using ID
     * http://localhost:8080/recipe?id=2
     * 
     * Need to also build up list of ingredients and steps that are associated with that recipe ID
     */
    @RequestMapping("/recipeingredients")
    public String getRecipeById(@RequestParam(value="id", defaultValue="0") final long id) {
    	//Get recipe
    	 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	 List<Recipe> result = jdbcTemplate.query("select * from RecipeApp.recipe_with_ingredients where recipe_id = ? order by recipe_id",
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
    	
    	List<Recipe> newList = condenseRecipesIngredients(result);
    	return newList.toString(); 	    	
    }
    
    @RequestMapping("/recipe")
    public String fullRecipe(@RequestParam(value="id", defaultValue="0") final long id){
    	//Get recipe
   	 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
   	 List<Recipe> result = jdbcTemplate.query("select * from RecipeApp.full_recipe where recipe_id = ? order by recipe_id",
   	   new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, id);
            }
   	   },
            new RowMapper<Recipe>() {
                @Override
                public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                	Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),rs.getString("amount"));             	 
                	RecipeStep s = new RecipeStep(rs.getLong("recipe_id"),rs.getInt("step"),rs.getString("step_description"));             	 
                    return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                            rs.getString("description"), rs.getInt("cooking_time"),i,s);
            }
          });
   	
   	List<Recipe> newList = condenseFullRecipe(result);
   	return newList.get(0).toString();     	    	
    }
    
    //http://localhost:8080/byingredients?ingredients=chicken,rice
    @RequestMapping(value = "/byingredients", method = RequestMethod.POST,headers ={"Accept=application/plain-text"})
    @ResponseBody
    public String byIngredients(@RequestBody String ingredients){
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);  	
    	ObjectMapper mapper = new ObjectMapper(); // create once, reuse
    	mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    	mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    	mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	List<String> names = null;
    	
    	try {
			names = mapper.readValue(ingredients, List.class);
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
    	
		String sql = "select * from"
				+ "	("
				+ " select  s.recipe_id, s.recipe_name, s.description, count(s.ingredient_name) as matching, ni.number_ingredients, count(s.ingredient_name) / ni.number_ingredients as match_rate"
				+ "    from"
				+ "    ("
				+ "     select DISTINCT r.recipe_id, r.recipe_name, r.description, i.ingredient_name"
				+ "	        from RecipeApp.recipes r, RecipeApp.ingredients i, RecipeApp.recipes_ingredients ri"
				+ "         where"
				+ "         ri.recipe_id = r.recipe_id and ri.ingredient_id = i.ingredient_id and"
				+ "	        (";
		
				
		//add ingredients into sql - change to injection once tested
		for(int i = 0; i<names.size()-1;i++)
			sql+= "i.ingredient_name = \""+names.get(i)+"\" OR ";

		sql+= "i.ingredient_name = \""+names.get(names.size()-1)+"\"";
		sql+= ")"
				+ " order by recipe_name"
				+ " ) s"
				+ " inner join ("
				+ " select ri.recipe_id, count(ri.ingredient_id) as number_ingredients"
				+ " from RecipeApp.recipes_ingredients ri"
				+ " group by recipe_id"
				+ " ) ni"
				+ " on s.recipe_id = ni.recipe_id"
				+ " group by recipe_name"
				+ " order by match_rate desc, matching, recipe_name"
				+ "	) m"
				+ " inner join RecipeApp.recipe_with_ingredients rwi on m.recipe_id = rwi.recipe_id";
	    	
        List<Recipe> results = jdbcTemplate.query(
                sql,
                new RowMapper<Recipe>() {
                    @Override
                    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),rs.getString("amount"));
                    	
                        return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                                rs.getString("description"), rs.getInt("cooking_time"),i,null);
                    }
                    
                });   
    	List<Recipe> newList = condenseRecipesIngredients(results); 	
    	return newList.toString();
    	
    }
    
    @RequestMapping("ingredients")
    public String allIngredients(){
      	 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
         List<Ingredient> results = jdbcTemplate.query(
                 "select * from RecipeApp.ingredients",
                 new RowMapper<Ingredient>() {
                     @Override
                     public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException { 
                         return new Ingredient(0,rs.getString("ingredient_name"),"");
                     }
                 });   
       	return results.toString();       	
 
    }
    
    
    @RequestMapping("/recipes")
    public String allRecipes(){
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	
        System.out.println("Querying for all recipes");
        List<Recipe> results = jdbcTemplate.query(
                "select * from RecipeApp.recipe_with_ingredients order by recipe_id",
                new RowMapper<Recipe>() {
                    @Override
                    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),rs.getString("amount"));
                    	
                        return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                                rs.getString("description"), rs.getInt("cooking_time"),i,null);
                    }
                    
                });   
    	List<Recipe> newList = condenseRecipesIngredients(results);
    	
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
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
		//System.out.println(r.toString());
		//will need to parse the ingredients and steps in too

    	return new ResponseEntity<String>(r.toString(), HttpStatus.OK);
    }
    
    
}