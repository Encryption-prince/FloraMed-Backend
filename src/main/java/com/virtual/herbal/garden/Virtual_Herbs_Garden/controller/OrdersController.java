package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.razorpay.RazorpayException;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Order API")
@RequestMapping("/payment")
public class OrdersController {
	
	@Autowired
	private OrderService orderService;

	@PostMapping("/create")
	public ResponseEntity<Orders> createOrder(@RequestBody Orders order) throws RazorpayException {
		Orders createdOrder = orderService.createOrder(order);
		return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
	}

	@PostMapping("/paymentCallback")
	public ResponseEntity<String> updatePaymentStatus(@RequestBody Map<String, String> callbackData) {
		Orders updatedOrder = orderService.updateStatus(callbackData);
		return ResponseEntity.ok("Payment status updated");
	}

	@PostMapping("/update-status")
	public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, String> payload) {
		Orders updatedOrder = orderService.updateStatus(payload);
		if (updatedOrder != null) {
			return ResponseEntity.ok(Map.of(
					"message", "Order updated and purchase logged successfully.",
					"orderId", updatedOrder.getOrderId(),
					"status", updatedOrder.getOrderStatus()
			));
		} else {
			return ResponseEntity.status(404).body(Map.of("error", "Order not found."));
		}
	}


}
