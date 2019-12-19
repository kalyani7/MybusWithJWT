/**
 * Created by srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cHome', ['ngTable', 'ui.bootstrap'])
.controller('B2cHomeController',function($scope, $log, $modal,$state, $filter, NgTableParams,$location, $rootScope,b2cHomeManager) {

    
	$log.debug("in myBusB2c.b2cHome at B2cHomeController");
	$scope.headline = "Buy Your Tickets Now!";
	$scope.allCities =[];
	$scope.dateOfJourney='';

	$scope.getAllCities = function(){
		b2cHomeManager.getAllcites(function(data){
			$scope.allCities = data;
		})
	}
	
	$scope.getAllCities();
	 $scope.selectFromCity = function(item){
         $scope.fromCity = item.name;
     };

     $scope.selectToCity = function(item, model, label, event){
         $scope.toCity = item.name;
     };
     
     var date = new Date();
     $scope.minDate = $filter('date')(date.setDate((new Date()).getDate()),'yyyy-MM-dd');
     $scope.manDate = $filter('date')(date.setDate((new Date()).getDate() + 30),'yyyy-MM-dd');
     
     $scope.onSelectDateOfJourney = function(){
	     if($scope.busJourney.dateOfJourney!=''){
	    	 $scope.rminDate = $filter('date')($scope.busJourney.dateOfJourney,'yyyy-MM-dd');
	     }else{
	    	 $scope.rminDate = $scope.minDate; 
	     }
     }
     $scope.searchBuses = function(){
	    $log.debug("$scope.busJourney -"+$scope.busJourney);
	    var sendDAta = angular.copy($scope.busJourney);
	    b2cHomeManager.getSearchForBus(sendDAta,function(data){
	    	$state.go('results');
	    })
     }
     $scope.ok = function(){
    	 var result = true;
    	 	 
    	 if((($scope.busJourney.fromCity|| '') !== '')&& (($scope.busJourney.toCity|| '') !== '') && (($scope.busJourney.dateOfJourney|| '') !== '')){
    		 var result = false;
    	 }
    	 	
    	 if($scope.busJourney.journeyType==='TWO_WAY' && ($scope.busJourney.returnJourney|| '') == ''){
    		 result = true;
    	 }
    	 if((($scope.busJourney.fromCity|| '') !== '')&& (($scope.busJourney.toCity|| '') !== '') && ($scope.busJourney.fromCity === $scope.busJourney.toCity)){
			result = true;
		 }
    	 if((($scope.busJourney.dateOfJourney|| '') !== '')&& (($scope.busJourney.returnJourney|| '') !== '')){
    		 var dateOfJourney = new Date($scope.busJourney.dateOfJourney)
    		 var returnJourney = new Date($scope.busJourney.returnJourney)
    		 if(dateOfJourney>returnJourney){
    			 result = true;
    		 }
    	 }
    	 
    	 return result
     }
});
