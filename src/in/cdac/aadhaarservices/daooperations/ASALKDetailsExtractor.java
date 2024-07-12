package in.cdac.aadhaarservices.daooperations;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * This class sets the License Key and Signing Details required by AUA and ASA
 * @author root
 *
 */
 public final class ASALKDetailsExtractor implements ResultSetExtractor<LKDetails>
{
	public LKDetails extractData(ResultSet rs)throws SQLException, DataAccessException
	{
		LKDetails lkDet = new LKDetails();
		while ( rs.next() )
		{
			lkDet.setAsa_valid_till(rs.getDate("asa_valid_till"));
			lkDet.setAsa_license_key(rs.getString("asa_license_key"));
			lkDet.setLicense_type(rs.getString("license_type"));
		}
		return lkDet;
	}
}