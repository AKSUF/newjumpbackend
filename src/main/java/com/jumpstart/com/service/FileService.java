package com.jumpstart.com.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	// file uploading method
//	String uploadImage(MultipartFile file) throws IOException;
//
//	// file serving method
//	InputStream getResource(String fileName) throws FileNotFoundException;
	String uploadImage(String path, MultipartFile file) throws IOException;
	InputStream getResource(String path, String fileName) throws FileNotFoundException;

	// file deleting method
	void deleteFile(String filename) throws IOException;
}
