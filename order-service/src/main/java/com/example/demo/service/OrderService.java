package com.example.demo.service;

import com.example.demo.client.ProductClient;
import com.example.demo.exception.InsufficientQuantityException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.ProductServiceException;
import com.example.demo.model.Order;
import com.example.demo.model.Product;  // From common module
import com.example.demo.repository.OrderRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;
    @Autowired
    private ProductClient productClient;

    public List<Order> getAll() {
        return repository.findAll();
    }

    public Order getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    public Order create(Order order) {
        try {
            Product product = productClient.getProductById(order.getProdId());
            if (product.getQuantity() < order.getQuantity()) {
                throw new InsufficientQuantityException("Insufficient product quantity for product id: " + order.getProdId());
            }
            // Decrement quantity
            product.setQuantity(product.getQuantity() - order.getQuantity());
            productClient.update(product.getId(), product);
            order.setTotalPrice(product.getPrice() * order.getQuantity());
            return repository.save(order);
        } catch (FeignException ex) {
            throw new ProductServiceException("Error communicating with Products Service: " + ex.getMessage());
        }
    }

    public Order update(Long id, Order updatedOrder) {
        Order existing = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        try {
            if (existing.getProdId().equals(updatedOrder.getProdId())) {
                // Same product, calculate delta
                int delta = updatedOrder.getQuantity() - existing.getQuantity();
                Product product = productClient.getProductById(existing.getProdId());
                if (delta > 0 && product.getQuantity() < delta) {
                    throw new InsufficientQuantityException("Insufficient product quantity for product id: " + existing.getProdId());
                }
                // Adjust quantity
                product.setQuantity(product.getQuantity() - delta);
                productClient.update(product.getId(), product);
            } else {
                // Different product: add back to old, subtract from new
                Product oldProduct = productClient.getProductById(existing.getProdId());
                oldProduct.setQuantity(oldProduct.getQuantity() + existing.getQuantity());
                productClient.update(oldProduct.getId(), oldProduct);
                Product newProduct = productClient.getProductById(updatedOrder.getProdId());
                if (newProduct.getQuantity() < updatedOrder.getQuantity()) {
                    throw new InsufficientQuantityException("Insufficient product quantity for product id: " + updatedOrder.getProdId());
                }
                newProduct.setQuantity(newProduct.getQuantity() - updatedOrder.getQuantity());
                productClient.update(newProduct.getId(), newProduct);
            }
            existing.setProdId(updatedOrder.getProdId());
            existing.setQuantity(updatedOrder.getQuantity());
            Product currentProduct = productClient.getProductById(existing.getProdId());  // Fetch again if needed for price
            existing.setTotalPrice(currentProduct.getPrice() * existing.getQuantity());
            return repository.save(existing);
        } catch (FeignException ex) {
            throw new ProductServiceException("Error communicating with Products Service: " + ex.getMessage());
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        repository.deleteById(id);
    }
}