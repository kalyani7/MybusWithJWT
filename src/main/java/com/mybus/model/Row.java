package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@ToString
@ApiModel(value = "Row")
@AllArgsConstructor
@NoArgsConstructor
public class Row {
	
	@Getter
	@Setter
	private List<Seat> seats;
	
	@Getter
	@Setter
	private boolean middleRow;

    @Getter
    @Setter
    private long noOfSeats;

    @Getter
    @Setter
    private String rowType;

    @Getter
    @Setter
    private boolean window;

    @Getter
    @Setter
    private boolean sideSleeper;

}
