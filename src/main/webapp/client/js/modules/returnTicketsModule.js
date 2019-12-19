"use strict";
/*global angular, _*/

angular.module('myBus.returnTicketsModule', ['ngTable', 'ui.bootstrap'])
    .controller("returnTicketsController",function($rootScope, $scope, $filter,$stateParams,returnTicketsManager, $location, $log,$uibModal, NgTableParams){
        $scope.headline = "Return Tickets Details";
        $scope.loading = true;
        $scope.returnTickets = {};
        returnTicketsManager.loadReturnTickets(function(data){
            $scope.loading = false;
            $scope.returnTickets = data;
        },function(error){
            swal("Error!","Error loading return tickets","error");
            $scope.loading = false;
        });

        $scope.size = function(obj) {
            var size = 0, key;
            for (key in obj) {
                if (obj.hasOwnProperty(key)) size++;
            }
            return size;
        };

        $scope.$on('ReturnTicketsLoaded', function (e, value) {
            $scope.returnTicketsByDateTableParams = new NgTableParams({
                page: 1,
                count:$scope.size($scope.returnTickets.allDuesMappedByDate),
                sorting: {
                    name: 'asc'
                }
            });
            $scope.returnTicketsByAgentTableParams = new NgTableParams({
                page: 1,
                count:$scope.size($scope.returnTickets.allDuesMappedByAgent),
                sorting: {
                    Id: 'asc'
                }
            });
            $scope.allReturnTicketsTableParams = new NgTableParams({
                page: 1,
                count:$scope.size($scope.returnTickets.allDues),
                sorting: {
                    Id: 'asc'
                }
            });

        });


        $scope.showReturnTicketsByDate = function(date) {
            $location.url('returnTicketsByDate/'+date);
        };
        $scope.showReturnTicketsByAgent = function(agent) {
            $location.url('returnTicketsByAgent/'+agent);
        }
        $scope.payBooking = returnTicketsManager.payBooking;
    })
    .controller('returnTicketsByDateController', function($scope, $rootScope, $stateParams,returnTicketsManager, NgTableParams) {
        $scope.headline = "Return Tickets by Date";
        $scope.currentPageOfTickets = [];
        var date = $stateParams.date;
        $scope.loading = false;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            returnTicketsManager.getDateData(function (data) {
                $scope.loading = false;
                $scope.currentPageOfTickets = data[date];
            })
        };

        $scope.byDateTableParams = new NgTableParams({
            page: 1,
            count:9999999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $scope.payBooking = returnTicketsManager.payBooking;
    })

    .controller('returnTicketsByAgentController', function($scope,returnTicketsManager, $rootScope, $stateParams,NgTableParams) {
        $scope.headline = "Return Tickets by Agent";
        $scope.loading = false;
        $scope.currentPageOfTickets = [];
        var agentName = $stateParams.agent;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            returnTicketsManager.getAgentsData(function (data) {
                $scope.loading = false;
                $scope.currentPageOfTickets = data[agentName];
            })
        };
        $scope.byAgentTableParams = new NgTableParams({
            page: 1,
            count:9999999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $scope.payBooking = returnTicketsManager.payBooking;
    })

    .factory('returnTicketsManager', function ($rootScope, $http, $log, dueReportManager) {
        var returnTickets ;
        var returnTicketsByDate ;
        var returnTicketsByAgent ;
    return {
        loadReturnTickets: function (callback, errorcallback) {
            $http.get('/api/v1/dueReport/returnTickets')
                .then(function (response) {
                     returnTickets = response.data;
                     returnTicketsByDate = returnTickets.allDuesMappedByDate;
                     returnTicketsByAgent = returnTickets.allDuesMappedByAgent;
                     $rootScope.$broadcast('ReturnTicketsLoaded');
                     callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving the details");
                    errorcallback(error);
                });
        },
        getAllReturnTickets: function(callback){
          callback(returnTickets);
        },
        getDateData : function(callback){
            callback(returnTicketsByDate);
        },

        getAgentsData: function (callback) {
            callback(returnTicketsByAgent);
        },
        payBooking : function(bookingId, callback) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    callback();
                },function (error) {
                    sweetAlert("Oops...", "Error submitting the report", "error");
                });
            });
        }

    }
});