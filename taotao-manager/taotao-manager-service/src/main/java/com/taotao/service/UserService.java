package com.taotao.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import org.springframework.beans.factory.annotation.Value;

public interface UserService
{
	TaotaoResult checkData(String content, Integer type);

	TaotaoResult createUser(TbUser user);

	TaotaoResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response);

	TaotaoResult getUserByToken(String token);

	TbUser getTbUserByToken(String token);

	String getTokenName();

	String getSSOBaseUrl();

	String getSSOPageLogin();
}