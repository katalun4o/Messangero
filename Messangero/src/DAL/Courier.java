package DAL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;
import com.ianywhere.ultralitejni12.UUIDValue;

public class Courier {
	private Float CourierID = 0F;
	private String VouchNumber = "";
	private Date VouchDate = new Date();
	private String VouchMemo = "";
	private String SenderName = "";
	private String SenderCity = "";
	private String SenderArea = "";
	private String SenderAddress = "";
	private String SenderGSM = "";
	private String ReceiverName = "";
	private String ReceiverArea = "";
	private String ReceiverCity = "";
	private String ReceiverAddress = "";
	private String ReceiverGSM = "";
	private int VouchCount = 0;
	private String DocType = "1";
	private Float DriverID = 0F;
	private String TransType = "";
	private String TransReceiverName = "";
	private Date TransDate = new Date();
	private Date DeliveryDate = new Date();
	private String TransReason = "";
	private Float TransNotDeliveredTypeID = 0F;
	private Bitmap TransSignature;
	private Float ExpCashValue = 0F;
	private Float ExpCheckValue = 0F;
	private int ExpCheckCount = 0;
	private Float RealCashValueReceived = 0F;
	private Float RealCheckValueReceived = 0F;
	private Integer RealChecksCountReceived = 0;
	private String OrderVoucherNumber = "";
	private Float BranID;
	private Date DateCreated = new Date();
	public double Latitude = 0;
	public double Longitude = 0;
	public String DeliveryHour = "";
	public int ParcelStatus = 0;
	public double DeliveryLatitude = 0;
	public double DeliveryLongitude = 0;
	public double Distance = 0;
	public String CustomerType = "";
	public double IfIsCreditValue = 0;
	public ArrayList<ChargeTrans> Charges;
	public int OrderIndex = 0;
	//public String ImageName = "";

	public boolean IsSelected = false;

	public boolean GetSignature = true;

	public Float getCourierID() {
		return CourierID;
	}

	public void setCourierID(Float courierID) {
		CourierID = courierID;
	}

	public String getVouchNumber() {
		return VouchNumber;
	}

	public void setVouchNumber(String vouchNumber) {
		VouchNumber = vouchNumber;
	}

	public String getVouchMemo() {
		return VouchMemo;
	}

	public void setVouchMemo(String vouchMemo) {
		VouchMemo = vouchMemo;
	}

	public Date getVouchDate() {
		return VouchDate;
	}

	public void setVouchDate(Date vouchDate) {
		this.VouchDate = vouchDate;
	}

	public String getTransType() {
		return TransType;
	}

	public void setTransType(String transType) {
		TransType = transType;
	}

	public String getSenderName() {
		if (SenderName == null)
			return "";
		return SenderName;
	}

	public void setSenderName(String senderName) {
		SenderName = senderName;
	}

	public String getSenderCity() {
		if (SenderCity == null)
			return "";
		return SenderCity;
	}

	public void setSenderCity(String senderCity) {
		SenderCity = senderCity;
	}

	public String getSenderAddress() {
		if (SenderAddress == null)
			return "";
		return SenderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		SenderAddress = senderAddress;
	}

	public String getReceiverName() {
		if (ReceiverName == null)
			return "";
		return ReceiverName;
	}

	public void setReceiverName(String receiverName) {
		ReceiverName = receiverName;
	}

	public String getReceiverCity() {
		if (ReceiverCity == null)
			return "";
		return ReceiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		ReceiverCity = receiverCity;
	}

	public String getSenderArea() {
		if (SenderArea == null)
			return "";
		return SenderArea;
	}

	public void setSenderArea(String senderArea) {
		SenderArea = senderArea;
	}

	public String getReceiverArea() {
		if (ReceiverArea == null)
			return "";
		return ReceiverArea;
	}

	public void setReceiverArea(String receiverArea) {
		ReceiverArea = receiverArea;
	}

	public String getReceiverAddress() {
		if (ReceiverAddress == null)
			return "";
		return ReceiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		ReceiverAddress = receiverAddress;
	}

	public int getVouchCount() {
		return VouchCount;
	}

	public void setVouchCount(int vouchCount) {
		VouchCount = vouchCount;
	}

	public String getDocType() {
		return DocType;
	}

	public void setDocType(String docType) {
		DocType = docType;
	}

	public Float getExpCashValue() {
		return ExpCashValue;
	}

	public void setExpCashValue(Float expCashValue) {
		ExpCashValue = expCashValue;
	}

	public Float getExpCheckValue() {
		return ExpCheckValue;
	}

