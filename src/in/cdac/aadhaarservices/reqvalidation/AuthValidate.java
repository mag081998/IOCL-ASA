package in.cdac.aadhaarservices.reqvalidation;

//import java.io.FileNotFoundException;
import java.io.*;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import generated.Kyc;
import in.cdac.aadhaarservices.util.Verhoeff;
import in.cdac.otp_v1.Otp;
import in.gov.uidai.authentication.common.types._2.Rc;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;

/**
 * 
 * This class validates the request XML against the XSD Specifications for
 * Authentication Requests
 *
 */
@Component
public final class AuthValidate implements IValidate 
{
	@Autowired
	ServletContext context;
	@Autowired
	Environment environment;

	private static final Logger logger = LogManager.getLogger(AuthValidate.class);

	@Override
	/**
	 * This method carries out Auth-XML validation against the Auth XSD
	 */
	public Map<String, String> validateXMLSyn(String xml) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try 
		{
			String content = "!DOCTYPE";
			if (StringUtils.containsIgnoreCase(xml, content)) 
			{
				logger.info("!DOCTYPE  matches");
				resultMap = getMapResult("n", "AUTH_XML_INVALID");
			} 
			else 
			{
				Schema schema = (Schema) context.getAttribute("authvalidator");
				Validator validator = schema.newValidator();
				StAXSource xmlSource = new StAXSource(AuthValidate.getXMLEventReader(new StreamSource(new StringReader(xml))));
				validator.validate(xmlSource, null);
				resultMap = getMapResult("y", "null");
			}
		} 
		catch (FileNotFoundException excp) 
		{
			logger.info("Error in Class_Name: AuthValidate Method_Name: validateXMLSyn", excp);
			resultMap = getMapResult("n", "MISSING_AUTH_SCHEMA_FILE");
		} 
		catch (Exception excp) 
		{
			logger.info("Error in Class_Name: AuthValidate Method_Name: validateXMLSyn", excp);
			resultMap = getMapResult("n", "AUTH_XML_INVALID");
		}
		return resultMap;
	}

	/*
	 * @Override
	 *//**
		 * This method carries out the Auth-XML element/attribute specific validations
		 * using Auth-XML as parameter
		 *//*
			 * public Map<String,String> validateXMLSem(String xml) { Map<String, String>
			 * resultMap = new HashMap<String, String>(); Auth authReq = null; try { authReq
			 * = (Auth)marshaller.unmarshal(new StreamSource(new StringReader(xml)));
			 * 
			 * if(authReq.getRc()!=null && authReq.getRc().equals(Rc.Y)) { resultMap =
			 * getMapResult("y", "null"); } else { logger.
			 * info("Error in Semantic Validation in Class_Name: AuthValidate Method_Name: validateXMLSem"
			 * ); resultMap = getMapResult("n", "AUTH_SEM_XML_INVALID"); } } catch
			 * (Exception excp) { logger.
			 * info("Error in Semantic Validation in Class_Name: AuthValidate Method_Name: validateXMLSem"
			 * ,excp); resultMap = getMapResult("n", "AUTH_SEM_XML_INVALID"); } return
			 * resultMap; }
			 */
	/**
	 * 
	 */
	@Override
	public Map<String, String> validateXMLSem(Auth authReq, String uidType) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try 
		{
			if (authReq.getRc() != null && authReq.getRc().equals(Rc.Y)) 
			{
				if (uidType.equalsIgnoreCase("A")) 
				{
					/*
					 * String uidLength = environment.getProperty("uidlength"); Pattern p =
					 * Pattern.compile("\\d{" + uidLength + "}"); Matcher m =
					 * p.matcher(authReq.getUid()); boolean b = m.matches();
					 */
					
					boolean b = Verhoeff.validateVerhoeff(authReq.getUid());
					if (!b) 
					{
						resultMap = getMapResult("n", "INVALID_UID_VALUE");
					} 
					else 
					{
						resultMap = getMapResult("y", "null");
					}
				}
				else 
				{
					resultMap = getMapResult("y", "null");
				}
			} 
			else 
			{
				logger.info("Error in Semantic Validation in Class_Name: AuthValidate Method_Name: validateXMLSem for AC Code:"+ authReq.getAc() + "Txn:" + authReq.getTxn());
				resultMap = getMapResult("n", "AUTH_SEM_XML_INVALID");
				return resultMap;
			}
		} catch (Exception excp) 
		{
			logger.info("Error in Semantic Validation in Class_Name: AuthValidate Method_Name: validateXMLSem for AC Code:"+ authReq.getAc() + "Txn:" + authReq.getTxn(),excp);
			resultMap = getMapResult("n", "AUTH_SEM_XML_INVALID");
		}
		return resultMap;
	}

	/**
	 * This method creates the Map to be returned to the calling class
	 * 
	 * @param status
	 * @param code
	 * @return
	 */
	private Map<String, String> getMapResult(String status, String code) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("status", status);
		resultMap.put("code", code);
		return resultMap;
	}

	/**
	 * 
	 * @param source
	 * @return
	 * @throws XMLStreamException
	 */
	private static XMLEventReader getXMLEventReader(Source source) throws XMLStreamException 
	{
		XMLInputFactory xmlif = null;
		XMLEventReader xmlr = null;
		xmlif = XMLInputFactory.newInstance();
		xmlif.setProperty("javax.xml.stream.isReplacingEntityReferences", false);
		xmlif.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
		xmlif.setProperty("javax.xml.stream.supportDTD", false);
		xmlr = xmlif.createXMLEventReader(source);
		return xmlr;
	}

	@Override
	public Map<String, String> validateXMLSem(Otp otpReq) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> validateXMLSem(Kyc kycReq) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}