package in.cdac.aadhaarservices.daooperations;

/**
 * Common DB Queries to be used by Auth, OTP and e-KYC classes
 * @author root
 *
 */
 public final class CommonDBQueries 
{
	/**
	 * 
	 * @param schema
	 * @return
	 */
	public static String getSchemaName( String schema )
	{
		final String schemaName = "SELECT d.asa_code "
									+ "FROM	"+schema+".dept_registration d"
									+ " WHERE d.aua_code = ? and d.is_aua_active = true and d.is_asa_active = true;";
		return schemaName;
	}

	public static String getAUADetailsQuery( String schema )
	{
		final String auaDetails = "SELECT a.aua_code, a.aua_valid_till, a.is_auth_allowed, a.is_otp_allowed, a.is_ekyc_allowed,a.lk,a.lk_expiry "
									+ "FROM " +schema+".aua_details a "
									+ "WHERE a.aua_code = ? and a.is_active = 'true';";
		return auaDetails;
	}
	
	/**
	 * 
	 * @param schema
	 * @return
	 */
	public static String getASALkDetailsQuery( String schema )
	{
		String auaLKDetails = "SELECT s.asa_license_key, s.asa_valid_till, s.license_type "
									+"FROM " +schema +".asa_lk_details s "
									+"WHERE s.is_active = 'true' and s.is_dept_activated = 'true' "
									+ "and s.asa_code = ? and s.license_type = ?;";

		return auaLKDetails;
	}

	/**
	 * 
	 * @param schema
	 * @return
	 */
	public static String getSignDetQuery(String schema) 
	{
		return "SELECT s.aua_code, s.is_sign_at_asa, s.decrypt_ekyc_at_asa, "
				+ "s.is_struid " 
				+"FROM " +schema +".sign_certificate_details s "
				+"WHERE s.aua_code = ?;";
	}
	
	/**
	 * 
	 * @param schema
	 * @return
	 */
	public static String getCryptoACLKCertQuery(String schema) 
	{
		return "SELECT lkdet.aua_code, lkdet.crypto_ac, lkdet.crypto_lk, lkdet.crypto_lk_expiry_date, "
				+"cdet.key_identifier, cdet.key_algorithm, cdet.crypto_opr, cdet.token_type " 
				+"FROM "+schema+".crypto_cert_details cdet INNER JOIN "+schema+".crypto_lk_details lkdet ON cdet.crypto_ac = lkdet.crypto_ac " + 
				"WHERE lkdet.aua_code = ? and lkdet.is_crypto_lk_active = 'true' and cdet.crypto_opr = ?;";
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getMasterConfigDBQuery()
	{
		return "Select para_name,para_value from public.m_config_para";
	}
	
public static String getTxnIdInsertQuery(String reqType) 
{
		
		String tablename=null;
		if(reqType.equalsIgnoreCase("otp")) 
		{
			tablename="otp_txn";
		}
		else if(reqType.equalsIgnoreCase("auth")) 
		{
			tablename="auth_txn";
		}
		else if(reqType.equalsIgnoreCase("kyc")) 
		{
			tablename="kyc_txn";
		}
		else if(reqType.equalsIgnoreCase("bfd")) 
		{
			tablename="bfd_txn";
		}
		return "INSERT INTO "+tablename+" (txn)VALUES( ?)";
	}
}