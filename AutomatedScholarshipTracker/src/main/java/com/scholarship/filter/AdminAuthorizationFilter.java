package com.scholarship.filter;

import com.scholarship.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Runs after AuthenticationFilter for every /admin/* request and makes
 * sure the logged-in user actually has the ADMIN role. A logged-in
 * student who tries to browse to an admin URL is redirected away with
 * an error rather than being shown any admin content.
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            // AuthenticationFilter should already have caught this, but guard anyway.
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!user.isAdmin()) {
            response.sendRedirect(request.getContextPath()
                    + "/student/dashboard.jsp?error=You+are+not+authorized+to+access+the+admin+area");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
