package com.mybus.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybus.SystemProperties;
import com.mybus.dao.BookingPaymentDAO;
import com.mybus.dao.BookingSessionInfoDAO;
import com.mybus.dao.PaymentResponseDAO;
import com.mybus.dao.impl.BookingMongoDAO;
import com.mybus.dao.impl.OperatorAccountMongoDAO;
import com.mybus.model.*;
import com.mybus.util.Constants;
import com.mybus.util.SendTaxInvoice;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 
 * @author yks-Srinivas
 */
@Service
public class BookingPaymentManager {


	private static final Logger LOGGER= LoggerFactory.getLogger(BookingPaymentManager.class);
	
	@Autowired
	private SystemProperties systemProperties;
	
	@Autowired
	private BookingPaymentDAO bookingPaymentDAO;
	
	@Autowired
    PaymentResponseDAO paymentResponseDAO;
	
	@Autowired
	private BookingSessionManager bookingSessionManager;
	
	@Autowired
	private BookingSessionInfoDAO bookingSessionInfoDAO;
	
	@Autowired
	private BookingTrackingManager bookingTrackingManager;

	@Autowired
	private BookingMongoDAO bookingMongoDAO;

	@Autowired
	private OperatorAccountMongoDAO operatorAccountMongoDAO;

	@Autowired
	private SendTaxInvoice sendTaxInvoice;

	public BookingPayment getPayuPaymentGatewayDetails(BookingPayment payment){

		LOGGER.info("PaymentManager :: getPayuPaymentGatewayDetails");
		PaymentGateway pg = new PaymentGateway();
		pg.setPgKey("eCwWELxi"); //payu  salt
		pg.setPgAccountID("gtKFFx"); //payu key
		pg.setPgRequestUrl("https://test.payu.in/_payment");
		pg.setPgCallbackUrl("http://localhost:8081/payUResponse");
		pg.setPaymentType("PG");
		pg.setName("PAYU");
		String merchantRefNo =  getRandamNo();
		String hashSequence = pg.getPgAccountID()+"|"+ merchantRefNo +"|"+ (int)payment.getAmount() +"|bus|"+
				payment.getFirstName() +"|"+ payment.getEmail() +"|||||||||||"+pg.getPgKey();
		LOGGER.info("hashSequence - "+hashSequence);		
		String hash = hashCal(Constants.SHA_512,hashSequence);
		LOGGER.info("secured hash - "+hash);
		payment.setHashCode(hash);
		payment.setPaymentGateways(pg);
		payment.setMerchantRefNo(merchantRefNo);
		PaymentResponse paymentRespose= new PaymentResponse();
		paymentRespose.setAmount(payment.getAmount());
		paymentRespose.setMerchantrefNo(payment.getMerchantRefNo());
		paymentRespose.setPaymentName(payment.getPaymentGateways().getName());
		paymentRespose.setPaymentType(payment.getPaymentGateways().getPaymentType());
		Status status = new Status();
		status.setSuccess(true);
		status.setStatusCode(Constants.SUCCESS_CODE);
		status.setStatusMessage("BookingPayment request in progress");
		paymentRespose.setStatus(status);
		payment = bookingPaymentDAO.save(payment);
		paymentRespose.setPaymentUserInfoId(payment.getId());
		paymentRespose = paymentResponseDAO.save(paymentRespose);
		BookingSessionInfo bookingSessionInfo = bookingSessionManager.getBookingSessionInfo();
		bookingSessionInfo.setBookingId(paymentRespose.getId());
		bookingSessionInfoDAO.save(bookingSessionInfo);
		payment.getPaymentGateways().setPgCallbackUrl(payment.getPaymentGateways().getPgCallbackUrl()+"?payID="+paymentRespose.getId());
		bookingSessionManager.setBookingSessionInfo(bookingSessionInfo);
		return payment;
	}
	
