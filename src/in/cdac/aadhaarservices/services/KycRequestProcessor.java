package in.cdac.aadhaarservices.services;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.w3._2000._09.xmldsig_.Signature;
import org.w3._2000._09.xmldsig_.X509Data;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import generated.Kyc;
import in.gov.uidai.kyc.uid_kyc_response._1.Resp;
import in.cdac.aadhaarservices.daooperations.AUADetails;
import in.cdac.aadhaarservices.daooperations.CryptoACLKCert;
import in.cdac.aadhaarservices.daooperations.LKDetails;
import in.cdac.aadhaarservices.daooperations.SignDet;
import in.cdac.aadhaarservices.daooperations.kyc.KycDaoOperations;
import in.cdac.aadhaarservices.model.KycTransactionRecord;
import in.cdac.aadhaarservices.util.Utility;
import in.cdac.cryptoservice.DataElement;
import in.cdac.cryptoservice.KeyType;
import in.cdac.cryptoservice.Operations;
import in.cdac.cryptoservice.Request;
import in.cdac.cryptoservice.Response;
import in.cdac.cryptoservice.Status;
import in.cdac.cryptoservice.TokenType;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;
import in.gov.uidai.authentication.uid_auth_response._2.AuthRes;
import in.gov.uidai.kyc.common.types._1.YesNoType;
import in.gov.uidai.kyc.uid_kyc_response._1.KycRes;

/**
 * This class carries out KYC authentication request processing.
 *
 */

/**
 * 
 * @author root
 *
 */
@Service
@Scope(value = "prototype")
public final class KycRequestProcessor extends RequestProcessor 
{
	private static final Logger logger = LogManager.getLogger(KycRequestProcessor.class);
	@Autowired
	private KycDaoOperations daoOperations;

	
	@Autowired
	private Jaxb2Marshaller marshaller;

