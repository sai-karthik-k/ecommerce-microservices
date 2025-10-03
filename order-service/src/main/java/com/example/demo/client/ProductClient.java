package com.example.demo.client;

import com.example.demo.model.Product;  // From common module
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "products-service")
public interface ProductClient {
	@GetMapping("/products/{id}")
	Product getProductById(@PathVariable("id") Long id);
	@PutMapping("/products/{id}")
	Product update(@PathVariable("id") Long id, @RequestBody Product product);
}