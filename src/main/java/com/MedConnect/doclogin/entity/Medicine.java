package com.MedConnect.doclogin.entity;

import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@CrossOrigin(origins = "https://medconnect-frontend-1.onrender.com")
@Entity
@Table(name ="medicines")
public class Medicine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "drug_name")
	private String drugName;
	
	private String stock;

	public Medicine(long id, String drugName, String stock) {
		super();
		this.id = id;
		this.drugName = drugName;
		this.stock = stock;
	}

	public Medicine() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String name) {
		this.drugName = name;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}
	
	

	
}
