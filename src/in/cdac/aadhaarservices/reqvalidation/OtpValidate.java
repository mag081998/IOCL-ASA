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
import in.cdac.aadhaarservices.util.Utility;
import in.cdac.aadhaarservices.util.Verhoeff;
import in.cdac.otp_v1.Otp;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;
/**
 * This class validates the request XML against the XSD Specifications for OTP Requests
 * @author root
 *
 */
@Component
public final class OtpValidate implements IValidate 
{
	@Autowired
	Utility utilFunctions;
	@Autowired
	ServletContext context;
	@Autowired
	Environment environment;

	private static final Logger logger = LogManager.getLogger(OtpValidate.class);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	/**
	 * This method carries out OTP-XML validation against the OTP XSD
	 */
	public Map validateXMLSyn(String xml) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try 
		{
			String content = "!DOCTYPE";
			if (StringUtils.containsIgnoreCase(xml, content)) 
			{
				logger.info("!DOCTYPE  matches");
				resultMap = getMapResult("n", "OTP_XML_INVALID");
			} 
			else 
			{	
				Schema schema = (Schema) context.getAttribute("otpvalidator");
				Validator validator = schema.newValidator();
				StAXSource xmlSource = new StAXSource(OtpValidate.getXMLEventReader(new StreamSource(new StringReader(xml))));
				validator.validate(xmlSource, null);
				resultMap = getMapResult("y", "null");
			}
		}
		catch(FileNotFoundException excp)
		{
			resultMap = getMapResult("n", "MISSING_OTP_SCHEMA_FILE");
			logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSyn", excp);
		}
		catch (Exception excp) 
		{
			resultMap = getMapResult("n", "OTP_XML_INVALID");
			logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSyn", excp);
		}
		return resultMap;
	}
	/**
	 * This method carries out the OTP XML element/attribute specific validations
	 */
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" })
	 * 
	 * @Override public Map validateXMLSem(String xml) { Map<String, String>
	 * resultMap = new HashMap<String, String>(); Otp otpReq = null; boolean
	 * validateFlag; try { otpReq = (Otp) marshaller.unmarshal(new StreamSource(new
	 * StringReader(xml)));
	 * validateFlag=utilFunctions.validateTimestamp(otpReq.getTs());
	 * if(!validateFlag) {
	 * logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSem");
	 * resultMap = getMapResult("n", "OTP_INVALID_TIMESTAMP"); } else { resultMap =
	 * getMapResult("y", "null"); } } catch (Exception excp) { resultMap =
	 * getMapResult("n", "OTP_SEM_XML_INVALID");
	 * logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSem"
	 * ,excp); } return resultMap; }
	 */
	
	@SuppressWarnings("unchecked")
	private Map<String, String> verifyUidLength(String uid, String type) 
	{
		Map<String, String> resultMap = new HashMap<String,String>();
		try 
		{
			if (type.equalsIgnoreCase("A"))
			{
				/*
				 * String uidLength = environment.getProperty("uidlength"); Pattern p =
				 * Pattern.compile("\\d{"+uidLength+"}"); Matcher m = p.matcher(uid); boolean b
				 * = m.matches();
				 */
				boolean b = Verhoeff.validateVerhoeff(uid);
				
				if (!b)	
				{
					resultMap = getMapResult("n", "INVALID_UID_VALUE");
					
				}
				else 
				{
					resultMap = getMapResult("y", "null");
				}
				
				
				
			}
			else if (type.equalsIgnoreCase("V"))
			{
				if (uid.length() != Integer.parseInt(environment.getProperty("virtualidlength")))	
				{
					resultMap = getMapResult("n", "INVALID_UID_VALUE");
				}
				else 
				{
					resultMap = getMapResult("y", "null");
				}
			}
			else if (type.equalsIgnoreCase("T"))
			{
				if (uid.length	() != Integer.parseInt(environment.getProperty("uidtokenlength")))	{
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
		catch(Exception excp)
		{
			logger.info(" Exception while extracting uid : ",excp);
			return resultMap = getMapResult("n", "INVALID_UID_VALUE");
		}
		return resultMap;
	}
	/**
	 * 
	 * @param otpReqObj
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map validateXMLSem(Otp otpReqObj) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		boolean validateFlag;
		try 
		{
			validateFlag=utilFunctions.validateTimestamp(otpReqObj.getTs());
			if(!validateFlag)
			{
				logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSem for AC Code:" +otpReqObj.getAc() +"Txn:" +otpReqObj.getTxn());
				resultMap = getMapResult("n", "OTP_INVALID_TIMESTAMP");
				return resultMap;
			}
			else 
			{
				String type = otpReqObj.getType().value();
				resultMap = verifyUidLength(otpReqObj.getUid(),type);
			}
		} 
		catch (Exception excp) 
		{
			resultMap = getMapResult("n", "OTP_SEM_XML_INVALID");
			logger.info("Error in Class_Name: OtpValidate Method_Name: validateXMLSem for AC Code:" +otpReqObj.getAc() +"Txn:" +otpReqObj.getTxn(),excp);
		}
		return resultMap;
	}
	/**
	 * 
	 * @param status
	 * @param code
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getMapResult(String status, String code) 
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
	public Map<String, String> validateXMLSem(Auth authReq,String uidType) 
	{
		// Implemented in Auth Subclass
		return null;
	}
	@Override
	public Map<String, String> validateXMLSem(Kyc kycReq) 
	{
		// Implemented in KYC Subclass
		return null;
	}
}