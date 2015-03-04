package hello;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadHandler {
	private List<MultipartFile> files;
	private ImgModel model;
	
	public FileUploadHandler(){
		this.files = new ArrayList<MultipartFile>();
		this.model = new ImgModel();
		
	}
	
	public FileUploadHandler(List<MultipartFile> files, ImgModel model){
		this.files = files;
		this.model = model;
	}
	
	public List<MultipartFile> getFiles() {
		return files;
	}
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
	public ImgModel getModel() {
		return model;
	}
	public void setModel(ImgModel model) {
		this.model = model;
	}
	
	
}
