package com.pedidos360.bff.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final RestClient ordersClient;

    public OrdersController(RestClient ordersClient) {
        this.ordersClient = ordersClient;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public String getOrders() {
        return ordersClient.get().uri("/api/orders").retrieve().body(String.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public String createOrder(@RequestBody String orderData) {
        return ordersClient.post().uri("/api/orders")
                .body(orderData).retrieve().body(String.class);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public String updateOrderStatus(@PathVariable String id, @RequestBody String statusData) {
        return ordersClient.patch().uri("/api/orders/" + id + "/status")
                .body(statusData).retrieve().body(String.class);
    }
}
