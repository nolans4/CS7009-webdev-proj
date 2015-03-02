package hello;

public class Image {
	private byte[] image;
	private String name;
	private String contenttype;
	private long size;
	private String description;
	
	public Image(byte[] image, String name, String contenttype, long size, String description){
		this.image = image;
		this.name = name;
		this.contenttype = contenttype;
		this.size = size;
		this.description = description;
	}
	
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	
}
