/* 
	OMG Project
	Copyright (c) 2025 Nadeem Abdul Hamid
	License: MIT
*/
package omg.interfaces;

/**
 * Responds to the "/api/cart-[apply|remove]-coupon?code=..." requests 
 * for the given coupon code query parameter.
 * 
 * Should return "true" or "false" depending on whether action succeeded.
 */
@FunctionalInterface
public interface RequestCouponHandler extends RequestHandler {
	@Override default String getResponse() { throw new RuntimeException("unimplemented"); }

	String getResponse(String code);
}
