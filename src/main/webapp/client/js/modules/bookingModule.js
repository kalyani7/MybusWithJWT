
"use strict";
/*global angular, _*/

angular.module('myBus.bookingModule', ['ngTable', 'ui.bootstrap'])
    .controller('BookingAnalyticsController', function($scope,$rootScope,paginationService, $state, $http,$uibModal, $log, $filter, NgTableParams, $location, bookingManager) {
        $scope.count = 0;
        $scope.bookingCounts = {};
        $scope.loading =false;
        $scope.countTableParams = {};
        var pageable;
        var self = this;
        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.loading = true;
            // var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            bookingManager.getBookingsCountByPhone(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.bookingCounts = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.bookingCounts;
                }
            });
        };

        $scope.init = function() {
            bookingManager.getUniquePhoneNumbers(function(count) {
                $scope.countTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 20,
                    count: 20,
                    sorting: {
                        username: 'asc'
                    },
                }, {
                    counts: [],
                    total: count,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            })
        };
        $scope.init();
        $scope.showBookingByPhone = function(count){
            $state.go('bookingsbyphone', {phoneNumber: count._id, totalBookings:count.totalBookings});
        };

        $scope.currentPageOfBookings = [];
        $scope.bookingsTableParams = {};
        $scope.search = function() {
            $scope.loading = true;
            bookingManager.getBookingsByPhone($scope.phoneNumber, function(response) {
                $scope.currentPageOfBookings = response;
                $scope.count = response.length;
                $scope.loading = false;
                self.bookingsTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 20,
                    count: 20,
                    sorting: {
                        username: 'asc'
                    },
                }, {
                    counts: [],
                    total: $scope.currentPageOfBookings.length,
                    getData: function (params) {

                    }
                });
            });
        };
    }).controller('BookingsByPhoneController', function($scope,$stateParams, $rootScope,paginationService, $state, $http, $log, $filter, NgTableParams, $location, bookingManager) {
        $scope.count = 0;
        $scope.bookingCounts = {};
        $scope.loading =false;
        $scope.currentPageOfBookings = [];
        $scope.countTableParams = {};
        $scope.phoneNumber = $stateParams.phoneNumber;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            // var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            bookingManager.getBookingsByPhone($stateParams.phoneNumber, function(response){
                $scope.loading = false;
                $scope.currentPageOfBookings = response;
                tableParams.total(response.length);
                $scope.count = response.length;
                tableParams.data = $scope.currentPageOfBookings;
            });
        };

        $scope.init = function() {
            $scope.bookingsTableParams = new NgTableParams({
                page: 1, // show first page
                size: 20,
                count: 20,
                sorting: {
                    username: 'asc'
                },
            }, {
                counts: [],
                total: $stateParams.totalBookings,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };
        $scope.init();
        $scope.search = function(phoneNumber, totalBookings) {
            $state.go('bookingsbyphone', {phoneNumber: count._id, totalBookings:count.totalBookings});
        }
    }).factory('bookingManager', function ($http, $log) {
        return {
            getBookingsCountByPhone: function (pageable, callback) {
                $http({url:'/api/v1/getBookingCounts',method: "GET",params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error getBookingCounts");
                    });
            },
            getBookingsByPhone: function (phoneNumber, callback) {
                $http({url:'/api/v1/getBookingsByPhone/'+phoneNumber,method: "GET"})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error getBookingsByPhone");
                    });
            },
            getUniquePhoneNumbers: function (callback) {
                $http({url:'/api/v1/getUniquePhoneNumbers',method: "GET"})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error getUniquePhoneNumbers");
                    });
            }
        }
});
