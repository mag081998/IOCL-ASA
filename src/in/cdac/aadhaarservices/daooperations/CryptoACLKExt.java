package in.cdac.aadhaarservices.daooperations;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

 public final class CryptoACLKExt implements ResultSetExtractor<CryptoACLKCert>
{
	@Override
	public CryptoACLKCert extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
		CryptoACLKCert cryptoDet = new CryptoACLKCert();
		while ( rs.next() )
		{
			cryptoDet.setAc( rs.getString("aua_code") );
			cryptoDet.setCrypto_ac(rs.getString("crypto_ac"));
			cryptoDet.setCrypto_lk(rs.getString("crypto_lk"));
			cryptoDet.setCrypto_lk_expiry_date(rs.getDate("crypto_lk_expiry_date"));
			cryptoDet.setKey_identifier(rs.getString("key_identifier"));
			cryptoDet.setKey_algorithm(rs.getString("key_algorithm"));
			cryptoDet.setCrypto_opr(rs.getString("crypto_opr"));
			cryptoDet.setToken_type(rs.getString("token_type"));
		}
		return cryptoDet;
	}
}