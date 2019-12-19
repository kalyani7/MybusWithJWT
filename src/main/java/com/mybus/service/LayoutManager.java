package com.mybus.service;

import com.google.common.base.Preconditions;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.impl.LayoutMongoDAO;
import com.mybus.model.Layout;
import com.mybus.model.LayoutType;
import com.mybus.model.Row;
import com.mybus.model.Seat;
import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by schanda on 01/15/16.
 */
@Service
public class LayoutManager {

	private static final Logger logger = LoggerFactory.getLogger(LayoutManager.class);

	public static final int SEMI_SLEEPER_DEFAULT_LEFT_ROWS = 2;

	public static final int SEMI_SLEEPER_DEFAULT_RIGHT_ROWS = 2;

	public static final int SEMI_SLEEPER_DEFAULT_COLUMNS = 11;

	@Autowired
	private LayoutMongoDAO layoutMongoDAO;

	@Autowired
	private LayoutDAO layoutDAO;

	public boolean deleteLayout(String id) {
		Preconditions.checkNotNull(id, "The layout id can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting layout :[{}]" + id);
		}
		if (layoutDAO.findById(id).isPresent()) {
			layoutDAO.deleteById(id);
		} else {
			throw new RuntimeException("Unknown layout id");
		}
		return true;
	}

	public Layout updateLayout(Layout layout) {
		Preconditions.checkNotNull(layout, "The layout can not be null");
		Preconditions.checkNotNull(layout.getName(), "The layout name can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Saving layout :[{}]" + layout);
		}
		Layout layoutUpdated = null;
		try {
			layoutUpdated = layoutMongoDAO.update(layout);
		} catch (Exception e) {
			throw new RuntimeException("error updating layout ", e);
		}
		return layoutUpdated;
	}

	public Layout saveLayout(Layout layout) {
		Preconditions.checkNotNull(layout, "The layout can not be null");
		Preconditions.checkNotNull(layout.getName(), "The layout name can not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Saving layout :[{}]" + layout);
		}
		return layoutDAO.save(layout);
	}

	/**
     * Module to build a map of <layoutId, layoutName>,
     * @param allLayouts -- when true all the names will be returned, when false only active layout names are returned
     * @return
     */
    public Map<String, String> getLayoutNames(boolean allLayouts) {
        List<Layout> layouts = null;
        if(allLayouts) {
        	layouts = IteratorUtils.toList(layoutDAO.findAll().iterator());
        } else {
            //find only active cities
        	layouts = IteratorUtils.toList(layoutDAO.findByActive(true).iterator());
        }
        if (layouts == null || layouts.isEmpty() ) {
            return new HashMap<>();
        }
        Map<String, String> map = layouts.stream().collect(
                Collectors.toMap(Layout::getId, layout -> layout.getName()));
        return map;
    }
    
	public Layout getDefaultLayout(LayoutType layoutType) {
		Layout layout = null;
		if (LayoutType.SLEEPER.equals(layoutType)) {
			layout = constructSleeperLayout();
		} else {
			layout = constructSemiSleeperLayout();
		}
		return layout;
	}

	/**
	 * Default layout for Semi-sleeper
	 */
	private Layout constructSemiSleeperLayout() {
		Layout layout = new Layout();
		layout.setActive(true);
		layout.setType(LayoutType.AC_SEMI_SLEEPER);
		char c = 'D';
		List<Row> rows = new ArrayList<Row>();

		// Right side rows..
		for (int i = 0; i < SEMI_SLEEPER_DEFAULT_RIGHT_ROWS; i++) {
			Row row = new Row();
			row.setMiddleRow(false);
			row.setWindow(i == 0);
			List<Seat> seats = new ArrayList<Seat>();
			for (int j = 0, k = i; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++, k += 1) {
				Seat seat = new Seat();
				seat.setActive(true);
				seat.setDisplay(true);
				seat.setDisplayName(j == 0 ? String.valueOf(c--) : "R" + k++);
				seats.add(seat);
			}
			row.setSeats(seats);
			rows.add(row);
		}

		// Middle row..
		rows.add(constructMiddleRow());

		// Left side rows..
		for (int i = 0; i < SEMI_SLEEPER_DEFAULT_LEFT_ROWS; i++) {
			Row row = new Row();
			row.setMiddleRow(false);
			row.setWindow(i + 1 == SEMI_SLEEPER_DEFAULT_LEFT_ROWS);
			List<Seat> seats = new ArrayList<Seat>();
			for (int j = 0, k = SEMI_SLEEPER_DEFAULT_LEFT_ROWS - i; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++) {
				Seat seat = new Seat();
				seat.setActive(!(j == 0 && i == 0));
				seat.setDisplay(!(j == 0 && i == 0));
				seat.setDisplayName(j == 0 ? String.valueOf(c--) : "L" + k);
				k += j == 0 ? 0 : 2;
				seats.add(seat);
			}
			row.setSeats(seats);
			rows.add(row);
		}

		layout.setRows(rows);
		return layout;
	}

	private Row constructMiddleRow() {
		Row middleRow = new Row();
		middleRow.setMiddleRow(true);
		List<Seat> seats = new ArrayList<Seat>();
		for (int j = 1; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++) {
			Seat seat = new Seat();
			seat.setActive(false);
			seat.setDisplay(false);
			seat.setDisplayName("");
			seats.add(seat);
		}
		Seat seat = new Seat();
		seat.setActive(true);
		seat.setDisplay(true);
		seat.setDisplayName(String.format("M%s", SEMI_SLEEPER_DEFAULT_COLUMNS * 2 - 1));
		seats.add(seat);
		middleRow.setSeats(seats);
		return middleRow;
	}

	/**
	 * Default layout for Sleeper
	 */
	private Layout constructSleeperLayout() {
		return null;
	}

	public long getLayoutsCount(JSONObject query) {
		return layoutMongoDAO.getLayoutsCount(query);
	}

	public Page<Layout> getAllLayouts(JSONObject query) {
		PageRequest pageRequest = PageRequest.of(0,Integer.MAX_VALUE);
		if(query.get("size") != null && query.get("page") != null){
			pageRequest = PageRequest.of((int)query.get("page"),(int)query.get("size"));
		}
		List<Layout> layouts = layoutMongoDAO.getAllLayouts(query,pageRequest);
		long count = getLayoutsCount(query);
		Page<Layout> page = new PageImpl<>(layouts, pageRequest, count);
		return page;
	}

}
