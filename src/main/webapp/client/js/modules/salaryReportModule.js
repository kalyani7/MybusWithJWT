"use strict";
/*global angular, _*/

angular.module('myBus.salaryReportModule', ['ngTable', 'ui.bootstrap'])
    .controller("salaryReportController", function ($rootScope, $scope, staffManager, DailyTripManager,salaryReportManager,vehicleManager,$state,paginationService,NgTableParams,$uibModal) {
        $scope.query = { };
        var pageable;
        $scope.selectedPayments = [];
        $scope.isPaid;

        staffManager.getStaffList({}, function (response) {
           $scope.staffList = response.content;
        });

        vehicleManager.getVehicles({}, function (res) {
            $scope.vehiclesList = res.content;
        });

        var loadTableParams = function (tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.query.page = pageable.page-1;
            $scope.query.size = pageable.size;
            salaryReportManager.getSalaryReports($scope.query,function(response){
                $scope.salaryReports = response.content;
                tableParams.data = $scope.salaryReports;
            });
        };
        $scope.getSalaryReports = function() {
            salaryReportManager.getSalaryReportCount($scope.query,function (count) {
                $scope.salaryReportParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        createdAt: 'desc'
                    }
                }, {
                    counts: [20, 50, 100],
                    total: count,
                    getData: function (params) {
                        loadTableParams(params);
                    }
                });
            });
        };
        $scope.payForTrip = function(salaryReportId){
            salaryReportManager.updateSalaryReport(salaryReportId,function(response){});
            $state.go('salaryreports');
        };
        $scope.searchSalaryReports = function(){
            $scope.query = {isPaid:$scope.isPaid };
            if($scope.fromDate) {
                var startDate = new Date($scope.fromDate);
                var startYear = startDate.getFullYear();
                var startMonth = startDate.getMonth() + 1;
                var startDay = startDate.getDate();
                $scope.query.fromDate = startYear + '-' + startMonth + '-' + startDay;
            }
            if($scope.toDate){
                var endDate = new Date($scope.toDate);
                var endYear = endDate.getFullYear();
                var endMonth = endDate.getMonth() + 1;
                var endDay = endDate.getDate();
                $scope.query.toDate = endYear + '-' + endMonth + '-' + endDay;
            }
            $scope.query.staffId = $scope.staffId;
            $scope.query.vehicleId = $scope.vehicleId;
            $scope.getSalaryReports();
        };
        $scope.getSalaryReportsByStatus = function(status){
            $scope.isPaid = status;
            $scope.query = { };
            $scope.query.isPaid = status;
            $scope.getSalaryReports();
        };
        $scope.toggleSalaryReportSelection = function (paymentId) {
            var idx = $scope.selectedPayments.indexOf(paymentId);
            if (idx > -1) {
                $scope.selectedPayments.splice(idx, 1);
            } else {
                $scope.selectedPayments.push(paymentId);
            }
        };
        $scope.paySalary = function(){
            $scope.payQuery = {selectedPayments:$scope.selectedPayments};
            salaryReportManager.paySalaryForSelected($scope.payQuery,$scope.amountPaid,function(response){
                swal("Great!");
                $scope.getSalaryReportsByStatus(false);
            });
            $scope.selectedPayments = [];
        };
        $scope.getSalaryReportsByStatus(false);
    }).factory("salaryReportManager", function ($http) {
        return{
            getSalaryReports: function (query,callback) {
                $http.post('/api/v1/dailyTrips/getSalaryReports', query)
                    .then(function (response) {
                        callback(response.data);
                    }, function(error){
                        swal("oops", error, "error");
                    });
            },
            updateSalaryReport:function (salaryReportId,callback) {
                $http.put('/api/v1/dailyTrips/updateSalaryReport/'+salaryReportId)
                    .then(function (response) {
                        callback(response.data);
                    }, function(error){
                        swal("oops", error, "error");
                    });
            },
            getSalaryReportCount:function (query,callback) {
                $http.post('/api/v1/dailyTrips/getSalaryReportsCount', query)
                    .then(function (response) {
                        callback(response.data);
                    }, function(error){
                        swal("oops", error, "error");
                    });
            },
            paySalaryForSelected: function (query,amountPaid,callback) {
                swal({
                    title: "Payment",
                    text: "Please enter amount to be paid:",
                    type: "input",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    inputPlaceholder: "Enter amount",
                    inputValue:amountPaid
                }, function (comment) {
                    if (comment === false) return false;
                    if (comment === "") {
                        swal.showInputError("Enter amount to be paid");
                        return false
                    }
                    query.amountPaid = comment;
                    $http.post('/api/v1/dailyTrips/paySalary',query)
                        .then(function (response) {
                            callback(response.data);
                        }, function(error){
                            swal("oops", error, "error");
                        });

                });
            }

        }
});