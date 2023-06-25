package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.BuyProduct;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.exception.UnauthorizedException;
import com.jumpstart.com.payloads.BuyProductDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.BuyProductRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.service.BuyProductService;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.status.DeliveryStatus;
import com.jumpstart.com.status.RequestStatus;
import com.jumpstart.com.utils.JwtUtils;



@Service
public class BuyProductServiceImpl implements BuyProductService {
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	BuyProductRepository buyProductRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FileService fileService;

	@Value("${project.buyProduct}")
	private String path;

	// shopkeeper can request the product from the manufacture/producer
	@Override
	public BuyProduct buyProduct(BuyProductDto buyProductDto, Long uid, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		System.out.println("Buy Product for store 1");
		Account account = accountRepository.findByEmail(email).get();

		System.out.println(account);
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}
		List<String> roleResponses = new ArrayList<String>();
		account.getUserStores().stream().forEach((role) -> {
			roleResponses.add(String.valueOf(role.getStore().getStore_id()));
		});

		User producerUser = this.userRepository.findById(uid)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", uid.toString()));
		boolean isProducer = producerUser.getAccount().getUserRoles().stream()
				.anyMatch(userRole -> userRole.getRole().getRole_name().equals("ROLE_PRODUCER"));
		if (!isProducer) {
			throw new InvalidParameterException(
					"The provided uid does not belong to a user with the ROLE_PRODUCER role.");
		}
		BuyProduct buyProduct = this.modelMapper.map(buyProductDto, BuyProduct.class);
		buyProduct.setStatus(RequestStatus.PENDING.name());
		buyProduct.setUser(account.getUser());
		buyProduct.setProducerId(uid);
		buyProduct.setMessage("We will Contact you Soon");
		//buyProduct.setAccepted(false);
		List<UserStore> userStores = account.getUserStores();
		if (userStores.isEmpty()) {
		    throw new IllegalStateException("User has no stores");
		}
		Store store = userStores.get(0).getStore();

