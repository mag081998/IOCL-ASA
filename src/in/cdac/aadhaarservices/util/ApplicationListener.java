package in.cdac.aadhaarservices.util;

import java.io.File;
import java.io.IOException;

//import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;



/**
 * Application Lifecycle Listener implementation class ApplicationListener
 *
 */
@WebListener
public final class ApplicationListener implements ServletContextListener 
{
	private final Logger logger =  LogManager.getLogger(ApplicationListener.class);
	
	@Autowired
	ResourceLoader resourceLoader;
	
	/**
	 * Default constructor. 
	 */
	public ApplicationListener() 
	{
		
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0)  
	{ 
		
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event)  
	{ 
		ServletContext context = event.getServletContext();
		try {
			context.setAttribute("otpvalidator",otpValidator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			context.setAttribute("authvalidator", authValidator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			context.setAttribute("kycvalidator", kycValidator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private Schema otpValidator() throws IOException 
	{
		StreamSource source1 = new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("uid-otp-request-1.6.xsd"));
		//Resource resource1 = resourceLoader.getResource("uid-otp-request-1.6.xsd");
		//File source1 = resource1.getFile();
		
		StreamSource source2 = new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("xmldsig-core-schema.xsd"));
		//Resource resource2 = resourceLoader.getResource("xmldsig-core-schema.xsd");
		//File source2 = resource2.getFile();
		
		SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema =null;
		try 
		{
			schema = sf.newSchema(new Source[] { (Source) source2, (Source) source1 });
		} 
		catch (SAXException excp) 
		{
			logger.info("Error in Loading OTP XSD Files"+excp.getMessage(),excp);
		}
		//Validator validator = schema.newValidator();
		logger.info("Loaded OTP XSD Files");
		return schema;
	}
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private Schema authValidator() throws IOException 
	{
		SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		StreamSource source1 = new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("uid-auth-request.xsd"));
		//Resource resource1 = resourceLoader.getResource("uid-auth-request.xsd");
		//File source1 = resource1.getFile();
		
		StreamSource source2 =  new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("common-types.xsd"));
		//Resource resource2 = resourceLoader.getResource("common-types.xsd");
		//File source2 = resource2.getFile();
		
		StreamSource source3 =  new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("xmldsig-core-schema.xsd"));
		//Resource resource3 = resourceLoader.getResource("common-types.xsd");
		//File source3 = resource3.getFile();
		
		Schema schema = null;
		try 
		{
			schema = sf.newSchema(new Source[] { (Source) source2, (Source) source3, (Source) source1 });
		} 
		catch (SAXException excp) 
		{
			logger.info("Error in Loading Auth XSD Files"+excp.getMessage(),excp);
		}
		//Validator validator = schema.newValidator();
		logger.info("Loaded Auth XSD Files");
		//return validator;
		return schema; 
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private Schema kycValidator() throws IOException 
	{
		SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		StreamSource source1 = new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("uid-kyc-2.1.xsd"));
		//Resource resource1 = resourceLoader.getResource("uid-kyc-2.1.xsd");
		//File source1 = resource1.getFile();
		
		StreamSource source2 = new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("common-types.xsd"));
		//Resource resource2 = resourceLoader.getResource("common-types.xsd");
		//File source2 = resource2.getFile();
		
		StreamSource source3 =  new StreamSource(ApplicationListener.class.getClassLoader().getResourceAsStream("xmldsig-core-schema.xsd"));
		//Resource resource3 = resourceLoader.getResource("xmldsig-core-schema.xsd");
		//File source3 = resource3.getFile();
		
		Schema schema = null;
		try 
		{
			schema = sf.newSchema(new Source[] { (Source) source2, (Source) source3, (Source) source1 });
		} 
		catch (SAXException excp) 
		{
			logger.info("Error in Loading KYC XSD Files"+excp.getMessage(),excp);
		}
		//Validator validator = schema.newValidator();
		logger.info("Loaded KYC XSD Files");
		return schema;
	}
}
