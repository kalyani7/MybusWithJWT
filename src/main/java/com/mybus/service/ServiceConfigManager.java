package com.mybus.service;

import com.mybus.dao.RouteDAO;
import com.mybus.dao.ServiceConfigDAO;
import com.mybus.dao.impl.LayoutMongoDAO;
import com.mybus.dao.impl.ServiceConfigMongoDAO;
import com.mybus.model.Layout;
import com.mybus.model.Route;
import com.mybus.model.ServiceConfig;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServiceConfigManager{

    @Autowired
    private LayoutMongoDAO layoutMongoDAO;

    @Autowired
    private RouteDAO routeDAO;

    @Autowired
    private CityManager cityManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ServiceConfigDAO serviceConfigDAO;

    @Autowired
    private ServiceConfigMongoDAO serviceConfigMongoDAO;

    public List<Layout> getLayoutsForService() {
        return layoutMongoDAO.getLayoutsForService();
    }

    public JSONObject getRouteForService(String routeId) {
        JSONObject obj = new JSONObject();
        Route route = routeDAO.findById(routeId).get();
        Map<String,String> cityNamesMap = cityManager.getCityNamesMap();
        List<JSONObject> viaCitiesNames = new ArrayList<>();
        List<String> viaCitiesIds = route.getViaCities();
        for (int i = 0; i < viaCitiesIds.size(); i++) {
            JSONObject obj1 = new JSONObject();
            obj1.put("viaCityId",viaCitiesIds.get(i));
            obj1.put("viaCityName",cityNamesMap.get(viaCitiesIds.get(i)));
            viaCitiesNames.add(i,obj1);
        }
        obj.put("fromCityName",cityNamesMap.get(route.getFromCityId()));
        obj.put("toCityName",cityNamesMap.get(route.getToCityId()));
        obj.put("fromCityId",route.getFromCityId());
        obj.put("toCityId",route.getToCityId());
        obj.put("viaCitiesNames",viaCitiesNames);
        return obj;
    }

    public ServiceConfig addService(ServiceConfig service) {
        service.setOperatorId(sessionManager.getOperatorId());
        return serviceConfigDAO.save(service);
    }

    public Page<ServiceConfig> getAllServices(Pageable pageable) {
       long count = getServicesCount();
       List<ServiceConfig> serviceConfigs = serviceConfigMongoDAO.findAllServiceConfigs(pageable);
       Page<ServiceConfig> page = new PageImpl<>(serviceConfigs, pageable, count);
       return page;
    }

    public long getServicesCount() {
        return serviceConfigMongoDAO.getServicesCount();
    }

    public ServiceConfig getServiceConfig(String serviceConfigId) {
        return serviceConfigDAO.findById(serviceConfigId).get();
    }

    public boolean deleteServiceConfig(String serviceConfigId) {
        serviceConfigDAO.deleteById(serviceConfigId);
        return true;
    }

    public ServiceConfig updateServiceConfig(ServiceConfig serviceConfig) {
        ServiceConfig savedService = serviceConfigDAO.findById(serviceConfig.getId()).get();
        return savedService;
    }
}