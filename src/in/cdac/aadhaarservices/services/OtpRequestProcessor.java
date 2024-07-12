package in.cdac.aadhaarservices.services;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
//import org.w3._2000._09.xmldsig_.Signature;
//import org.w3._2000._09.xmldsig_.X509Data;
import org.w3._2000._09.xmldsig_.Signature;
import org.w3._2000._09.xmldsig_.X509Data;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import in.cdac.aadhaarservices.daooperations.AUADetails;
import in.cdac.aadhaarservices.daooperations.CryptoACLKCert;
import in.cdac.aadhaarservices.daooperations.LKDetails;
import in.cdac.aadhaarservices.daooperations.SignDet;
import in.cdac.aadhaarservices.daooperations.otp.OtpDaoOperations;
import in.cdac.aadhaarservices.model.OtpTransactionRecord;
import in.cdac.aadhaarservices.util.OtpRes;
import in.cdac.aadhaarservices.util.Utility;
import in.cdac.cryptoservice.DataElement;
import in.cdac.cryptoservice.KeyType;
import in.cdac.cryptoservice.Operations;
import in.cdac.cryptoservice.Request;
import in.cdac.cryptoservice.Response;
import in.cdac.cryptoservice.Status;
import in.cdac.cryptoservice.TokenType;
import in.cdac.otp_v1.Otp;

/**
 * This class carries out OTP request processing.
 * 
 * @author root
 *
 */
@Service
@Scope(value = "prototype")
public final class OtpRequestProcessor extends RequestProcessor
{
	private static final Logger logger = LogManager.getLogger(OtpRequestProcessor.class);
	
	@Autowired
	OtpDaoOperations otpDaoOperations;
	@Autowired
	private Jaxb2Marshaller marshaller;

