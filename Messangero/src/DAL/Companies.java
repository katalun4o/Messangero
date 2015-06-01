package DAL;

import android.content.Context;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.common.app_settings;

public class Companies
{	
	public static void LoadCompanyPhone(Context ctx)
	{
		Connection conn = DBUtil.CreateConnection(ctx);
		PreparedStatement ps;
		try
		{
			ps = conn.prepareStatement("SELECT TOP 1 comp_phone FROM Companies");

			try
			{
				ResultSet rs = ps.executeQuery();
				if (rs.next())
				{
					//Comp_phone = rs.getString(1);
					app_settings.CompanyPhoneNumber = rs.getString(1);
				}
				rs.close();
			} finally
			{
				ps.close();
				conn.release();
			}			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
