package in.cdac.aadhaarservices.common;

/**
 * Enum ConfigParaKey will be used to store m_config_para
 */
public enum ConfigParaKey 
{
	//ASA AC Code
	ASA_AC_CODE("asa_ac_code"),
	//Aadhaar Vault Url
	AADHAAR_VAULT_URL("aadhaar_vault_url"),
	//UIADI OTP Url string
	UIDAI_OTP_URL("uidai_otp_url"),
	//UIDAI Auth Url   
	UIDAI_AUTH_URL("uidai_auth_url"),
	//UIDAIe-KYC Url
	UIDAI_EKYC_URL("uidai_ekyc_url"),
	//Crypto Service URL
	CRYPTO_URL("crypto_url"),
	//UIDAI BFD URL
	UIDAI_BFD_URL("uidai_bfd_url");

	
	private final String configParaName;
	/**
	 * Instantiates a new config para key.
	 * @param configParaName
	 */
	ConfigParaKey(String configParaName) 
	{
		this.configParaName = configParaName;
	}

	/**
	 * Gets the config para name.
	 * @return the config para name
	 */
	public String getConfigParaName() 
	{
		return configParaName;
	}

	/**
	 * Gets the config para.
	 * @param configParaName
	 * @return config para
	 */
	public static ConfigParaKey getConfigPara(String configParaName) 
	{
		for (ConfigParaKey configParaKey : ConfigParaKey.values()) 
		{
			if (configParaKey.configParaName.equals(configParaName)) 
			{
				return configParaKey;
			}
		}
		return null;
	}
}