	/**
	 * This method carries out OTP request processing
	 */
	@Override
	public String processReq(String otpReqXML, String clientIP,String lk) 
	{
		String modifiedOtpXml = null;
		Otp otpReqObj = null;
		String otpResponse = null;
		LKDetails lkDet = null;
		AUADetails auaDet = null;
		String url = null;
		String signedXml = null;
		String otpUidaiResponseXML = null;
		String errorCode=null;
		OtpTransactionRecord txnRecord = new OtpTransactionRecord();
		txnRecord.setClientIP( clientIP );
		txnRecord.setLk(lk);
		txnRecord.setReqReceiptTime(new Timestamp(System.currentTimeMillis()));
		errorHandler = errorHandlerFactory.getReqType("OTP");
		validate = validationFactory.getReqType("OTP");
		try 
		{
			if ( syntactValidate ) 
			{
				Map<String, String> validateXMLSyn = validate.validateXMLSyn( otpReqXML );
				if ( validateXMLSyn.get("status").equalsIgnoreCase("n") ) 
				{
					return errorResponse(validateXMLSyn.get("code"),false, txnRecord,otpReqObj);
				}
			}
			try 
			{
				otpReqObj = (Otp) marshaller.unmarshal(new StreamSource(new StringReader(otpReqXML)));
			} 
			catch (Exception excp) 
			{
				logger.info("Error in Parsing the Request OTP XML in Class Name:OtpRequestProcessor Method Name: processReq ",excp);
				return errorResponse("UNABLE_TO_PARSE_OTP_XML", false, txnRecord,otpReqObj);
			}
			
			//To check duplicate transaction
			Map<String, String> checkDuplicateTxn=otpDaoOperations.insertTxnAtStart(otpReqObj.getTxn(), "OTP");
			if(checkDuplicateTxn.get("status").equalsIgnoreCase("n")) 
			{
				return otpResponse = errorResponse(((String)checkDuplicateTxn.get("code")),false, txnRecord,null);
			}
			txnRecord.setTransactionId( otpReqObj.getTxn() );
			txnRecord.setReqSA(otpReqObj.getSa());
			txnRecord.setReqAC(otpReqObj.getAc());
			txnRecord.setSchema( commonSchemaName );
			Map<String, Object> resultSchemaName = otpDaoOperations.getSchemaName( otpReqObj.getAc(), commonSchemaName );
			
			if (((String)resultSchemaName.get("status")).equalsIgnoreCase("n")) 
			{
				return errorResponse(((String)resultSchemaName.get("errorCode")),false, txnRecord,otpReqObj);
			} 
			else 
			{
				txnRecord.setAsaAc( (String) resultSchemaName.get("ac") );
			}
			if ( semanticValidate ) 
			{
				errorCode = semanticValidation(otpReqObj);
				if (errorCode != null) 
				{
					return errorResponse(errorCode, true, txnRecord,otpReqObj);
				}
			}
			
			//AUA AC and lk verification with expiry and check opr allowed
			auaDet = getAUADetails(otpReqObj.getAc(), txnRecord.getSchema());
			if ( !auaDet.getStatus() )
			{
				return errorResponse( auaDet.getCode(), true, txnRecord,otpReqObj );
			}
			errorCode = verifyAUA(otpReqObj.getAc(), auaDet, txnRecord.getTransactionId(),txnRecord.getLk() );
			if ( errorCode != null )
			{
				return errorResponse( errorCode, true, txnRecord ,otpReqObj);
			}
			// get asa lk details
			lkDet = getAsaLkDetails( txnRecord.getAsaAc(), txnRecord.getSchema() ); 
			if ( !lkDet.getStatus() ) 
			{
				return errorResponse( lkDet.getCode(), true, txnRecord,otpReqObj );
			}
			errorCode = verifyASALKValidity( lkDet, otpReqObj.getTxn() );
			if( errorCode != null )
			{
				return errorResponse( errorCode, true, txnRecord ,otpReqObj);
			}
            // check if sign required or not 
			if (signrequired)
			{
				//Check already signed or not
				//Else
				//Sign the XML
					Signature s = otpReqObj.getSignature();
					//X509Data x509Data = null;
					if (s != null )
					{
						return errorResponse( "XML_IS_ALREADY_SIGNED", true, txnRecord,otpReqObj );
					}
					//return errorResponse(verSign.get("code"), true, txnRecord, otpReqObj);
					SignDet signDet = otpDaoOperations.getSignDet(otpReqObj.getAc(), txnRecord.getSchema());
					if ( !signDet.getStatus() )
					{
							return errorResponse( signDet.getCode(), true, txnRecord,otpReqObj );
					}
					try 
					{
							//modifiedOtpXml = marshalXml(otpReqObj);
						if ( signDet.getIs_sign_at_asa() ) 
						{
							CryptoACLKCert crypaclkcert = otpDaoOperations.getCryptoAcLkCertDet( txnRecord.getAsaAc(), txnRecord.getSchema(), Operations.SIGN.value() );
							if ( !crypaclkcert.getStatus() )
							{
								return errorResponse( crypaclkcert.getCode(), true, txnRecord,otpReqObj );
							}
							Map<String, String> digitalSigning = digitalSigning(otpReqXML, txnRecord, crypaclkcert);
							if ( digitalSigning.get("status").equalsIgnoreCase("n") ) 
							{
								errorCode = digitalSigning.get("data");
								return otpResponse=errorResponse(errorCode, true, txnRecord,otpReqObj);
							}
							else
							{
								signedXml = digitalSigning.get("data");
							}
						} 
						else 
						{
							signedXml = otpReqXML;
						}
							
					} 
					catch (Exception excp) 
					{
						logger.info("Error in Marshaling the Modified OTP XML in Class Name:OtpRequestProcessor Method Name: processReq",excp);
						otpResponse = errorResponse("UNABLE_TO_PARSE_OTP_XML", true, txnRecord,otpReqObj);
						return otpResponse;
					}
			}
			//else
			if(verifySign)
			{
				// if  sign  is not  required means verify xml
				Signature s = otpReqObj.getSignature();
				X509Data x509Data = null;
				if (s == null ||  s.getKeyInfo() ==null || s.getKeyInfo().getContent() == null)
				{
					return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,otpReqObj );
				}
				x509Data = (X509Data)s.getKeyInfo().getContent().get(0);
				if ( x509Data == null )
				{
					 	return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,otpReqObj );
				}
				@SuppressWarnings("rawtypes")
				JAXBElement el2e = (JAXBElement) x509Data.getX509IssuerSerialsAndX509SKISAndX509SubjectNames().get(1);
				
				if ( el2e == null )
				{
					return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,otpReqObj );
				}
				
