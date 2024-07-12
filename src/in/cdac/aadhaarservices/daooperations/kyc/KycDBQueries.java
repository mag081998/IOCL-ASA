
package in.cdac.aadhaarservices.daooperations.kyc;

/**
 * e-KYC DB Queries
 * @author root
 *
 */
public final class KycDBQueries 
{
	
	
	
	public static String insertASAKycQuery(String schema)
	{
		final String insertKycQuery="INSERT INTO " +schema+".kyc_log("+
				"ver, ra, rc, lr, de, pfr, txn, auth_rc, aua_code, sa, auth_ver, "
				+ "auth_txn, pi, pa, pfa, bio, bt, pin, otp, "
				+ "request_receipt_time, client_ip, type "
				+ ",code, status,ts,ko,actn,ret,err,request_forward_time,response_receipt_time,response_forward_time,response_time_cidr,auth_actn,auth_err,kyc_err,kyc_ret,server_ip,ci)"
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?::inet, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?::inet,? )";
		return insertKycQuery; 
	}

	/**
	 * Update Query for logging e-KYC metadata
	 * @param schema
	 * @return
	 */
	public static String updateAuaKycQuery(String schema)	
	{
		final String updateKycQuery = "UPDATE "+ schema+".kyc_log "+
				"SET code=?, status = ?, ts = ?, ko=?, "+
				"actn=?, ret=?, err=?,"+ 
			       "request_forward_time=?, response_receipt_time=?, response_forward_time=?,"+
			       "response_time_cidr=?, auth_actn=?, auth_err=?, kyc_err=?, kyc_ret = ? WHERE txn=?";
		return updateKycQuery; 

	}
}
