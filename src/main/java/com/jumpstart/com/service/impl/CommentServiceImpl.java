package com.jumpstart.com.service.impl;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Comment;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.CommentDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.CommentRepository;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.service.CommentService;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private JwtUtils jwtUtils;
	
	// User can comment on product description
	@Override
	public CommentDto addComment(CommentDto commentDto, Long pid, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		Product product = this.productRepository.findById(pid).orElseThrow(()->
		new ResourceNotFoundException("Product", " Id", pid.toString()));
		
		Comment comment = this.modelMapper.map(commentDto, Comment.class);
		comment.setProduct(product);
		comment.setUser(account.getUser());
		comment.setCommentDate(new Date());

		Comment savedComment = this.commentRepository.save(comment);
		return this.modelMapper.map(savedComment, CommentDto.class);
	}
	@Override
	public void deleteComment(Long cid) {
		Comment comment = this.commentRepository.findById(cid).orElseThrow(()-> new
				ResourceNotFoundException("Comment", "Id", cid.toString()));
		this.commentRepository.delete(comment);
		
	}
	
	// User can Update his/her comment
	@Override
	public CommentDto editComment(CommentDto commentDto,Long pid, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		Product product = this.productRepository.findById(pid).orElseThrow(()->
		new ResourceNotFoundException("Product", " Id", pid.toString()));

		this.commentRepository.findById(commentDto.getComment_id())
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment id", commentDto.getComment_id().toString()));

		Comment updatedComment = this.modelMapper.map(commentDto, Comment.class);
		updatedComment.setProduct(product);
		updatedComment.setUser(account.getUser());

		this.commentRepository.save(updatedComment);

		return this.modelMapper.map(updatedComment, CommentDto.class);
	}

}
