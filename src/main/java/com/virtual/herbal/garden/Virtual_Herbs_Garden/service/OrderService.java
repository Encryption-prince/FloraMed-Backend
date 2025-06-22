package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.PlantPurchase;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.OrdersRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantPurchaseRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private PlantPurchaseRepository plantPurchaseRepo;


	@Value("${razorpay.key.id}")
	private String razorpayId;
	@Value("${razorpay.key.secret}")
	private String razorpaySecret;
	
	private RazorpayClient razorpayCLient;
	
	@PostConstruct
	public void init() throws RazorpayException {
		this.razorpayCLient = new RazorpayClient(razorpayId, razorpaySecret);
	}
	
	public Orders createOrder(Orders order) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("amount", order.getAmount() * 100); // amount in paise
        options.put("currency", "INR");
        options.put("receipt", order.getEmail());
        Order razorpayOrder = razorpayCLient.orders.create(options);
        if(razorpayOrder != null) {
        order.setRazorpayOrderId(razorpayOrder.get("id"));
        order.setOrderStatus(razorpayOrder.get("status"));
        }
        return ordersRepository.save(order);
    }
//actual
//	public Orders updateStatus(Map<String, String> map) {
//    	String razorpayId = map.get("razorpay_order_id");
//    	Orders order = ordersRepository.findByRazorpayOrderId(razorpayId);
//    	order.setOrderStatus("PAYMENT DONE");
//    	Orders orders = ordersRepository.save(order);
//    	return orders;
//    }

public Orders updateStatus(Map<String, String> map) {
	String razorpayId = map.get("razorpay_order_id");
	Orders order = ordersRepository.findByRazorpayOrderId(razorpayId);
	order.setOrderStatus("PAYMENT DONE");
	Orders updatedOrder = ordersRepository.save(order);

	if ("PAYMENT DONE".equals(updatedOrder.getOrderStatus())) {
		// Save purchase directly without API call
		PlantPurchase purchase = PlantPurchase.builder()
				.plantId(updatedOrder.getPlantId())
				.userEmail(updatedOrder.getEmail())
				.purchasedAt(LocalDateTime.now())
				.build();

		plantPurchaseRepo.save(purchase);
		System.out.println("✅ Purchase saved for " + updatedOrder.getEmail());
	}

	return updatedOrder;
}



}
