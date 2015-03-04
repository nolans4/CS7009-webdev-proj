package hello;

public class ImgModel {
	private String description;
	private String name;
	private Long recipeid;
	
	public ImgModel(){
		this.description = "";
		this.name = "";
		this.recipeid = -1L;
	}
	
	public ImgModel(String description, String name, Long recipeid){
		this.description = description;
		this.name = name;
		this.recipeid = recipeid;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getRecipeid() {
		return recipeid;
	}
	public void setRecipeid(Long recipeid) {
		this.recipeid = recipeid;
	}
	
	
	
	
	
}
