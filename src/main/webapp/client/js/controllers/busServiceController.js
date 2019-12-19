//"use strict";
/*global angular, _*/

angular.module('myBus.serviceModules', ['ngTable', 'ui.bootstrap'])

    // ==================================================================================================================
    // ====================================    BusServiceController   ================================================
    // ==================================================================================================================

    .controller('BusServiceController', function ($scope, $http, $log, NgTableParams, $modal, $filter, busServiceManager, $location) {
        $log.debug('BusServiceController loading');

        var busServiceCtrl = this;

        busServiceCtrl.currentPageOfServices = [];


        busServiceCtrl.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        busServiceCtrl.headline = "Bus Services";

        busServiceCtrl.goToBusService = function (serviceId) {
            $location.url('/services/' + serviceId);
        };

        busServiceCtrl.deleteService = function (service) {
            console.log(service.id);
            busServiceManager.deleteService(service.id);
        };

        busServiceCtrl.busServicePublish = function(serviceID) {
        	busServiceManager.busServicePublish(serviceID);
        }
        var loadTableData = function (tableParams, $defer) {
            var data = busServiceManager.getAllServices();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            busServiceCtrl.services = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            busServiceCtrl.currentPageOfServices = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('servicesInitComplete', function (e, value) {
            loadTableData(busServiceCtrl.serviceContentTableParams);
        });
        
        $scope.$on('servicesInitStart', function (e, value) {
        	busServiceManager.fetchAllBusServices();
        });
        
        $scope.$on('servicesDeleteComplete', function (e, value) {
            loadTableData(busServiceCtrl.serviceContentTableParams);
        });

        busServiceManager.fetchAllBusServices();

        busServiceCtrl.addNewBusService = function(){
            $location.url('/services/' + 'create');
        }
        
        busServiceCtrl.serviceContentTableParams = new NgTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: busServiceCtrl.currentPageOfServices.length,
            getData: function ($defer, params) {
                $scope.$on('servicesInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        return busServiceCtrl;

})