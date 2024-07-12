package in.cdac.aadhaarservices.reqvalidation;

//import java.io.FileNotFoundException;
import java.io.*;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.stereotype.Component;
import generated.Kyc;
import in.cdac.otp_v1.Otp;
import in.gov.uidai.authentication.common.types._2.Rc;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;


/**
 * This class validates the request XML against the XSD Specifications for e-KYC Requests
 */
@Component
public final class KycValidate implements IValidate 
{
	@Autowired
	ServletContext context;
	Map<String, String> resultMap = new HashMap<String, String>();
	private static final Logger logger = LogManager.getLogger(KycValidate.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	/**
	 * This method carries out KYC-XML validation against the KYC XSD
	 */
	public Map validateXMLSyn(String xml) 
	{
		try 
        {
			String content = "!DOCTYPE";
			if (StringUtils.containsIgnoreCase(xml, content)) 
			{
				logger.info("!DOCTYPE  matches");
				resultMap = getMapResult("n", "KYC_XML_INVALID");
			}
			else
			{
				Schema schema = (Schema) context.getAttribute("kycvalidator");
				Validator validator = schema.newValidator();
				StAXSource xmlSource = new StAXSource(KycValidate.getXMLEventReader(new StreamSource(new StringReader(xml))));
				validator.validate(xmlSource, null);
				resultMap = getMapResult("y", "null");
			}
        }
		catch(FileNotFoundException excp)
		{
			logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSyn", excp);
			resultMap = getMapResult("n", "MISSING_KYC_SCHEMA_FILE");
		}
		catch (Exception excp) 
		{
			logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSyn", excp);
			resultMap = getMapResult("n", "KYC_XML_INVALID");
		}
		return resultMap;
	}

	/**
	 * This method carries out the e-KYC-XML element/attribute specific
	 * validations
	 */
	
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" })
	 * 
	 * @Override public Map validateXMLSem(String xml) { Kyc KycReq = null; try {
	 * KycReq = (Kyc)marshaller.unmarshal(new StreamSource(new StringReader(xml)));
	 * 
	 * if(KycReq.getRc()!=null && KycReq.getRc().name().equals(Rc.Y.name())) {
	 * resultMap = getMapResult("y", "null"); } else {
	 * logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSem");
	 * resultMap = getMapResult("n", "KYC_SEM_XML_INVALID"); }
	 * 
	 * } catch (Exception excp) {
	 * logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSem",
	 * excp); resultMap = getMapResult("n", "KYC_SEM_XML_INVALID"); } return
	 * resultMap; }
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> validateXMLSem(Kyc kycReq) 
	{
		try
		{
			if(kycReq.getRc()!=null && kycReq.getRc().name().equals(Rc.Y.name()))
			{  
				resultMap = getMapResult("y", "null");
			}
			else
			{
				logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSem");
				resultMap = getMapResult("n", "KYC_SEM_XML_INVALID");
			}
		}
		catch (Exception excp) 
		{
			logger.info("Error in Class_Name: KycValidate Method_Name: validateXMLSem", excp);
			resultMap = getMapResult("n", "KYC_SEM_XML_INVALID");
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
		return null;
	}

	@Override
	public Map<String, String> validateXMLSem(Auth authReq, String uidType) 
	{
		return null;
	}

	
}