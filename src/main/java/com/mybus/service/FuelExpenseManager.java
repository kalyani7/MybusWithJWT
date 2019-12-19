package com.mybus.service;

import com.mybus.dao.FuelExpenseDAO;
import com.mybus.dao.VehicleDAO;
import com.mybus.dao.impl.FuelExpenseMongoDAO;
import com.mybus.dao.impl.ServiceReportMongoDAO;
import com.mybus.dao.impl.VehicleMongoDAO;
import com.mybus.model.FuelExpense;
import com.mybus.model.Vehicle;
import com.mybus.util.ServiceUtils;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class FuelExpenseManager{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private FuelExpenseDAO fuelExpenseDAO;
    @Autowired
    private FuelExpenseMongoDAO fuelExpenseMongoDAO;

    @Autowired
    private VehicleManager vehicleManager;
    @Autowired
    private SuppliersManager supplierManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private ServiceReportMongoDAO serviceReportMongoDAO;
    @Autowired
    private com.mybus.dao.impl.ReminderMongoDAO ReminderMongoDAO;
    @Autowired
    private VehicleDAO vehicleDAO;

    @Autowired
    private VehicleMongoDAO vehicleMongoDAO;

    public JSONObject addFuelExpense(FuelExpense currentFuelExpense)throws ParseException{
        currentFuelExpense.setOperatorId(sessionManager.getOperatorId());
//        Map<String, String> vehicleNumbers = vehicleManager.findVehicleNumbers();
        Vehicle loadVehicle=vehicleDAO.findById(currentFuelExpense.getVehicleId()).get();
        String vehicleNumber=loadVehicle.getRegNo();
        String date=currentFuelExpense.getDateString();
         currentFuelExpense.setServiceName(serviceReportMongoDAO.findServiceName(date,vehicleNumber));
        if(currentFuelExpense.isFillup()) {
            calculateFuelEconomy(currentFuelExpense);
        }
        JSONObject q = new JSONObject();
        q.put("vehicleId", currentFuelExpense.getVehicleId());
        q.put("date", currentFuelExpense.getDate());
        q.put("mileage", currentFuelExpense.getOdometer());
        boolean b = ReminderMongoDAO.updateRemainder(q);
        fuelExpenseDAO.save(currentFuelExpense);
        vehicleMongoDAO.updateMilage(currentFuelExpense.getVehicleId(), currentFuelExpense.getOdometer());
        JSONObject obj=new JSONObject();
        obj.put("status",b);
        obj.put("vehicle",vehicleNumber);
        return  obj;
    }

    /**
     * Method to calculate the fuel economy
     * @param currentExpense
     * @throws ParseException
     */
    private void calculateFuelEconomy(FuelExpense currentExpense)throws  ParseException{
        JSONObject  q=new JSONObject();
        q.put("vehicleId",currentExpense.getVehicleId());
        q.put("date",currentExpense.getDate());
        List<FuelExpense> fuelExpenses = IteratorUtils.toList(
                fuelExpenseMongoDAO.findPreviousFuelExpenses(q ).iterator());
        double quantity = currentExpense.getQuantity();
        FuelExpense oldFuelExpense = null;
        for(int i=0;i<fuelExpenses.size();i++) {
            oldFuelExpense=fuelExpenses.get(i);
            if(oldFuelExpense.isFillup()&&!oldFuelExpense.getId().equals(currentExpense.getId())){
                break;
            } else {
                if (currentExpense.getId() == null ||
                        (currentExpense.getId() != null && !currentExpense.getId().equals(fuelExpenses.get(i).getId()))) {
                    quantity += oldFuelExpense.getQuantity();
                }
            }
        }
        if(oldFuelExpense != null) {
            double totalDistance = currentExpense.getOdometer() - oldFuelExpense.getOdometer();
            currentExpense.setEconomy(totalDistance/quantity);
        }
    }


    public JSONObject updateFuelExpense(FuelExpense fuelExpense)throws ParseException {
        FuelExpense savedFuelExpense = fuelExpenseDAO.findById(fuelExpense.getId()).get();
        savedFuelExpense.setDate(fuelExpense.getDate());
        savedFuelExpense.setDateString(ServiceUtils.formatDate(fuelExpense.getDate()));
        savedFuelExpense.setSupplierId(fuelExpense.getSupplierId());
        savedFuelExpense.setPaid(fuelExpense.isPaid());
        savedFuelExpense.setOdometer(fuelExpense.getOdometer());
        savedFuelExpense.setOperatorId(sessionManager.getOperatorId());
        savedFuelExpense.setQuantity(fuelExpense.getQuantity());
        savedFuelExpense.setRate(fuelExpense.getRate());
        savedFuelExpense.setCost(fuelExpense.getCost());
        savedFuelExpense.setFillup(fuelExpense.isFillup());
        savedFuelExpense.setRemarks(fuelExpense.getRemarks());
        Vehicle loadVehicle=vehicleDAO.findById(fuelExpense.getVehicleId()).get();
        String vehicleNumber=loadVehicle.getRegNo();
        String date = fuelExpense.getDateString();
        savedFuelExpense.setServiceName(serviceReportMongoDAO.findServiceName(date, vehicleNumber));
        savedFuelExpense.setVehicleId(fuelExpense.getVehicleId());
        if(fuelExpense.isFillup()){
            calculateFuelEconomy(savedFuelExpense);
        }else{
            savedFuelExpense.setEconomy(0);
        }
        vehicleMongoDAO.updateMilage(fuelExpense.getVehicleId(), fuelExpense.getOdometer());
        JSONObject q = new JSONObject();
        q.put("vehicleId", fuelExpense.getVehicleId());
        q.put("date", fuelExpense.getDate());
        q.put("mileage", fuelExpense.getOdometer());
        boolean b = ReminderMongoDAO.updateRemainder(q);

        fuelExpenseDAO.save(savedFuelExpense);
        JSONObject obj=new JSONObject();
        obj.put("status",b);
        obj.put("vehicle",vehicleNumber);
        return  obj;
    }

    public Page<FuelExpense> getAllFuelExpenses(String dateString, Pageable pageable)throws ParseException {
        long total = count(dateString);
        List<FuelExpense> fuelExpenses = IteratorUtils.toList(fuelExpenseMongoDAO.findAllFuelExpenses( dateString,pageable).iterator());
        if(fuelExpenses.size() > 0){
            fillNames(fuelExpenses);
        }
        Page<FuelExpense> page = new PageImpl<>(fuelExpenses, pageable, total);
        return page;
    }

    private void fillNames(List<FuelExpense> fuelExpenses) {
        Map<String, String> vehicleNumbers = vehicleManager.findVehicleNumbers();
        Map<String, String> supplierNames = supplierManager.findNames();
        Map<String, String> userNames = userManager.getUserNames(true);
        fuelExpenses.stream().forEach(expense -> {
            expense.getAttributes().put("vehicleNumber", vehicleNumbers.get(expense.getVehicleId()));
            expense.getAttributes().put("supplierName", supplierNames.get(expense.getSupplierId()));
            expense.getAttributes().put("createdBy", userNames.get(expense.getCreatedBy()));
        });
    }

    public Page<FuelExpense> searchFuelExpenses(JSONObject query)throws ParseException {
//        long total = fuelExpenseMongoDAO.getCount(query);
        List<FuelExpense> fuelExpenses = IteratorUtils.toList(fuelExpenseMongoDAO.findAllFuelExpenses( query).iterator());
        if(fuelExpenses.size() > 0){
            fillNames(fuelExpenses);
        }
        Page<FuelExpense> page = new PageImpl<>(fuelExpenses);
        return page;
    }

    public FuelExpense getFuelExpense(String id) {
        FuelExpense fuelExpense = fuelExpenseDAO.findById(id).get();
        return fuelExpense;
    }
    public boolean delete(String id) {
        fuelExpenseDAO.deleteById(id);
        return true;
    }
    public long count(String date) throws ParseException {
        return  fuelExpenseMongoDAO.count(date);
    }
    public Page<FuelExpense> updateServiceNames(String date, Pageable pageable)throws ParseException {
        List<FuelExpense> fuelExpenses = IteratorUtils.toList(fuelExpenseMongoDAO.findAllFuelExpenses( date,pageable).iterator());
        for (int i=0;i<fuelExpenses.size();i++)
        {
            FuelExpense fuelExpense=fuelExpenses.get(i);
            if(fuelExpense.getServiceName()==null){

            updateFuelExpense(fuelExpense);
            }
        }
        Page<FuelExpense> page = new PageImpl<>(fuelExpenses);
        return page;

    }
}