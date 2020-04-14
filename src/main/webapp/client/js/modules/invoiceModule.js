"use strict";
/*global angular, _*/

angular.module('myBus.invoiceModule', ['ngTable', 'ui.bootstrap'])
    .controller("InvoiceController",function($rootScope, $scope, $filter, $location,paginationService, $log,$uibModal, NgTableParams, userManager, invoiceManager){
        $scope.invoice = {};
        /* date picker functions */
        $scope.check = true;
        $scope.dt = new Date();
        $scope.dt2 = new Date();
        $scope.loading = false;
        $scope.searchInit = function (){
            $scope.searchTableParams = new NgTableParams({
                page: 1, // show first page
                count:15,
                sorting: {
                    date: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    $scope.loading = true;
                    invoiceManager.search($scope.query,function(invoice) {
                        $scope.loading = false;
                        $scope.invoice = invoice;
                    });
                }
            });
        }

        $scope.search = function(){
            $scope.query = {
                "startDate" : $scope.dt.getFullYear()+"-"+[$scope.dt.getMonth()+1]+"-"+$scope.dt.getDate(),
                "endDate" :  $scope.dt2.getFullYear()+"-"+[$scope.dt2.getMonth()+1]+"-"+$scope.dt2.getDate(),
                "channel" :$scope.channel
            }
            $scope.searchInit();
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

    })
    .factory('invoiceManager', function ($rootScope, $q, $http, $log, services) {
        var cashTransfer = {};
        return {
            search: function(query, callback){
                services.post('/api/v1/invoice/search', query, function (response) {
                    if (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }
                }, function (err, status) {
                    sweetAlert("Error searching bookings", err.message, "error");
                })
                // $http.post('/api/v1/invoice/search', query).then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error searching bookings", err.message, "error");
                // });
            },
        };
    });

