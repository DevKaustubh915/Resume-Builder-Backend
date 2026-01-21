package com.example.resumebuilderapi.controller;


import com.example.resumebuilderapi.document.Payment;
import com.example.resumebuilderapi.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.resumebuilderapi.util.AppConstants.PREMIUM;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String , String> request,
                                         Authentication authentication) throws RazorpayException {

        //step 1: validate the request
        String planType = request.get("planType");
        if (!PREMIUM.equalsIgnoreCase(planType)){
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid plan type"));
        }


        //step 2: call the service method
        Payment payment = paymentService.createOrder(authentication.getPrincipal() , planType);


        // step 3: prepare the response object

        Map<String , Object> response = Map.of(
                "orderId", payment.getRazorpayOrderId(),
                "amount", payment.getAmount(),
                "currency", payment.getCurrency(),
                "receipt", payment.getReceipt()
        );


        //step 4:  return the response
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String , String> request) throws RazorpayException {

        //step 1: verify the request

        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId = request.get("razorpay_payment_id");
        String razorpaySignature = request.get("razorpay_signature");

        if (Objects.isNull(razorpayOrderId) || Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpaySignature)){
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required payment parameters"));
        }


        //step2: call the service method
        boolean isValid = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId,razorpaySignature);

        //step 3: return the response
        if (isValid){
            return ResponseEntity.ok(Map.of(
                    "message","Payment verified sucessfully",
                    "status","success"
            ));
        }else{
            return ResponseEntity.badRequest().body(Map.of(
                    "message","Payment verification failed..."));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){

        //step 1: call the service method
        List<Payment> payments =paymentService.getUserPayments(authentication.getPrincipal());


        //step 2: return the response
        return ResponseEntity.ok(payments);
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){

        //step 1: call the service method
        Payment paymentDetails = paymentService.getPaymentDeatils(orderId);


        //step 2: return response
        return ResponseEntity.ok(paymentDetails);
    }
}