	public void setExpCheckValue(Float expCheckValue) {
		ExpCheckValue = expCheckValue;
	}

	public int getExpCheckCount() {
		return ExpCheckCount;
	}

	public void setCheckCount(int expCheckCount) {
		ExpCheckCount = expCheckCount;
	}

	public Bitmap getTransSignature() {
		return TransSignature;
	}

	public void setTransSignature(Bitmap transSignature) {
		TransSignature = transSignature;
	}

	public String getTransReceiverName() {
		return TransReceiverName;
	}

	public void setTransReceiverName(String transReceiverName) {
		TransReceiverName = transReceiverName;
	}

	public Float getRealCashValueReceived() {
		return RealCashValueReceived;
	}

	public void setRealCashValueReceived(Float realCashValueReceived) {
		RealCashValueReceived = realCashValueReceived;
	}

	public Float getRealCheckValueReceived() {
		return RealCheckValueReceived;
	}

	public void setRealCheckValueReceived(Float realCheckValueReceived) {
		RealCheckValueReceived = realCheckValueReceived;
	}

	public Integer getRealChecksCountReceived() {
		return RealChecksCountReceived;
	}

	public void setRealChecksCountReceived(Integer realChecksCountReceived) {
		RealChecksCountReceived = realChecksCountReceived;
	}

	public Date getDeliveryDate() {
		return DeliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		DeliveryDate = deliveryDate;
	}

	public Float getDriverID() {
		return DriverID;
	}

	public void setDriverID(Float driverID) {
		DriverID = driverID;
	}

	public Date getTransDate() {
		return TransDate;
	}

	public void setTransDate(Date transDate) {
		TransDate = transDate;
	}

	public String getTransReason() {
		return TransReason;
	}

	public void setTransReason(String transReason) {
		TransReason = transReason;
	}

	public Float getTransNotDeliveredTypeID() {
		return TransNotDeliveredTypeID;
	}

	public void setTransNotDeliveredTypeID(Float transNotDeliveredTypeID) {
		TransNotDeliveredTypeID = transNotDeliveredTypeID;
	}

	public String getOrderVoucherNumber() {
		return OrderVoucherNumber;
	}

	public void setOrderVoucherNumber(String orderVoucherNumber) {
		OrderVoucherNumber = orderVoucherNumber;
	}

	public Float getBranID() {
		return BranID;
	}

	public void setBranID(Float branID) {
		BranID = branID;
	}

	public Date getDateCreated() {
		return DateCreated;
	}

	public String getSenderGSM() {
		if (SenderGSM == null)
			return "";
		return SenderGSM;
	}

	public void setSenderGSM(String senderGSM) {
		SenderGSM = senderGSM;
	}

	public String getReceiverGSM() {
		if (ReceiverGSM == null)
			return "";
		return ReceiverGSM;
	}

	public void setReceiverGSM(String receiverGSM) {
		ReceiverGSM = receiverGSM;
	}

	public void setDateCreated(Date dateCreated) {
		DateCreated = dateCreated;
	}

	public Courier() {
	}

