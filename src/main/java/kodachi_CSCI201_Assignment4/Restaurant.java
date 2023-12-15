package kodachi_CSCI201_Assignment4;

import java.sql.Timestamp;

public class Restaurant {
	private String name;
	private String address;
	private String link;
	private double rating;
	private String phone;
	private String price;
	private String cuisine;
	private String image;
	private int ind;
	private Timestamp dt;
	
	
	public Restaurant() {
		this.name = "";
		this.address = "";
		this.link = "";
		this.phone = "";
		this.price = "";
		this.cuisine = "";
		this.image = "";
	}
	
	public Restaurant(String n, String a, String l, double r, String ph, String pr, String c, String i) {
		this.name = n;
		this.address = a;
		this.link = l;
		this.rating = r;
		this.phone = ph;
		this.price = pr;
		this.cuisine = c;
		this.image = i;
	}
	
	public String getN() {
		return this.name;
	}
	
	public String getA() {
		return this.address;
	}
	
	public String getL() {
		return this.link;
	}
	
	public double getR() {
		return this.rating;
	}
	
	public String getPh() {
		return this.phone;
	}
	
	public String getPr() {
		return this.price;
	}
	
	public String getC() {
		return this.cuisine;
	}
	
	public String getI() {
		return this.image;
	}
	
	public int getInd() {
		return this.ind;
	}
	
	public Timestamp getDT() {
		return this.dt;
	}
	
	public void setN(String n) {
		this.name = n;
	}
	
	public void setA(String a) {
		this.address = a;
	}
	
	public void setL(String l) {
		this.link = l;
	}
	
	public void setR(double r) {
		this.rating = r;
	}
	
	public void setPh(String ph) {
		this.phone = ph;
	}
	
	public void setPr(String p) {
		this.price = p;
	}
	
	public void setC(String c) {
		this.cuisine = c;
	}
	
	public void setI(String i) {
		this.image = i;
	}
	
	public void setInd(int ind) {
		this.ind = ind;
	}
	
	public void setDT(Timestamp dt) {
		this.dt = dt;
	}
}