	@Override
	public String processReq(String kycReqXML, String clientIP,String lk) 
	{
		AUADetails auaDet = null;
		Auth authReqObj = null;
		Kyc kycReqObj = null;
		String kycResponse = null;
		String decodedAuthXml = null;
		LKDetails lkDet = null;
		String modifiedKYCXml = null;
		String signedAuthXml = null;
		Map<String, String> validateXMLSyn = null;
		String uidaiEkycResponse = null;
		String url = null;
		String uidType = null;
		Resp respObj = null;
		String errorCode = null;
		KycTransactionRecord txnRecord = new KycTransactionRecord();
		txnRecord.setReqReceiptTime(new Timestamp(System.currentTimeMillis()));
		txnRecord.setClientIP(clientIP);
		txnRecord.setLk(lk);
		errorHandler = errorHandlerFactory.getReqType("KYC");
		validate = validationFactory.getReqType("AUTH");
		kycValidate = validationFactory.getReqType("KYC");
		try {
			if (syntactValidate) {
				// Validate KYC XML
				validateXMLSyn = kycValidate.validateXMLSyn(kycReqXML);
				if (validateXMLSyn.get("status").equalsIgnoreCase("n")) {
					return errorResponse(validateXMLSyn.get("code"), false, txnRecord, kycReqObj, authReqObj);
				}
			}
			try {
				kycReqObj = (Kyc) marshaller.unmarshal(new StreamSource(new StringReader(kycReqXML)));
				decodedAuthXml = new String(kycReqObj.getRad());
				// Validate Auth XML
				if (syntactValidate) {
					validateXMLSyn = validate.validateXMLSyn(decodedAuthXml);
					if (validateXMLSyn.get("status").equalsIgnoreCase("n")) {
						return errorResponse(validateXMLSyn.get("code"), false, txnRecord, kycReqObj, authReqObj);
					}
				}
			} catch (Exception excp) {
				logger.info("Error in Parsing the KYC XML in Class Name:KycRequestProcessor Method Name: processReq : ",
						excp);
				return errorResponse("UNABLE_TO_PARSE_KYC_XML", false, txnRecord, kycReqObj, authReqObj);
			}
			try {
				authReqObj = (Auth) marshaller.unmarshal(new StreamSource(new StringReader(decodedAuthXml)));
			} catch (Exception excp) {
				logger.info(
						"Error in Parsing the Auth XML of KYC in Class Name:KycRequestProcessor Method Name: processReq : ",
						excp);
				return errorResponse("UNABLE_TO_PARSE_KYC_XML", false, txnRecord, kycReqObj, authReqObj);
			}

			// To check duplicate transaction
			Map<String, String> checkDuplicateTxn = daoOperations.insertTxnAtStart(authReqObj.getTxn(), "KYC");
			if (checkDuplicateTxn.get("status").equalsIgnoreCase("n")) {
				return errorResponse(((String) checkDuplicateTxn.get("code")), false, txnRecord, kycReqObj, authReqObj);
			}
			txnRecord.setTransactionId(authReqObj.getTxn());
			txnRecord.setAc(authReqObj.getAc());
			uidType = getUIDType(authReqObj.getUid());
			txnRecord.setSchema(commonSchemaName);
			
			Map<String, Object> resultSchemaName = daoOperations.getSchemaName(txnRecord.getAc(), commonSchemaName);
			if (((String) resultSchemaName.get("status")).equalsIgnoreCase("n")) {
				return errorResponse(((String) resultSchemaName.get("errorCode")), false, txnRecord, kycReqObj,
						authReqObj);
			} else {
				txnRecord.setAsaAc((String) resultSchemaName.get("ac"));
			}
			if (uidType == null) 
			{
				return errorResponse("INVALID_UID_LENGTH", true, txnRecord, kycReqObj, authReqObj);
			}
			else 
			{
				txnRecord.setUidType(uidType);
			}
			// Semantic Validation for Auth and KYC
			if (semanticValidate) 
			{
				errorCode = semanticValidation(authReqObj, kycReqObj, uidType);
				if (errorCode != null) 
				{
					return errorResponse(errorCode, true, txnRecord, kycReqObj, authReqObj);
				}
			}
			
			auaDet = getAUADetails(authReqObj.getAc(), txnRecord.getSchema());
			if (!auaDet.getStatus()) 
			{
				return errorResponse(auaDet.getCode(), true, txnRecord, kycReqObj, authReqObj);
			}
			if (verifyLk) 
			{
				errorCode = verifyAUA(txnRecord.getAc(), auaDet, authReqObj);
				if (errorCode != null) 
				{
					return errorResponse(errorCode, true, txnRecord, kycReqObj, authReqObj);
				}
			}
			lkDet = getAsaLkDetails(txnRecord.getAsaAc(), txnRecord.getSchema());
			if (!lkDet.getStatus()) 
			{
				return errorResponse(lkDet.getCode(), true, txnRecord, kycReqObj, authReqObj);
			}
			errorCode = verifyASALKValidity(lkDet, txnRecord.getTransactionId());
			if (errorCode != null) {
				return errorResponse(errorCode, true, txnRecord, kycReqObj, authReqObj);
			}
			
			// check if sign required or not 
			if (signrequired) 
			{
				// Check already signed or not
				// Else
				// Sign the XML
				Signature s = authReqObj.getSignature();
				// X509Data x509Data = null;
				if (s != null) 
				{
					return errorResponse("XML_IS_ALREADY_SIGNED", true, txnRecord, kycReqObj, authReqObj);
				}
				// return errorResponse(verSign.get("code"), true, txnRecord, otpReqObj);
				SignDet signDet = daoOperations.getSignDet(authReqObj.getAc(), txnRecord.getSchema());
				if (!signDet.getStatus()) 
				{
					return errorResponse(signDet.getCode(), true, txnRecord, kycReqObj, authReqObj);
				}
				txnRecord.setSigndet(signDet);
				try 
				{
					// modifiedOtpXml = marshalXml(otpReqObj);
					if (signDet.getIs_sign_at_asa()) 
					{
						CryptoACLKCert crypaclkcert = daoOperations.getCryptoAcLkCertDet(txnRecord.getAsaAc(),
								txnRecord.getSchema(), Operations.SIGN.value());
						if (!crypaclkcert.getStatus()) 
						{
							return errorResponse(crypaclkcert.getCode(), true, txnRecord, kycReqObj, authReqObj);
						}
						Map<String, String> digitalSigning = digitalSigning(txnRecord, decodedAuthXml, crypaclkcert);
						if (digitalSigning.get("status").equalsIgnoreCase("n")) 
						{
							errorCode = digitalSigning.get("data");
							return errorResponse(errorCode, true, txnRecord, kycReqObj, authReqObj);
						}
						else
						{
							signedAuthXml = digitalSigning.get("data");
						}
						Map<String, String> modifiedKyc = prepareKycXml(signedAuthXml, kycReqObj, txnRecord,
								authReqObj);
						modifiedKYCXml = modifiedKyc.get("data");
						if (modifiedKyc.get("status").equalsIgnoreCase("n"))
						{
							return kycResponse = modifiedKYCXml;
						}
					}
					else 
					{
						modifiedKYCXml = kycReqXML;
					}

				} 
				catch (Exception excp) 
				{
					logger.info(
							"Error in Parsing the Auth XML of KYC in Class Name:KycRequestProcessor Method Name: processReq : ",
							excp);
					kycResponse = errorResponse("UNABLE_AUTH_XML_PARSING", false, txnRecord, kycReqObj, authReqObj);
					return kycResponse;
				}
			} 
			//else
			if(verifySign)
			{
				// if sign is not required means verify xml
				Signature s = authReqObj.getSignature();
				X509Data x509Data = null;
				if (s == null || s.getKeyInfo() == null || s.getKeyInfo().getContent() == null) 
				{
					return errorResponse("SIGN_VERIFICATION_FAILED", true, txnRecord, kycReqObj, authReqObj);
				}
				x509Data = (X509Data) s.getKeyInfo().getContent().get(0);
				if (x509Data == null)
				{
					return errorResponse("SIGN_VERIFICATION_FAILED", true, txnRecord, kycReqObj, authReqObj);
				}
				@SuppressWarnings("rawtypes")
				JAXBElement el2e = (JAXBElement) x509Data.getX509IssuerSerialsAndX509SKISAndX509SubjectNames().get(1);

				if (el2e == null) 
				{
					return errorResponse("SIGN_VERIFICATION_FAILED", true, txnRecord, kycReqObj, authReqObj);
				}

				byte[] cert = (byte[]) el2e.getValue();
				Map<String, String> verSign = verifySignature(decodedAuthXml, cert);
				if (verSign.get("status").equalsIgnoreCase("n"))
				{
					return errorResponse(verSign.get("code"), true, txnRecord, kycReqObj, authReqObj);
				}
				//signedXml = decodedAuthXml;
			}
			if(!verifySign)
			{
				modifiedKYCXml=kycReqXML;
			}
			
			if (verifyCi)
			{
				errorCode = verifyUIDCI(authReqObj.getSkey().getCi());
				if( errorCode != null )
				{
					return errorResponse(errorCode, true, txnRecord,kycReqObj, authReqObj);
				}
			}
			char[] uidCharArray = { '0', '0' };
			if (uidType.equalsIgnoreCase("A")) 
			{
				uidCharArray = authReqObj.getUid().toCharArray();
			}
			url = generateURL(uidaiEkycUrl, uidCharArray, lkDet.getAsa_license_key(), authReqObj.getAc());
			txnRecord.setUidaiUrl(url);
			txnRecord.setServiceType(UIDAI_SERVICE);
			// Call UIDAI Web-Service
			Map<String, String> uidaiResponse = callWebService(modifiedKYCXml, txnRecord);
			if (uidaiResponse.get("status").equalsIgnoreCase("n")) {
				kycResponse = errorResponse(uidaiResponse.get("data"), true, txnRecord, kycReqObj, authReqObj);
				return kycResponse;
			}
			try {
				uidaiEkycResponse = uidaiResponse.get("data");
				respObj = (Resp) marshaller.unmarshal(new StreamSource(new StringReader(uidaiEkycResponse)));
				kycMetaInfo(respObj, txnRecord);
			} catch (Exception excp) {
				logger.info("Error in Class Name:KycRequestProcessor Method Name: processReq in Parsing Response XML"
						+ authReqObj.getTxn(), excp);
				return errorResponse("UNABLE_TO_PARSE_KYC_RESPONSE", true, txnRecord, kycReqObj, authReqObj);
			}
			/*
			 * Kyc error returned will be extracted for Auth error.
			 */
			if (!respObj.getStatus().equalsIgnoreCase("0")) {
				errorCode = getInfo(respObj, txnRecord);
				if (errorCode != null) {
					logger.info(
							"INFO Detail Extraction Error in Class Name:KycRequestProcessor Method Name: processReq : Txn : "
									+ authReqObj.getTxn() + " " + errorCode);
				}
			}
			updateResponse(txnRecord, kycReqObj, authReqObj);
		} catch (Exception excp) {
			logger.info("Main Exception in Class Name:KycRequestProcessor Method Name: processReq", excp);
			return errorResponse("KYC_SERVICE_ISSUE", false, txnRecord, kycReqObj, authReqObj);
		}
		return uidaiEkycResponse;
	}

	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	private AUADetails getAUADetails(String ac, String schema) {
		AUADetails auadet = daoOperations.getAUADetails(ac, schema);
		return auadet;
	}

