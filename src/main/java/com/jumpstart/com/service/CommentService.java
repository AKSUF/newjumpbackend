package com.jumpstart.com.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jumpstart.com.payloads.CommentDto;

public interface CommentService {

	CommentDto addComment (CommentDto commentDto, Long pid, String token)throws JsonProcessingException ;
	CommentDto editComment (CommentDto commentDto, Long pid, String token);
	void deleteComment(Long cid);
}