	public BookingPayment getEBSPaymentGatewayDetails(BookingPayment payment){
		
		LOGGER.info("PaymentManager :: getEBSPaymentGatewayDetails");
		PaymentGateway pg = new PaymentGateway();
		pg.setPgKey("ebs Secret Key");
		pg.setPgAccountID("account id");
		pg.setPgRequestUrl("https://secure.ebs.in/pg/ma/payment/request");
		pg.setPgCallbackUrl("http://localhost:8081/eBSResponse");
		pg.setPaymentType("PG");
		pg.setName("EBS");
		String merchantRefNo =  getRandamNo();
		payment.setAlgo("MD5");
		payment.setChannel("0");
		payment.setCurrency("INR");
		payment.setDescription("Description");
		payment.setMode("TEST");
		payment.setMerchantRefNo(merchantRefNo);
		payment.setPaymentGateways(pg);
		
		PaymentResponse paymentRespose= new PaymentResponse();
		paymentRespose.setAmount(payment.getAmount());
		paymentRespose.setMerchantrefNo(payment.getMerchantRefNo());
		paymentRespose.setPaymentName(payment.getPaymentGateways().getName());
		paymentRespose.setPaymentType(payment.getPaymentGateways().getPaymentType());
		Status status = new Status();
		status.setSuccess(true);
		status.setStatusCode(Constants.SUCCESS_CODE);
		status.setStatusMessage("BookingPayment request in progress");
		payment = bookingPaymentDAO.save(payment);
		paymentRespose.setPaymentUserInfoId(payment.getId());
		paymentRespose.setStatus(status);
		paymentRespose = paymentResponseDAO.save(paymentRespose);
		payment.getPaymentGateways().setPgCallbackUrl(payment.getPaymentGateways().getPgCallbackUrl()+"?payID="+paymentRespose.getId());
		String hash = hashForEbs(payment);
		LOGGER.info("secured hash - "+hash);
		payment.setHashCode(hash);
		
		return payment;
	}
	
	public PaymentResponse paymentResponseFromEBS(Map<String, String> map, String _id) {
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setId(_id);
		paymentResponse.setAmount(Double.parseDouble(map.get("Amount")));
		paymentResponse.setPaymentId(map.get("PaymentID"));
		paymentResponse.setMerchantrefNo(map.get("MerchantRefNo"));
		paymentResponse.setPaymentDate(map.get("DateCreated"));
		paymentResponse.setPaymentType("PG");
		paymentResponse.setPaymentName("EBS");
		paymentResponse.setResponseParams(new JSONObject(map));
		Status status = new Status();
		
		if("0".equals(map.get("ResponseCode")) /*&& ebsHashValidation(map) */&& "No".equalsIgnoreCase(map.get("IsFlagged"))){
			status.setSuccess(true);
			status.setStatusCode(Constants.SUCCESS_CODE);
			status.setStatusMessage("BookingPayment has success");
		}else{
			status.setSuccess(false);
			status.setStatusCode(Constants.FAILED_CODE);
			status.setStatusMessage("BookingPayment has failed");
		}
		LOGGER.info("BookingPayment Resone from ebs"+map);
		paymentResponse.setStatus(status);
		update(paymentResponse);
		BookingTracking bookingTracking = new BookingTracking();
		bookingTracking.setBookingId(paymentResponse.getId());
		bookingTracking.setMerchantRefNo(paymentResponse.getMerchantrefNo());
		bookingTrackingManager.savePayment(bookingTracking);
		
		return paymentResponse;
	}
	
	public PaymentResponse paymentResponseFromPayu(Map<String, String> map, String id) {
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setId(id);
		paymentResponse.setAmount(Double.parseDouble(map.get("amount")));
		paymentResponse.setPaymentId(map.get("txnid"));
		paymentResponse.setMerchantrefNo(map.get("mihpayid"));
		paymentResponse.setPaymentDate(map.get("addedon"));
		paymentResponse.setPaymentType("PG");
		paymentResponse.setPaymentName("PAYU");
		paymentResponse.setResponseParams(new JSONObject(map));
		Status status = new Status();
		String hashSequence = "eCwWELxi|"+map.get("status")+"|||||||||||"+ map.get("email") +"|"+ map.get("firstname") +"|"+ map.get("productinfo")+"|"+ map.get("amount") +"|"+ map.get("txnid") +"|"+map.get("key");
		if(!hashCal(Constants.SHA_512,hashSequence).equalsIgnoreCase(map.get("hash")) || !Constants.status.SUCCESS.name().equalsIgnoreCase(map.get("status")) || !Constants.PAYU_SUCCESS_CODE.equalsIgnoreCase(map.get("error"))){
			status.setSuccess(false);
			status.setStatusCode(Constants.FAILED_CODE);
			status.setStatusMessage("BookingPayment has failed");
			LOGGER.info("hash failed from payu.. request and response hash both are not same...");	
		}else{
			status.setSuccess(true);
			status.setStatusCode(Constants.SUCCESS_CODE);
			status.setStatusMessage("BookingPayment has success");
		}
		paymentResponse.setStatus(status);
		update(paymentResponse);
		BookingTracking bookingTracking = new BookingTracking();
		bookingTracking.setBookingId(paymentResponse.getId());
		bookingTracking.setMerchantRefNo(paymentResponse.getMerchantrefNo());
		bookingTrackingManager.savePayment(bookingTracking);
		
		return paymentResponse;
	}

