'use strict';
/*global angular,_*/

angular.module('myBus.staffComplaintsModule', ['ngTable', 'ui.bootstrap'])
    .controller('StaffComplaintsController', function ($scope,$rootScope, $state,$http, $log,paginationService, $location, staffManager, NgTableParams, staffComplaintManager) {

        $scope.title = "Staff Complaints";

        var pageable;

        $scope.addStaffComplaint = function () {
            $state.go('addStaffComplaint');
        };

        $scope.staffList = function () {
            staffManager.getStaffList({}, function (response) {
                $scope.staffList = response.content;
            })
        }

        $scope.staffList();


        $scope.init = function(query) {
            staffComplaintManager.count(query, function(complaintsCount) {
                $scope.complaintsTableParams = new NgTableParams({
                    page: 1,
                    size: 10,
                    count: 10,
                    sorting: {
                        username: 'asc'
                    },
                }, {
                    counts: [],
                    total: complaintsCount,
                    getData: function (params) {
                        params.query = query;
                        loadTableData(params);
                    }
                });
            })
        };

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response, query:tableParams.query};
            });
            $scope.loading = true;
            staffComplaintManager.getComplaints(pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.staffComplaint = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.staffComplaint;
                    $scope.currentPageOfStaffComplaints =  $scope.staffComplaint;
                }
            });
        };

        $scope.init('');

        $scope.deleteComplaint = function(id){
            staffComplaintManager.deleteComplaint(id,function(response){
                $scope.init('');
            });
        };

    })
    .controller('addEditStaffComplaintController', function ($scope,$rootScope, $state,$http, $log, $location, staffManager, staffComplaintManager, vehicleManager) {
        $scope.title = "Add Staff Complaint";
        $scope.staffComplaint = {};

        $scope.staffList = function () {
            staffManager.getStaffList({}, function (response) {
                $scope.staffList = response.content;
            })
        }
        $scope.staffList();

        $scope.vehicleList = function () {
            vehicleManager.getVehicles({},function (response) {
                $scope.allVehicles = response.content;
            })
        }
        $scope.vehicleList();

        $scope.save = function(){
            if (!$scope.staffComplaint.staffid) {
                swal("Error","please select staff","error");
            }
            else if (!$scope.staffComplaint.incidentDate) {
                swal("Error","please select incident date","error");
            } else if (!$scope.staffComplaint.remarks) {
                swal("Error","please enter remarks","error");
            }else {
                staffComplaintManager.addComplaint($scope.staffComplaint, function (res) {
                    swal("Great","Your complaint has been sucessfully added","success");
                    $state.go('staffcomplaints');
                }, function (error) {
                    console.log('error');
                });
            }
        }

        $scope.cancel = function() {
            $state.go('staffcomplaints');
        };




    }).factory('staffComplaintManager', function ($rootScope, $http) {
        var complaint = {};
    return {
        addComplaint : function (complaint,callback,error) {
            $http.post('/api/v1/staffComplaints/addComplaint',complaint).then(function (response) {
                callback(response.data);
            },function (err,status) {
                sweetAlert("Error saving complaint",err.data.message,"error");
            });
        },

        getComplaints: function (pageable, callback) {
            $http({url:'/api/v1/staffComplaints/getAll', method: "GET",params: pageable})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving complaints");
                });
        },

        count: function (query, callback) {
            $http.get('/api/v1/staffComplaints/count?query='+query)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving route count");
                });
        },

        deleteComplaint: function (id, callback) {
            $http.delete('/api/v1/staffComplaints/delete/'+id)
                .then(function (response) {
                    callback(response);
                    sweetAlert("Great", "Complaint successfully deleted", "success");

                },function (error) {
                    sweetAlert("Oops...", "Error deleting Complaint!", "error" + angular.toJson(error));
                })
        }

    }
});
