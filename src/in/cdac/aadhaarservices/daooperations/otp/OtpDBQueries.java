package in.cdac.aadhaarservices.daooperations.otp;

/**
 * 
 * @author root
 *
 */
public final class OtpDBQueries {
	/**
	 * 
	 * @param schema
	 * @return
	 */

	public static String insertASAOtpQuery(String schema) {
		String insertOtpQuery = "INSERT INTO " + schema
				+ ".otp_log( txn, aua_code, ver, sa, type, request_receipt_time, packet_request_time, client_ip,"
				+ "request_forward_time, response_receipt_time,response_forward_time,packet_response_time,ret,resp_code,err,server_ip,ch) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?::inet,?,?,?,?,?,?,?,?::inet,?)";
		return insertOtpQuery;
	}
}
