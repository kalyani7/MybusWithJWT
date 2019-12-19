/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');

portalApp.factory('b2cHomeManager', function ($rootScope, $http, $filter,$log, $window) {
	var busJourney = {};
	return {
		getAllcites:function(callback){
			$http.get("/api/v1/stations")
			.success(function(data){
				callback(data);
			}).
			error(function(error){

			});
		},
		getBusJourney:function(){
			return busJourney;
		},
		getSearchForBus:function(busJourney,callback){

			if(busJourney.journeyType=='ONE_WAY'){
				busJourney.returnJourney=$filter('date')(angular.copy(busJourney.dateOfJourney),'yyyy-MM-dd');
			} else{
				busJourney.returnJourney=$filter('date')(busJourney.returnJourney,'yyyy-MM-dd');
			}
			busJourney.dateOfJourney =  $filter('date')(busJourney.dateOfJourney,'yyyy-MM-dd');
			$http.get("/api/v1/searchForBus", {params : busJourney})
			.success(function(data){
				searchID = data;
				callback(data);
			}).
			error(function(error){

			});
		}
	}
	
});