	public void LoadCourier(Context context, String number) throws ULjException {
		Connection conn = DBUtil.CreateConnection(context);
		try {
			PreparedStatement ps = conn
					.prepareStatement(" SELECT CourierID"
							+ " ,VouchNumber"
							+ " ,VouchDate"
							+ " ,VouchMemo"
							+ " ,SenderName"
							+ " ,SenderArea"
							+ " ,SenderCity"
							+ " ,SenderAddress"
							+ " ,SenderGSM"
							+ " ,ReceiverName"
							+ " ,ReceiverArea"
							+ " ,ReceiverCity"
							+ " ,ReceiverAddress"
							+ " ,ReceiverGSM"
							+ " ,VouchCount"
							+ " ,DocType"
							+ " ,DriverID"
							+ " ,TransType"
							+ " ,TransReceiverName"
							+ " ,TransDate"
							+ " ,DeliveryDate"
							+ " ,TransReason"
							+ " ,TransNotDeliveredTypeID"
							+ " ,ExpCashValue"
							+ " ,ExpCheckValue"
							+ " ,ExpCheckCount"
							+ " ,ReceivedCashValue"
							+ " ,ReceivedCheckValue"
							+ " ,ReceivedCheckCount"
							+ " ,BranID"
							+ " ,OrderVoucherNumber "
							+ " ,DateCreated "
							+ ",Latitude "
							+ ",Longitude "
							+ ",DeliveryHour "
							+ ",ParcelStatus "
							+ ",CourierCustomerType "
							+ ",IfIsCreditValue "
							+ ", (6371 * ACOS( COS( RADIANS(37.996569) ) * COS( RADIANS( Latitude ) ) * COS( RADIANS( Longitude ) - RADIANS(23.673277) )"
							+ " + SIN( RADIANS(37.996569) ) * SIN( RADIANS( Latitude ) ) ) ) AS Distance, OrderIndex "
							+ " ,TransSignature" + " FROM RemoteCourier "
							+ " WHERE VouchNumber = '" + number + "'");

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Fetch(rs);
				rs.close();
			}

			try {
				ps = conn
						.prepareStatement(" SELECT  CourierChargeDesc, CreditDebit, SumCash, SumCheck "
								+ "FROM RCourierChargeTrans "
								+ "LEFT JOIN CourierCharge ON  CourierCharge.CourierChargeID = RCourierChargeTrans.CourierChargeID "
								+ "WHERE RCourierChargeTrans.CourierID = ? ");
				ps.set(1, CourierID);

				Charges = new ArrayList<ChargeTrans>();
				ResultSet rsCharges = ps.executeQuery();
				while (rsCharges.next()) {
					ChargeTrans chrg = new ChargeTrans();
					chrg.ChargeName = rsCharges.getString("CourierChargeDesc");
					chrg.CreditDebit = rsCharges.getString("CreditDebit");
					chrg.SumCash = rsCharges.getDouble("SumCash");
					chrg.SumCheck = rsCharges.getDouble("SumCheck");
					if (chrg.SumCash != 0 || chrg.SumCheck != 0)
						Charges.add(chrg);
				}
				rsCharges.close();
			} catch (Exception e) {
				int a = 0;
			}
			ps.close();

		} finally {
			conn.release();
		}
	}

	public void LoadCourier(Context context, Float id) throws ULjException {
		Connection conn = DBUtil.CreateConnection(context);
		try {
			if(conn == null)
				return;
			
			PreparedStatement ps = conn
					.prepareStatement(" SELECT  CourierID "
							+ ", VouchNumber "
							+ ", VouchDate "
							+ ", VouchMemo "
							+ ", SenderName "
							+ ", SenderArea "
							+ ", SenderCity "
							+ ", SenderAddress "
							+ ", SenderGSM "
							+ ", ReceiverName "
							+ ", ReceiverArea "
							+ ", ReceiverCity "
							+ ", ReceiverAddress "
							+ ", ReceiverGSM "
							+ ", VouchCount "
							+ ", DocType "
							+ ", DriverID "
							+ ", TransType "
							+ ", TransReceiverName "
							+ ", TransDate "
							+ ", DeliveryDate "
							+ ", TransReason "
							+ ", TransNotDeliveredTypeID "
							+ ", ExpCashValue "
							+ ", ExpCheckValue "
							+ ", ExpCheckCount "
							+ ", ReceivedCashValue "
							+ ", ReceivedCheckValue "
							+ ", ReceivedCheckCount "
							+ ", BranID "
							+ " ,OrderVoucherNumber "
							+ ", DateCreated"
							+ ", Latitude"
							+ ", Longitude"
							+ ", DeliveryHour"
							+ ", ParcelStatus"
							+ ",CourierCustomerType "
							+ ",IfIsCreditValue "
							+ ", (6371 * ACOS( COS( RADIANS(37.996569) ) * COS( RADIANS( Latitude ) ) * COS( RADIANS( Longitude ) - RADIANS(23.673277) )"
							+ " + SIN( RADIANS(37.996569) ) * SIN( RADIANS( Latitude ) ) ) ) AS Distance, OrderIndex "
							+ ", TransSignature " + " FROM RemoteCourier"
							+ " WHERE CourierID = ?");
			ps.set(1, id);
			try {
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					Fetch(rs);
				}
				rs.close();

				try {
					ps = conn
							.prepareStatement(" SELECT  CourierChargeDesc, CreditDebit, SumCash, SumCheck "
									+ "FROM RCourierChargeTrans "
									+ "LEFT JOIN CourierCharge ON  CourierCharge.CourierChargeID = RCourierChargeTrans.CourierChargeID "
									+ "WHERE RCourierChargeTrans.CourierID = ? ");
					ps.set(1, CourierID);

					Charges = new ArrayList<ChargeTrans>();
					ResultSet rsCharges = ps.executeQuery();
					while (rsCharges.next()) {
						ChargeTrans chrg = new ChargeTrans();
						chrg.ChargeName = rsCharges
								.getString("CourierChargeDesc");
						chrg.CreditDebit = rsCharges.getString("CreditDebit");
						chrg.SumCash = rsCharges.getDouble("SumCash");
						chrg.SumCheck = rsCharges.getDouble("SumCheck");
						Charges.add(chrg);
					}
					rsCharges.close();
				} catch (Exception e) {
					Crashlytics.logException(e);
				}

			} finally {
				ps.close();
			}
		} finally {
			if (conn != null)
				conn.release();
		}
	}

	public void Fetch(ResultSet rs) throws ULjException {
		CourierID = rs.getFloat("CourierID");
		VouchNumber = rs.getString("VouchNumber");
		VouchDate = rs.getDate("VouchDate");
		VouchMemo = rs.getString("VouchMemo");
		SenderName = rs.getString("SenderName");
		SenderCity = rs.getString("SenderCity");
		SenderArea = rs.getString("SenderArea");
		SenderAddress = rs.getString("SenderAddress");
		SenderGSM = rs.getString("SenderGSM");
		ReceiverName = rs.getString("ReceiverName");
		ReceiverCity = rs.getString("ReceiverCity");
		ReceiverArea = rs.getString("ReceiverArea");
		ReceiverAddress = rs.getString("ReceiverAddress");
		ReceiverGSM = rs.getString("ReceiverGSM");
		VouchCount = rs.getInt("VouchCount");
		DocType = rs.getString("DocType");
		DriverID = rs.getFloat("DriverID");
		TransType = rs.getString("TransType");
		TransReceiverName = rs.getString("TransReceiverName");
		TransDate = rs.getDate("TransDate");
		DeliveryDate = rs.getDate("DeliveryDate");
		TransReason = rs.getString("TransReason");
		TransNotDeliveredTypeID = rs.getFloat("TransNotDeliveredTypeID");

		if (GetSignature) {
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

		ExpCashValue = rs.getFloat("ExpCashValue");
		ExpCheckValue = rs.getFloat("ExpCheckValue");
		ExpCheckCount = rs.getInt("ExpCheckCount");
		RealCashValueReceived = rs.getFloat("ReceivedCashValue");
		RealCheckValueReceived = rs.getFloat("ReceivedCheckValue");
		RealChecksCountReceived = rs.getInt("ReceivedCheckCount");
		OrderVoucherNumber = rs.getString("OrderVoucherNumber");
		BranID = rs.getFloat("BranID");
		DateCreated = rs.getDate("DateCreated");
		Latitude = rs.getDouble("Latitude");
		Longitude = rs.getDouble("Longitude");
		DeliveryHour = rs.getString("DeliveryHour");
		ParcelStatus = rs.getInt("ParcelStatus");
		CustomerType = rs.getString("CourierCustomerType");
		IfIsCreditValue = rs.getDouble("IfIsCreditValue");
		Distance = rs.getDouble("Distance");
		OrderIndex = rs.getInt("OrderIndex");
		//ImageName = rs.getString("ImageName");

	}

	public static void SaveOrderIndexes(Context ctx,
			ArrayList<Courier> listVouchers) {
		try {
			Connection conn = DBUtil.CreateConnection(ctx);
			try {

				for (int i = 0; i < listVouchers.size(); i++) {
					StringBuffer sb = new StringBuffer(
							"UPDATE RemoteCourier  SET OrderIndex = ? WHERE CourierID = ?");
					PreparedStatement ps = conn.prepareStatement(sb.toString());
					Courier c = listVouchers.get(i);
					ps.set(1, c.OrderIndex);
					ps.set(2, c.CourierID);

					ps.execute();
					ps.close();
				}
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				Crashlytics.logException(e);
			} finally {

				conn.release();
			}

		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void InsertCourier(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {
			CourierID = 0f;
			PreparedStatement psGetMinID = conn
					.prepareStatement("SELECT MIN(CourierID) FROM RemoteCourier");
			try {
				ResultSet rsMin = psGetMinID.executeQuery();
				if (rsMin.next()) {
					CourierID = rsMin.getFloat(1);
				}
				rsMin.close();
			} finally {
				psGetMinID.close();
			}

			if (CourierID >= 0)
				CourierID = -1f;
			else
				CourierID--;

			StringBuffer sb = new StringBuffer(
					"INSERT INTO RemoteCourier  (CourierID, VouchNumber, VouchDate, DriverID, DocType, TransType, TransDate, DateCreated) "
							+ "VALUES (?,?,?,?,?,?,?,?)");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, CourierID);
			ps.set(2, VouchNumber);
			ps.set(3, VouchDate);
			ps.set(4, DriverID);
			ps.set(5, "1");// doc type
			ps.set(6, "4");// trans type
			ps.set(7, TransDate);
			ps.set(8, DateCreated);

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

	public void UpdateCourier(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {

			StringBuffer sb = new StringBuffer("UPDATE RemoteCourier  "
					+ "SET " + "VouchNumber = ?, " + "VouchDate = ?, "
					+ "SenderName = ?, " + "SenderArea = ?, "
					+ "SenderCity = ?, " + "SenderAddress = ?, "
					+ "ReceiverName = ?, " + "ReceiverArea = ?, "
					+ "ReceiverCity = ?, " + "ReceiverAddress = ?, "
					+ "VouchCount = ?," + "DriverID = ?," + "TransType = ?, "
					+ "TransReceiverName = ?, " + "TransDate = ?, "
					+ "DeliveryDate = ?, " + "TransReason = ?, "
					+ "TransNotDeliveredTypeID = ?, " + "ExpCashValue = ?,"
					+ "ExpCheckValue = ?," + "ExpCheckCount = ?,"
					+ "ReceivedCashValue = ?, " + "ReceivedCheckValue = ?, "
					+ "ReceivedCheckCount = ?, " + "OrderVoucherNumber = ?, "
					+ "BranID = ?, " + "DateCreated = ?, "  
					+ "ParcelStatus = ?," + "TransSignature = ? "
					+ " WHERE CourierID = ? ");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, VouchNumber);
			ps.set(2, VouchDate);
			ps.set(3, SenderName);
			ps.set(4, SenderArea);
			ps.set(5, SenderCity);
			ps.set(6, SenderAddress);
			ps.set(7, ReceiverName);
			ps.set(8, ReceiverArea);
			ps.set(9, ReceiverCity);
			ps.set(10, ReceiverAddress);
			ps.set(11, VouchCount);
			ps.set(12, DriverID);
			ps.set(13, TransType);
			ps.set(14, TransReceiverName);
			ps.set(15, TransDate);
			ps.set(16, DeliveryDate);
			ps.set(17, TransReason);
			ps.set(18, TransNotDeliveredTypeID);
			ps.set(19, ExpCashValue);
			ps.set(20, ExpCheckValue);
			ps.set(21, ExpCheckCount);
			ps.set(22, RealCashValueReceived);
			ps.set(23, RealCheckValueReceived);
			ps.set(24, RealChecksCountReceived);
			ps.set(25, OrderVoucherNumber);
			ps.set(26, BranID);
			ps.set(27, DateCreated);
			//ps.set(28, ImageName);
			ps.set(28, ParcelStatus);

			if (TransSignature != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransSignature.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm
																				// is
																				// the
																				// bitmap
																				// object
				byte[] bytesToSave = baos.toByteArray();
				ps.set(29, bytesToSave);
			} else {
				ps.setNull(29);
			}
			ps.set(30, CourierID);

			ps.execute();
			ps.close();
			conn.commit();
		} finally {
			conn.release();
		}

	}

	public static void UpdateCourierCoordinates(Context ctx, float courierID,
			double lat, double lng) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {
			StringBuffer sb = new StringBuffer("UPDATE RemoteCourier  "
					+ "SET " + "DeliveryLatitude = ?, "
					+ "DeliveryLongitude = ?" + " WHERE CourierID = ? ");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, lat);
			ps.set(2, lng);
			ps.set(3, courierID);

			ps.execute();
			ps.close();
			conn.commit();
		} finally {
			if (conn != null)
				conn.release();
		}
	}

	public void AcceptOrder(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {
			TransType = "18";
			StringBuffer sb = new StringBuffer("UPDATE RemoteCourier  "
					+ "SET " + "DeliveryDate = ?, " + "TransType = ? "
					+ " WHERE CourierID = ? ");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, new Date());
			ps.set(2, TransType);
			ps.set(3, CourierID);

			ps.execute();
			ps.close();
			conn.commit();
		} finally {
			conn.release();
		}
	}

	public void DeclineOrder(Context ctx) throws ULjException {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {
			TransType = "19";
			StringBuffer sb = new StringBuffer("UPDATE RemoteCourier  "
					+ "SET " + "DeliveryDate = ?, " + "TransType = ? "
					+ " WHERE CourierID = ? ");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set(1, new Date());
			ps.set(2, TransType);
			ps.set(3, CourierID);

			ps.execute();
			ps.close();
			conn.commit();
		} finally {
			conn.release();
		}
	}

}
