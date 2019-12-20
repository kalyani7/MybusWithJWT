"use strict";
/*global angular, _*/

angular.module('myBus.cargoDashboard', ['ngTable', 'ui.bootstrap'])
    .controller("CargoDashboardController",function($rootScope, $scope, NgTableParams,userManager, cargoDashboardManager,$location, branchOfficeManager){
        $scope.headline = "Cargo Dashboard";
        $scope.dt = new Date();
        $scope.tableParams = {};
        $scope.members = [];
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        $scope.$watch('dt', function(newValue, oldValue) {
            console.log('load data for '+ $scope.dt);
        });


    }).factory('cargoDashboardManager', function ($rootScope, $q, $http, $log, $location) {
        return {
            findCargoBookings: function (filter, callback) {
                console.log('filter ' + filter);
                $http.post('/api/v1/shipments', filter)
                    .then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching cargo booking", err.message, "error");
                    });
            },
            getCargoBooking: function (id, callback) {
                $http.get("/api/v1/shipment/"+id)
                    .then(function (response) {
                        callback(response.data)
                    }, function (error) {
                        swal("oops", error, "error");
                    })
            },
            getShipmentTypes: function ( callback) {
                $http.get("/api/v1/shipment/types")
                    .then(function (response) {
                        callback(response.data)
                    }, function (error) {
                        swal("oops", error, "error");
                    })
            },createShipment: function(cargoBooking, successcallback){
                $http.post("/api/v1/shipment", cargoBooking)
                    .then(function (response) {
                        successcallback(response.data)
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    })
            },
            count:function (filter, callback) {
                $http.post('/api/v1/shipments/count', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving shipments count");
                    });
            },
            lookupCargoBooking : function(LRNumber) {
                $http.get("/api/v1/shipment/search/byLR/"+LRNumber)
                    .then(function (response) {
                        $location.url('viewcargobooking/'+response.data);
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    });


            }
        }
    });
