package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Orders")
public class Orders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer orderId;
	@ElementCollection
	private List<Long> productIds;
	private String name;
	private String email;
	private Integer amount;
	private String orderStatus;
	private String razorpayOrderId;
	@CreationTimestamp   // Hibernate will auto-set this on insert
	private LocalDateTime createdAt;
//	public Integer getOrderId() {
//		return orderId;
//	}
//	public List<Long> getProductIds() {return productIds;}
//	public void setProductIds(List<Long> plantIds) {this.productIds = productIds;}
//	public void setOrderId(Integer orderId) {
//		this.orderId = orderId;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getEmail() {
//		return email;
//	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
//	public Integer getAmount() {
//		return amount;
//	}
//	public void setAmount(Integer amount) {
//		this.amount = amount;
//	}
//	public String getOrderStatus() {
//		return orderStatus;
//	}
//	public void setOrderStatus(String orderStatus) {
//		this.orderStatus = orderStatus;
//	}
//	public String getRazorpayOrderId() {
//		return razorpayOrderId;
//	}
//	public void setRazorpayOrderId(String razorpayOrderId) {
//		this.razorpayOrderId = razorpayOrderId;
//	}

}
