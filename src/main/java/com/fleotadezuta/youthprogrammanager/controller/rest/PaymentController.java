package com.fleotadezuta.youthprogrammanager.controller.rest;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> response = new HashMap<>();
        response.put("publishableKey", stripePublishableKey);
        return response;
    }

    @PostMapping("/create-payment-intent")
    public Map<String, String> createPaymentIntent() {
        Map<String, String> response = new HashMap<>();
        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("EUR")
                            .setAmount(1999L)
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            response.put("clientSecret", paymentIntent.getClientSecret());
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }
}
