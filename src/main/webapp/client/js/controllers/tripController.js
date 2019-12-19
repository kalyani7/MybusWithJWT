"use strict";
/*global angular, _*/

angular.module('myBus.tripModule', ['ngTable', 'ui.bootstrap'])
.controller("TripController",function($scope, $rootScope, $log, $filter,NgTableParams,tripManager,cityManager,$cacheFactory,$location,bookingHelper){

	var tripCtrl = this;
	
	tripCtrl.trips = [];
	
	//this reference has to be removed
	tripCtrl.trip = {};
	
	tripCtrl.currentPageTrips = [];
	
	tripCtrl.busLayouts = {};
	
	var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};
	
	tripCtrl.searchFields = {
			fromCity : '',
			toCity : '',
			tripDate : ''
	};
	
	tripCtrl.getAllTrips = tripManager.getAllTrips(function(data){
		tripCtrl.trips =data;
	});

	tripCtrl.addTrip = tripManager.addTrip($scope.trip,function(data){
	});
	
	tripCtrl.getTripById = tripManager.getTripByID($scope.id,function(data){
		tripCtrl.trip = data;
	});
	
	tripCtrl.searchTrips = function(){
		 var searchFields = angular.copy(tripCtrl.searchFields);
		 searchFields.tripDate = $filter('date')(searchFields.tripDate,'yyyy-MM-dd');
		 tripCtrl.trips = tripManager.searchBuses(searchFields,function(data){
			 tripCtrl.trips = data;
		 })
	};
	
	$scope.loadFromCities = cityManager.getCities(function(data){
		tripCtrl.cities = data;
	})
	
	tripCtrl.resetTrip = function(){
		tripCtrl.trip= {};
	};
	
	
	tripCtrl.getTrip = function(tripId){
		 $log.debug("Selected trip..." + tripId);
		 var selectedTrip = {};
		for(var i=0; i<tripCtrl.trips.length ; i++)	{
			if ( tripCtrl.trips[i].id == tripId) {
				selectedTrip = tripCtrl.trips[i];
				tripCtrl.trip = tripCtrl.trips[i];
			}
		}
		
		tripCtrl.createLayout(tripCtrl.trip);
		//call this method if already not loaded
		cityManager.getBoardingPoints(tripCtrl.trip.fromCityId,function(data){
			var cityBoardingPoints = data;
			var tripBoardingPoints = [];
			var counter = 0;
			for(var i=0;i<cityBoardingPoints.length;i++) {
				for(var j=0;j<tripCtrl.trip.boardingPoints.length;j++) {
					if ( tripCtrl.trip.boardingPoints[j].refId === cityBoardingPoints[i].id) {
						tripBoardingPoints[counter] = cityBoardingPoints[i];
						counter++;
					}
				}
			}
			//initialize tripInfo
			if (tripCtrl.busLayouts[tripCtrl.trip.id] == undefined){
				tripCtrl.busLayouts[tripCtrl.trip.id]={};
			}
			var tripInfo = tripCtrl.busLayouts[tripCtrl.trip.id];
			tripInfo["boardingPoints"] = tripBoardingPoints;
			tripInfo["selectedSeats"] = [];
			tripInfo["selectedBoardingPoint"] = null;
			tripInfo["showLayout"] = true;
			tripInfo["showContinue"] = false;
			tripCtrl.busLayouts[tripCtrl.trip.id] = tripInfo;
		})
	};
	
	tripCtrl.createLayout = function(trip) {
		 $scope.showLayout= true;
		 var tripId = trip.id;
         var cache = $cacheFactory.get($rootScope.id);
         var busLayout = {};
//         if (tripCtrl.busLayouts.hasOwnProperty(tripId) && typeof tripCtrl.busLayouts[tripId].busLayout != undefined) {
//        	 return;
//         }
		 if(cache) {
		     busLayout = cache.get(trip.layoutId);
		 } 
		 if (typeof  busLayout != "undefined" && !busLayout.hasOwnProperty('id')) {
			tripManager.getLayoutByID(trip.layoutId, function(layout) {
				tripCtrl.populateLayoutInfo(layout);
				//group with trip Id
				if (tripCtrl.busLayouts[tripId] == undefined){
					tripCtrl.busLayouts[tripId]={};
				}
				var tripInfo = tripCtrl.busLayouts[tripId];
				tripInfo["busLayout"] = layout;
				tripCtrl.busLayouts[tripId] = tripInfo;
			});
		 } else {
			 tripCtrl.populateLayoutInfo(busLayout);
			//group with trip Id
			if (tripCtrl.busLayouts[tripId] == undefined){
				tripCtrl.busLayouts[tripId]={};
			}
			var tripInfo = tripCtrl.busLayouts[tripId];
			tripInfo["busLayout"] = busLayout;
			tripCtrl.busLayouts[tripId] = tripInfo;
		 }
	};
	
	tripCtrl.populateLayoutInfo= function(busLayout) {
		 if(busLayout.type === 'SLEEPER'){
			 busLayout.sleeper = true;
			 busLayout.layoutCls = 'sleeper';
         }else{
        	 busLayout.layoutCls = 'seat';
         }
		 var rows = angular.copy(busLayout.rows);
		 
		 if(busLayout.sleeper && busLayout.seatsPerRow && busLayout.totalRows){
             for(var k = 0; k < 2; k++){
                 if(k===0){
                	 busLayout.upper = getSeats(busLayout,true, rows);
                	 busLayout.upperHeader = 'Upper';
                 }else{
                	 busLayout.lower = getSeats(busLayout,true, rows);
                	 busLayout.lowerHeader = 'Lower';
                 }
             }
         } else if(busLayout.seatsPerRow && busLayout.totalRows){
        	 busLayout.rows = getSeats(busLayout,false, rows);
         }
	};
	
	function getName(id){
        return $filter('filter')(seatNames.seats, {id: id })[0];
    };
	
	
	function getSeats(busLayout,sleeper, oldrows){
        var rows = [];
        var middleseat = busLayout.middleRowSeat;
        var  middleseatpos = busLayout.middleRowPosition;
        var cols = busLayout.seatsPerRow;

        if(sleeper && cols > 2){
            cols = 2;
        }

        if(middleseatpos > 0){
            cols = parseInt(cols) +1;
        }

        if (cols > 4){
        	busLayout.isBig = true;
        }

        for (var i = 1; i <= cols; i++){
            var seats = [];
            if(i === parseInt(middleseatpos)){
                for (var j = 1; j <= busLayout.totalRows; j++){
                    var number = getName(j).name+''+i;
                    console.log(j+','+busLayout.totalRows);
                    if(angular.equals(middleseat, true) && angular.equals(j, parseInt(busLayout.totalRows))){
                        if(!sleeper){
                        	busLayout.totalSeats = busLayout.totalSeats + 1;
                            seats.push({
                            	number : number, 
                            	[number]: number
                            });
                        }
                    }else{
                        seats.push({number : null, [number]: null});
                    }
                }
            }else{
                for (var j = 1; j <= busLayout.totalRows; j++){
                    var number = getName(j).name+''+i;
                    var displayName = number;
                    if(oldrows && !sleeper){
//                        console.log(rows);
//                        var row = oldrows[i-1].seats;
//                        displayName = $filter('filter')(row, {number: number})[0].displayName;
                    }
                    busLayout.totalSeats = busLayout.totalSeats + 1;
                    seats.push({number : number, [number]: displayName});
                }
            }
            rows.push({seats :seats})
        }
        return rows;
    };
    
    tripCtrl.getSeatName = function(seat){
        return seat.number;
    };

    
    tripCtrl.markSeatForSelection = function(tripId,seat , rowNumber) {
    	var index = tripCtrl.busLayouts[tripId].selectedSeats.length;
    	tripCtrl.busLayouts[tripId].selectedSeats[index] = rowNumber + "-" + seat.number;
    	if(tripCtrl.busLayouts[tripId].selectedBoardingPoint != null){
    		tripCtrl.busLayouts[tripId].showContinue = true;
    	}
    };
    
    tripCtrl.proceedForBooking = function(tripId) {
    	$location.url("/booking");
    	//change selectedBoardingPoint to name instead of id 
    	bookingHelper.setTripInfo(tripId,tripCtrl.busLayouts[tripId].selectedSeats,tripCtrl.busLayouts[tripId].selectedBoardingPoint);
    };
    
    $scope.getMatchingClass = function(tripId,rowNumber,seat) {
    	if ( seat.number == null) {
    		return "";
    	} 
    	if (tripCtrl.busLayouts[tripId].selectedSeats !== undefined) {
	    	if ( tripCtrl.busLayouts[tripId].selectedSeats.indexOf(rowNumber + "-" + seat.number) !== -1) {
	    		return "selectingSeat";
	    	}
    	}
	    return "seat";
    };
    
    tripCtrl.enableContinue = function(tripId){
    	if( tripCtrl.busLayouts[tripId].selectedSeats.length > 0) {
    		tripCtrl.busLayouts[tripId].showContinue = true;
    	}
    }
	
    
	tripCtrl.tripsTableParams = new NgTableParams({
         page: 1,
         count: 25,
         sorting: {
             tripDate: 'asc'
         }
     }, {
         total: tripCtrl.currentPageTrips.length,
         getData: function ($defer, params) {
//             $scope.$on('servicesInitComplete', function (e, value) {
//                 loadTableData(params);
//           });
         }
     });
	
	return tripCtrl;
});