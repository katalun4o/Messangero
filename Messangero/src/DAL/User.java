package DAL;

import android.content.Context;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.common.app_settings;

public class User {

	public static void UpdateUserGCM_ID(Context ctx, String gcm_id) {
		Connection conn = DBUtil.CreateConnection(ctx);
		if(conn == null)
			return;
		try {
			
			StringBuffer sb = new StringBuffer("UPDATE rUsers  " + "SET "
					+ "GCM_ID = ?  WHERE UserID = ? ");
			PreparedStatement ps;
			
			ps = conn.prepareStatement(sb.toString());

			ps.set(1, gcm_id);
			ps.set(2, app_settings.CurrentUserID); 

			ps.execute();
			ps.close();
			conn.commit();
			
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.release();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
