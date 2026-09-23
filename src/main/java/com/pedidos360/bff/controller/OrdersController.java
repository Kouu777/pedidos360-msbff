package com.pedidos360.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final RestClient ordersClient;
    private final ObjectMapper objectMapper;

    public OrdersController(RestClient ordersClient, ObjectMapper objectMapper) {
        this.ordersClient = ordersClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public String getOrders(Authentication authentication, @AuthenticationPrincipal Jwt jwt) {
        boolean canViewAll = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin") || a.getAuthority().equals("ROLE_Operador"));

        if (!canViewAll) {
            // Usuario con rol Cliente: filtrar exclusivamente por su correo
            String email = getEmailFromJwt(jwt);
            return ordersClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/orders")
                            .queryParam("customerEmail", email)
                            .build())
                    .header("X-Customer-Email", email)
                    .retrieve()
                    .body(String.class);
        }

        // Admin u Operador: obtienen todos los pedidos del sistema
        return ordersClient.get().uri("/api/orders").retrieve().body(String.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public String createOrder(@RequestBody String orderData, @AuthenticationPrincipal Jwt jwt) {
        String email = getEmailFromJwt(jwt);
        String name = getNameFromJwt(jwt);

        String payloadToSend = orderData;
        try {
            JsonNode node = objectMapper.readTree(orderData);
            if (node.isObject()) {
                ObjectNode obj = (ObjectNode) node;
                obj.put("customerEmail", email);
                obj.put("customerName", name);
                payloadToSend = objectMapper.writeValueAsString(obj);
            }
        } catch (Exception ignored) {
        }

        return ordersClient.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Customer-Email", email)
                .header("X-Customer-Name", name)
                .body(payloadToSend)
                .retrieve()
                .body(String.class);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public String updateOrderStatus(@PathVariable String id, @RequestBody String statusData) {
        return ordersClient.patch().uri("/api/orders/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body(statusData).retrieve().body(String.class);
    }

    private String getEmailFromJwt(Jwt jwt) {
        if (jwt == null) return "cliente@pedidos360.local";
        if (jwt.hasClaim("preferred_username") && jwt.getClaimAsString("preferred_username") != null) {
            return jwt.getClaimAsString("preferred_username");
        }
        if (jwt.hasClaim("email") && jwt.getClaimAsString("email") != null) {
            return jwt.getClaimAsString("email");
        }
        if (jwt.hasClaim("upn") && jwt.getClaimAsString("upn") != null) {
            return jwt.getClaimAsString("upn");
        }
        return jwt.getSubject() != null ? jwt.getSubject() : "cliente@pedidos360.local";
    }

    private String getNameFromJwt(Jwt jwt) {
        if (jwt == null) return "Cliente";
        if (jwt.hasClaim("name") && jwt.getClaimAsString("name") != null) {
            return jwt.getClaimAsString("name");
        }
        return getEmailFromJwt(jwt);
    }
}
