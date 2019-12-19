"use strict";
/*global angular, _*/

angular.module('myBus.serviceEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusServiceEditController   ================================================
  // ==================================================================================================================

  .controller('BusServiceEditController', function ($rootScope, $scope, $http, $log, NgTableParams, $modal, $filter,
													busServiceManager, routesManager,cityManager,cancelManager,
													amenitiesManager, layoutNamesPromise,
													routeNamesPromise, amenitiesNamesPromise,
													$location, $stateParams) {
		$log.debug('BusServiceController loading');
        var busServiceEditCtrl = this;
		busServiceEditCtrl.valid = true;
		busServiceEditCtrl.totalSeats = 0;
		$scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';
		var date = new Date();
		$scope.minDate = $filter('date')(date.setDate((new Date()).getDate()),'yyyy-MM-dd');
		$scope.maxDate = $filter('date')(date.setDate((new Date()).getDate() + 30),'yyyy-MM-dd');
		$scope.headline = "Service Details";
		$scope.sSDates=[];
		$scope.amenitiesForUi=[];
		$scope.updateServiceButton = false;

		$scope.onSelectDateOfJourney = function(dateOfJourney){
			if(dateOfJourney!=''){
				$scope.rminDate = $filter('date')(dateOfJourney,'yyyy-MM-dd');
			}else{
				$scope.rminDate = $scope.minDate;
			}
		}
		$scope.onSelectServiceStartDate = function(dateOfJourney){
			$scope.startDt = new Date(dateOfJourney);
			$scope.maxDate = $scope.startDt.setDate($scope.startDt.getDate() + 30 );
			if(dateOfJourney!=''){
				busServiceEditCtrl.busService.schedule.endDate=new Date($scope.maxDate);
			}else{
				$scope.rminDate = $scope.minDate;
			}
		}

		busServiceEditCtrl.initialize = function(){

			busServiceEditCtrl.busService = {
				active:false,
				serviceName:null,
				serviceNumber:null,
				phoneEnquiry:null,
				cutoffTime:null,
				serviceTaxType:null,
				serviceTax:null,
				routeId:null,
				schedule : {
					startDate:null,
					endDate:null,
					frequency:null,
					weeklyDays:[],
					specialServiceDates:[]
				},
				boardingPoints:[],
				dropingPoints:[],
				serviceFares:[],
				amenityIds:[]
			}
			busServiceEditCtrl.layouts = layoutNamesPromise.data;
			busServiceEditCtrl.routes = routeNamesPromise.data;
			busServiceEditCtrl.routesMap = {};
			//create map data
			for(var rt in routeNamesPromise.data){
				busServiceEditCtrl.routesMap[busServiceEditCtrl.routes[rt].id] = busServiceEditCtrl.routes[rt].name;
				busServiceEditCtrl.routesMap[busServiceEditCtrl.routes[rt].name] = busServiceEditCtrl.routes[rt].id;
			}
			busServiceEditCtrl.amenities = amenitiesNamesPromise.data;
			console.log("amenities " + busServiceEditCtrl.amenities.length);
			angular.forEach(busServiceEditCtrl.amenities,function(amenity){
				amenity.active = false;
			});

			busServiceEditCtrl.weeklyDays = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
			busServiceEditCtrl.specialDays = [{'date':''}]
			busServiceEditCtrl.taxModes  = [
				{id: 'FIXED', name: 'FIXED'},
				{id: 'PERCENTAGE', name: 'PERCENTAGE'}
			];

			var serviceId = $stateParams.id;
			if($stateParams.id != "create" && serviceId !== ''){
				$scope.updateServiceButton = true;
				busServiceManager.getService(serviceId, function(service) {
					//update the amenities for the service
					_.each(service.amenityIds, function(amenityId) {
						_.find(busServiceEditCtrl.amenities, function(amenity) {
							return amenity.id === amenityId;
						}).active = true;
					})

					busServiceEditCtrl.busService = service;
					busServiceEditCtrl.busService.schedule.startDate = new Date(busServiceEditCtrl.busService.schedule.startDate);
					busServiceEditCtrl.busService.schedule.endDate = new Date(busServiceEditCtrl.busService.schedule.endDate);
					if(!$scope.$$phase) {
						$scope.$apply();
					}
				});
			}
        }
		$scope.$watch('busServiceEditCtrl.busService.routeName', function() {
			if(busServiceEditCtrl.routesMap && busServiceEditCtrl.routesMap[busServiceEditCtrl.busService.routeName]){
				busServiceEditCtrl.busService.routeId = busServiceEditCtrl.routesMap[busServiceEditCtrl.busService.routeName];
				busServiceEditCtrl.updateServiceConfig();
			}
		});
		/**
		 * Module to update the service component after selecting/changing the route
		 */
        busServiceEditCtrl.updateServiceConfig = function(){
        	busServiceManager.updateServiceConfig(busServiceEditCtrl.busService,function(data){
        		busServiceEditCtrl.busService = data;
        		busServiceEditCtrl.busService.schedule.startDate = new Date(busServiceEditCtrl.busService.schedule.startDate);
        		busServiceEditCtrl.busService.schedule.endDate = new Date(busServiceEditCtrl.busService.schedule.endDate);
        		busServiceEditCtrl.routeCities = [];
        		
        		/*cityManager.getCities(function(data){
        			var cities = data;
        			$scope.serviceFares = [];
        			$scope.dropingPoints=[];
        			$scope.boardingPoints=[];
        			angular.forEach(busServiceEditCtrl.busService.serviceFares,function(serviceFare,index){
        				var destinationCityId = $filter('filter')(cities,serviceFare.destinationCityId);
        				var sourceCityId = $filter('filter')(cities,serviceFare.sourceCityId); 
        				if(serviceFare.active){
        					$scope.serviceFares.push({
        						sourceCityId:sourceCityId[0],
        						destinationCityId:destinationCityId[0],
        						departureTime:serviceFare.departureTime,
        						arrivalTime:serviceFare.arrivalTime,
        						active:true
        					})
        				}else {
        					$scope.serviceFares.push({
        						sourceCityId:sourceCityId[0],
        						destinationCityId:destinationCityId[0],
        						departureTime:serviceFare.departureTime,
        						arrivalTime:serviceFare.arrivalTime,
        						active:false
        					});
        				}
        				$log.debug("arrivalTime:serviceFare.arrivalTime,"+serviceFare.arrivalTime)
        			});
        			
    				angular.forEach(busServiceEditCtrl.busService.boardingPoints,function(bpid){
    					var bpCityTem = $filter('filter')(cities,bpid.refId);
    					angular.forEach(bpCityTem[0].boardingPoints,function(bp){
    						if(bp.id==bpid.refId){
        						bp.active = false
        						$scope.boardingPoints.push(bp);
    						}
    					});
                    });
    				
    				angular.forEach(busServiceEditCtrl.busService.dropingPoints,function(dpid){
    					var dpCityTem = $filter('filter')(cities,dpid.droppingPointId);
    					angular.forEach(dpCityTem[0].boardingPoints,function(dp){
    						if(dp.id==dpid.droppingPointId){
        						dp.active = false
        						$scope.dropingPoints.push(dp);
    						}
    					});
                    });
        			
    				busServiceEditCtrl.busService.serviceFares = [];
    				angular.forEach($scope.serviceFares,function(sf){
    					if(sf.active){
    						busServiceEditCtrl.busService.serviceFares.push(
    								{
    									active: true,
			    						cutOffTimeInMinutes: 0,
			    						destinationCityId: sf.destinationCityId.id,
			    						fare: 0,
			    						journeyDuration: 0,
			    						sourceCityId: sf.sourceCityId.id
    								});
    					}
    				});
    				busServiceEditCtrl.busService.dropingPoints= [];
    				busServiceEditCtrl.busService.boardingPoints=[]
        		});*/
        	});
		};

        busServiceEditCtrl.goToServices = function(){
        	$location.url('/services');
        };

        $scope.$on('servicesCreateComplete', function (e, value) {
             busServiceEditCtrl.goToServices();
        });

        $scope.saveService = function (){
        	var service = angular.copy(busServiceEditCtrl.busService);
        	service.schedule.startDate = $filter('date')(service.schedule.startDate,'yyyy-MM-dd');
        	service.schedule.endDate = $filter('date')(service.schedule.endDate,'yyyy-MM-dd');
        	busServiceManager.createService(service)
        };
        
        $scope.updateService = function (){
        	var service = angular.copy(busServiceEditCtrl.busService);
        	service.schedule.startDate = $filter('date')(service.schedule.startDate,'yyyy-MM-dd');
        	service.schedule.endDate = $filter('date')(service.schedule.endDate,'yyyy-MM-dd');
        	
        	busServiceManager.updateService(service)
        };
        
        $scope.editPublishedService=function(){
        	busServiceEditCtrl.busService.status = "ACTIVE";
        }
        
        $scope.weeklyDays = function(checkedOrUnchecked,index) {

        	busServiceEditCtrl.busService.schedule.specialServiceDates = [];
        	if(busServiceEditCtrl.busService.schedule.weeklyDays==undefined){
        		busServiceEditCtrl.busService.schedule.weeklyDays=[];
        	}
        	if(checkedOrUnchecked){
        		busServiceEditCtrl.busService.schedule.weeklyDays.push(busServiceEditCtrl.weeklyDays[index]);
        	}else{
        		busServiceEditCtrl.busService.schedule.weeklyDays.splice(busServiceEditCtrl.busService.schedule.weeklyDays.indexOf(busServiceEditCtrl.weeklyDays[index]), 1);
        	}        	  
        };
       
       $scope.specialServiceDates = function(specialServiceDateUI) {
    	   var specialServiceDate = angular.copy(specialServiceDateUI)
    	   busServiceEditCtrl.busService.schedule.weeklyDays=[];
    	   if(busServiceEditCtrl.busService.schedule.specialServiceDates==undefined){
    		   busServiceEditCtrl.busService.schedule.specialServiceDates=[];
    		  /* busServiceEditCtrl.busService.specialServiceDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
    		   $scope.sSDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));*/
    	   }
    	   var index = busServiceEditCtrl.busService.schedule.specialServiceDates.indexOf($filter('date')(specialServiceDate,'yyyy-MM-dd'))
    	   if(index==-1){
    	   		busServiceEditCtrl.busService.schedule.specialServiceDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
    	   		$scope.sSDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
    	   }   	  
       };
        $scope.removeSpecialServiceDatesFromList = function(specialServiceDate) {
      	  if(busServiceEditCtrl.busService.schedule.specialServiceDates==undefined){
      		busServiceEditCtrl.busService.schedule.specialServiceDates=[];
      	  }else{
      		var index = busServiceEditCtrl.busService.schedule.specialServiceDates.indexOf(specialServiceDate)
      		if(index!=-1)
      			busServiceEditCtrl.busService.schedule.specialServiceDates.splice(index, 1);
      			$scope.sSDates.splice(index, 1);
      	  }
      	  return '';
      };
        
        $scope.addOrRemoveDropingtime = function(bpOrdbID,active,bpOrdb,timeFromUI,index,name){
        	var time = angular.copy(timeFromUI);
        	switch (bpOrdb) {
			case 'bp':
				if(active){ 
					var boardPoint = $filter('filter')(busServiceEditCtrl.busService.boardingPoints, bpOrdbID)
					if(boardPoint.length>0){
						boardPoint[0].time = $filter('date')(time,'HH:mm a');
					}else{
						busServiceEditCtrl.busService.boardingPoints.push({
							refId:bpOrdbID,
							bpName:name,
							time : $filter('date')(time,'HH:mm a')
						});
					}
				}else{
					busServiceEditCtrl.busService.boardingPoints.splice(index, 1);
				}
				break;
				
			case 'dp':
				if(active){
					var dropingPoint = $filter('filter')(busServiceEditCtrl.busService.dropingPoints, bpOrdbID);
					if(dropingPoint.length>0){
						dropingPoint[0].droppingTime = $filter('date')(time,'HH:mm a');
					}else{
						busServiceEditCtrl.busService.dropingPoints.push({
							droppingPointId:bpOrdbID,
							droppingName:name,
							droppingTime:$filter('date')(time,'HH:mm a')
						})
					}
				}else{
					busServiceEditCtrl.busService.dropingPoints.splice(index,1)
				}
				
				break;
			default:
				break;
			}
        };
        
        $scope.addServiceFare = function(sourceCityId,destinationCityId,fieldValue,fieldName) {
        	if(busServiceEditCtrl.busService.serviceFares.length>0){
        		angular.forEach(busServiceEditCtrl.busService.serviceFares,function(sf){
        			if(sf.destinationCityId==destinationCityId && sf.sourceCityId==sourceCityId){
        				switch (fieldName) {
						case "at":
							var temp = angular.copy(fieldValue);
							sf['arrivalTime'] = $filter('date')(temp,'HH:mm a');
							break;
						case "dt":
							var temp = angular.copy(fieldValue);
							sf['departureTime']= $filter('date')(temp,'HH:mm a');
							break;
						case "fare":
							sf['fare'] =fieldValue;
							break;
						case "active":
							sf['active'] =fieldValue;
							break;
						default:
							break;
						}
        			}
        		})
        	}
        }
        $scope.dailyService = function(){
        	busServiceEditCtrl.busService.schedule.specialServiceDates = [];
        	busServiceEditCtrl.busService.schedule.weeklyDays=[];
        }
        $scope.addOrRemoveAmenitiesToService = function(checkedOrUnchecked,amenityId){
        	if(busServiceEditCtrl.busService.amenityIds===undefined){
        		busServiceEditCtrl.busService.amenityIds=[];
        	}
        	if(checkedOrUnchecked){
				busServiceEditCtrl.busService.amenityIds.push(amenityId);
        	}else{
				busServiceEditCtrl.busService.amenityIds.splice(busServiceEditCtrl.busService.amenityIds.indexOf(amenityId), 1);
        	}
			console.log("amenities " + busServiceEditCtrl.busService.amenityIds);
        }
		$scope.cancelServiceEdit = function(theForm){
			cancelManager.cancel(theForm);
		};
		busServiceEditCtrl.initialize();
		return busServiceEditCtrl;
  });