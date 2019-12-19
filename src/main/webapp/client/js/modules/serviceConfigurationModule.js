"use strict";
/*global angular, _*/

angular.module('myBus.serviceConfigurationModule', ['ngTable', 'ui.bootstrap'])
    .controller("serviceListController", function ($rootScope, $scope, $log, NgTableParams, paginationService) {

    }).controller("addorEditServiceController", function ($rootScope, $scope, busLayoutManager, routesManager, serviceConfigManager, $stateParams) {

    $scope.title = "Add Service";

    busLayoutManager.getAllLayouts({}, function (data) {
        $scope.layoutNames = data;
    });

    $scope.getLayout = function (id) {
        busLayoutManager.layoutById(id, function (data) {
            $scope.layoutByIdData = data.data;
        });
    };

    routesManager.getActiveRouteNames(function (data) {
        $scope.routeNames = data;
    });
    $scope.getViaCities = function (id) {
        serviceConfigManager.getRoute(id, function (data) {
            $scope.viaCities = data;
            // console.log("$scope.viaCities",$scope.viaCities);
        });
    };

    $scope.selectedViaCities = [];
    $scope.selectCity = function(data){
        $scope.selectedViaCities.push(data);
    };

    $scope.service = {};
    $scope.addBusService = function(){
        var params = $scope.service;
        if(!params.serviceName){
            swal("Error!", "Please Enter a Service Name!", "error");
        }else if(!params.serviceNumber){
            swal("Error!", "Please Enter a Service Number!", "error");
        }else if(!params.enquiryPhoneNumber){
            swal("Error!", "Please Enter Enquiry Phone Number!", "error");
        }else if(!params.cutoffTime){
            swal("Error!", "Please Enter Cutoff Time!", "error");
        }else if(!params.layoutName){
            swal("Error!", "Please Select Layout!", "error");
        }else if(!params.status){
            swal("Error!", "Please Select Status!", "error");
        }else {
            if($stateParams.id){
                serviceConfigManager.updateService(params, function (response) {
                    if (response) {
                        swal("Updated!", "Service was successfully Updated!", "success");
                    }
                });
            }else {
                serviceConfigManager.addService(params, function (response) {
                    if (response) {
                        swal("Added!", "Service was successfully Added!", "success");
                    }
                });
            }
        }
    };

    $scope.timeSlots = {"hours": [], "minutes": [], "sec": []};
    for (var i = 0; i < 24; i++) {
        if(i <= 9){
            $scope.timeSlots.hours.push({"id": i, "name": '0'+i});
        }else{
            $scope.timeSlots.hours.push({"id": i, "name": i});
        }
    }
    for (var j = 0; j < 60; j++) {
        if(j <= 9) {
            $scope.timeSlots.minutes.push({"id": j, "name": '0'+j});
        }else{
            $scope.timeSlots.minutes.push({"id": j, "name": j});
        }
    }
    for (var k = 0; k < 60; k++) {
        if(k <= 9) {
            $scope.timeSlots.sec.push({"id": k, "name": '0'+k});
        }else{
            $scope.timeSlots.sec.push({"id": k, "name": k});
        }
    }

    $scope.selectedTime = function (hour, min, sec) {
        $scope.service.journeyStartTime = [];
    };

}).factory('serviceConfigManager', function ($rootScope, $http, $log) {
    return {
        getRoute: function (routeId, callback) {
            $http.get('/api/v1/serviceConfig/getRoute/' + routeId)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving cities");
                });
        },
        getLayoutsForService: function (callback) {
            $http.get('/api/v1/serviceConfig/getLayoutsForService')
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving cities");
                });
        },
        addService: function (callback) {
            $http.get('/api/v1/serviceConfig/getLayoutsForService')
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error adding service");
                });
        },
        updateService: function (callback) {
            $http.get('/api/v1/serviceConfig/getLayoutsForService')
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving cities");
                });
        },
    };

});