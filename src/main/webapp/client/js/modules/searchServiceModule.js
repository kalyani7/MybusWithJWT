'use strict';
/*global angular,_*/

angular.module('myBus.searchServiceModule', ['ngTable', 'ui.bootstrap'])
    .controller('SearchServiceController', function ($scope,$rootScope, $state,$http, $log,paginationService, $location, NgTableParams,searchServiceManager,cityManager ) {
        $scope.title = "Search Service";

        var pageable;

        $scope.query = {};

        cityManager.getActiveCityNames(function(data) {
            $scope.cities = data;
        });

        $scope.init = function(query) {
            searchServiceManager.searchService(query, function(servicesCount) {
                $scope.sevicesTableParams = new NgTableParams({
                    page: 1,
                    size: 10,
                    count: 10,
                    sorting: {
                        username: 'asc'
                    }
                }, {
                    counts: [],
                    total: servicesCount,
                    getData: function (params) {
                        params.query = query;
                        loadTableData(params);
                    }
                });
            })
        };

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response, query:tableParams.query};
            });
            $scope.loading = true;
            searchServiceManager.searchService(pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.searchService = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.searchService;
                    $scope.currentPageOfServices =  $scope.searchService;
                }
            });
        };

        $scope.init('');

        $scope.search = function(){
            $scope.query = {
                "from" : $scope.fromFilterByCities,
                "to" : $scope.toFilterByCities
            };
            console.log('hh',$scope.query)
        }


    }).factory('searchServiceManager', function ($rootScope, $http) {
    var searchservice = {};
    return {

        searchService: function (query, pageable, callback) {
            $http.post('/api/v1/route/searchServices',query,pageable)
                         .then(function (response) {
                callback(response.data);
            }, function (err, status) {
                sweetAlert("error retrieving services", err.data.message, "error");
            });
        }


    }
});