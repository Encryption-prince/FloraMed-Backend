package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;


import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer>{

	Orders findByRazorpayOrderId(String razorpayId);

}
