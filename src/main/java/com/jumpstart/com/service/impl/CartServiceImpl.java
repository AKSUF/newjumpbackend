package com.jumpstart.com.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.AddtoCart;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.AddtoCartDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.AddToCartRepository;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.service.CartService;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private AddToCartRepository addCartRepository;
//	@Autowired
//	private CheckoutRepository checkOutRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ModelMapper modelMapper;

	// User can add the product to the cart
	@Override
	public AddtoCartDto addCartbyUserIdAndProductId(Long pid, String token) throws Exception {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

	    Product product = this.productRepository.findById(pid)
	            .orElseThrow(() -> new ResourceNotFoundException("Product", " Id", pid.toString()));

	    // Check if the product has already been added to the user's cart
	    AddtoCart existingCart = this.addCartRepository.findByUserAndProduct(account.getUser(), product);
	    if (existingCart != null) {
	        // Increment the quantity of the existing cart item
	        existingCart.setQty(existingCart.getQty() + 1);
	        existingCart.setPrice((double) (product.getPrice() * existingCart.getQty()));
	        AddtoCart savedToCart = this.addCartRepository.save(existingCart);
	        return this.modelMapper.map(savedToCart, AddtoCartDto.class);
	    } else {
	        // Create a new cart item
	        AddtoCart addToCart = new AddtoCart();
	        addToCart.setProduct(product);
	        addToCart.setAddedDate(new Date());
	        addToCart.setQty(1);
	        addToCart.setPrice((double) (product.getPrice() * addToCart.getQty()));
	        addToCart.setUser(account.getUser());
	        AddtoCart savedToCart = this.addCartRepository.save(addToCart);
	        return this.modelMapper.map(savedToCart, AddtoCartDto.class);
	    }
	}


	// User can increase product quantity
	@Override
	public void updateQtyByCartId(Long cid, int qty) throws Exception {
				AddtoCart updatedQty = this.addCartRepository.findById(cid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", cid.toString()));

		updatedQty.setCart_id(cid);;
		updatedQty.setQty(qty);
		


		this.addCartRepository.save(updatedQty);

	}

	// User will get all his cart together 
	@Override
	public List<AddtoCartDto> getCartByUserId(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<AddtoCart> addCart = this.addCartRepository.findByUser(account.getUser());

		List<AddtoCartDto> addToCart = addCart.stream().map(order -> this.modelMapper.map(order, AddtoCartDto.class))
				.collect(Collectors.toList());

		return addToCart;
	}

	@Override
	public void removeCartByUserId(Long cartId) {
		AddtoCart addtoCart = this.addCartRepository.findById(cartId).orElseThrow(()-> new
				ResourceNotFoundException("Comment", "Id", cartId.toString()));
		addCartRepository.delete(addtoCart);
		
	}

	@Override
	public List<AddtoCartDto> removeAllCartByUserId(String token) {
		
		
		return null;
	}

	@Override
	public Boolean checkTotalAmountAgainstCart(double totalAmount, String token) {
		// TODO Auto-generated method stub
		return null;
	}


	// User can delete/remove the cart 
	@Override
	public void removeCart(Long cartId, String token) {
		
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}
		AddtoCart addtoCart = this.addCartRepository.findById(cartId).orElseThrow(()->
		new ResourceNotFoundException("Product", " Id", cartId.toString()));
		
		addCartRepository.delete(addtoCart); 
	}

	@Override
	public AddtoCartDto updateQtyByCartId(AddtoCartDto addtoCartDto, Long pid, String token) throws Exception {
	
		return null;
	}


	@Override
	public AddtoCartDto addCartbyUserIdAndProductId(AddtoCartDto addtoCartDto, Long pid, String token)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
