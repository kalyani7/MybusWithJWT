/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');
portalApp.factory('b2cEticketManager', function ($http, $log) {
	return {
		getBookedTicket:function(bookingId,callback){
			$http.get("/api/v1/getBookedTicket")
			.success(function(data){
				ticketinfo = data;
				callback(data);
			}).
			error(function(error){
			});
		},
		getTicktPassingerinfo:function(paymentId,callback){
			$http.get("/api/v1/getTicketPassengerInfo")
			.success(function(data){
				callback(data);
			}).
			error(function(error){
			});
		}
	}
});