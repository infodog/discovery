package net.xinshi.discovery.search.mgt.filter;

import net.xinshi.discovery.search.mgt.auth.SessionMgt;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 4/7/13
 * Time: 2:55 PM
 */
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getPathInfo();

        if ("/servicesNS/search/syncJob".equals(path) || "/services/auth/login".equals(path) || "/services/auth/saasLogin".equals(path) || "/services/server/info".equals(path) || "/domains".equals(path)) {
            filterChain.doFilter(servletRequest,servletResponse);
        } else {
            String sessionKey = request.getHeader("Authorization");

            if (sessionKey == null || "".equals(sessionKey.trim())) {
                response.sendError(401, "unauthorized");
                return;
            }

            Map<String, String> session = SessionMgt.sessions.getIfPresent(sessionKey);

            if (session == null) {
                response.sendError(401, "unauthorized");
            }

            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
