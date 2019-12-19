"use strict";
/*global angular, _*/

angular.module('myBus.cargoBranchSummary', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBranchSummaryController",function($rootScope, $scope, NgTableParams,userManager, cargoBookingManager,$location,paginationService, branchOfficeManager){
        $scope.headline = "Branch Booking Summary";
        $scope.currentUser = userManager.getUser();
        $scope.filter = {};
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
            $scope.offices.unshift({"name":"All"});
        });
        $scope.summaryData = null;
        $scope.filter.startDate = new Date();
        $scope.filter.startDate.setDate( $scope.filter.startDate.getDate() -1);
        $scope.filter.endDate = new Date();
        $scope.filter.fromBranchId = $scope.currentUser.branchOfficeId;
        cargoBookingManager.getBranchSummary($scope.filter, function(data){
            $scope.summaryData = data;
        });

        $scope.search = function(){
            cargoBookingManager.getBranchSummary($scope.filter, function(data){
                $scope.summaryData = data;
            });
        }
        $scope.exportToExcel = function (tableId, fileName) {
            console.log('export');
            paginationService.exportToExcel(tableId, fileName);
        }

    });