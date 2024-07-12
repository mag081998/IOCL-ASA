package in.cdac.aadhaarservices.daooperations;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public final class SignDetExt implements ResultSetExtractor<SignDet>
{
	@Override
	public SignDet extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
		SignDet signdet = new SignDet();
		while ( rs.next() )
		{
			signdet.setAc( rs.getString("aua_code") );
			signdet.setIs_sign_at_asa( rs.getBoolean("is_sign_at_asa") );
			signdet.setDecrypt_ekyc_at_aua( rs.getBoolean("decrypt_ekyc_at_asa") );
			signdet.setIs_struid( rs.getBoolean("is_struid") );
		}
		return signdet;
	}
}