'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');
portalApp.service('bookingHelper', function() {
	
	this.bookingInfo = {};
	
	this.getTripInfo = function() {
		return this.bookingInfo;
	};

	this.setTripInfo = function(tripId, selectedSeats, boardingPoint) {
		this.bookingInfo.tripId = tripId;
		this.bookingInfo.selectedSeats = selectedSeats;
		this.bookingInfo.boardingPoint = boardingPoint;
		this.bookingInfo.totalSeats = selectedSeats.length;
	}
});
