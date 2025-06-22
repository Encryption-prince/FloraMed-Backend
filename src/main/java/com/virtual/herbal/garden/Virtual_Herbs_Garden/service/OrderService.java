package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.OrdersRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	
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

	if (updatedOrder.getOrderStatus().equals("PAYMENT DONE")) {
		// Call Virtual Herbal Garden purchase logging API
		try {
			String apiUrl = "https://quarrelsome-mae-subham-org-14444f5f.koyeb.app/purchases"; // adjust host/port

			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			String jsonPayload = String.format(
					"{\"plantId\":%d, \"userEmail\":\"%s\", \"purchasedAt\":\"%s\"}",
					updatedOrder.getPlantId(),
					updatedOrder.getEmail(),
					LocalDateTime.now()
			);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = jsonPayload.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = conn.getResponseCode();
			if (responseCode == 200 || responseCode == 201) {
				System.out.println("Purchase logged successfully.");
			} else {
				System.out.println("Failed to log purchase.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	return updatedOrder;
}

}