				byte[] cert = (byte[])el2e.getValue();
				Map<String, String> verSign= verifySignature(otpReqXML, cert);
				if (verSign.get("status").equalsIgnoreCase("n"))
				{
					return errorResponse(verSign.get("code"), true, txnRecord, otpReqObj);
				
				}
				signedXml=otpReqXML;
			}
			if(!verifySign)
			{
				signedXml=otpReqXML;
			}
			
			char[] uidCharArray = {'0','0'};
			if ( otpReqObj.getType().value().equalsIgnoreCase("A") )	
			{
				uidCharArray = otpReqObj.getUid().toCharArray();
			}
			url = generateURL( uidaiOtpUrl, uidCharArray, lkDet.getAsa_license_key(), otpReqObj.getAc());
			txnRecord.setUidaiUrl(url);
			txnRecord.setServiceType( UIDAI_SERVICE );
			Map<String, String> uidaiResponse = callWebService( signedXml, txnRecord );
			if (uidaiResponse.get("status").equalsIgnoreCase("n")) 
			{
				otpResponse = errorResponse(uidaiResponse.get("data"), true, txnRecord,otpReqObj);
				return otpResponse;
			}
			otpUidaiResponseXML = uidaiResponse.get("data");
			otpResponse = updateResponse(otpUidaiResponseXML, txnRecord,otpReqObj);
			if (otpResponse != null) 
			{
				return otpResponse;
			}
		} 
		catch (Exception excp) 
		{
			logger.info("Main Exception in Class Name:OtpRequestProcessor Method Name: processReq", excp);
			otpResponse = errorResponse("OTP_SERVICE_ISSUE", false, txnRecord,otpReqObj);
			return otpResponse;
		}
		return otpUidaiResponseXML;
	}

	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	private AUADetails getAUADetails(String ac, String schema) 
	{
		AUADetails auaDet = otpDaoOperations.getAUADetails( ac, schema );
		return auaDet;
	}
	
	/**
	 * 
	 * @param lkdet
	 * @param txn
	 * @return
	 */
	private String verifyASALKValidity(LKDetails lkdet, String txn) 
	{
		int asa_dateDiff = utilityFunctions.validateDate( lkdet.getAsa_valid_till());
		if ( asa_dateDiff > 0 )
		{
			return "OTP_ASA_LK_EXPIRED";
		}
		return null;
	}
	/**
	 * 
	 * @param ac
	 * @param lkdet
	 * @param transactionId
	 * @return
	 */
	private String verifyAUA( String ac, AUADetails lkdet, String transactionId ,String lk) 
	{
		if ( !lkdet.getAua_code().trim().equalsIgnoreCase( ac.trim() ) )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		if ( !lkdet.getLk().trim().equalsIgnoreCase( lk.trim() ) )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		int aua_lkDiff = utilityFunctions.validateDate( lkdet.getLk_expiry());
		
		if ( aua_lkDiff > 0 )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		
		int aua_dateDiff = utilityFunctions.validateDate( lkdet.getAua_valid_till() );
		if ( aua_dateDiff > 0 )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		return null;
	}
	/**
	 * @param otpReqObj
	 * @return
	 */
	private String marshalXml(Otp otpReqObj) 
	{
		String modifiedOtpXml;
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		marshaller.marshal(otpReqObj, result);
		modifiedOtpXml = sw.toString();
		return modifiedOtpXml;
	}
	/**
	 * Perform semantic validation for the values received in the request
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
	private String semanticValidation(Otp otpReqObj) throws Exception 
	{
		String errorCode = null;
		Map<String, String> validateXMLSem = validate.validateXMLSem(otpReqObj);
		if (validateXMLSem.get("status").equalsIgnoreCase("n")) 
		{
			errorCode = validateXMLSem.get("code");
			return errorCode;
		}
		return errorCode;
	}

	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	private LKDetails getAsaLkDetails( String ac, String schema ) 
	{
		String lt = asaLicenseType;
		LKDetails auaLkDetails = otpDaoOperations.asaLkDetails( ac, schema, lt );
		return auaLkDetails;
	}

	/**
	 * 
	 * @param reqXml
	 * @param txnRecord
	 * @param crypaclkcert
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> digitalSigning( String reqXml, OtpTransactionRecord txnRecord, CryptoACLKCert crypaclkcert ) 
	{
		Map<String, String> mapResult = new HashMap<>();
		String CryotoUrl = cryptoServiceBaseUrl + "/"+SIGNING;
		System.out.println("crypto url "+CryotoUrl);
		txnRecord.setCryptoServiceUrl(CryotoUrl);
		txnRecord.setServiceType(CRYPTO_SERVICE);
		Request cryptoReq=new Request();
		Response crytpoResp;
		try
		{
			cryptoReq.setAc( crypaclkcert.getCrypto_ac() );
			cryptoReq.setTxn( txnRecord.getTransactionId() );
			cryptoReq.setTs( Utility.generateTimeStamp() );
			cryptoReq.setKeyidentifier( crypaclkcert.getKey_identifier() );
			cryptoReq.setKeytype( KeyType.fromValue(crypaclkcert.getKey_algorithm().trim()) );
			cryptoReq.setLk( crypaclkcert.getCrypto_lk() );
			cryptoReq.setOpr( Operations.fromValue(crypaclkcert.getCrypto_opr().trim()) );
			cryptoReq.setTkntype( TokenType.fromValue(crypaclkcert.getToken_type().trim()) );
			DataElement dle = new DataElement();
			dle.setValue(Base64.getEncoder().encode(reqXml.getBytes(StandardCharsets.UTF_8)));
			cryptoReq.setData(dle);
			String crytpoXML=marshalCryptoXml(cryptoReq);
			mapResult = callWebService(crytpoXML, txnRecord);
			if (mapResult.get("status").equalsIgnoreCase("y")) 
			{
				crytpoResp = (Response) marshaller.unmarshal(new StreamSource(new StringReader(mapResult.get("data"))));
				if(crytpoResp.getStatus().equals(Status.N))
				{
					logger.info("Error returned from Crypto Service for digital signing in Class Name:OtpRequestProcessor Method Name:digitalSigning");
					mapResult = getMapResult("n", "OTP_SIGNING_ISSUE");
					return mapResult;
				}
				mapResult=getMapResult("y", new String(Base64.getDecoder().decode(crytpoResp.getData().getValue()), StandardCharsets.UTF_8));
				logger.info("OTP Request Signed Succesfully by Crypto-Service");
			} 
			else 
			{
				mapResult = getMapResult("n", "OTP_SIGNING_ISSUE");
				return mapResult;
			}
		}
		catch(Exception excp)
		{
			logger.info("Crypto Service Invocation or Marshalling error in Class Name:OtpRequestProcessor Method Name: digitalSigning",excp);
			mapResult = getMapResult("n", "OTP_SIGNING_ISSUE");
			return mapResult;
		}
		return mapResult;
	}

	/**
	 * 
	 * @param errMsg
	 * @param logInDB
	 * @param txnRecord
	 * @return
	 */
	private String errorResponse(String errMsg, boolean logInDB, OtpTransactionRecord txnRecord,Otp otp) 
	{
		String errorCode = null;
		String otpResponse = null;
		txnRecord.setResponseTs(Utility.generateTimeStamp());
		errorCode=environment.getProperty(errMsg);
		txnRecord.setErrorCode(errorCode);
		txnRecord.setRet("n");
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		txnRecord.setRespForwardTime(respForwardTime);
		txnRecord.setRespCidrTime( new Timestamp(txnRecord.getResponseTs().toGregorianCalendar().getTimeInMillis()));
		otpResponse = errorHandler.errorResponse(txnRecord.getTransactionId(), txnRecord.getErrorCode(), txnRecord.getResponseTs());
		if ( logInDB ) 
		{
			otpDaoOperations.insertOtpRecord(  otp, txnRecord );
		}
		logger.info("Error returned in Class Name:OtpRequestProcessor Method Name: errorResponse:" + "--Txn:" + txnRecord.getTransactionId()+ "--ErrorCode:--" + errorCode+ "--ErrorMessage:--" + errMsg);
		return otpResponse;
	}

	/**
	 * 
	 * @param response
	 * @param txnRecord
	 * @return
	 */
	private OtpTransactionRecord getResponseBean(OtpRes response, OtpTransactionRecord txnRecord) 
	{
		txnRecord.setRet(response.getRet().value());
		txnRecord.setErrorCode(response.getErr());
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		if (response.getTs() != null) 
		{
			txnRecord.setRespCidrTime(new Timestamp(response.getTs().toGregorianCalendar().getTimeInMillis()));
		}
		txnRecord.setRespForwardTime(respForwardTime);
		txnRecord.setCode(response.getCode());
		logger.info("OTP Response returned from ASA/UIDAI for Transaction ID:--" +txnRecord.getTransactionId()+ "--Status:" +txnRecord.getRet()+ "--ErrorCode--:"+txnRecord.getErrorCode());
		return txnRecord;
	}
	
	/**
	 * 
	 * @param reqXMLForward
	 * @param txnRecord
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> callWebService(String reqXMLForward, OtpTransactionRecord txnRecord) 
	{
		Map<String, String> mapResult = new HashMap<>();
		Map<String, String> response = null;
		switch (txnRecord.getServiceType()) 
		{
		case UIDAI_SERVICE:
			txnRecord.setReqForwardTime(new Timestamp(System.currentTimeMillis()));
			response = callRestService.callRestMethod(txnRecord.getUidaiUrl(), reqXMLForward,txnRecord.getTransactionId());
			txnRecord.setRespReceiptTime(new Timestamp(System.currentTimeMillis()));
			break;
		case CRYPTO_SERVICE:
			response = callRestService.callRestMethodCrypto(txnRecord.getCryptoServiceUrl(), reqXMLForward);
			break;
		default:
			break;
		}
		if (response.get("status").equalsIgnoreCase("n")) 
		{
			mapResult = getMapResult("n", response.get("code"));
		} 
		else 
		{
			mapResult = getMapResult("y", response.get("data"));
		}
		return mapResult;
	}
	
	/**
	 * 
	 * @param responseXml
	 * @param txnRecord
	 * @return
	 */
	private String updateResponse(String responseXml, OtpTransactionRecord txnRecord,Otp otp) 
	{
		String otpResponse = null;
		OtpRes otpResObj = null;
		try 
		{
			otpResObj = (OtpRes) marshaller.unmarshal(new StreamSource(new StringReader(responseXml)));
		} 
		catch (Exception excp) 
		{
			logger.info("Error in Class Name:OtpRequestProcessor Method Name: updateResponse in parsing the response XML",excp);
			otpResponse = errorResponse("UNABLE_TO_PARSE_OTP_RESPONSE", true, txnRecord,otp);
			return otpResponse;
		}
		otpDaoOperations.insertOtpRecord(otp, getResponseBean(otpResObj, txnRecord));
		return otpResponse;
	}

	/**
	 * 
	 * @param cryptoObj
	 * @return
	 * @throws Exception
	 */
	private String marshalCryptoXml(Request cryptoObj) throws Exception
	{
		String cryptoXml=null;;
		OutputStream out = new ByteOutputStream();
		Writer writer=null;
		writer = new OutputStreamWriter(out, "UTF-8");
		StreamResult result = new StreamResult(writer);
		marshaller.marshal(cryptoObj, result);
		cryptoXml = out.toString();
		return cryptoXml;
	}
	
}
