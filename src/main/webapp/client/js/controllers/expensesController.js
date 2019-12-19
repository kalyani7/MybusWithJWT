"use strict";
/*global angular, _*/

angular.module('myBus.expensesModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    ExpensesController   ================================================
  // ==================================================================================================================

  .controller('ExpensesController', function ($scope, $http, $log, NgTableParams, $modal, $filter, expensesManager, $location) {
    $log.debug('ExpensesController loading');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Expenses";

    $scope.allExpenses = [];
    $scope.currentPageOfExpenses = [];
    $scope.expensesManager = expensesManager;

    var loadTableData = function (tableParams, $defer) {
      var data = expensesManager.getAllData()
        , orderedData = null;

      orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
      $scope.allExpenses = orderedData;
      tableParams.total(data.length);
      if (angular.isDefined($defer)) {
        $defer.resolve(orderedData);
      }
      $scope.currentPageOfExpenses = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };

    $scope.$on('expensesInitComplete', function (e, value) {
      loadTableData($scope.expensesContentTableParams);
    });


    $scope.goToExpensesList = function (id) {
      $location.url('/expenses/' + id);
    };
        
    $scope.expensesContentTableParams = new NgTableParams({
      page: 1,
      count: 50,
      sorting: {
        date: 'asc',
        description: 'asc'
      }
    }, {
      total: $scope.currentPageOfExpenses.length,
      getData: function ($defer, params) {
        $scope.$on('expensesInitComplete', function (e, value) {
          loadTableData(params);
        });
      }
    });
    expensesManager.fetchAllExpenses();

    $scope.handleClickAddStateCity = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'neighborhood-add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
            //$scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
  })
