package com.mybus.service;


import com.mybus.SystemProperties;
import com.mybus.dao.ReminderDAO;
import com.mybus.dao.impl.ReminderMongoDAO;
import com.mybus.model.Reminder;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class ReminderManager{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ReminderDAO reminderDAO;
    @Autowired
    private ReminderMongoDAO reminderMongoDAO;
    @Autowired
    private UserManager userManager;
    @Autowired
    private SystemProperties systemProperties;

    public Reminder addReminder(Reminder reminder) {
        if(reminder.getReminderDate() == null){
            throw new RuntimeException("Invalid reminder date");
        }
        if(reminder.getDescription() == null){
            throw new RuntimeException("Add reminder text");
        }
        reminder.setOperatorId(sessionManager.getOperatorId());
        return reminderDAO.save(reminder);
    }

    public Reminder updateReminder(Reminder reminder) {
        if(reminder.getReminderDate() == null){
            throw new RuntimeException("Invalid reminder date");
        }
        if(reminder.getDescription() == null){
            throw new RuntimeException("Add reminder text");
        }
        Reminder savedReminder = reminderDAO.findById(reminder.getId()).get();
        savedReminder.setReminderDate(reminder.getReminderDate());
        savedReminder.setDescription(reminder.getDescription());
        savedReminder.setCompleted(reminder.isCompleted());
        savedReminder.setRemarks(reminder.getRemarks());
        savedReminder.setUserId(reminder.getUserId());
        return reminderDAO.save(savedReminder);
    }

    public boolean delete(String reminderId) {
        reminderDAO.deleteById(reminderId);
        return true;
    }

    public Reminder getReminder(String id) {
        return reminderDAO.findById(id).get();
    }

    public long reminderCount(JSONObject query) throws ParseException {
        return  reminderMongoDAO.getReminderCount(query);
    }

    public Page<Reminder> getReminders(JSONObject query) throws ParseException {
        PageRequest pageable = null;
        if(query.get("size") != null && query.get("page") != null && query.get("sort") != null){
            int page = (int) query.get("page");
            int size = (int) query.get("size");
            pageable = new PageRequest(page,size);
        }else{
            pageable = new PageRequest(0, Integer.MAX_VALUE);
        }
        long totalCount = reminderCount(query);
        List<Reminder> reminders = IteratorUtils.toList(reminderMongoDAO.getAllReminders(query,pageable).iterator());
        Map<String,String> usersMap = userManager.getUserNames(true);
        for(Reminder reminder:reminders){
            reminder.getAttributes().put("userName",usersMap.get(reminder.getUserId()));
        }
        Page<Reminder> page = new PageImpl<>(reminders, pageable, totalCount);
        return page;
    }

    public List<Reminder> getUpcomingReminders( ) {
        int buffer = Integer.parseInt(systemProperties.getProperty(SystemProperties.SysProps.EXPIRATION_BUFFER));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + buffer));
        List<Reminder> reminders = IteratorUtils.toList(reminderMongoDAO.getUpcomingReminders(calendar.getTime()).iterator());
        Map<String,String> usersMap = userManager.getUserNames(true);
        for(Reminder reminder:reminders){
            reminder.getAttributes().put("userName",usersMap.get(reminder.getUserId()));
        }
        return reminders;
    }

}