	/**
	 * This is the common method for refund amount to all payment gateways
	 * @param pID
	 * @param refundAmount
	 * @param disc
	 * @return
	 */
	public RefundResponse refundProcessToPaymentGateways(String pID, double refundAmount, String disc) {
		
		PaymentResponse paymentResponse = paymentResponseDAO.findById(pID).get();
		RefundResponse refundResponse = new RefundResponse();
		refundResponse.setRefundAmount(refundAmount);
		refundResponse.setDisc(disc);
		if(paymentResponse.getPaymentName().equalsIgnoreCase("PAYU")){
			String uniqueId = getRandamNo();
			String hashSequence =null;
			refundResponse.setPaymentType("PG");
			refundResponse.setPaymentName("PAYU");
			hashSequence = "gtKFFx|cancel_refund_transaction|"+paymentResponse.getMerchantrefNo()+"|eCwWELxi";
			java.net.URL url;
			java.io.OutputStreamWriter wr = null;
			try {
				url = new java.net.URL("https://test.payu.in/merchant/postservice?form=2");
				java.net.URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				wr = new java.io.OutputStreamWriter(conn.getOutputStream());
				wr.write("key=gtKFFx&command=cancel_refund_transaction&hash="+ hashCal("SHA-512",hashSequence) +"&var1="+ refundAmount +"&var2="+ uniqueId +"&var3="+refundAmount);
				LOGGER.info("PAYU Refund request ::"+hashSequence);
				wr.flush();
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = null;
				StringBuilder rowResonse = new StringBuilder();
				while ((line = rd.readLine()) != null) {
					rowResonse.append(line);
					LOGGER.info("PAYU Refund response ::"+line);
				}
				Status status = new Status();
				status.setSuccess(true);
				status.setStatusCode(Constants.SUCCESS_CODE);
				status.setStatusMessage("Refund has success");
				refundResponse.setStatus(status);
				refundResponse.setRefundResponseParams(rowResonse.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(wr!=null){
					try {
						wr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			String paymentID =paymentResponse.getPaymentId();
			java.net.URL url;
			PrintStream ps = null;
			HttpURLConnection connection = null;
			BufferedReader in = null;
			refundResponse.setPaymentType("PG");
			refundResponse.setPaymentName("EBS");
			try {
				url = new java.net.URL("https://api.secure.ebs.in/api/1_0");
				LOGGER.info("Starting refund operation at EBS" + url);
				connection = (HttpURLConnection) url.openConnection();
				HttpURLConnection.setFollowRedirects(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				String formData = "Action=refund&AccountID=ebs accountid&SecretKey=ebs Secret Key&Amount=" + refundAmount+"&PaymentID=" + paymentID;
				ps = new PrintStream(connection.getOutputStream());
				ps.print(formData);
				LOGGER.info("EBS Refund request ::"+formData);
				connection.connect();
				if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
					in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder stringXML = new StringBuilder();
					String inputXML = null;
					while ((inputXML = in.readLine()) != null) {
						stringXML.append(inputXML);
					}
					LOGGER.info("EBS Refund response :: " + stringXML.toString());
					if (stringXML.toString().contains(" errorCode=") && stringXML.toString().contains(" error=")){
						Status status = new Status();
						status.setSuccess(false);
						status.setStatusCode(Constants.FAILED_CODE);
						status.setStatusMessage("Refund has been failed");
						refundResponse.setStatus(status);
						refundResponse.setRefundResponseParams(stringXML.toString());
						
					} else {
						try {
							Status status = new Status();
							status.setSuccess(true);
							status.setStatusCode(Constants.SUCCESS_CODE);
							status.setStatusMessage("Refund has success");
							refundResponse.setStatus(status);
							refundResponse.setRefundResponseParams(stringXML.toString());
							LOGGER.info("Cancellation response from EBS :: "	+ refundResponse.toString());
						} catch (RuntimeException e) {
							Status status = new Status();
							status.setSuccess(false);
							status.setStatusCode(Constants.FAILED_CODE);
							status.setStatusMessage("Refund has been failed");
							refundResponse.setStatus(status);
							refundResponse.setRefundResponseParams(stringXML.toString());
							LOGGER.info("EBS Response parsing error came at the time of refund::"+e);
						}			
					}
					in.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				if(ps!=null){
					ps.close();
				}
				if(in!=null){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				connection.disconnect();
			}
		}
		paymentResponse.setRefundResponse(refundResponse);
		update(paymentResponse);
		return refundResponse;
	}
	
	public Iterable<PaymentResponse> getPaymentDetails() {
        return paymentResponseDAO.findAll();
    }
	
	private String getRandamNo(){
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}

	private String hashCal(String type,String str){
		byte[] hashseq=str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try{
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();
			for (int i=0;i<messageDigest.length;i++) {
				String hex=Integer.toHexString(0xFF & messageDigest[i]);
				if(hex.length()==1){ 
					hexString.append("0");
				}
				hexString.append(hex);
			}
		}catch(NoSuchAlgorithmException nsae){ 
		}
		return hexString.toString();
	}	
	
	public String md5(String str) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
			}
			e.printStackTrace();
		}
		byte[] data = str.getBytes();
		m.update(data,0,data.length);
		BigInteger i = new BigInteger(1,m.digest());
		String hash = String.format("%1$032X", i);
		return hash;
	}

	public boolean ebsHashValidation(Map<String, String> map){

		//sort the map
		Map<String,String> requestFields = new TreeMap<String,String>(map);
		requestFields.remove("SecureHash");
		String md5HashData = "ebskey";
		for (Iterator<String> i = requestFields.keySet().iterator(); i.hasNext(); ){
			String key = (String)i.next();
			String value = (String)requestFields.get(key);
			md5HashData += "|"+value;
		}
		if(md5(md5HashData).equalsIgnoreCase(map.get("SecureHash"))){
			return true;
		}
		return false;
	}
	public String hashForEbs(BookingPayment payment){
		
		String md5HashData = "";
		md5HashData = payment.getPaymentGateways().getPgKey()+"|"+payment.getPaymentGateways().getPgAccountID()+"|"+payment.getAddress()+"|"+payment.getAlgo()+"|"+(int)payment.getAmount()+"|"+payment.getChannel()+"|"+payment.getCity()+"|"+payment.getCountry()+"|"+payment.getCurrency()+"|"+payment.getDescription()+"|"+payment.getEmail()+"|"+payment.getMode()+"|"+payment.getFirstName()+"|"+payment.getPhoneNo()+"|"+payment.getPostalCode()+"|"+payment.getMerchantRefNo()+"|"+payment.getPaymentGateways().getPgCallbackUrl()+"|"+payment.getAddress()+"|"+payment.getCity()+"|"+payment.getCountry()+"|"+payment.getFirstName()+"|"+payment.getPhoneNo()+"|"+payment.getPostalCode()+"|"+payment.getState()+"|"+payment.getState();
		LOGGER.info("md5HashData :: "+md5HashData);
		String hash = md5(md5HashData);
		LOGGER.info("hash"+hash);
		return hash;
	}

	public boolean update(PaymentResponse paymentResponse) {
        
        PaymentResponse pr = paymentResponseDAO.findById(paymentResponse.getId()).get();
        try {
        	paymentResponse.setPaymentUserInfoId(pr.getPaymentUserInfoId());
        	pr.merge(paymentResponse);
        	paymentResponseDAO.save(pr);
        } catch (Exception e) {
        	LOGGER.error("Error updating the Route ", e);
           throw new RuntimeException(e);
        }
        return true;
    }
	public double refundAmount(DateTime doj,double SeatFare){
		double refundAmount = 0.0;
		DateTime systemdate = new DateTime();
		List<CancellationPolicy> cancellationPolicyList = getCancellationPolicy();
		long correntTimeMil = systemdate.getMillis();
	    long dojTimeMil = doj.getMillis();
	    long timediffernt = dojTimeMil - correntTimeMil;
	    int days = (int) (timediffernt / (1000*60*60*24));
	    int count = 1;
	    for(CancellationPolicy cp:cancellationPolicyList){
	    	if(cp.getCutoffTime()>(days*24)){
	    		LOGGER.info(""+cp.getRefundInPercentage());
	    		refundAmount = refundCalculation(SeatFare,cp.getRefundInPercentage());
	    	}else if(count==cancellationPolicyList.size()){
	    		LOGGER.info(""+cp.getRefundInPercentage());
	    		refundAmount = refundCalculation(SeatFare,cp.getRefundInPercentage());
	    	}
	    	count++;
	    }
	    LOGGER.info("refundAmount"+refundAmount);
	    return refundAmount;
	}
	private double refundCalculation(double seatFare,double refundInPercentage){
		double refundAmount = 0.0;
		if(!(refundInPercentage==0)){
			double pgCharges= Double.parseDouble(systemProperties.getProperty("pgCharges"));
			refundAmount = ((seatFare*refundInPercentage)/100-(seatFare*pgCharges)/100);
		}
		return refundAmount;
	}
	public List<CancellationPolicy> getCancellationPolicy(){
		String cancellationPolicyJsonString = systemProperties.getProperty("cancellationPolicy");
		List<CancellationPolicy> cancellationPolicyList = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			cancellationPolicyList = mapper.readValue(cancellationPolicyJsonString, new TypeReference(){});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("cancellation Policy List ::"+cancellationPolicyList);
		return cancellationPolicyList;
	}

}