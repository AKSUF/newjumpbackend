package com.jumpstart.com.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.BuyProduct;
import com.jumpstart.com.payloads.BuyProductDto;

public interface BuyProductService {
	BuyProduct buyProduct(BuyProductDto buyProductDto, Long uid, String token);
	
	List<BuyProductDto> getProducerProducts(String token);
	
	void uploadImage(MultipartFile multipartFile, Long bpid) throws IOException;

	List<BuyProductDto> getAcceptProducts(String token);

	BuyProductDto sendDetailsToShopKeeper(BuyProductDto buyProductDto, String token);



	void deleteBuyProductRequest(Long bpid, String token) throws IOException;

	List<BuyProductDto> getShopkeeperProductBuy(String token);

	List<BuyProductDto> getProducerRequestAcceptedProducts(String token);

//	BuyProductDto shopkeeperProductDeliveryStatus(Long bpid, String status, String token);

	BuyProductDto shopkeeperProductDeliveryStatus(Long bpid, String token);

	BuyProductDto getProduct(Long bpid);

	BuyProductDto updateBuyProducttDetails(BuyProductDto buyProductDto, String token);


}
