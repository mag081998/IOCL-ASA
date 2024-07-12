package in.cdac.aadhaarservices.daooperations.bfd;

/**
 * @author root
 */
public final class BfdDBQueries {

	public static String insertASAAuthQuery(String schema) {

		final String insertAuthQuery = "INSERT INTO " + schema + ".bfd_log("
				+ "txn, aua_code, ver, pi, pa, pfa, bio, bt, pin, otp," + "sa, rc,"
				+ "request_receipt_time, client_ip, type"
				+ ",res_code,actn,ret,err,request_forward_time,response_receipt_time,response_forward_time,packet_response_time,server_ip,ci)"
				+ "VALUES ( ?, ?," + "?, ?, ?, ?, ?, ?, ?, ?," + "?, ?, "
				+ " ?, ?::inet, ?,?,?,?,?,?,?,?,?,?::inet,? )";
		return insertAuthQuery;
	}
}
