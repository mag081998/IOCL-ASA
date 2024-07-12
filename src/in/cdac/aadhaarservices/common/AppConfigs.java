package in.cdac.aadhaarservices.common;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * This class loads all the configuration required for the web-service
 * @author root
 *
 */
@Configuration
 public class AppConfigs 
{
	@Autowired 
	MasterConfig masterConfigDB;
	@Autowired
	Environment environment;
	protected String commonSchemaName=null;
	protected boolean syntactValidate=false;
	protected boolean semanticValidate=false;
	protected boolean verifyLk=false;
	protected boolean verifyCi=false;
	protected boolean verifySign=false;
	protected boolean signrequired=false;
	protected String asaLicenseType=null;
	protected String ksaLicenseType=null;
	protected String aadhaarVaultUrl=null;
	protected String uidaiOtpUrl=null;
	protected String uidaiAuthUrl=null;
	protected String uidaiEkycUrl=null;
	protected String uidaiBfdUrl=null;
	protected String cryptoServiceBaseUrl = null;
	public static final int UIDAI_SERVICE = 1;
	public static final int CRYPTO_SERVICE = 2;
	public static final String SIGNING = "sign";
	public static final String DECRYPTION = "decrypt";
	public static final String HARD = "hard";
	/**
	 * This method loads the values from the property file
	 */
	@PostConstruct
	protected void getPropertyConfigurations() 
	{
		commonSchemaName=environment.getProperty("commonschemaname");
		syntactValidate=Boolean.parseBoolean(environment.getProperty("xmlsyntaxvalidation"));
		semanticValidate=Boolean.parseBoolean(environment.getProperty("xmlsymanticvalidation"));
		verifyLk=Boolean.parseBoolean(environment.getProperty("verifylk"));
		verifyCi=Boolean.parseBoolean(environment.getProperty("verifyCi"));
		verifySign=Boolean.parseBoolean(environment.getProperty("verifysign"));
		signrequired=Boolean.parseBoolean(environment.getProperty("signrequired"));
		asaLicenseType=environment.getProperty("asalicensetype");
		ksaLicenseType=environment.getProperty("ksalicensetype");
	}
	
	/**
	 * This method loads the configurations from the DB into context
	 */
	@PostConstruct
	protected void getDBConfigurations() 
	{
		uidaiOtpUrl = this.masterConfigDB.getConfigPara(ConfigParaKey.UIDAI_OTP_URL).getParavalue();
		uidaiAuthUrl = this.masterConfigDB.getConfigPara(ConfigParaKey.UIDAI_AUTH_URL).getParavalue();
		uidaiEkycUrl = this.masterConfigDB.getConfigPara(ConfigParaKey.UIDAI_EKYC_URL).getParavalue();
		cryptoServiceBaseUrl=this.masterConfigDB.getConfigPara(ConfigParaKey.CRYPTO_URL).getParavalue();
		aadhaarVaultUrl=this.masterConfigDB.getConfigPara(ConfigParaKey.AADHAAR_VAULT_URL).getParavalue();
		uidaiBfdUrl=this.masterConfigDB.getConfigPara(ConfigParaKey.UIDAI_BFD_URL).getParavalue();
	}
}
