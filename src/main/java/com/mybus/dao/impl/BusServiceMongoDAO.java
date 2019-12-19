package com.mybus.dao.impl;

import com.mongodb.client.result.UpdateResult;
import com.mybus.dao.BusServiceDAO;
import com.mybus.model.BusService;
import com.mybus.service.SessionManager;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by schanda on 02/02/16.
 */
@Repository
public class BusServiceMongoDAO {

	@Autowired
	private BusServiceDAO busServiceDAO;

	@Autowired
	private SessionManager sessionManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	public BusService save(BusService busService) {
		return busServiceDAO.save(busService);
	}

	public BusService update(BusService busService) throws Exception {
		BusService dbCopy = busServiceDAO.findById(busService.getId()).get();
		dbCopy.merge(busService);
		return busServiceDAO.save(dbCopy);
	}

	/**
	 * Update amenityIds for service(s). The List<JSONObject>  should in the below format
	 * [{
	 *     "serviceId":"1234", "amenityIds":["2323","33423","33523"]
	 * },{
	 *     "serviceId":"1434", "amenityIds":["233433","3333423"]
	 * }]
	 *
	 * @return
	 */
	public boolean updateServiceAmenities(List<JSONObject> services) {
		/*
		BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BusService.class);
		for (JSONObject service: services) {
			Query query = new Query(where(AbstractDocument.KEY_ID).is(service.get("serviceId").toString()));
			Update updateOp = new Update();
			updateOp.set("amenityIds", service.get("amenityIds"));
			ops.updateOne(query, updateOp);
		}
		BulkWriteResult result = ops.execute();
		return result.getModifiedCount() == services.size(); */

		for (JSONObject jsonObject : services) {
			Update updateOp = new Update();
			updateOp.set("amenityIds", jsonObject.get("amenityIds"));
			final Query query = new Query();
			query.addCriteria(where("_id").is(jsonObject.get("serviceId")));
			UpdateResult writeResult =  mongoTemplate.updateMulti(query, updateOp, BusService.class);
			if(writeResult.getModifiedCount() != 1) {
				return false;
			}
		}
		return true;
	}
}
