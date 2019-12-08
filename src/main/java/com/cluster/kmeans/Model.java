package com.cluster.kmeans;

public class Model {
	public long id;         
	public String title;      
	public String publication;
	public String author;     
	public long date;   
	public String year;   
	public String month; 
	public String url;     
	public String content;    

	public Model() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Model [id=" + id + ", publication=" + publication + ", author=" + author + ", date=" + date + "]";
	}



	

}
