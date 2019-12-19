package com.mybus.service;

import com.mybus.dao.JobDAO;
import com.mybus.dao.ReminderDAO;
import com.mybus.dao.impl.JobMongoDAO;
import com.mybus.dao.impl.ReminderMongoDAO;
import com.mybus.dto.InventoryDTO;
import com.mybus.exception.BadRequestException;
import com.mybus.model.Job;
import com.mybus.model.Reminder;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class JobManager{
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private InventoryManager inventoryManager;
    @Autowired
    private JobMongoDAO jobMongoDAO;
    @Autowired
    private ReminderMongoDAO reminderMongoDAO;
    @Autowired
    private VehicleManager vehicleManager;
    @Autowired
    private ReminderDAO reminderDAO;

    public Job addJob(Job job){
        if(job.getInventories().size() == 0){
            throw new RuntimeException("Select atleast one Inventory");
        }
        if(job.getVehicleId() == null){
            throw new RuntimeException("Select vehicle");
        }
        if(job.isJobCompleted()) {
            inventoryManager.updateRemainingQuantity(job.getInventories());
        }
        job.setOperatorId(sessionManager.getOperatorId());
        job = jobDAO.save(job);
        if(job.isReminder()){
            Reminder reminder = new Reminder();
            reminder.setOperatorId(sessionManager.getOperatorId());
            reminder.setDescription(job.getReminderText());
            reminder.setReminderDate(job.getReminderDate());
            reminder.setJobRefId(job.getId());
            reminder.setUserId(job.getForUser());
            reminder.setRemarks(job.getRemarks());
            reminder.setExpectedMileage(job.getExpectedMileage());
            reminder.setVehicleId(job.getVehicleId());
            reminderDAO.save(reminder);
        }
        return job;
    }

    public Job updateJob(Job job) {
        if(job.getInventories().size() == 0){
            throw new RuntimeException("Select atleast one Inventory");
        }
        if(job.getVehicleId() == null){
            throw new RuntimeException("Select vehicle");
        }
        Job savedJob = jobDAO.findById(job.getId()).get();
        if(savedJob.isJobCompleted()){
            throw new BadRequestException("Completed job cant be updated");
        }
        //update the inventory quantity only if not saved job
        if(job.isJobCompleted() && !savedJob.isJobCompleted()) {
            inventoryManager.updateRemainingQuantity(job.getInventories());
        }
        savedJob.setInventories(job.getInventories());
        savedJob.setVehicleId(job.getVehicleId());
        savedJob.setRemarks(job.getRemarks());
        savedJob.setJobDate(job.getJobDate());
        savedJob.setMileage(job.getMileage());
        savedJob.setJobCompleted(job.isJobCompleted());
        boolean remainder = savedJob.isReminder();
        savedJob.setReminder(job.isReminder());
        savedJob.setReminderDate(job.getReminderDate());
        savedJob.setReminderText(job.getReminderText());
        savedJob.setExpectedMileage(job.getExpectedMileage());
        savedJob.setJobDescription(job.getJobDescription());
        savedJob.setForUser(job.getForUser());
        job= jobDAO.save(savedJob);
        if(remainder) {
            Reminder reminder = reminderMongoDAO.getReminderJobRefId(job.getId());
            reminder.setOperatorId(sessionManager.getOperatorId());
            reminder.setDescription(job.getReminderText());
            reminder.setReminderDate(job.getReminderDate());
            reminder.setUserId(job.getForUser());
            reminder.setRemarks(job.getRemarks());
            reminder.setExpectedMileage(job.getExpectedMileage());
            reminder.setVehicleId(job.getVehicleId());
            reminder.setJobRefId(job.getId());
            reminderDAO.save(reminder);
        } else {
            if(job.isReminder()) {
                Reminder reminder = new Reminder();
                reminder.setOperatorId(sessionManager.getOperatorId());
                reminder.setDescription(job.getReminderText());
                reminder.setReminderDate(job.getReminderDate());
                reminder.setUserId(job.getForUser());
                reminder.setRemarks(job.getRemarks());
                reminder.setExpectedMileage(job.getExpectedMileage());
                reminder.setVehicleId(job.getVehicleId());
                reminder.setJobRefId(job.getId());
                reminderDAO.save(reminder);
            }
        }
        return  job;
    }

    public boolean delete(String id) {
        jobDAO.deleteById(id);
        return true;
    }

    public Job getJob(String id) {
        Job job = jobDAO.findById(id).get();
        return job;
    }

    public Page<Job> getJobs(Pageable pageable, boolean completed) throws ParseException {
        JSONObject query = new JSONObject();
        query.put("completed" ,completed);
        long total = getCount(query);
        List<Job> jobs = IteratorUtils.toList(jobMongoDAO.findAllJobs(query,pageable).iterator());
        setNames(jobs);
        Page<Job> page = new PageImpl<>(jobs, pageable, total);
        return page;
    }

    public Page<Job> search(JSONObject query) throws ParseException {
        long total = getCount(query);
        PageRequest pageable = null;
        if(query.get("size") != null && query.get("page") != null){
            int page = (int) query.get("page");
            int size = (int) query.get("size");
            pageable = new PageRequest(page,size);
        } else {
            pageable = new PageRequest(0,Integer.MAX_VALUE);
        }
        List<Job> jobs = IteratorUtils.toList(jobMongoDAO.findAllJobs(query,pageable).iterator());
        setNames(jobs);
        Page<Job> page = new PageImpl<>(jobs, pageable, total);
        return page;
    }

    public long getCount(JSONObject query) throws ParseException {
        return jobMongoDAO.getCount(query);

    }

    private void setNames(List<Job> jobs){
        Map<String,String> vehicleNames = vehicleManager.findVehicleNumbers();
        Map<String,String> inventoryNames = inventoryManager.findInventoryNames();
        StringBuilder sb = new StringBuilder();
        jobs.stream().forEach(job -> {
            List<InventoryDTO> inventories = job.getInventories();
            if(inventories != null && inventories.size() > 0){
                for(InventoryDTO inventoryDTO:inventories){
                    if(sb.length() > 0){
                        sb.append(',');
                    }
                    sb.append(inventoryNames.get(inventoryDTO.getInventoryId()) +":"+ inventoryDTO.getQuantity());
                }
                job.getAttributes().put("inventory",sb.toString());
            }
            job.getAttributes().put("RegNo",vehicleNames.get(job.getVehicleId()));
            sb.setLength(0);
        });
    }
}