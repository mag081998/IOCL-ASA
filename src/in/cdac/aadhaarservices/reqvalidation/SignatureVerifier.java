package in.cdac.aadhaarservices.reqvalidation;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public final  class SignatureVerifier {

	private static final Logger logger = LogManager.getLogger(SignatureVerifier.class);
	
	final private static String ELEMENT_SIGN_TAG_NAME="Signature";
	final private static String PROVIDER_TYPE="BC";
	final private static String CERT_TYPE="X.509";
	final private static String DOM="DOM";

	static 
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public static Map<String, String> verify(String signedXml, byte[] publicKey) {

		boolean verificationResult = false;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document signedDocument = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(signedXml)));

			NodeList nl = signedDocument.getElementsByTagNameNS(XMLSignature.XMLNS, ELEMENT_SIGN_TAG_NAME);
			if (nl.getLength() == 0) 
			{
				//throw new IllegalArgumentException("Cannot find Signature element");
				//return verificationResult =false;
				
				return getMapResult("n", "SIGN_VERIFICATION_FAILED");
			}

			XMLSignatureFactory fac = XMLSignatureFactory.getInstance(DOM);

			DOMValidateContext valContext = new DOMValidateContext(getCertificateFromBytes(publicKey).getPublicKey(), nl.item(0));
			
			
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);
			
			verificationResult = signature.validate(valContext);
			
			if (verificationResult)
			{
				return getMapResult("y", "");
			}
			else 
			{
				return getMapResult("n", "SIGN_VERIFICATION_FAILED");
			}
			
		} 
		catch(CertificateExpiredException e)
		{
			logger.info("Error while verifying digital siganature" + e.getMessage());
			
			return getMapResult("n", "SIGN_CERTIFICATE_EXPIRED");
		}
		catch (Exception e) {
			logger.info("Error while verifying digital siganature" + e.getMessage());
			//e.printStackTrace();
			//return verificationResult = false;
			return getMapResult("n", "SIGN_VERIFICATION_FAILED");
		}

		//return verificationResult;
		
	}

	private static X509Certificate getCertificateFromBytes(byte[] certificate) throws GeneralSecurityException, IOException {
		ByteArrayInputStream fis = null;
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance(CERT_TYPE, PROVIDER_TYPE);
			fis = new ByteArrayInputStream(certificate);
			X509Certificate x509 = (X509Certificate) certFactory.generateCertificate(fis);
			x509.checkValidity();
			return x509;
		} finally {
			if (fis != null) {
				fis.close();
			}
		}

	}
	
	public static Map<String, String> getMapResult(String status, String code) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("status", status);
		resultMap.put("code", code);
		return resultMap;
	}

}
