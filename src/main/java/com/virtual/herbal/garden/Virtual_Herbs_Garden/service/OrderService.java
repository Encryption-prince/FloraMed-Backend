package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Orders;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductPurchase;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Product;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.OrdersRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductPurchaseRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ProductPurchaseRepository plantPurchaseRepo;

	@Autowired
	private ProductRepository productRepo;


	@Value("${razorpay.key.id}")
	private String razorpayId;
	@Value("${razorpay.key.secret}")
	private String razorpaySecret;
	
	private RazorpayClient razorpayCLient;
	
	@PostConstruct
	public void init() throws RazorpayException {
		this.razorpayCLient = new RazorpayClient(razorpayId, razorpaySecret);
	}
	//actual
//	public Orders createOrder(Orders order) throws RazorpayException {
//        JSONObject options = new JSONObject();
//        options.put("amount", order.getAmount() * 100); // amount in paise
//        options.put("currency", "INR");
//        options.put("receipt", order.getEmail());
//        Order razorpayOrder = razorpayCLient.orders.create(options);
//        if(razorpayOrder != null) {
//        order.setRazorpayOrderId(razorpayOrder.get("id"));
//        order.setOrderStatus(razorpayOrder.get("status"));
//        }
//        return ordersRepository.save(order);
//    }
	public Orders createOrder(Orders orderRequest) throws RazorpayException {
		List<Long> productIds = orderRequest.getProductIds();
		if (productIds == null || productIds.isEmpty()) {
			throw new IllegalArgumentException("Product IDs cannot be empty");
		}

		// Fetch prices from DB
		List<Product> products = productRepo.findAllById(productIds);
		if (products.size() != productIds.size()) {
			throw new IllegalArgumentException("One or more product IDs are invalid");
		}

		// Calculate total
		int totalAmount = products.stream()
				.mapToInt(product -> product.getPrice().intValue()) // assuming price is BigDecimal
				.sum();

		// Create Razorpay Order
		JSONObject options = new JSONObject();
		options.put("amount", totalAmount * 100); // in paise
		options.put("currency", "INR");
		options.put("receipt", orderRequest.getEmail());

		Order razorpayOrder = razorpayCLient.orders.create(options);

		// Save order to DB
		Orders newOrder = new Orders();
		newOrder.setProductIds(productIds);
		newOrder.setEmail(orderRequest.getEmail());
		newOrder.setName(orderRequest.getName());
		newOrder.setAmount(totalAmount);
		newOrder.setRazorpayOrderId(razorpayOrder.get("id"));
		newOrder.setOrderStatus(razorpayOrder.get("status"));

		return ordersRepository.save(newOrder);
	}

//actual
//	public Orders updateStatus(Map<String, String> map) {
//    	String razorpayId = map.get("razorpay_order_id");
//    	Orders order = ordersRepository.findByRazorpayOrderId(razorpayId);
//    	order.setOrderStatus("PAYMENT DONE");
//    	Orders orders = ordersRepository.save(order);
//    	return orders;
//    }

//public Orders updateStatus(Map<String, String> map) {
//	String razorpayId = map.get("razorpay_order_id");
//	Orders order = ordersRepository.findByRazorpayOrderId(razorpayId);
//	order.setOrderStatus("PAYMENT DONE");
//	Orders updatedOrder = ordersRepository.save(order);
//
//	if ("PAYMENT DONE".equals(updatedOrder.getOrderStatus())) {
//		// Save purchase directly without API call
//		PlantPurchase purchase = PlantPurchase.builder()
//				.plantId(updatedOrder.getPlantId())
//				.userEmail(updatedOrder.getEmail())
//				.purchasedAt(LocalDateTime.now())
//				.build();
//
//		plantPurchaseRepo.save(purchase);
//		System.out.println("✅ Purchase saved for " + updatedOrder.getEmail());
//	}
//
//	return updatedOrder;
//}
public Orders updateStatus(Map<String, String> map) {
	String razorpayId = map.get("razorpay_order_id");
	Orders order = ordersRepository.findByRazorpayOrderId(razorpayId);
	order.setOrderStatus("PAYMENT DONE");
	Orders updatedOrder = ordersRepository.save(order);

	if ("PAYMENT DONE".equals(updatedOrder.getOrderStatus())) {
		// Loop through plantIds and save each purchase
		for (Long productId : updatedOrder.getProductIds()) {
			ProductPurchase purchase = ProductPurchase.builder()
					.productId(productId)
					.userEmail(updatedOrder.getEmail())
					.purchasedAt(LocalDateTime.now())
					.build();

			plantPurchaseRepo.save(purchase);
			System.out.println("✅ Purchase logged for plant ID: " + productId);
		}
	}

	return updatedOrder;
}

}