	/**
	 * 
	 * @param lkdet
	 * @param txn
	 * @return
	 */
	private String verifyASALKValidity(LKDetails lkdet, String txn) {
		int asa_dateDiff = utilityFunctions.validateDate(lkdet.getAsa_valid_till());
		if (asa_dateDiff > 0) {
			return "KYC_ASA_LK_EXPIRED";
		}
		return null;
	}

	/**
	 * 
	 * @param ac
	 * @param lkdet
	 * @param authReqObj
	 * @return
	 */
	private String verifyAUA(String ac, AUADetails lkdet, Auth authReqObj) {
		if (!lkdet.getAua_code().trim().equalsIgnoreCase(ac.trim())) {
			return "AUA_VERIFICATION_FAILED";
		}

		int aua_dateDiff = utilityFunctions.validateDate(lkdet.getAua_valid_till());
		if (aua_dateDiff > 0) {
			return "AUA_VERIFICATION_FAILED";
		}
		return null;
	}

	/**
	 * 
	 * @param respObj
	 * @param txnRecord
	 */
	private void kycMetaInfo(Resp respObj, KycTransactionRecord txnRecord) {
		txnRecord.setRespPacketTime(new Timestamp(respObj.getTs().toGregorianCalendar().getTimeInMillis()));
		txnRecord.setKycRet(respObj.getRet().value());
		txnRecord.setKycErr(respObj.getErr());
		txnRecord.setCode(respObj.getCode());
		txnRecord.setStatus(respObj.getStatus());
		txnRecord.setKo(respObj.getKo());
		txnRecord.setRet(respObj.getRet().value());
		txnRecord.setErrorCode(respObj.getErr());
	}

