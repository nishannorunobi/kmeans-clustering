package com.cluster.kmeans;

public class Model {
	public long indexNo;
	public long id;
	public String title;      
	public long publication;
	public long author;
	public long date;   
	public long year;
	public long month;
	public String url;     
	public String content;    

	public Model() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Model{" +
				"indexNo=" + indexNo +
				", id=" + id +
				", publication=" + publication +
				", author=" + author +
				", date=" + date +
				", year=" + year +
				", month=" + month +
				'}';
	}
}
