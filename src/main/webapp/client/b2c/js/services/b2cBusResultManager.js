/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');

portalApp.factory('b2cBusResultsManager', function ($rootScope, $http, $log, $window) {
	var busJourney = {};
	return {

		getbusJourney:function(callback){
			$http.get("/api/v1/getsearchForBus")
			.success(function(data){
				busJourney = data;
				callback(data);
			}).
			error(function(error){

			});
		},
		getAvailableTrips:function(journeyType,callback){
			$http.get("/api/v1/availabletrip?journeyType="+journeyType)
			.success(function(data){
				callback(data);
			})
			.error(function(error){

			});
		},
		seatLayout:function(layoutID,callback){
			$http.get("/api/v1/busLayout/"+layoutID)
			.success(function(data){
				$log.debug(data);
				callback(data);
			}).
			error(function(error){

			});
		},
		blockSeat:function(busJourney,callback){
			$http.post('/api/v1/blockSeat',busJourney)
			.success(function (data) {
				callback(data);
				$log.debug(data)
			}).error(function(err,status) {
				$log.debug(err)
			});
		}
	}
	
});