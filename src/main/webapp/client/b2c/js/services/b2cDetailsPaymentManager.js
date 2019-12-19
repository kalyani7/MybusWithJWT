/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');
portalApp.factory('b2cDetailsPaymentManager', function ($rootScope, $http, $log, $window) {
	var busJourney = {};
	return {
		getbusJourney:function(callback){
			$http.get("/api/v1/getblockInfo")
			.success(function(data){
				busJourney = data;
				callback(data);
			}).
			error(function(error){
			});
		},	
	}
});