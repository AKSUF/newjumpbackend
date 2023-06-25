package com.jumpstart.com.payloads;

import java.util.Date;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CommentDto {
	private Long comment_id;
	@Size(min = 5, message = "min 11 and max 15  characters are allowed")
	private String text;
	private Date commentDate;

	private UserDto user;
}
