package in.cdac.aadhaarservices.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author root
 *
 */
final public class ClickjackFilter implements Filter 
{

	private String mode = "DENY";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse)response;
		res.addHeader("X-FRAME-OPTIONS", mode );	
		if (request.isSecure())
		{
			res.setHeader("Strict-Transport-Security", "max-age=31622400; includeSubDomains");
		}
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		res.setDateHeader("Expires", 0);
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
		res.setHeader( "X-XSS-Protection", "1; mode=block");
		res.setHeader( "X-Content-Type-Options", "nosniff");
		res.setHeader( "X-Content-Security-Policy", "default-src 'self'");
		res.setHeader( "WebKit-X-CSP", "None");
		chain.doFilter(request, response);
	}
	/**
	 * 
	 */
	public void destroy() 
	{
	
	}
	/**
	 * 
	 */
	public void init(FilterConfig filterConfig) 
	{
		String configMode = filterConfig.getInitParameter("mode");
		if ( configMode != null ) 
		{
			mode = configMode;
		}
	}
}
