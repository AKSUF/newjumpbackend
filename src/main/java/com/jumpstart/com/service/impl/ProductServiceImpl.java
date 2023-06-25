package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Delivery;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.ProductDto;
import com.jumpstart.com.payloads.ProductResponse;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.DeliveryRepository;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.repository.RiderDeliveryRepository;
import com.jumpstart.com.repository.UserStoreRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.ProductService;
import com.jumpstart.com.utils.JwtUtils;

import calculator.ShippingCalculator;


@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DeliveryRepository deliveryRepository;

	
	@Autowired
	private RiderDeliveryRepository riderDeliveryRepository;

	@Autowired
	private FileService fileService;
	@Autowired
	private UserStoreRepository userStoreRepository;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Value("${project.product}")
	private String path;
	
	
	@Override
	public Product addProductDetails(ProductDto productDto, String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    Product product = this.modelMapper.map(productDto, Product.class);
	    double charge = ShippingCalculator.calculateCharge(productDto.getShippingAddress(), productDto.getWeight(), productDto.getDimensions());
	    List<String> roleResponses = new ArrayList<String>();
	    account.getUserStores().stream().forEach((role) -> {
	        roleResponses.add(String.valueOf(role.getStore().getStore_id()));
	    });

	    product.setUser(account.getUser());
	    product.setAddedDate(new Date());
	    product.setStatus("Available");
	    product.setShippingAddress(productDto.getShippingAddress());
	    product.setWeight(productDto.getWeight());
	    product.setDimensions(productDto.getDimensions());
	    product.setCharge(charge);
	    product.setApproved(false);
	    product.setAdminMessage("Haven't given a review yet");
	    
	    Store store = account.getUserStores().get(0).getStore(); // Get the first store from the user stores
	    product.setStore(store); // Set the store of the product

	    return this.productRepository.save(product);
	}



	
	@Override
	public void uploadImage(MultipartFile multipartFile, Long mid) throws IOException {
		Product product = this.productRepository.findById(mid)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "Product id", mid.toString()));

		// deleting old image
		if (product.getImage() != null) {
			this.fileService.deleteFile(product.getImage());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(path,multipartFile);

		// setting image name
		product.setImage(uploadedImage);

		// updating user
		this.productRepository.save(product);

	}
	
	@Override
	public List<ProductDto> getAllStoreDetails(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    User user = account.getUser();
	    List<UserStore> userStores = userStoreRepository.findByAccount(account);
	    Set<Store> stores = userStores.stream().map(UserStore::getStore).collect(Collectors.toSet());

	    List<ProductDto> productDtos = new ArrayList<>();
	    for (Store store : stores) {
	        List<Product> products = productRepository.findByStore(store);
	        List<ProductDto> storeProductDtos = products.stream()
	                .map(product -> modelMapper.map(product, ProductDto.class))
	                .collect(Collectors.toList());
	        productDtos.addAll(storeProductDtos);
	    }

	    return productDtos;
	}

	
	
		
	@Override
	public void deleteSingleProduct(Long pid) throws IOException {
		Product product = this.productRepository.findById(pid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", pid.toString()));

		String image = product.getImage();
		List<Delivery> delivery = product.getDelivery();
		if (image != null) {
			this.fileService.deleteFile(product.getImage());
		}
		if(delivery != null) {
			delivery.stream().forEach((deli) -> {
				if(deli.getRiderDelivery() != null) {
					this.riderDeliveryRepository.delete(deli.getRiderDelivery());
				}
				
				this.deliveryRepository.delete(deli);
			});
		}
		this.productRepository.delete(product);
		
	}
	
	 

	@Override
	public ProductDto updateProductDetails(ProductDto productDto, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		this.productRepository.findById(productDto.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", productDto.getProductId().toString()));

		Product updatedProduct = this.modelMapper.map(productDto, Product.class);
		updatedProduct.setUser(account.getUser());
		updatedProduct.setApproved(productDto.isApproved());

		this.productRepository.save(updatedProduct);

		return this.modelMapper.map(updatedProduct, ProductDto.class);
	}


	@Override
	public ProductResponse getAllProducts(Long pageNumber, Long pageSize, String sortBy, String sortDir) {
		Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		Pageable p = PageRequest.of(pageNumber.intValue(), pageSize.intValue(), sort);

		Page<Product> pageProduct = this.productRepository.findAll(p);

		List<Product> products = pageProduct.getContent();

		List<ProductDto> allProductsDto = products.stream().map(meal -> this.modelMapper.map(meal, ProductDto.class))
				.collect(Collectors.toList());

		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(allProductsDto);
		productResponse.setPageNumber(pageProduct.getNumber());
		productResponse.setPageSize(pageProduct.getSize());
		productResponse.setTotalElements(pageProduct.getTotalElements());
		productResponse.setTotalPages(pageProduct.getTotalPages());
		productResponse.setLastPage(pageProduct.isLast());

		return productResponse;
	}

//	@Override
//	public List<ProductDto> getAvailableProducts() {
//		List<Product> allProduct = this.productRepository.findAll();
//
//		List<ProductDto> allProductDtos = allProduct.stream().map(product -> this.modelMapper.map(product, ProductDto.class))
//				.collect(Collectors.toList());
//
//		return allProductDtos;
//	}
	
	@Override
	public List<ProductDto> getApproveProducts() {
	    List<Product> approvedProducts = this.productRepository.findByApprovedTrue();
	    List<ProductDto> approvedProductDtos = approvedProducts.stream()
	            .map(product -> this.modelMapper.map(product, ProductDto.class))
	            .collect(Collectors.toList());
	    return approvedProductDtos;
	}
	
//	@Override
//	 public List<Product> getPendingProducts() {
//	        return productRepository.findByApprovedFalse();
//	    }
	
	@Override
	public List<ProductDto> getPendingProducts() {
	    List<Product> approvedProducts = this.productRepository.findByApprovedFalse();
	    List<ProductDto> approvedProductDtos = approvedProducts.stream()
	            .map(product -> this.modelMapper.map(product, ProductDto.class))
	            .collect(Collectors.toList());
	    return approvedProductDtos;
	}
	
	public List<ProductDto> getAllProducts(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	        .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    
	    User user = account.getUser();
	    List<Product> products = productRepository.findAllByUser(user);
	    
	    List<ProductDto> productDtos = products.stream()
	        .map(product -> {
	            ProductDto productDto = modelMapper.map(product, ProductDto.class);
	            productDto.setAdminMessage(product.getAdminMessage());
	            return productDto;
	        })
	        .collect(Collectors.toList());
	        
	    return productDtos;
	}


	
	
	// shopkeeper and admin can view all pending projects
	@Override
	public ProductDto getProduct(Long pid) {
		Product product = this.productRepository.findById(pid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", pid.toString()));

		return this.modelMapper.map(product, ProductDto.class);
	}

	
	// Search Product
	@Override
	public List<ProductDto> searchProducts(String keyword) {
		List<Product> products =this.productRepository.search(keyword);
		List<ProductDto> productDto = products.stream().map((product)-> this.modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());
		return productDto;
	}
	
	// to get the message which is given by admin
	@Override
	public String getAdminMessageForProduct(Long productId) {
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id ","" , productId.toString()));

	    return product.getAdminMessage();
	}


	
	@Override
	public Product getProductByIdWithoutGet(Long id) {
		Optional<Product> product = productRepository.findById(id);
	        if (product.isPresent()) {
	            return product.get();
	        } else {
	            throw new ResourceNotFoundException("Product with id "  , " not found", id.toString());
	        }
	    }

	
	
}
