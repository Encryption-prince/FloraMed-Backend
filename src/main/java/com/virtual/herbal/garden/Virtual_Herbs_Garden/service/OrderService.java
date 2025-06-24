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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public Orders createOrder(Orders orderRequest) throws RazorpayException {
		List<Long> productIds = orderRequest.getProductIds();
		if (productIds == null || productIds.isEmpty()) {
			throw new IllegalArgumentException("Product IDs cannot be empty");
		}

// Fetch unique product IDs
		Set<Long> uniqueProductIds = new HashSet<>(productIds);

// Fetch products from DB
		List<Product> products = productRepo.findAllById(uniqueProductIds);

// Validate existence
		if (products.size() != uniqueProductIds.size()) {
			throw new IllegalArgumentException("One or more product IDs are invalid");
		}

// Calculate total amount based on requested productIds (including duplicates)
		int totalAmount = productIds.stream()
				.mapToInt(id -> {
					Product product = products.stream()
							.filter(p -> p.getProductId().equals(id))
							.findFirst()
							.orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
					return product.getPrice().intValue();
				})
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
