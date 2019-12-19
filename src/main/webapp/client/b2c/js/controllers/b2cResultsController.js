/**
 * Created by yks-srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, NgTableParams,$location, $rootScope,$compile,b2cHomeManager,b2cBusResultsManager) {
	$scope.trips=[];
	$scope.selectedSeat = [];
	$scope.bJny ;
	$scope.bJnys;
	$scope.selectedSeates = [];
	$scope.modifySearch = false;
	$scope.returnJourneyMesg = 'ONE_WAY';
	$scope.allCities =[];
	$scope.getAllCities = function(){
		b2cHomeManager.getAllcites(function(data){
			$scope.allCities = data;
		})
	}
	$scope.getAllCities();
	$scope.getBusJourney=function(){
		b2cBusResultsManager.getbusJourney(function(data){
			$scope.bJnys = data;
			$scope.bJny = data.busJournies[0];
			$scope.getAvailableTrips($scope.bJny.journeyType);
			if(data.busJournies && data.busJournies.length==2)
				$scope.bJny['returnJourney']=data.busJournies[1].dateOfJourney
		});
	}
	$scope.getBusJourney();
	$log.debug($scope.bJny)
	$scope.modifySearchEvent=function(){
			$scope.modifySearch = $scope.modifySearch?false:true;
	}
	$scope.getAvailableTrips = function(journeyType){
		b2cBusResultsManager.getAvailableTrips(journeyType,function(data){
			if($scope.returnJourneyMesg!='TWO_WAY')
				$scope.returnJourneyMesg = 'ONE_WAY';
			$scope.trips = data;
		})
	}
	$scope.sleeper = [];
	$scope.upper=[];
	$scope.lower=[];
	$scope.CurrentTrips = []
	$scope.seatLayout = function(trip,index){
		console.log('Trip ' +trip);
		$scope.CurrentTrips[index]=trip;
		var layoutId = trip.layoutId,serviceNumber = trip.serviceNumber;
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
		$scope.seatlayout = '';
		b2cBusResultsManager.seatLayout(layoutId,function(data){
			$scope.seatlayout = data;
			$scope.lowRows = [];
			$scope.upRows = [];
			$scope.upper[index] = [];
			$scope.lower[index] = [];
			angular.forEach($scope.seatlayout.rows,function(row){
				angular.forEach(row.seats,function(seat){
					if(seat.sleeper){
						if(seat.upperDeck){
							$scope.upRows.push(seat);
						}else{
							$scope.lowRows.push(seat);
						}
						$scope.sleeper[index] = seat.sleeper ;
						console.log('assigned to '+$scope.sleeper[index])
					}else{
					}
					
				})
				if($scope.upRows.length>0)
					$scope.upper[index].push($scope.upRows);
				if($scope.lowRows.length>0)
					$scope.lower[index].push($scope.lowRows);
				$scope.upRows=[];
				$scope.lowRows=[];
			});
			console.log($scope.upper[index])
	
		});
	}
	$scope.seatLayoutHide = function(serviceNumber){
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
	}
	$scope.showDropingPoints = function(dps,serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		$scope.dpsViews = dps;
		var temp = '<table class="bpDptop dp-pull"><tr><td>Droping Point</td><td>Time</td></tr>'+
		'<tr ng-repeat="dpsV in dpsViews"><td>{{dpsV.droppingName}}</td><td>{{dpsV.droppingTime}}</td></tr></table>'
		angular.element(document.getElementById(dropingPoint)).append($compile(temp)($scope));
	}
	$scope.hideDropingPoints = function(serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		angular.element(document.getElementById(dropingPoint)).empty();
	}
	
	$scope.showBoardingPoints = function(bps,serviceNumber){
		$scope.bpsViews = bps;
		var boardingPoint = 'boardingPoint-'+serviceNumber;
		var temp ='<table class="bpDptop bp-pull"><tr><td>Boarding Point</td><td>Time</td></tr>'+
		'<tr  ng-repeat="bpv in bpsViews"  in bpsViews"><td>{{bpv.bpName}}</td><td>{{bpv.time}}</td></tr></table>'
		angular.element(document.getElementById(boardingPoint)).append($compile(temp)($scope));
	}
	$scope.hideBoardingPoints = function(serviceNumber){
		var boardingPoint = 'boardingPoint-'+serviceNumber
		angular.element(document.getElementById(boardingPoint)).empty();
	}
	$scope.busJourney =new Array();
	$scope.seatSelect = function(seat,trip,tripIndex){
		var serviceNumber = trip.serviceNumber;
		$scope.busJourney[tripIndex] = $scope.busJourney[tripIndex] ? $scope.busJourney[tripIndex] : [];
		$scope.busJourney[tripIndex][$scope.returnJourneyMesg] = $scope.busJourney[tripIndex][$scope.returnJourneyMesg] ? $scope.busJourney[tripIndex][$scope.returnJourneyMesg] : [];
		/*$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney=$scope.bJny;*/
		
		$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney= $scope.returnJourneyMesg=='ONE_WAY' ? $scope.bJnys.busJournies[0] : $scope.bJnys.busJournies[1];
		
		$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.fare = trip.serviceFares[0].fare;
		$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.serviceName = trip.serviceName;
		$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.serviceNumber = trip.serviceNumber;
		if(!$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers)
			$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers= [];
		if(seat.seatStatus=="BOOKED"||seat.seatStatus=="UNAVAILABLE"){
		}else{
			if(seat.seatStatus=="AVAILABLE"){
				$scope.seatTotalFare=seat
				$scope.seatSelected = true;
				$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers.push(seat.number);
				$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.totalFare=( $scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.totalFare ? $scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.totalFare :  0 ) + $scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.fare;
				seat.seatStatus = 'BOOKING_INPROGRSS';
			}else{
				seat.seatStatus = 'AVAILABLE';
				var index = $scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers.indexOf(seat.number)
				if(index>=0){
					$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers.splice(index, 1);
					$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.totalFare-=$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.fare;
				}
				if($scope.selectedSeates.lenght<=0)
					$scope.seatSelected = false;
			}	
		}
	}
	$scope.continueToReturn=function(busJourney){
		$log.debug(busJourney +"|" +$scope.bJnys);
		busJourney.boardingPoint = angular.fromJson(busJourney.boardingPoint);
		busJourney.dropingPoint = angular.fromJson(busJourney.dropingPoint);
		b2cBusResultsManager.blockSeat(busJourney,function(data){
			$scope.returnJourneyMesg = 'TWO_WAY';
			$scope.getAvailableTrips($scope.bJnys.busJournies[1].journeyType,function(data){
				$scope.trips = data;
			})
		})
	}
	$scope.continueToPayment = function(busJourney){
		$log.debug(busJourney)
		busJourney.boardingPoint = angular.fromJson(busJourney.boardingPoint);
		busJourney.dropingPoint = angular.fromJson(busJourney.dropingPoint);
		b2cBusResultsManager.blockSeat(busJourney,function(data){
			$location.url('/detailsPayment');
		})
	}
	$scope.searchBuses = function(){
		$scope.trips=[];
		b2cHomeManager.getSearchForBus($scope.bJny,function(data){
			$scope.getAvailableTrips($scope.bJny.journeyType);
			if(data.bJny && data.bJny.length==2)
				$scope.bJny['returnJourney']=data.bJny[0].returnJourney
		})
	}
	
	$scope.ok=function(tripIndex){
		var result= true;
		if($scope.busJourney[tripIndex]){
			if($scope.busJourney[tripIndex][$scope.returnJourneyMesg]){
				if($scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers&&$scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.seatNumbers.length>0){
					if(($scope.CurrentTrips[tripIndex].boardingPoints.length<=0 || angular.isString($scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.boardingPoint)) && ($scope.CurrentTrips[tripIndex].dropingPoints.length<=0 || angular.isString($scope.busJourney[tripIndex][$scope.returnJourneyMesg].busJourney.dropingPoint))){
						$log.debug($scope.busJourney[tripIndex][$scope.returnJourneyMesg]);
						result = false
					}
				}
			}
		}
		return result;
	}
});