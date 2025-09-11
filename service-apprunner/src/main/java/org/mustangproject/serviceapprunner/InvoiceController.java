package org.mustangproject.serviceapprunner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    @GetMapping("/health")
    public String health() {
        return "Mustang Invoice Service is running";
    }

    @GetMapping("/info")
    public String info() {
        return "Mustang Invoice Service - E-invoice processing and validation";
    }
}
