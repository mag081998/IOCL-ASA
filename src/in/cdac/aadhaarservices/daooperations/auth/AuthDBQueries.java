package in.cdac.aadhaarservices.daooperations.auth;

/**
 * @author root
 */
public  class AuthDBQueries 
{
	
	public static String insertASAAuthQuery(String schema)
	{
		
		final String insertAuthQuery="INSERT INTO " +schema+".auth_log("+
	            "txn, aua_code, ver, pi, pa, pfa, bio, bt, pin, otp,"+
	            "sa, rc,"+
	            "request_receipt_time, client_ip, type"+
	            ",res_code,actn,ret,err,request_forward_time,response_receipt_time,response_forward_time,packet_response_time,server_ip,ci)"+
	    "VALUES ( ?, ?,"+ 
	            "?, ?, ?, ?, ?, ?, ?, ?,"+
	            "?, ?, "+
	            " ?, ?::inet, ?,?,?,?,?,?,?,?,?,?::inet,? )";
		return insertAuthQuery; 
	}
	
	/**
	 * Update Query for Authentication Metadata Logging
	 * @param schema
	 * @return
	 */
	public static String updateAuthQuery(String schema)
	{
		final String updateAuthQuery= "UPDATE "+ schema+".auth_log "+
				  "SET res_code=?, "+
				       "actn=?, ret=?, err=?,"+ 
				       "request_forward_time=?, response_receipt_time=?, response_forward_time=?,"+
				       "packet_response_time=? WHERE txn=?";
				return updateAuthQuery; 
	}
}
