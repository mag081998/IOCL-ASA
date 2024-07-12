package in.cdac.aadhaarservices.maincontroller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import in.cdac.aadhaarservices.common.MasterConfig;
import in.cdac.aadhaarservices.services.RequestProcessor;
import in.cdac.aadhaarservices.services.RequestProcessorFactory;

@EnableAsync
@RestController
@Configuration
@PropertySources(value = {@PropertySource(value = "classpath:system-error-messages.properties"),
@PropertySource(value = "classpath:config.properties")})
public  class FrontController 
{
	private static final Logger logger = LogManager.getLogger(FrontController.class);
	@Autowired
	MasterConfig masterConfigBD;
	@Autowired
	RequestProcessorFactory requestProcessorFactory;
	@Autowired 
	Environment environment;
	/**
	 * @param String This is otpMessage received from the client
	 * @return String This is response returned to client after processing
	 * @description handles post request of given url for otp and sends response back in xml format.
	 */
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@RequestMapping(value = "/otprequest/{lk}", method = RequestMethod.POST,headers = "Accept=application/xml")
	public @ResponseBody String otpRequestSync(@RequestBody String otpXml,@PathVariable(value="lk") final String lk,HttpServletRequest request) 
	{
		String otpRes=null;
		String clientIP = getClientIp(request);
		try
		{
			if (!request.getContentType().equalsIgnoreCase("application/xml"))
			{
				return "Invalid header content media type";
			}
			if (isAllowedSize(otpXml.getBytes().length))
			{
				return "Invalid Request Size";
			}
			RequestProcessor reqType = requestProcessorFactory.getReqType("OTP");
			otpRes=reqType.processReq(otpXml, clientIP,lk);
		}
		catch (Exception excp) 
		{
			logger.info("Error in Controller",excp);
			otpRes="Service Error";
		}
		return otpRes;
	}
	/**
	 * @param String This is authMessage received from the client
	 * @return String This is response returned to client after processing
	 * @description handles post request of given url for auth and sends response back in xml format.
	 */
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@RequestMapping(value = "/authrequest/{lk}", method = RequestMethod.POST, headers = "Accept=application/xml")
	public @ResponseBody String authRequestSync(@RequestBody String authXml,@PathVariable(value="lk") final String lk, HttpServletRequest request) 
	{	
		String clientIP = getClientIp(request);
		String authRes=null;
		try
		{
			if (!request.getContentType().equalsIgnoreCase("application/xml"))
			{
				return "Invalid header content media type";
			}
			if (isAllowedSize(authXml.getBytes().length))
			{
				return "Invalid Request Size";
			}
			RequestProcessor reqType = requestProcessorFactory.getReqType("AUTH");
			authRes = reqType.processReq(authXml,clientIP,lk);
		}
		catch (Exception excp) 
		{
			logger.info("Error in Controller",excp);
			authRes="Service Error";
		}
		return authRes;
	}

	/**
	 * @param String This is kycMessage received from the client
	 * @return String This is response returned to client after processing
	 * @description handles post request of given url for Kyc and sends response back in xml format.
	 */
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@RequestMapping(value = "/kycrequest/{lk}", method = RequestMethod.POST,headers = "Accept=application/xml")
	public @ResponseBody String kycRequestSync(@RequestBody String kycXml,@PathVariable(value="lk") final String lk, HttpServletRequest request) 
	{   
		String kycRes =null;
		String clientIP = getClientIp(request);
		try
		{
			if (!request.getContentType().equalsIgnoreCase("application/xml"))
			{
				return "Invalid header content media type";
			}
			if (isAllowedSize(kycXml.getBytes().length))
			{
				return "Invalid Request Size";
			}
			RequestProcessor reqType = requestProcessorFactory.getReqType("KYC");
			kycRes = reqType.processReq(kycXml,clientIP,lk);
		}
		catch (Exception excp) 
		{
			logger.info("Error in Controller",excp);
			kycRes="Service error";
		}
		return kycRes;
	}
	
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@RequestMapping(value = "/bfdrequest/{lk}", method = RequestMethod.POST, headers = "Accept=application/xml")
	public @ResponseBody String bfdRequestSync(@RequestBody String authXml,@PathVariable(value="lk") final String lk, HttpServletRequest request) 
	{	
		String clientIP = getClientIp(request);
		String authRes=null;
		try
		{
			if (!request.getContentType().equalsIgnoreCase("application/xml"))
			{
				return "Invalid header content media type";
			}
			if (isAllowedSize(authXml.getBytes().length))
			{
				return "Invalid Request Size";
			}
			RequestProcessor reqType = requestProcessorFactory.getReqType("BFD");
			authRes = reqType.processReq(authXml,clientIP,lk);
		}
		catch (Exception excp) 
		{
			logger.info("Error in Controller",excp);
			authRes="Service Error";
		}
		return authRes;
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() 
	{
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	/**
	 * @description Simply selects the home view to render by returning its name.
	 */
	@RequestMapping("*")
	public ModelAndView fallbackMethod(Locale locale, Model model) 
	{
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime1", formattedDate);
		model.addAttribute("serverTime", new Date().getTime());
		return new ModelAndView("home");
	}

	public boolean isAllowedSize(int requestLength )	
	{
		int allowedLength = Integer.parseInt(environment.getProperty("requestxmllength"));
		if (requestLength > allowedLength)
		{
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private String getClientIp(HttpServletRequest request) 
	{
		String remoteAddr = "";
		try 
		{
			 remoteAddr = request.getHeader("X-FORWARDED-FOR");
			 if (remoteAddr == null || "".equals(remoteAddr)) {
				 remoteAddr = request.getRemoteAddr();
			 }
		}
		catch(Exception excp)
		{
			logger.info("Error in fetching ip details");
		}
		return remoteAddr;
	}
}
