package DAL;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.crashlytics.android.Crashlytics;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CourierTran {
	public String RemoteCourierTranID = "";
	public Float CourierID = 0F;
	public Float DriverID = 0F;
	public String TransType = "";
	public String TransReceiverName = "";
	public Date TransDate = new Date();
	public Date DateCreated = new Date();
	public String TransReason = "";
	public Float TransNotDeliveredTypeID = 0F;
	public Bitmap TransSignature;
	public Float ReceivedCashValue = 0F;
	public Float ReceivedCheckValue = 0F;
	public String OrderVoucherNumber = "";
	public int ParcelStatus = 0;
	public double DeliveryLatitude = 0;
	public double DeliveryLongitude = 0;
	
	public CourierTran()
	{
		try {
			RemoteCourierTranID = new MyUUID().getString();
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<CourierTran> GetCourierTrans(Context context, float courierID)
	{
		ArrayList<CourierTran> res = new ArrayList<CourierTran>();
		Connection conn = null;
		try {
			conn = DBUtil.CreateConnection(context);
			
			if(conn == null)
				return null;
			
			PreparedStatement ps; 
			
				ps = conn
						.prepareStatement(" SELECT RemoteCourierTranID, CourierID "
								+ ", DriverID "
								+ ", TransType "
								+ ", TransReceiverName "
								+ ", TransDate "
								+ ", TransReason "
								+ ", TransNotDeliveredTypeID "
								+ ", ReceivedCashValue "
								+ ", ReceivedCheckValue "								
								+ ", OrderVoucherNumber "
								+ ", TransSignature "
								+ " FROM RemoteCourierTrans"
								+ " WHERE CourierID = ? ORDER BY DateCreated, TransDate");
			
			ps.set(1, courierID);
			try {
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					CourierTran tran = new CourierTran();
					tran.Fetch(rs);
					res.add(tran);
				}
				rs.close();
				
			} finally {
				ps.close();
			}			
		}
		catch (ULjException e1) 
		{
			Crashlytics.logException(e1);
		}
		finally 
		{
			if (conn != null)
				try {
					conn.release();
				} catch (ULjException e) {
					Crashlytics.logException(e);
				}
		}	
		
		return res;
	}
	
	public void LoadLastTran(Context context, float courierID)
	{		
		Connection conn = null;
		try {
			conn = DBUtil.CreateConnection(context);
			
			if(conn == null)
				return;
			
			PreparedStatement ps;
			
				ps = conn
						.prepareStatement(" SELECT TOP 1 RemoteCourierTranID, CourierID "
								+ ", DriverID "
								+ ", TransType "							
								+ ", TransReceiverName "
								+ ", TransDate "
								+ ", TransReason "
								+ ", TransNotDeliveredTypeID "
								+ ", ReceivedCashValue "
								+ ", ReceivedCheckValue "
								+ ",OrderVoucherNumber "
								//+ ",ParcelStatus " 
								+ ", TransSignature " + " FROM RemoteCourierTrans"
								+ " WHERE CourierID = ? ORDER BY CourierTransID DESC");
			
			ps.set(1, courierID);
			try {
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					Fetch(rs);
				}
				rs.close();
				
			} finally {
				ps.close();
			}			
		}
		catch (ULjException e1) 
		{
			Crashlytics.logException(e1);
		}
		finally 
		{
			if (conn != null)
				try {
					conn.release();
				} catch (ULjException e) {
					Crashlytics.logException(e);
				}
		}	
	}

	public void Fetch(ResultSet rs) throws ULjException {
		RemoteCourierTranID = rs.getString("RemoteCourierTranID");
		CourierID = rs.getFloat("CourierID");
		DriverID = rs.getFloat("DriverID");
		TransType = rs.getString("TransType");
		TransReceiverName = rs.getString("TransReceiverName");
		TransDate = rs.getDate("TransDate");
		TransReason = rs.getString("TransReason");
		TransNotDeliveredTypeID = rs.getFloat("TransNotDeliveredTypeID");
		ReceivedCashValue = rs.getFloat("ReceivedCashValue");
		ReceivedCheckValue = rs.getFloat("ReceivedCheckValue");
		OrderVoucherNumber = rs.getString("OrderVoucherNumber");
		//ParcelStatus = rs.getInt("ParcelStatus");

		
			byte[] signatureBytes = rs.getBytes("TransSignature");
			try {
				if (signatureBytes.length > 0) {
					TransSignature = BitmapFactory.decodeByteArray(
							signatureBytes, 0, signatureBytes.length);
				} else {
					TransSignature = null;
				}
			} catch (Exception ex) {
				TransSignature = null;
			}
		
	}

	public void InsertCourierTrans(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {

			DateCreated = new Date();
			StringBuffer sb = new StringBuffer( 
					"INSERT INTO RemoteCourierTrans  (RemoteCourierTranID, CourierID, DriverID, TransType, TransReceiverName, "
							+ "TransDate, TransReason, TransNotDeliveredTypeID, ReceivedCashValue,"
							+ "ReceivedCheckValue, DeliveryLatitude, DeliveryLongitude, DateCreated, TransSignature) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, RemoteCourierTranID);
			ps.set(2, CourierID);
			ps.set(3, DriverID);
			ps.set(4, TransType);// doc type
			ps.set(5, TransReceiverName);// trans type 
			ps.set(6, TransDate);
			ps.set(7, TransReason);
			ps.set(8, TransNotDeliveredTypeID);
			ps.set(9, ReceivedCashValue);
			ps.set(10, ReceivedCheckValue);
			ps.set(11, DeliveryLatitude);
			ps.set(12, DeliveryLongitude);
			ps.set(13, DateCreated);
			
			if (TransSignature != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransSignature.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm
																				// is
																				// the
																				// bitmap 
																				// object
				byte[] bytesToSave = baos.toByteArray();
				ps.set(14, bytesToSave);
			} else {
				ps.setNull(14);
			}

			ps.execute();
			ps.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		} finally {
			conn.release();
		}

	}
	
	public void UpdateCourierTrans(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {

			StringBuffer sb = new StringBuffer( 
					"UPDATE RemoteCourierTrans  SET CourierID = ?, DriverID = ?, TransType = ? , TransReceiverName = ?, "
							+ "TransDate = ?, TransReason = ?, TransNotDeliveredTypeID = ?, ReceivedCashValue = ?,"
							+ "ReceivedCheckValue = ?, DeliveryLatitude = ?, DeliveryLongitude = ?, TransSignature = ? "
							+ " WHERE RemoteCourierTranID = ?");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, CourierID);
			ps.set(2, DriverID);
			ps.set(3, TransType);// doc type
			ps.set(4, TransReceiverName);// trans type 
			ps.set(5, TransDate);
			ps.set(6, TransReason);
			ps.set(7, TransNotDeliveredTypeID);
			ps.set(8, ReceivedCashValue);
			ps.set(9, ReceivedCheckValue);
			ps.set(10, DeliveryLatitude);
			ps.set(11, DeliveryLongitude);
			
			if (TransSignature != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransSignature.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm
																				// is
																				// the
																				// bitmap 
																				// object
				byte[] bytesToSave = baos.toByteArray();
				ps.set(12, bytesToSave);
			} else {
				ps.setNull(12);
			}
			ps.set(13, RemoteCourierTranID);

			ps.execute();
			ps.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		} finally {
			conn.release();
		}

	}
}
