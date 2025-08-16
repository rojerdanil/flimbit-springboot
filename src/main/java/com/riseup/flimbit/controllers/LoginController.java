package com.riseup.flimbit.controllers;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.request.EmailRequest;
import com.riseup.flimbit.request.LoginRequest;
import com.riseup.flimbit.request.PaymentMethodFeeDTO;
import com.riseup.flimbit.request.PaymentUpdateRequest;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PaymentFeeResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.service.AdminUserService;
import com.riseup.flimbit.service.MovieInvestService;
import com.riseup.flimbit.service.SystemSettingsService;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;
import com.riseup.flimbit.utility.TaxCalculator;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
	Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	UserRegisterService userRegisterService;
	@Autowired
	JwtService jwtService;

	@Autowired
	AdminUserService adminUserService;

	@Autowired
	private SystemSettingsService systemSettingsService;

	@Autowired
	TaxCalculator taxCalculator;

	@Autowired
	MovieInvestService movieInvestService;

	@PostMapping(path = "/web/login", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		TokenResponse tokenResponse = adminUserService.loginWithIdentifier(request);

		if (tokenResponse != null) {
			return HttpResponseUtility.getHttpSuccess(tokenResponse); // or generate token, etc.
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
		}
	}

	@PostMapping(path = "/sendregotp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> generatePhoneOtpForRegister(@RequestBody PhoneRegisterRequest phoneValidateRequest) {

		return new ResponseEntity<>(userRegisterService.generateRegPhoneOtp(phoneValidateRequest), HttpStatus.OK);

	}

	@PostMapping(path = "/validateRegOtp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> validateRegOtpPhone(@RequestHeader(value = "X-Device-ID") String deviceId,
			@RequestBody PhoneRegValidateRequest phoneValidateRequest) {

		return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest, deviceId),
				HttpStatus.OK);

	}

	@PostMapping(path = "/admin/sendVerificationEmail", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> sendVerfificationMail(@RequestHeader(value = "X-Device-ID") String deviceId,
			@RequestBody EmailRequest emailRequest) {

		// return new
		// ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);
		return null;

	}

	@PostMapping("/web/verify-token")
	public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader,
			@RequestHeader("X-Device-ID") String deviceId) {
		CommonResponse response = null;

		try {
			// Extract token from "Bearer <token>"

			// Extract username/email/device from token (depending on how you built it)

			// Call your custom validation method
			response = adminUserService.validateWebToken(authHeader, deviceId);

			if (response == null)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
			else {

				if (response.getStatus() == Messages.STATUS_SUCCESS)
					return HttpResponseUtility.getHttpSuccess(response);
				else
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
			}

		} catch (Exception ex) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
		}

	}

	@GetMapping("/web/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader,
			@RequestHeader("X-Device-ID") String deviceId) {
		CommonResponse response = null;

		try {
			// Extract token from "Bearer <token>"

			// Extract username/email/device from token (depending on how you built it)

			// Call your custom validation method
			response = adminUserService.validateRefreshWebToken(authHeader, deviceId);

			if (response == null)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
			else {

				if (response.getStatus() == Messages.STATUS_SUCCESS)
					return HttpResponseUtility.getHttpSuccess(response);
				else
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
			}

		} catch (Exception ex) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
		}

	}

	

	@PostMapping(path = "/mobile/createToken", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getMobileLogin(@RequestHeader("X-Device-ID") String deviceId,
			@RequestHeader("phoneNumber") String phoneNumber, @RequestBody LoginRequest request) {
		TokenResponse tokenResponse = userRegisterService.createMobileToken(deviceId, phoneNumber, request);

		if (tokenResponse != null) {
			return HttpResponseUtility.getHttpSuccess(tokenResponse); // or generate token, etc.
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
		}
	}

	@GetMapping("/calculate-fee")
	public PaymentFeeResponse calculateFee(@RequestParam BigDecimal amount, @RequestParam String method) {

		// Build and return response
		return taxCalculator.getPaymentTax(amount, method);

	}

	@GetMapping("/payment-methods")
	public ResponseEntity<?> getPaymentMethods() {

		Map<String, BigDecimal> feeRates = new LinkedHashMap<>();
		feeRates.put("UPI", taxCalculator.getBigDecimalSetting("gateway_fee_upi", BigDecimal.ZERO));
		feeRates.put("CARD", taxCalculator.getBigDecimalSetting("gateway_fee_card", BigDecimal.valueOf(0.02)));
		feeRates.put("NETBANKING",
				taxCalculator.getBigDecimalSetting("gateway_fee_netbanking", BigDecimal.valueOf(0.02)));
		feeRates.put("GST", taxCalculator.getBigDecimalSetting("gateway_gst_rate", BigDecimal.valueOf(0.18)));

		// Convert fee to percentage for display
		List<PaymentMethodFeeDTO> listEntity = feeRates.entrySet().stream().map(e -> new PaymentMethodFeeDTO(e.getKey(),
				e.getValue().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) // 2 decimal places
		)).collect(Collectors.toList());

		return HttpResponseUtility.getHttpSuccess(listEntity);
	}

	private boolean verifySignature(String payload, String razorpaySignature, String secret) throws Exception {
		Mac sha256Hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		sha256Hmac.init(secretKey);
		String generated = new String(Hex.encodeHex(sha256Hmac.doFinal(payload.getBytes())));
		return generated.equals(razorpaySignature);
	}

	@PostMapping(value = "/webhook/razorpay")
	public ResponseEntity<String> handleWebhook(HttpServletRequest request,
			@RequestHeader(value = "X-Razorpay-Signature", required = false) String razorpaySignature,
			@RequestHeader(value = "x-razorpay-signature", required = false) String razorpaySignatureLower) {
		try {
			// Read raw payload
			StringBuilder payload = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				payload.append(line);
			}
			logger.info("🔔 Razorpay Webhook Payload: {}", payload);

			String signature = (razorpaySignature != null) ? razorpaySignature : razorpaySignatureLower;
			if (signature == null || !verifySignature(payload.toString(), signature, "filmBit_webhook_secret")) {
				logger.info("invalid signature ");
				return ResponseEntity.badRequest().body("Invalid signature");
			}

			logger.info("Signature :: " + signature);
			JSONObject json = new JSONObject(payload.toString());
			String event = json.optString("event");

			if ("payment.captured".equals(event) || "order.paid".equals(event)) {

				String orderId = "";
				String paymentId = "";
				String status = "";

				if ("payment.captured".equals(event)) {
					JSONObject paymentData = json.getJSONObject("payload").getJSONObject("payment")
							.getJSONObject("entity");
					orderId = paymentData.optString("order_id");
					paymentId = paymentData.optString("id");
					status = paymentData.optString("status");
				} else if ("order.paid".equals(event)) {
					JSONObject orderData = json.getJSONObject("payload").getJSONObject("order").getJSONObject("entity");
					JSONObject paymentData = json.getJSONObject("payload").getJSONObject("payment")
							.getJSONObject("entity");

					orderId = orderData.optString("id"); // ✅ order id
					paymentId = paymentData.optString("id"); // ✅ payment id
					status = orderData.optString("status"); // "paid"
				}

				logger.info("orderId :: {} : paymentId: {} : status: {}", orderId, paymentId, status);

				PaymentUpdateRequest paymentRequest = new PaymentUpdateRequest();
				paymentRequest.setOrderId(orderId);
				paymentRequest.setPaymentId(paymentId);
				paymentRequest.setPaymentMethod("Webhook");
				paymentRequest.setSignature(signature);

				movieInvestService.updatePaymentStatus(paymentRequest);
				logger.info("✅ Payment Webhook Processed: OrderId={}, PaymentId={}, Status={}", orderId, paymentId,
						status);
			}

			if ("payment.failed".equals(event)) {
				JSONObject payloadData = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
				String orderId = payloadData.optString("order_id");
				String paymentId = payloadData.optString("id");
				String status = payloadData.optString("status"); // "failed"

				String reason = payloadData.optString("error_description",
						payloadData.optString("error_reason", "No reason provided"));

				logger.warn("❌ Payment Failed: OrderId={}, PaymentId={}, Reason={}", orderId, paymentId, reason);

				PaymentUpdateRequest paymentRequest = new PaymentUpdateRequest();
				paymentRequest.setOrderId(orderId);
				paymentRequest.setPaymentId(paymentId);
				paymentRequest.setPaymentMethod("Webhook");
				paymentRequest.setSignature(razorpaySignature);
                paymentRequest.setErrorMsg(reason);
				movieInvestService.updatePaymentStatus(paymentRequest);
			}
			return ResponseEntity.ok("Webhook processed successfully");

		} catch (Exception ex) {
			logger.error("Unexpected error in webhook processing", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
		}
	}

}