		buyProduct.setStore(store);
		return this.buyProductRepository.save(buyProduct);

	}

	// Producer can see all his product requests
	@Override
	public List<BuyProductDto> getProducerProducts(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Long producerId = account.getUser().getUser_id();
		List<BuyProduct> buyProducts = this.buyProductRepository.findByProducerIdAndAcceptedFalse(producerId);

		List<BuyProductDto> acceptedProductDtos = buyProducts.stream()
				.map(product -> this.modelMapper.map(product, BuyProductDto.class)).collect(Collectors.toList());
		return acceptedProductDtos;
	}

	// Shopkeeper can update his/her buy product details
	@Override
	public BuyProductDto updateBuyProducttDetails(BuyProductDto buyProductDto, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		this.buyProductRepository.findById(buyProductDto.getBuyProductId()).orElseThrow(
				() -> new ResourceNotFoundException("meal", "meal id", buyProductDto.getBuyProductId().toString()));

		BuyProduct updateBuyProduct = this.modelMapper.map(buyProductDto, BuyProduct.class);
		updateBuyProduct.setUser(account.getUser());
		updateBuyProduct.setStatus(RequestStatus.PENDING.name());

		this.buyProductRepository.save(updateBuyProduct);

		return this.modelMapper.map(updateBuyProduct, BuyProductDto.class);
	}

	// to get all by product
	@Override
	public BuyProductDto getProduct(Long bpid) {
		BuyProduct buyProduct = this.buyProductRepository.findById(bpid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", bpid.toString()));

		return this.modelMapper.map(buyProduct, BuyProductDto.class);
	}

	// Producer can see all his product accepted Product requests
	@Override
	public List<BuyProductDto> getProducerRequestAcceptedProducts(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Long producerId = account.getUser().getUser_id();
		List<BuyProduct> buyProducts = this.buyProductRepository.findByProducerIdAndAcceptedTrue(producerId);

		List<BuyProductDto> acceptedProductDtos = buyProducts.stream()
				.map(product -> this.modelMapper.map(product, BuyProductDto.class)).collect(Collectors.toList());
		return acceptedProductDtos;
	}

	// Shopkeeper can see all his product requests
	@Override
	public List<BuyProductDto> getShopkeeperProductBuy(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<BuyProduct> buyProducts = this.buyProductRepository.findByUser(account.getUser());
		List<BuyProductDto> acceptedProductDtos = buyProducts.stream()
				.map(product -> modelMapper.map(product, BuyProductDto.class)).collect(Collectors.toList());
		return acceptedProductDtos;
	}

//	@Override
//	public List<BuyProductDto> getAcceptProducts() {
//		
//	    List<BuyProduct> acceptedProductReq = this.buyProductRepository.findByAcceptedTrue();
//	    List<BuyProductDto> approvedProductDtos = acceptedProductReq.stream()
//	            .map(product -> this.modelMapper.map(product, BuyProductDto.class))
//	            .collect(Collectors.toList());
//	    return approvedProductDtos;
//	}
	@Override
	public List<BuyProductDto> getAcceptProducts(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Long producerId = account.getUser().getUser_id();
		List<BuyProduct> acceptedProductReq = this.buyProductRepository.findByProducerIdAndAcceptedTrue(producerId);
//	    List<BuyProduct> acceptedProductReq = this.buyProductRepository.findByAcceptedTrue();
		List<BuyProductDto> approvedProductDtos = acceptedProductReq.stream()
				.map(product -> this.modelMapper.map(product, BuyProductDto.class)).collect(Collectors.toList());
		return approvedProductDtos;
	}

	@Override
	public BuyProductDto sendDetailsToShopKeeper(BuyProductDto buyProductDto, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		this.buyProductRepository.findById(buyProductDto.getBuyProductId()).orElseThrow(
				() -> new ResourceNotFoundException("meal", "meal id", buyProductDto.getBuyProductId().toString()));

		BuyProduct sendDetailsToShopKeeper = this.modelMapper.map(buyProductDto, BuyProduct.class);
		sendDetailsToShopKeeper.setUser(account.getUser());
		sendDetailsToShopKeeper.setAccepted(buyProductDto.isAccepted());

		this.buyProductRepository.save(sendDetailsToShopKeeper);

		return this.modelMapper.map(sendDetailsToShopKeeper, BuyProductDto.class);
	}

	// Shopkeeper can remove/delete his/her buy product 
	@Override
	public void deleteBuyProductRequest(Long bpid, String token) throws IOException {
		try {
			String email = jwtUtils.getUserNameFromToken(token);
			Account account = accountRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException("Account", "Email", email));
			BuyProduct buyProduct = buyProductRepository.findById(bpid)
					.orElseThrow(() -> new ResourceNotFoundException("BuyProduct", "Id", bpid.toString()));

			if (!buyProduct.getUser().getUser_id().equals(account.getUser().getUser_id())) {
				throw new UnauthorizedException("You are not authorized to delete this BuyProduct request.");
			}

			buyProductRepository.delete(buyProduct);
		} catch (Exception e) {
			throw new IOException("An error occurred while processing the request.", e);
		}
	}

	@Override
	public void uploadImage(MultipartFile multipartFile, Long bpid) throws IOException {
		BuyProduct buyProduct = this.buyProductRepository.findById(bpid)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product id", bpid.toString()));

		// deleting old image
		if (buyProduct.getImage() != null) {
			this.fileService.deleteFile(buyProduct.getImage());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(path, multipartFile);

		// setting image name
		buyProduct.setImage(uploadedImage);

		// updating user
		this.buyProductRepository.save(buyProduct);

	}


	@Override
	public BuyProductDto shopkeeperProductDeliveryStatus(Long bpid, String token) {
		BuyProduct buyProduct = buyProductRepository.findById(bpid)
				.orElseThrow(() -> new ResourceNotFoundException("BuyProduct", "Id", bpid.toString()));
		buyProduct.setDeliveryStatus(DeliveryStatus.DELIVERED.name());
		BuyProduct changedStatus = this.buyProductRepository.save(buyProduct);
		return this.modelMapper.map(changedStatus, BuyProductDto.class);

	}
}
