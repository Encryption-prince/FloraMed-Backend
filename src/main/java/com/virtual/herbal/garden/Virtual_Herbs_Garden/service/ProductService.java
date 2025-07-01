package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Product;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductPurchase;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductReview;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductPurchaseRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductPurchaseRepository productPurchaseRepository;
    private final ProductReviewRepository productReviewRepository;

    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory(category);
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepo.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setImageUrls(updatedProduct.getImageUrls());
            product.setCategory(updatedProduct.getCategory());
            product.setPrice(updatedProduct.getPrice());
            product.setSellerName(updatedProduct.getSellerName());
            return productRepo.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> recommendProductsForUser(String userEmail, int limit) {
        // Get all purchases by user
        List<ProductPurchase> userPurchases = productPurchaseRepository.findByUserEmail(userEmail);
        Set<Long> purchasedProductIds = userPurchases.stream().map(ProductPurchase::getProductId)
                .collect(Collectors.toSet());

        // Count category frequency
        Map<String, Long> categoryFrequency = userPurchases.stream()
                .map(ProductPurchase::getProductId)
                .map(productRepo::findById)
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getCategory())
                .collect(Collectors.groupingBy(cat -> cat, Collectors.counting()));

        // Get all products not purchased by user
        List<Product> allProducts = productRepo.findAll();
        List<Product> notPurchased = allProducts.stream()
                .filter(p -> !purchasedProductIds.contains(p.getProductId()))
                .collect(Collectors.toList());

        // Calculate average rating for each product
        Map<Long, Double> productAvgRating = new HashMap<>();
        for (Product p : notPurchased) {
            List<ProductReview> reviews = productReviewRepository.findByProductId(p.getProductId());
            double avg = reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);
            productAvgRating.put(p.getProductId(), avg);
        }

        // Sort by: (1) category frequency, (2) average rating, (3) price ascending
        notPurchased.sort((a, b) -> {
            long freqA = categoryFrequency.getOrDefault(a.getCategory(), 0L);
            long freqB = categoryFrequency.getOrDefault(b.getCategory(), 0L);
            if (freqA != freqB)
                return Long.compare(freqB, freqA);
            double ratingA = productAvgRating.getOrDefault(a.getProductId(), 0.0);
            double ratingB = productAvgRating.getOrDefault(b.getProductId(), 0.0);
            if (Double.compare(ratingB, ratingA) != 0)
                return Double.compare(ratingB, ratingA);
            return a.getPrice().compareTo(b.getPrice());
        });

        return notPurchased.stream().limit(limit).collect(Collectors.toList());
    }

}
