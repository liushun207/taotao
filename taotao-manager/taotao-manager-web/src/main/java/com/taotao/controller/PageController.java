package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转controller
 * <p>Title: PageController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.com</p> 
 * @author
 * @date
 * @version 1.0
 */
@Controller
@RequestMapping("/page")
public class PageController
{
	/**
	 * 打开首页
	 */
	@RequestMapping("/")
	public String showIndex()
	{
		return "index";
	}

	/**
	 * 展示其他页面
	 * <p>Title: showpage</p>
	 * <p>Description: </p>
	 *
	 * @param page
	 * @return
	 */
	@RequestMapping("/{page}")
	public String showpage(@PathVariable String page)
	{
		return page;
	}

	@RequestMapping("/register")
	public String showRegister()
	{
		return "register";
	}

	@RequestMapping("/login")
	public String showLogin(String redirect, Model model)
	{
		model.addAttribute("redirect", redirect);
		return "login";
	}
}
