'use strict';
/*global angular,_*/

angular.module('myBus.preferredStaffModule', ['ngTable', 'ui.bootstrap'])
    .controller('PreferredStaffListController',function ($scope,$state,PreferredStaffManager,vehicleManager,cityManager,NgTableParams,paginationService) {
        $scope.query = {};
        var pageable;
        vehicleManager.getVehicles({}, function (res) {
            $scope.allVehicles = res.content;
        });
        cityManager.getCities({},function(response){
            $scope.cities = response;
        });
        $scope.addPreferredStaff = function (id) {
            $state.go('add-editpreferredStaff',{staffId:id});
        };
        $scope.deleteStaff = function(staffId){
            PreferredStaffManager.deletePreferredStaff(staffId,function (response) {
                $scope.init();
            });
        };
        var loadTableParams = function (tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.query.page = pageable.page-1;
            $scope.query.size = pageable.size;
            PreferredStaffManager.getAllStaff($scope.query,function(response){
                $scope.staffList = response.content;
                tableParams.data = $scope.staffList;
            });
        };
        $scope.init = function() {
            PreferredStaffManager.preferredStaffCount($scope.query,function (count) {
                $scope.preferredStaffTableParams = new NgTableParams({
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
        $scope.init();

    })
    .controller('Add-EditPreferredStaffController',function ($scope,$state,$rootScope,vehicleManager,staffManager,PreferredStaffManager,$stateParams,$location,cityManager) {
        $scope.titleName = "Add Preferred Staff" ;
        $scope.allStaff = [];
        $scope.staffDetails = [];
        $scope.getAllVehicles = function(){
            vehicleManager.getVehicles({}, function (res) {
                $scope.allVehicles = res.content;
                $rootScope.$broadcast('vehicles', $scope.allVehicles);
            });
        };
        $scope.getAllCities = function(){
           cityManager.getCities({},function(response){
               $scope.cities = response;
            });
        };
        $scope.getAllStaff = function(){
            staffManager.getStaffList(null, function (response) {
                $scope.allStaff= response.content;
                $scope.allStaffDuplicate = response.content;
                if($stateParams.staffId) {
                    $scope.titleName = "Edit Preferred Staff";
                    PreferredStaffManager.getPreferredStaff($stateParams.staffId,function (response) {
                        $scope.staff = response;
                        $scope.staffDetails = [];
                        for(var i =0; i < $scope.staff.staffIds.length; i++) {
                            var newStaffObj = _.find($scope.allStaffDuplicate, function (s) {
                                return s.id === $scope.staff.staffIds[i];
                            });
                            $scope.staffDetails.push(newStaffObj);
                        }
                    });
                }
            });
        };
        $scope.addStaff = function () {
            if (!$scope.staff.staffIds) {
                $scope.staff.staffIds = [];
            }
            $scope.staff.staffIds.push($scope.newStaffId);
            $scope.staffDetails = [];
            for(var i =0; i < $scope.staff.staffIds.length; i++) {
                var newStaffObj = _.find($scope.allStaffDuplicate, function (s) {
                    return s.id === $scope.staff.staffIds[i];
                });
                if(newStaffObj === undefined){
                    sweetAlert("Error", "Please select Staff", "error");
                }else {
                    $scope.staffDetails.push(newStaffObj);
                }
            }
            for(var j = 0; j < $scope.staff.staffIds.length; j++) {
                $scope.allStaff = _.without($scope.allStaff, _.findWhere($scope.allStaff, {id:$scope.staff.staffIds[j]}));
            }
        };
        $scope.deleteStaff = function (staff, index) {
            $scope.staffDetails.splice(index, 1);
            $scope.staff.staffIds = _.without($scope.staff.staffIds, staff.id);
            $scope.allStaff.unshift(staff);
        };
        $scope.save = function(){
            if(!$scope.staff){
                sweetAlert("Error","All fields are Mandatory","error");
            }else if(!$scope.staff.vehicleId){
                sweetAlert("Error","Select Vehicle","error");
            }else if(!$scope.staff.sourceId){
                sweetAlert("Error","Select Source","error");
            }else if(!$scope.staff.destinationId){
                sweetAlert("Error","Select Destination","error");
            }
            else {
                if ($stateParams.staffId) {
                    PreferredStaffManager.updatePreferredStaff($scope.staff, function (response) {
                        swal("success", "staff Updated", "success");
                        $state.go('preferredstaff');
                    });
                } else {
                    PreferredStaffManager.addPreferredStaff($scope.staff, function (response) {
                        swal("success", "staff created", "success");
                        $state.go('preferredstaff');
                    });
                }
            }
        };
        $scope.cancel = function(){
            $state.go('preferredstaff');
        };
        $scope.getAllVehicles();
        $scope.getAllStaff();
        $scope.getAllCities();
}).factory('PreferredStaffManager',function ($http) {
    return{
        addPreferredStaff:function (staff, callback) {
            $http.post('/api/v1/preferredStaff/addPreferredStaff', staff)
                .then(function (response) {
                    callback(response);
                }, function (err) {});
        },
        updatePreferredStaff:function (staff, callback) {
            $http.put('/api/v1/preferredStaff/updatePreferredStaff', staff)
                .then(function (response) {
                    callback(response);
                }, function (err) {});
        },
        getAllStaff: function (query,callback) {
            $http.post('/api/v1/preferredStaff/getAllStaff', query)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        deletePreferredStaff:function (id,callback) {
            swal({   title: "Are you sure?",   text: "You will not be able to recover this !",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false }, function() {
                $http.delete('/api/v1/preferredStaff/delete/' + id)
                    .then(function (response) {
                        callback(response.data);
                        sweetAlert("Great", "successfully deleted", "success");
                    },function (error) {
                        sweetAlert("Oops...", "Error!", "error" + angular.toJson(error));
                    });
            });
        },
        getPreferredStaff: function (id,callback) {
            $http.get('/api/v1/preferredStaff/get/'+id)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        preferredStaffCount: function (query,callback) {
            $http.post('/api/v1/preferredStaff/getCount', query)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getStaffForDailyTrips:function (query,callback) {
            $http.post('/api/v1/preferredStaff/getStaffForDailyTrips', query)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        }

    }

});
