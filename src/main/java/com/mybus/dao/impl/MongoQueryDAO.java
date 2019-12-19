package com.mybus.dao.impl;

import com.mongodb.BasicDBObject;
import com.mybus.service.SessionManager;
import com.mybus.util.ServiceUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * A DAO implementation for querying subset of fields from database
 *
 * Created by skandula on 2/13/16.
 */
@Repository
public class MongoQueryDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SessionManager sessionManager;

    public long count(final Class className, String collectionName, final String[] fields,
                      final JSONObject queryInfo) {
        Query query = prepareQuery(collectionName, fields, queryInfo, null);
        return mongoTemplate.count(query, collectionName);
    }

    /**@link getDocuments()
     * @param className
     * @param collectionName
     * @param fields
     * @param queryInfo
     * @param pageable
     * @return
     */

    public Iterable getDocuments(final Class className, String collectionName, final String[] fields,
                                    final JSONObject queryInfo, final Pageable pageable) {
        /*Preconditions.checkArgument(mongoTemplate.collectionExists(collectionName),
                new BadRequestException("No collection found with name " + collectionName));*/
        Query query = prepareQuery(collectionName, fields, queryInfo, pageable);
        return mongoTemplate.find(query, className, collectionName);
    }

    private Query prepareQuery(String collectionName, String[] fields, JSONObject queryInfo, Pageable pageable) {
        Query query = new Query();
        if (fields != null) {
            for(String field :fields){
                query.fields().include(field);
            }
        }
        if(queryInfo != null && queryInfo.get(SessionManager.OPERATOR_ID) == null) {
            query.addCriteria(where(SessionManager.OPERATOR_ID).is(sessionManager.getOperatorId()));
        }
        if (queryInfo != null) {
            for(Object key:queryInfo.keySet()) {
                if(collectionName.equals("shipment") && key.toString().equals("dispatchDate")) {
                    String dateValues[] = queryInfo.get(key.toString()).toString().split(",");
                    Date start = null;
                    Date end = null;
                    try {
                        start = ServiceUtils.parseDate(dateValues[0]);
                        if(dateValues.length == 2) {
                            end = ServiceUtils.parseDate(dateValues[1]);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    query.addCriteria(where(key.toString()).gte(start));
                } else if(queryInfo.get(key.toString()) instanceof Date){
                    query.addCriteria(where(key.toString()).is(queryInfo.get(key.toString())));
                }else {
                    query.addCriteria(where(key.toString()).is(queryInfo.get(key.toString())));
                }
            }
        }
        if (pageable != null) {
            query.with(pageable);
        }
        return query;
    }

    /**
     * * @deprecated  As of release 01-Mar-2017, replaced by {@link #getDocuments
     * @param collectionName
     * @param fields
     * @param queryInfo
     * @param pageable
     * @return
     */
    @Deprecated
    public Iterable getDocuments(String collectionName, final String[] fields,
                                 final JSONObject queryInfo, final Pageable pageable) {
        return getDocuments(BasicDBObject.class, collectionName, fields, queryInfo, pageable);
    }

    public static void createTimeFrameQuery(String key, Date start, Date end, List<Criteria> criteria) {

        if (start == null && end == null) {
            // No timeframe specified, so search over everything
            return;
        } else if (end == null) {
            criteria.add(where(key).gte(start));
        } else if (start == null) {
            criteria.add(where(key).lte(end));
        } else {
            criteria.add(where(key).gte(start).lte(end));
        }
    }
}
