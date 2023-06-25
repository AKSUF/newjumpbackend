package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.payloads.AddtoCartDto;

public interface CartService {
	AddtoCartDto addCartbyUserIdAndProductId(AddtoCartDto addtoCartDto, Long pid, String token) throws Exception;
	AddtoCartDto updateQtyByCartId(AddtoCartDto addtoCartDto, Long pid, String token) throws Exception;
	List<AddtoCartDto> getCartByUserId(String token);
	void removeCart(Long cartId,String token);
	List<AddtoCartDto> removeAllCartByUserId(String token);
	Boolean checkTotalAmountAgainstCart(double totalAmount,String token);
//	List<CheckoutCart> getAllCheckoutByUserId(String token);
//	List<CheckoutCart> saveProductsForCheckout(List<CheckoutCart> tmp)  throws Exception;
	void removeCartByUserId(Long cartId);
	AddtoCartDto addCartbyUserIdAndProductId(Long pid, String token) throws Exception;
	void updateQtyByCartId(Long cid, int qty) throws Exception;
}
