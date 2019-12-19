/**
 * Created by sriharshakota on 7/11/17.
 */
angular.module('myBus.sequenceModule', ['ngTable', 'ui.bootstrap'])
        .controller('sequenceController', function($scope,sequenceManager,NgTableParams) {
            $scope.headline = "Shipment Sequences";
            $scope.currentPageOfSequence = [];
            $scope.sequences = {};
            $scope.count = 0;

            var loadTableData = function (tableParams) {
                $scope.loading = true;
                sequenceManager.getAllSequences(function (response) {
                    if(angular.isArray(response.data)) {
                        $scope.loading = false;
                        $scope.sequences = response.data;
                        tableParams.total(response.totalElements);
                        $scope.count = response.totalElements;
                        tableParams.data = $scope.cities;
                        $scope.currentPageOfsequences =  $scope.sequences;
                    }
                });
            };

            $scope.init = function() {
                    $scope.sequenceTableParams = new NgTableParams({
                        page: 1,
                        size:9999,
                        count:9999,
                        sorting: {
                            name: 'asc'
                        },
                    }, {
                        counts:[],
                        total: 100000,
                        getData: function (params) {
                            loadTableData(params);
                        }

                    });
            };
            $scope.init();

        })

.factory('sequenceManager', function ($rootScope, $http, $log) {

    return {
        getAllSequences: function (callback) {
            $http({url: '/api/v1/shipmentSequence/all', method: "GET"})
                .then(function (response) {
                    callback(response);
                }, function (error) {
                    $log.debug("error retrieving sequences");
                });
        }
    }
})