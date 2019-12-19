
"use strict";
/*global angular, _*/

angular.module('myBus.gstFilters', ['ngTable', 'ui.bootstrap'])
    .controller('GSTFiltersController', function($scope,$rootScope,paginationService, $state, $http,$uibModal, $log, $filter, NgTableParams, $location, gstFilterManager) {
        $scope.headline = "GST Filters";
        $scope.filters = [];
        $scope.loading =false;
        $scope.agentTableParams = {};

        var loadTableData = function (tableParams) {
            $scope.loading = true;
            gstFilterManager.getFilters(function(response){
                if(angular.isArray(response)) {
                    $scope.loading = false;
                    $scope.filters = response;
                    tableParams.data = $scope.filters;
                }
            });
        };

        $scope.init = function() {
            $scope.filterTableParams = new NgTableParams({
                sorting: {
                    serviceName: 'asc'
                }
            }, {
                counts: [],
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };

        $scope.init();

        $scope.save = function(){
            gstFilterManager.save($scope.filters, function(response){
                $scope.filters = response;
            });
        }


    }).factory('gstFilterManager', function ($http, $log,$rootScope) {
    return {
        getFilters: function (callback) {
            $http({url:'/api/v1/GSTFilters/',method: "GET"})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving filters");
                });
        },
        save: function(filters, callback) {
            $http.post('/api/v1/GSTFilters/',filters).then(function(response){
                if(angular.isFunction(callback)){
                    callback(response.data);
                }
                $rootScope.$broadcast('FiltersUpdated');
            },function (err,status) {
                sweetAlert("Error",err.data.message,"error");
            });

        }
    }
});