	/**
	 * 
	 * @param respObj
	 * @param txnRecord
	 * @return
	 */
	private String getInfo(Resp respObj, KycTransactionRecord txnRecord) {
		KycRes kycRes = null;
		String errorCode = null;
		String kycResString = null;
		AuthRes authResObj = null;
		try {
			if (respObj.getKycRes() != null) {
				kycResString = new String(respObj.getKycRes());
				kycRes = (KycRes) marshaller.unmarshal(new StreamSource(new StringReader(kycResString)));
				txnRecord.setActn(kycRes.getActn());
				if (kycRes.getRar() != null) {
					authResObj = (AuthRes) marshaller
							.unmarshal(new StreamSource(new StringReader(new String(kycRes.getRar()))));
					txnRecord.setAuthErr(authResObj.getErr());
					txnRecord.setAuthActn(authResObj.getActn());
				}
			}
		} catch (Exception e) {
			logger.info("Exception in parsing KYC Response from Kyc Txn : " + txnRecord.getTransactionId() + "Error : "
					+ respObj.getErr(), e);
			errorCode = "UNABLE_TO_PARSE_KYC_AUTH_RESPONSE";
		}
		return errorCode;

	}


	/**
	 * 
	 * @param authReqObj
	 * @param kycReq
	 * @param uidType
	 * @return
	 * @throws Exception
	 */
	private String semanticValidation(Auth authReqObj, Kyc kycReq, String uidType) throws Exception {
		String errorCode = null;
		// Perform Semantic Validation
		Map<String, String> validateXMLSem = kycValidate.validateXMLSem(kycReq);
		if (validateXMLSem.get("status").equalsIgnoreCase("n"))
		{
			errorCode = validateXMLSem.get("code");
		} 
		else 
		{
			validateXMLSem = validate.validateXMLSem(authReqObj, uidType);
			if (validateXMLSem.get("status").equalsIgnoreCase("n")) 
			{
				errorCode = validateXMLSem.get("code");
			} 
			else 
			{
				errorCode = null;
			}
		}
		return errorCode;
	}

