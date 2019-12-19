'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('expensesManager', function ($rootScope, $http, $log) {

  var expenses = {};

  return {
    fetchAllExpenses: function () {
        $log.debug("fetching expenses data ...");
          $http.get('/api/v1/expenses')
          .success(function (data) {
             expenses = data;
             $rootScope.$broadcast('expensesInitComplete');
          })
          .error(function (error) {
            $log.debug("error retrieving expenses");
          });
    },

    getAllData: function () {
      return expenses;
    },
    getOneById: function (id) {
      return _.first(_.select(expenses, function (value) {
        return value.id === id;
      }));
    }
  };
});


