package com.itranswarp.crypto.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.user.User;
import com.itranswarp.crypto.user.UserContext;
import com.itranswarp.crypto.user.UserService;

@Component
public class RestFilter implements Filter {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		// parse user from header like:
		// Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
		User user = parseUserFromHeader(req);
		try (UserContext ctx = new UserContext(user)) {
			chain.doFilter(req, resp);
		} catch (ApiException e) {
			logger.warn(e.getMessage(), e);
			sendErrorResponse(resp, e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			sendErrorResponse(resp, new ApiException(ApiError.INTERNAL_SERVER_ERROR, null, e.getMessage()));
		}
	}

	void sendErrorResponse(HttpServletResponse resp, ApiException e) throws IOException {
		if (!resp.isCommitted()) {
			resp.setContentType("application/json");
			resp.sendError(400, "json");
			PrintWriter pw = resp.getWriter();
			pw.print("{\"error\":");
			pw.print(objectMapper.writeValueAsString(e.error));
			pw.print("{\"error\":");
			pw.print(objectMapper.writeValueAsString(e.data));
			pw.print("{\"message\":");
			pw.print(objectMapper.writeValueAsString(e.getMessage()));
			pw.print("}");
			pw.flush();
		}
	}

	User parseUserFromHeader(HttpServletRequest req) {
		String auth = req.getHeader("Authorization");
		if (auth == null) {
			return null;
		}
		if (!auth.startsWith("Basic ")) {
			return null;
		}
		try {
			String up = new String(Base64.getDecoder().decode(auth.substring(6)), StandardCharsets.UTF_8);
			int n = up.indexOf(':');
			if (n <= 0) {
				return null;
			}
			String u = URLDecoder.decode(up.substring(0, n), "UTF-8");
			String p = up.substring(n + 1);
			return userService.signin(u, p);
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("init filter: " + getClass().getName());
	}

	@Override
	public void destroy() {
		logger.info("destroy filter: " + getClass().getName());
	}

}