	/**
	 * 
	 * @param auaCode
	 * @return
	 */
	private LKDetails getAsaLkDetails(String auaCode, String schema) {
		String licenseType = ksaLicenseType;
		LKDetails lkVerificationResult = daoOperations.asaLkDetails(auaCode, schema, licenseType);
		return lkVerificationResult;
	}

	/**
	 * 
	 * @param txnRecord
	 * @param reqXml
	 * @param certdet
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> digitalSigning(KycTransactionRecord txnRecord, String reqXml, CryptoACLKCert certdet) {
		Map<String, String> mapResult = new HashMap<>();
		String cryptoUrl = cryptoServiceBaseUrl + "/" + SIGNING;
		Request cryptoReq = new Request();
		String txn = txnRecord.getTransactionId();
		txnRecord.setCryptoServiceUrl(cryptoUrl);
		txnRecord.setServiceType(CRYPTO_SERVICE);
		Response cryptoResp;
		try {
			cryptoReq.setAc(certdet.getCrypto_ac());
			cryptoReq.setLk(certdet.getCrypto_lk());
			cryptoReq.setTxn(txn);
			cryptoReq.setTs(Utility.generateTimeStamp());
			cryptoReq.setKeyidentifier(certdet.getKey_identifier());
			cryptoReq.setKeytype(KeyType.fromValue(certdet.getKey_algorithm().trim()));
			cryptoReq.setOpr(Operations.fromValue(certdet.getCrypto_opr().trim()));
			cryptoReq.setTkntype(TokenType.fromValue(certdet.getToken_type().trim()));
			DataElement dle = new DataElement();
			dle.setValue(Base64.getEncoder().encode(reqXml.getBytes("UTF-8")));
			cryptoReq.setData(dle);
			String crytpoXML = marshalCryptoXml(cryptoReq);
			mapResult = callWebService(crytpoXML, txnRecord);
			if (mapResult.get("status").equalsIgnoreCase("y")) {
				cryptoResp = (Response) marshaller.unmarshal(new StreamSource(new StringReader(mapResult.get("data"))));
				if (cryptoResp.getStatus().equals(Status.N)) {
					mapResult = getMapResult("n", "KYC_AUTH_SIGNING_ISSUE");
					return mapResult;
				}
				mapResult = getMapResult("y",
						new String(Base64.getDecoder().decode(cryptoResp.getData().getValue()), "UTF-8"));
				logger.info("KYC Request Signed Succesfully by Crypto-Service");
			} else {
				mapResult = getMapResult("n", "KYC_AUTH_SIGNING_ISSUE");
				return mapResult;
			}
		} catch (Exception excp) {
			logger.info(
					"Crypto Service Invocation or Marshalling error in Class Name:KycRequestProcessor Method Name: digitalSigning ",
					excp);
			mapResult = getMapResult("n", "KYC_AUTH_SIGNING_ISSUE");
			return mapResult;
		}
		return mapResult;
	}

	/**
	 * 
	 * @param signedXml
	 * @param kycReq
	 * @param txnRecord
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> prepareKycXml(String signedXml, Kyc kycReq, KycTransactionRecord txnRecord,
			Auth authReqObj) {
		Map<String, String> mapResult = new HashMap<>();
		String kycResponse = null;
		String modifiedKyc = null;
		byte[] encodeAuthXml = signedXml.getBytes();
		kycReq.setRad(encodeAuthXml);
		try {
			StringWriter sw = new StringWriter();
			Result result = new StreamResult(sw);
			marshaller.marshal(kycReq, result);
			modifiedKyc = sw.toString();
			mapResult = getMapResult("y", modifiedKyc);
		} catch (Exception excp) {
			logger.info("Marshalling in Class Name:KycRequestProcessor Method Name: prepareKycXml", excp);
			kycResponse = errorResponse("KYC_REQUEST_FORMATION_ERROR", true, txnRecord, kycReq, authReqObj);
			mapResult = getMapResult("n", kycResponse);
		}
		return mapResult;
	}

	/**
	 * 
	 * @param kycResp
	 * @param txnRecord
	 * @return
	 */
	private String updateResponse(KycTransactionRecord txnRecord, Kyc kycReq, Auth authReq) {
		String kycResponse = null;
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		txnRecord.setRespForwardTime(respForwardTime);
		daoOperations.insertASAKycRecord(kycReq, authReq, txnRecord);
		return kycResponse;
	}

