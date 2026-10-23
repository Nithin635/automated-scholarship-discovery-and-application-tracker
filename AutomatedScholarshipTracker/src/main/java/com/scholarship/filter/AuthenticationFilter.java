package com.scholarship.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Blocks any request to /student/* or /admin/* (JSPs or servlets) from a
 * client that doesn't have a logged-in session. Public pages (home,
 * login, register, static assets) are untouched.
 */
@WebFilter(urlPatterns = {"/student/*", "/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;

        if (!loggedIn) {
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/login.jsp?error=You+must+log+in+to+access+that+page");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
