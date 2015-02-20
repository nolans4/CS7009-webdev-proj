package hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) throws SQLException {
    	
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        Properties properties = new Properties();
        try {
        	  properties.load(new FileInputStream("db.properties"));
        	} catch (IOException e) {
        	  e.getStackTrace();
        	}
        
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUsername(properties.getProperty("db.username"));
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setPassword(properties.getProperty("db.password")); //run    
     /*   JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        	
        System.out.println("Querying for all recipes");
        List<Recipe> results = jdbcTemplate.query(
                "select * from recipes",
                new RowMapper<Recipe>() {
                    @Override
                    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Recipe(rs.getLong("recipe_id"), rs.getString("recipe_name"),
                                rs.getString("description"), rs.getInt("cooking_time"));
                    }
                });
        System.out.println("Querying for all ingredients");
        for (Recipe r : results) {
            System.out.println(r);
        } 	
        
        List<Ingredient> res = jdbcTemplate.query(
                "select * from ingredients",
                new RowMapper<Ingredient>() {
                    @Override
                    public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"));
                    }
                });

        for (Ingredient i : res) {
            System.out.println(i);
        } 
    	*/
		//ApplicationContext context = new ClassPathXmlApplicationContext(
		//		"applicationContext.xml");
    	
    	ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    	context.getBean(Controller.class).setDataSource(dataSource);
    }
}
