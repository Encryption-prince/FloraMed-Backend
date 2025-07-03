package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;


import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer>{

	Orders findByRazorpayOrderId(String razorpayId);
	@Query(value = "SELECT DATE(created_at) as date, SUM(amount) as totalRevenue " +
			"FROM orders GROUP BY DATE(created_at) ORDER BY DATE(created_at) ASC", nativeQuery = true)
	List<Object[]> findRevenueByDate();


}
