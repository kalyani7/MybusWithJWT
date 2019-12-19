//"use strict";
/*global angular, _*/

angular.module('myBus.layoutModules', ['ngTable', 'ui.bootstrap'])


    // ==================================================================================================================
    // ====================================    BusLayoutController   ================================================
    // ==================================================================================================================

    .controller('BusLayoutController', function ($scope, $http, $log, NgTableParams, $modal, $filter, busLayoutManager, $location) {
        $log.debug('BusLayoutController loading');

        var busLayoutCtrl = this;

        busLayoutCtrl.currentPageOfLayouts = [];


        busLayoutCtrl.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        busLayoutCtrl.headline = "Layouts";

        busLayoutCtrl.goToBusLayout = function (busId) {
            $location.url('/layouts/' + busId);
        };

        busLayoutCtrl.deleteLayout = function (layout) {
            busLayoutManager.deleteLayout(layout.id);
            $location.url('/layouts');
        };

        var loadTableData = function (tableParams, $defer) {
            var data = busLayoutManager.getAllLayouts();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            busLayoutCtrl.layouts = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            busLayoutCtrl.currentPageOfLayouts = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('layoutsInitComplete', function (e, value) {
            loadTableData(busLayoutCtrl.layoutContentTableParams);
        });

        $scope.$on('layoutsDeleteComplete', function (e, value) {
            loadTableData(busLayoutCtrl.layoutContentTableParams);
        });

        busLayoutManager.fetchAllBusLayouts();

        busLayoutCtrl.addNewBusLayout = function(){
            $location.url('/layouts/' + 'create');
        }

        busLayoutCtrl.layoutContentTableParams = new NgTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: busLayoutCtrl.currentPageOfLayouts.length,
            getData: function ($defer, params) {
                $scope.$on('layoutsInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        return busLayoutCtrl;

});