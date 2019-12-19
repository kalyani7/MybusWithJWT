
"use strict";
/*global angular, _*/

angular.module('myBus.tripReportsModule', ['ngTable', 'ui.bootstrap'])
    .controller('TripReportsController', function($scope,$rootScope,paginationService, $state,$stateParams, $http,$uibModal, $log, $filter, NgTableParams, $location) {
        $scope.headline = "Trip Reports";
        $scope.urlDate = $stateParams.date;
        if(!$scope.urlDate) {
            $scope.dt = new Date();
        } else {
            $scope.dt = new Date($scope.urlDate);
        }

        $scope.nextDay = function() {
           var dt = $scope.dt;
            dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
            $scope.dt.setTime(dt.getTime());
            $scope.dt = new Date($scope.dt)
        }
        $scope.previousDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
            $scope.dt = new Date($scope.dt)
        }
        $scope.init = function () {

        }
        $scope.tripReportByDate = function(date){
            var dateObj = date;
            var month = dateObj.getMonth() + 1;
            var day = dateObj.getDate();
            var year = dateObj.getFullYear();
            var newdate = year + "-" + month + "-" + day;
            $location.url('/tripReports/' + newdate);
        }
        $scope.dateChanged = function() {
            $scope.tripReportByDate($scope.dt);
        }

        $scope.$watch('dt', function(newValue, oldValue) {
            $scope.dateChanged();
        });
    }).factory('tripReportsManager', function ($http, $log, $rootScope) {

});

