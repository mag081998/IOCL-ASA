package in.cdac.aadhaarservices.daooperations;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public final class AUADetailsExtractor implements ResultSetExtractor<AUADetails> 
{

	@Override
	public AUADetails extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
		AUADetails auadet = new AUADetails();;
		while ( rs.next() )
		{
			auadet.setAua_code(rs.getString("aua_code"));
			auadet.setAua_valid_till(rs.getDate("aua_valid_till"));
			auadet.setIs_auth_allowed(rs.getBoolean("is_auth_allowed"));
			auadet.setIs_otp_allowed(rs.getBoolean("is_otp_allowed"));
			auadet.setIs_ekyc_allowed(rs.getBoolean("is_ekyc_allowed"));
			auadet.setLk(rs.getString("lk"));
			auadet.setLk_expiry(rs.getDate("lk_expiry"));
		}
		return auadet;
	}

}