	/**
	 * 
	 * @param errMsg
	 * @param logInDB
	 * @param txnRecord
	 * @return
	 */
	private String errorResponse(String errMsg, boolean logInDB, KycTransactionRecord txnRecord, Kyc kycReqObj,
			Auth authReqObj) {
		String errorCode = null;
		String kycResponse = null;
		String status = null;
		txnRecord.setRet(YesNoType.N.value());
		status = "-1";
		String txn = txnRecord.getTransactionId();
		txnRecord.setResponseTs(Utility.generateTimeStamp());
		txnRecord.setTs(new Timestamp(txnRecord.getResponseTs().toGregorianCalendar().getTimeInMillis()));
		errorCode = environment.getProperty(errMsg);
		txnRecord.setErrorCode(errorCode);
		txnRecord.setStatus(status);
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		txnRecord.setRespForwardTime(respForwardTime);
		kycResponse = errorHandler.errorResponse(txn, errorCode, txnRecord.getResponseTs());
		if (logInDB) 
		{
			daoOperations.insertASAKycRecord(kycReqObj, authReqObj, txnRecord);
		}
		logger.info("Error returned in Class Name:KycRequestProcessor Method Name: errorResponse:--" + "Txn:--" + txn
				+ "--ErrorCode:--" + errorCode+ "--ErrorMessage:--" + errMsg);
		return kycResponse;
	}

	/**
	 * 
	 * @param reqXMLForward
	 * @param txnRecord
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> callWebService(String reqXMLForward, KycTransactionRecord txnRecord) {
		Map<String, String> mapResult = new HashMap<>();
		Map<String, String> response = new HashMap<>();
		switch (txnRecord.getServiceType()) {
		case UIDAI_SERVICE:
			txnRecord.setReqForwardTime(new Timestamp(System.currentTimeMillis()));
			response = callRestService.callRestMethod(txnRecord.getUidaiUrl(), reqXMLForward,
					txnRecord.getTransactionId());
			txnRecord.setRespReceiptTime(new Timestamp(System.currentTimeMillis()));
			break;
		case CRYPTO_SERVICE:
			response = callRestService.callRestMethodCrypto(txnRecord.getCryptoServiceUrl(), reqXMLForward);
			break;
		default:
			break;
		}
		if (response.get("status").equalsIgnoreCase("n")) {
			mapResult = getMapResult("n", response.get("code"));
		} else {
			mapResult = getMapResult("y", response.get("data"));
		}
		return mapResult;
	}

	/**
	 * 
	 * @param cryptoObj
	 * @return
	 * @throws Exception
	 */
	private String marshalCryptoXml(Request cryptoObj) throws Exception {
		String cryptoXml = null;
		;
		OutputStream out = new ByteOutputStream();
		Writer writer = null;
		writer = new OutputStreamWriter(out, "UTF-8");
		StreamResult result = new StreamResult(writer);
		marshaller.marshal(cryptoObj, result);
		cryptoXml = out.toString();
		return cryptoXml;
	}

	/**
	 * 
	 * @param status
	 * @param data
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getMapResult(String status, Object data, Resp resp) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", status);
		resultMap.put("data", data);
		resultMap.put("resp", resp);
		return resultMap;
	}
}