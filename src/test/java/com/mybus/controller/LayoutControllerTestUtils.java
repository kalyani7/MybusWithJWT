package com.mybus.controller;

import com.mybus.model.Layout;
import com.mybus.model.LayoutType;
import com.mybus.model.Row;
import com.mybus.model.Seat;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.mybus.service.LayoutManager.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by schanda on 1/17/16.
 */

public class LayoutControllerTestUtils {

	/**
	 * Default layout for Semi-sleeper
	 */
	public static Layout constructSemiSleeperLayout(String id, String layoutName, LayoutType layoutType, int totalSeats) {
		Layout layout = new Layout();
		layout.setId(id);
		layout.setName(layoutName);
		layout.setActive(true);
		layout.setType(layoutType);
		layout.setTotalSeats(totalSeats);
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

	private static Row constructMiddleRow() {
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

	public static void validateResult(ResultActions actions) throws Exception {

		actions.andExpect(jsonPath("$.active").value(true));
		actions.andExpect(jsonPath("$.type").value("AC_SEMI_SLEEPER"));

		// validating front seats
		actions.andExpect(jsonPath("$.rows[0].window").value(true));
		actions.andExpect(jsonPath("$.rows[0].seats[0].displayName").value("D"));
		actions.andExpect(jsonPath("$.rows[0].seats[0].display").value(true));		
		actions.andExpect(jsonPath("$.rows[0].seats[0].active").value(true));

		actions.andExpect(jsonPath("$.rows[1].window").value(false));
		actions.andExpect(jsonPath("$.rows[1].seats[0].displayName").value("C"));
		actions.andExpect(jsonPath("$.rows[1].seats[0].display").value(true));		
		actions.andExpect(jsonPath("$.rows[1].seats[0].active").value(true));

		actions.andExpect(jsonPath("$.rows[2].window").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[0].displayName").value(""));
		actions.andExpect(jsonPath("$.rows[2].seats[0].display").value(false));		
		actions.andExpect(jsonPath("$.rows[2].seats[0].active").value(false));

		actions.andExpect(jsonPath("$.rows[3].window").value(false));
		actions.andExpect(jsonPath("$.rows[3].seats[0].displayName").value("B"));
		actions.andExpect(jsonPath("$.rows[3].seats[0].display").value(false));		
		actions.andExpect(jsonPath("$.rows[3].seats[0].active").value(false));

		actions.andExpect(jsonPath("$.rows[4].window").value(true));
		actions.andExpect(jsonPath("$.rows[4].seats[0].displayName").value("A"));
		actions.andExpect(jsonPath("$.rows[4].seats[0].display").value(true));		
		actions.andExpect(jsonPath("$.rows[4].seats[0].active").value(true));

		// validating middle row
		actions.andExpect(jsonPath("$.rows[2].window").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[0].displayName").value(""));
		actions.andExpect(jsonPath("$.rows[2].seats[0].display").value(false));		
		actions.andExpect(jsonPath("$.rows[2].seats[0].active").value(false));

		actions.andExpect(jsonPath("$.rows[2].window").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[10].displayName").value(
				String.format("M%s", SEMI_SLEEPER_DEFAULT_COLUMNS * 2 - 1)));
		actions.andExpect(jsonPath("$.rows[2].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[2].seats[10].active").value(true));

		// validating back seats
		actions.andExpect(jsonPath("$.rows[0].window").value(true));
		actions.andExpect(jsonPath("$.rows[0].seats[10].displayName").value("R19"));
		actions.andExpect(jsonPath("$.rows[0].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[0].seats[10].active").value(true));

		actions.andExpect(jsonPath("$.rows[1].window").value(false));
		actions.andExpect(jsonPath("$.rows[1].seats[10].displayName").value("R20"));
		actions.andExpect(jsonPath("$.rows[1].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[1].seats[10].active").value(true));

		actions.andExpect(jsonPath("$.rows[2].window").value(false));
		actions.andExpect(jsonPath("$.rows[2].seats[10].displayName").value("M21"));
		actions.andExpect(jsonPath("$.rows[2].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[2].seats[10].active").value(true));

		actions.andExpect(jsonPath("$.rows[3].window").value(false));
		actions.andExpect(jsonPath("$.rows[3].seats[10].displayName").value("L20"));
		actions.andExpect(jsonPath("$.rows[3].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[3].seats[10].active").value(true));

		actions.andExpect(jsonPath("$.rows[4].window").value(true));
		actions.andExpect(jsonPath("$.rows[4].seats[10].displayName").value("L19"));
		actions.andExpect(jsonPath("$.rows[4].seats[10].display").value(true));		
		actions.andExpect(jsonPath("$.rows[4].seats[10].active").value(true));
	}

}