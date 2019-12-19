package com.mybus.service;

import com.mybus.dao.PaymentDAO;
import com.mybus.dao.UserDAO;
import com.mybus.dao.impl.PaymentMongoDAO;
import com.mybus.dao.impl.UserMongoDAO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.*;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by srinikandula on 12/12/16.
 */

@Service
public class PaymentManager {
    private static final Logger logger = LoggerFactory.getLogger(PaymentManager.class);

    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private UserManager userManager;
    @Autowired
    private PaymentMongoDAO paymentMongoDAO;

    @Autowired
    private UserMongoDAO userMongoDAO;

    @Autowired
    private UserDAO userDAO;


    public Payment updatePayment(Payment payment) {
        if(payment.getStatus() != null && (payment.getStatus().equals(Payment.STATUS_APPROVED) ||
                payment.getStatus().equals(Payment.STATUS_AUTO))){
            updateUserBalance(payment);
        }
        return paymentMongoDAO.save(payment);
    }

    private void updateUserBalance(Payment payment) {
        User currentUser = null;
        if(payment.getCreatedBy() != null) {
            currentUser = userManager.findOne(payment.getCreatedBy());
        }
        if(payment.getSubmittedBy() != null) {
            currentUser = userManager.findOne(payment.getSubmittedBy());
        }
        if(currentUser == null){
            currentUser = sessionManager.getCurrentUser();
        }
        if(currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }
        if(!payment.getStatus().equals(Payment.STATUS_AUTO)){
            currentUser = userManager.findOne(payment.getCreatedBy());
        }
        if(payment.getType().equals(PaymentType.EXPENSE)){
            if(!userMongoDAO.updateCashBalance(currentUser.getId(), (0-payment.getAmount()))){
                throw new BadRequestException("Update balance failed");
            }
        } else if(payment.getType().equals(PaymentType.INCOME)){
            if(!userMongoDAO.updateCashBalance(currentUser.getId(), payment.getAmount())){
                throw new BadRequestException("Update balance failed");
            }
        }
    }

