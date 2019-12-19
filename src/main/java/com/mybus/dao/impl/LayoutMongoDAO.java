package com.mybus.dao.impl;

import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import org.apache.commons.collections.IteratorUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by schanda on 1/16/16.
 */
@Repository
public class LayoutMongoDAO {

	@Autowired
	private LayoutDAO layoutDAO;
	@Autowired
	private MongoQueryDAO mongoQueryDAO;
	@Autowired
	private MongoTemplate mongoTemplate;

	public Layout save(Layout layout) {
		return layoutDAO.save(layout);
	}

	public Layout update(Layout layout) throws Exception {
		Layout dbCopy = layoutDAO.findById(layout.getId()).get();
		dbCopy.merge(layout);
		return layoutDAO.save(dbCopy);
	}

   /* public List<Layout> getAllLayouts() {
		Query query = new Query();
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("totalSeats");
		query.fields().include("rows");
		query.fields().include("seatsPerRow");
		List<Layout> layouts = IteratorUtils.toList(mongoTemplate.find(query,Layout.class).iterator());
		return layouts;
	}*/

	public long getLayoutsCount(JSONObject query) {
		Query q = new Query();
		return mongoTemplate.count(q,Layout.class);
	}

	public List<Layout> getLayoutsForService(){
		Query query = new Query();
		query.fields().include("name");
		return mongoTemplate.find(query,Layout.class);
	}

	public List<Layout> getAllLayouts(JSONObject query, PageRequest pageRequest) {
		Query q = new Query();
		q.fields().include("name");
		q.fields().include("type");
		q.fields().include("totalSeats");
		q.fields().include("rows");
		q.fields().include("seatsPerRow");
		if (pageRequest != null) {
			q.with(pageRequest);
		}
		List<Layout> layouts = IteratorUtils.toList(mongoTemplate.find(q,Layout.class).iterator());
		return layouts;
	}
}
