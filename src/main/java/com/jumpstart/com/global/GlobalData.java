package com.jumpstart.com.global;

import java.util.ArrayList;
import java.util.List;

import com.jumpstart.com.entities.Product;

public class GlobalData {
	public static List<Product> cart;
	static {
		cart = new ArrayList<Product>();
	}
}
