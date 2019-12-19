/**
 * Created by yks-srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cEticket', ['ngTable', 'ui.bootstrap'])
.controller("B2cEticketController",function($log,$rootScope, $http,$scope,$modal,b2cEticketManager){
	$scope.ticketinfo={};
	$scope.passengerData={};
	$scope.getbookedTicket = function(){
		b2cEticketManager.getBookedTicket('57691ca184ae43986aac962d',function(data){
			$scope.ticketinfo = data
			b2cEticketManager.getTicktPassingerinfo(data.bookingId,function(data){
				$scope.passengerData = data;
			})
		})
	}
	$scope.getbookedTicket();

    $scope.setTab = function(newTab){
      $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum){
      return $scope.tab === tabNum;
    };
});


