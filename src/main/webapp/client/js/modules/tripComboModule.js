"use strict";
/*global angular, _*/

angular.module('myBus.tripComboModule', ['ngTable', 'ui.bootstrap'])
    .controller("TripComboController",function($scope,$rootScope,$uibModal,$location, $filter, $log,NgTableParams,paginationService,tripComboManager) {
        $scope.headline = "Trip Combos";
        $scope.loading = false;
        $scope.currentPageOfTripCombos = [];
        var pageable ;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            tripComboManager.getAll(pageable, function (response) {
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.tripCombos = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.tripCombos;
                    $scope.currentPageOfTripCombos = $scope.tripCombos;
                }
            });
        };
        $scope.init = function() {
            tripComboManager.count(function (tripCombosCount) {
                $scope.tripComboTableParams = new NgTableParams({
                    page: 1,
                    size: 10,
                    count: 10,
                    sorting: {
                        comboNumber: 'asc'
                    }
                }, {
                    counts: [],
                    total: tripCombosCount,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            })
         }
        $scope.init();

        $scope.$on('reloadTripCombo', function (e, value) {
            $scope.init();
        });
        $scope.delete = function(id){
            tripComboManager.delete(id,function(data){
            });
        }

        $scope.handleClickEditTrip = function(tripId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'update-tripCombo-modal.html',
                controller : 'AddTripComboModalController',
                resolve : {
                    tripId : function(){
                        return tripId;
                    }
                }
            });
        };
    })

// ==========================Adding and Updating Trip  =================================
.controller('AddTripComboModalController', function ($scope, $rootScope,tripComboManager,tripId) {
    $scope.tripCombo = {};
    $scope.save =function(){
        tripComboManager.save($scope.tripCombo,function(data){
            $rootScope.modalInstance.close(data);
        });
    }
    $scope.isInputValid = function () {
        return ($scope.tripCombo.comboNumber || '') !== '';
    };
    if(tripId){
        tripComboManager.getById(tripId,function (data) {
            $scope.tripCombo = data;
        })
    }
    $scope.cancel = function () {
        $rootScope.modalInstance.dismiss('cancel');
    };
})
.factory("tripComboManager",function ($rootScope,$http,$location,$log) {
    var tripCombos = [];
    return{
        getAll: function(pageable, callback){
            $http({url: '/api/v1/tripCombos',method: "GET",params: pageable})
                .then(function(response){
                    tripCombos= response.data;
                    callback(tripCombos);
                },function(error){
                    swal("oops", error, "error");
                });
        },
        getById : function(id,callback){
            $http.get("/api/v1/tripCombo/"+id).then(function(response){
                callback(response.data);
            },function(error){
                swal("oops", error, "error");
            })
        },
        save: function (tripCombo,callback) {
            if(!tripCombo.id) {
                $http.post("/api/v1/tripCombo",tripCombo).then(function(response){
                    $location.url('tripcombo');
                    $rootScope.modalInstance.close();
                    $rootScope.$broadcast("reloadTripCombo");
                    swal("Great", "Trip Combo has been successfully added", "success");
                },function(error){
                    swal("oops", error, "error");
                });
            } else {
                $http.put("/api/v1/tripCombo",tripCombo).then(function(response){
                    $location.url('tripcombo');
                    $rootScope.modalInstance.close();
                    $rootScope.$broadcast("reloadTripCombo");
                    swal("Great", "Trip Combo has been updated successfully", "success");
                },function(error){
                    swal("oops", error, "error");
                });
            }
        },
        count: function (callback) {
            $http.get('/api/v1/tripCombos/count',{})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving trip combos");
                });
        },
        delete: function (tripId,callback) {
            swal({
                title: "Are you sure?",
                text: "Are you sure you want to delete this Trip Combo?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "Yes, delete it!",
                confirmButtonColor: "#ec6c62"},function(){
                $http.delete("/api/v1/tripCombo/"+tripId).then(function(data){
                    $rootScope.$broadcast("reloadTripCombo");
                    swal("Deleted!", "TripCombo has been deleted successfully!", "success");
                },function(error){
                    swal("Oops", "We couldn't connect to the server!", "error");
                })
            })
        }
    }
})