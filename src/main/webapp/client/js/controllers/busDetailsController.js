"use strict";
/*global angular, _*/

angular.module('myBus.busDetailModule', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusDetailsController', function ($scope, $http, $log, NgTableParams, $modal, $filter, expensesManager, $location) {
    $log.debug('BusDetailsController loading');
    var busDetailsCtrl = this;

    $scope.busdetails = JSON.parse('{"buses":[{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"},{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"},{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"}]}');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Bus Details";

    $scope.goToBusLayout = function (name) {
        $location.url('/layouts/' + name);
    };

    return busDetailsCtrl;

  })
