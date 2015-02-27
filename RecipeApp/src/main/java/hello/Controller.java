package hello;

//import java.util.concurrent.atomic.AtomicLong;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;


@RestController
public class Controller {
	private SimpleDriverDataSource dataSource;
	
	public void setDataSource(SimpleDriverDataSource dataSource){
		this.dataSource = dataSource;	
	}
	/*
	 * Condenses results into recipes and ingredients
	 */
	public List<Recipe> condenseRecipesIngredients(List<Recipe> result){
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)!=0){
    			newList.add(curr);
    		}else{
    			endOfList.addIngredient(curr.getFirstIngredient());
    		}	
    	}
    	 return newList;			
	}
	/*
	 * Condenses results into full recipes
	 */
	public List<Recipe> condenseFullRecipe(List<Recipe> result){
    	List<Recipe> newList = new ArrayList<Recipe>();    	
    	
    	//Collections.sort(result);
    	newList.add(result.get(0));
    	//go through list adding to new list 
    	for(int i = 1; i<result.size();i++){
    		Recipe curr = result.get(i);
    		Recipe endOfList = newList.get(newList.size()-1); 		
    		//if we have a new recipe to add the id will be greater than the last recipe in the list
    		if(curr.compareTo(endOfList)!=0){
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
	
	/*
	 * Not currently used?
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
    
    /*
     * Retrieve full recipe by id
     */
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
    
    /*
     * Retrieve recipes ranked by ingredients passed
     */
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
                    	Ingredient i = new Ingredient(0,rs.getString("ingredient_name"),"");
                    	Recipe r = new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                                rs.getString("description"), rs.getInt("cooking_time"),i,null);
                    	r.contains = true;
                        return r;
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
    
    /*
     * Insert a new recipe
     */
    @RequestMapping(value ="/sendRecipe", method = RequestMethod.POST,headers ={"Accept=application/plain-text"})
    @ResponseBody
    public ResponseEntity<String> sendNewRecipe(@RequestBody String recipe){
    	ObjectMapper mapper = new ObjectMapper(); // create once, reuse
    	mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    	mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    	mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	//mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    	//mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,true);
 
    	Recipe r=null;
		try {
			r = mapper.readValue(recipe, Recipe.class);
			//System.out.println("Recipe "+r.toString());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error", HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error", HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error", HttpStatus.BAD_REQUEST);
		}
		
		//Insert recipe and get the id needed for the ingredient inserts
		SimpleJdbcCall recipeCall = new SimpleJdbcCall(dataSource).withCatalogName("RecipeApp").withProcedureName("new_recipe")
				.withoutProcedureColumnMetaDataAccess()
				.declareParameters(new SqlParameter("name", Types.VARCHAR), new SqlParameter("descr", Types.VARCHAR), new SqlParameter("time", Types.INTEGER), new SqlOutParameter("id", Types.BIGINT));
		
		SimpleJdbcCall ingredientCall = new SimpleJdbcCall(dataSource).withCatalogName("RecipeApp").withProcedureName("new_recipe_ingredient")
				.withoutProcedureColumnMetaDataAccess()
				.declareParameters(new SqlParameter("name", Types.VARCHAR), new SqlParameter("amount", Types.VARCHAR), new SqlParameter("recipe_id", Types.BIGINT), new SqlOutParameter("ing_id", Types.BIGINT));
		
		SimpleJdbcCall stepCall = new SimpleJdbcCall(dataSource).withCatalogName("RecipeApp").withProcedureName("new_recipe_step")
			.withoutProcedureColumnMetaDataAccess()
			.declareParameters(new SqlParameter("step", Types.INTEGER), new SqlParameter("description", Types.VARCHAR), new SqlParameter("recipe_id", Types.BIGINT));
		
		
		SqlParameterSource recipe_in = new MapSqlParameterSource()//addValues(r.getTitle(),r.getDescription(), r.getTime());
                .addValue("name", r.getTitle()).addValue("descr", r.getDescription()).addValue("time", r.getTime());

		Map<String, Object> out = recipeCall.execute(recipe_in);
		Long recipe_id = (Long)out.get("id");
		
		for(int i = 0; i<r.getIngredients().size(); i++){
			Ingredient curr = r.getIngredients().get(i);
			SqlParameterSource ingredient_in = new MapSqlParameterSource().
	                addValue("name", curr.getName()).addValue("amount", curr.getAmount()).addValue("recipe_id", recipe_id);
			ingredientCall.execute(ingredient_in);					
		}
		
		for(int i = 0; i<r.getSteps().size(); i++){
			RecipeStep curr = r.getSteps().get(i);
			SqlParameterSource step_in = new MapSqlParameterSource().
	                addValue("step", curr.getStep()).addValue("recipe_id", recipe_id).addValue("description", curr.getDescription());
			stepCall.execute(step_in);			
		}		
		
		//TEST
		/*SimpleJdbcCall testCall = new SimpleJdbcCall(dataSource).withCatalogName("RecipeApp").withProcedureName("test_proc").withoutProcedureColumnMetaDataAccess()
			    .declareParameters(new SqlParameter("id",Types.BIGINT),new SqlOutParameter("name", Types.VARCHAR));;//.useInParameterNames("name","description","cooking_time");
		SqlParameterSource in = new MapSqlParameterSource().addValue("id",1);//.addValues(r.getTitle(),r.getDescription(), r.getTime());
		Map<String, Object> result = testCall.execute(in);
		System.out.println(result.get("name"));*/
    	return new ResponseEntity<String>(r.toString(), HttpStatus.OK);
    }
    
}