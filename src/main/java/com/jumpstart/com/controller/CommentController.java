package com.jumpstart.com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.CommentDto;
import com.jumpstart.com.service.CommentService;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/user/")
public class CommentController {

	@Autowired
	private CommentService commentService;
	@Autowired
	private JwtUtils jwtUtils;

	// User can comment on product description
	@PostMapping("/product/{pid}/comments")
	public ResponseEntity<CommentDto> addComment(
			@RequestBody CommentDto commentDto,
			@PathVariable Long pid,
			HttpServletRequest request) throws JsonProcessingException{
		CommentDto createComment = this.commentService.addComment(commentDto, pid, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<CommentDto>(createComment, HttpStatus.CREATED);
		
	}
	
	// User can Update his/her comment
	@PutMapping("/product/{pid}/comments")
	public ResponseEntity<ApiResponse> editComment(@Valid @RequestBody CommentDto commentDto,
			@PathVariable Long pid,
			HttpServletRequest request) {
		this.commentService.editComment(commentDto, pid, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}


	// user can delete his comment
	@DeleteMapping("/comments/{cid}")
	public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long cid) {
		this.commentService.deleteComment(cid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Comment deleted successfully !!", true), HttpStatus.OK);

	}
}