    /**
     * Create payment for Cargo Booking
     * @param booking
     * @return
     */
    public Payment createPayment(CargoBooking booking) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setAmount(booking.getTotalCharge());
        payment.setDate(booking.getCreatedAt().toDate());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setBookingId(booking.getShipmentNumber());
        payment.setSubmittedBy(currentUser.getId());
        payment.setDescription(Payment.CARGO_BOOKING + " : " + booking.getShipmentNumber());
        payment.setType(PaymentType.INCOME);
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDuePaidOn(new Date());
        return updatePayment(payment);
    }

    /**
     * Create payment for Cargo Booking
     * @param booking
     * @return
     */
    public Payment cancelCargoBooking(CargoBooking booking) {
        Optional<User> user = userDAO.findById(booking.getCreatedBy());
        if(!user.isPresent()) {
            user = userDAO.findById(booking.getForUser());
        }
        Payment payment = new Payment();
        payment.setCreatedBy(user.get().getId());
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setAmount(booking.getTotalCharge());
        payment.setDate(booking.getCreatedAt().toDate());
        payment.setBranchOfficeId(user.get().getBranchOfficeId());
        payment.setBookingId(booking.getShipmentNumber());
        payment.setSubmittedBy(user.get().getId());
        payment.setDescription("Cancel " + Payment.CARGO_BOOKING + " : " + booking.getShipmentNumber());
        payment.setType(PaymentType.EXPENSE);
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDuePaidOn(new Date());
        return updatePayment(payment);
    }

    /**
     * Create payment for given office and update office's cash balance
     * @param booking
     * @return
     */
    public Payment createPayment(Booking booking) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setAmount(booking.getNetAmt());
        payment.setDate(booking.getJourneyDate());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setBookingId(booking.getId());
        payment.setSubmittedBy(currentUser.getId());
        payment.setDescription(Payment.BOOKING_DUE_PAYMENT + " "+booking.getBookedBy()+" : " + booking.getTicketNo());
        payment.setType(PaymentType.INCOME);
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDuePaidOn(new Date());
        return updatePayment(payment);
    }

    public Payment createPayment(SalaryPayment salaryPayment){
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setAmount(salaryPayment.getAmountPaid());
        payment.setDate(salaryPayment.getPaidOn());
        payment.setDescription(salaryPayment.getDescription());
        payment.setSubmittedBy(currentUser.getId());
        payment.setType(PaymentType.EXPENSE);
//        payment.setStatus(Payment.STATUS_AUTO);
        return updatePayment(payment);
    }

    public Payment createPayment(ServiceForm serviceForm, boolean deleteForm) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setAmount(serviceForm.getNetCashIncome());
        payment.setServiceFormId(serviceForm.getId());

        //need to set this for updating the balance for verified forms
        payment.setSubmittedBy(serviceForm.getSubmittedBy());
        if(deleteForm){
            payment.setType(PaymentType.EXPENSE);
            payment.setDescription("Service form refresh");
        } else {
            payment.setType(PaymentType.INCOME);
            payment.setDescription("Service form: "+ serviceForm.getServiceName());
        }
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDate(serviceForm.getJDate());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        return updatePayment(payment);
    }

    public void delete(String paymentId) {
        Payment payment = paymentDAO.findById(paymentId).get();
        if(payment.getStatus() != null) {
            throw new BadRequestException("Payment can not be deleted");
        }
        paymentDAO.delete(payment);
    }

    /**
     * Finding payments for pagination
     * @param query
     * @param pageable
     * @return
     */
    public Page<Payment> findPayments(JSONObject query, Pageable pageable) throws ParseException {
        List<Payment> payments = IteratorUtils.toList(paymentMongoDAO.find(query, pageable).iterator());
        serviceUtils.fillInUserNames(payments);
        Page<Payment> page = new PageImpl<>(payments);
        return page;
    }

    public Page<Payment> findPendingPayments(Pageable pageable) {
        Page<Payment> page = paymentMongoDAO.findPendingPayments(pageable);
        serviceUtils.fillInUserNames(page.getContent());
        return page;
    }
    public Page<Payment> findNonPendingPayments(Pageable pageable) {
        Page<Payment> page = paymentMongoDAO.findNonPendingPayments(pageable);
        serviceUtils.fillInUserNames(page.getContent());
        return page;
    }


    public Payment findOne(String id) {
        Payment payment = paymentDAO.findByIdAndOperatorId(id, sessionManager.getOperatorId());
        if(payment == null) {
            throw new BadRequestException("No Payment found");
        }
        serviceUtils.fillInUserNames(payment);
        return payment;
    }

    public Page<Payment> findPaymentsByDate(String date, Pageable pageable) throws IOException {
        Page<Payment> payments = paymentMongoDAO.findPaymentsByDate(date, pageable);
        serviceUtils.fillInUserNames(payments.getContent(), ServiceReport.SUBMITTED_BY);
        return payments;
    }

    public List<Payment> search(JSONObject query, Pageable pageable) throws ParseException {
        query = ServiceUtils.addOperatorId(query, sessionManager);
        return paymentMongoDAO.search(query, pageable);
    }

    public List<Payment> approveOrRejectExpenses(List<String> ids, Boolean approve) {
        List<Payment> payments = new ArrayList<>();
        User currentUser = sessionManager.getCurrentUser();
        ids.stream().forEach(id -> {
            Payment payment = paymentDAO.findById(id).get();
            if(payment.getStatus() != null){
                throw new BadRequestException("payment has invalid status");
            }
            if(approve){
                payment.setStatus(Payment.STATUS_APPROVED);
                payment.setReviewedBy(currentUser.getId());
                payment.setReviewdOn(new Date());
                payment = updatePayment(payment);
            } else {
                payment.setStatus(Payment.STATUS_REJECTED);
                payment.setReviewedBy(currentUser.getId());
                payment.setReviewdOn(new Date());
                payment = updatePayment(payment);
            }
            payments.add(payment);
        });
        return payments;
    }

    public Payment save(Payment paymnet) {
        paymnet.setOperatorId(sessionManager.getOperatorId());
        return paymentMongoDAO.save(paymnet);
    }

    public long getPaymentsCount(boolean pendingPayments, Pageable pageable) {
        return paymentMongoDAO.getPaymentsCount(pendingPayments, pageable);
    }

    public Payment createPayment(FullTrip fullTrip) {
        User currentUser = sessionManager.getCurrentUser();
        Payment payment = new Payment();
        payment.setOperatorId(sessionManager.getOperatorId());
        payment.setBranchOfficeId(currentUser.getBranchOfficeId());
        payment.setAmount(fullTrip.getCharge());
        payment.setDate(new Date());
        payment.setBookingId(fullTrip.getId());
        payment.setSubmittedBy(currentUser.getId());
        payment.setDescription(Payment.FULLTRIP_AMOUNT + " "+fullTrip.getFrom()+" - " + fullTrip.getTo() +" "+ fullTrip.getTripDate());
        payment.setType(PaymentType.INCOME);
        payment.setStatus(Payment.STATUS_AUTO);
        payment.setDuePaidOn(new Date());
        return updatePayment(payment);
    }
}
