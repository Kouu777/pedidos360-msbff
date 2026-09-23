package com.pedidos360.bff.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/catalog/products")
public class CatalogController {

    private final RestClient catalogClient;

    public CatalogController(RestClient catalogClient) {
        this.catalogClient = catalogClient;
    }

    // GET: Los tres roles pueden consultar productos
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public String getCatalog() {
        // El BFF llama al microservicio real y devuelve su respuesta
        return catalogClient.get().uri("/api/catalog/products").retrieve().body(String.class);
    }

    // POST: Solo Admin
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public String createProduct(@RequestBody String productData) {
        return catalogClient.post().uri("/api/catalog/products")
                .body(productData).retrieve().body(String.class);
    }

    // PUT: Admin y Operador
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public String updateProduct(@PathVariable String id, @RequestBody String productData) {
        return catalogClient.put().uri("/api/catalog/products/" + id)
                .body(productData).retrieve().body(String.class);
    }

    // DELETE: Solo Admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public void deleteProduct(@PathVariable String id) {
        catalogClient.delete().uri("/api/catalog/products/" + id).retrieve().toBodilessEntity();
    }

    // PATCH: Admin y Operador
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public String updateStock(@PathVariable String id, @RequestBody String stockData) {
        return catalogClient.patch().uri("/api/catalog/products/" + id + "/stock")
                .body(stockData).retrieve().body(String.class);
    }
}
