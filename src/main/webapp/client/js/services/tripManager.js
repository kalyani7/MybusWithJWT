
var portalApp = angular.module('myBus');
portalApp.factory('tripManager',function($rootScope,$http,$window,$log){
	
	return {
		getAllTrips : function(callback){
			$http.get("").success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		},
		addTrip: function(trip,callback){
			
			$http.post("",trip).success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		},
		getTripByID : function(id,callback){
			$http.get("",id).success(function(data){
				callback(data)
			}).
			error(function(error){
				
			})
		},
		selectSeat : function(serviceId,seat,rowNumber){
			$http.get("/api/v1/services/"+id , {
				
			}).success(function(data){
				callback(data)
			}).
			error(function(error){
				
			})
		},
		getLayoutByID : function(id,callback){
			$http.get("/api/v1/layout/"+id).success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		},
		searchBuses : function(searchFields,callback){
			$http.get("/api/v1/buses",{
				params:{ fromCityId : searchFields.fromCity,
					     toCityId :searchFields.toCity,
					     travelDate :searchFields.tripDate
				       }
			}).success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		}
		
	}
});