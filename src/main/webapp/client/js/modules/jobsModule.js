'use strict';
/*global angular,_*/

angular.module('myBus.jobsModule', ['ngTable', 'ui.bootstrap'])
    .controller('jobListController', function ($scope, $rootScope, $state, jobManager, paginationService, NgTableParams, vehicleManager, printManager,inventoryManager) {
        var pageable;
        $scope.query = {};
        $scope.addJob = function () {
            $state.go('addJob');
        };
        inventoryManager.getAllInventories(null,function(response){
            $scope.inventories = response.content;
        });

        var loadPending = function(tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response,
                    query:$scope.query
                };
            });
            jobManager.getPendingJobs(pageable, function(response){
                if(angular.isArray(response.content)){
                    $scope.pendingJobs = response.content;
                    $scope.pendingCount = response.totalElements;
                    tableParams.data = $scope.pendingJobs;
                }
            });
        };

        var loadCompleted = function(tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response,
                    query:$scope.query
                };
            });
            jobManager.getCompletedJobs(pageable, function(response){
                if(angular.isArray(response.content)){
                    $scope.completedJobs = response.content;
                    $scope.completedCount = response.totalElements;
                    tableParams.data = $scope.completedJobs;
                }
            });
        };

        $scope.init = function() {
            jobManager.count({"completed":false},function (count) {
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    }
                }, {
                    counts: [20, 50, 100],
                    total: count,
                    getData: function (params) {
                        loadPending(params);
                    }
                });
            });
            jobManager.count({"completed":true},function (count) {
                $scope.completedTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    }
                }, {
                    counts: [20, 50, 100],
                    total: count,
                    getData: function (params) {
                        loadCompleted(params);
                    }
                });
            });
        }

        $scope.init();

        $scope.editJob = function(id){
            $state.go('addJob',{id:id});
        };

        $scope.deleteJob = function(id){
            jobManager.deleteJob(id,function(response){
                $scope.init();
            });
        };

        $scope.getVehicles = function () {
            vehicleManager.getVehicles({}, function (res) {
                $scope.allVehicles = res.content;
                $rootScope.$broadcast('vehicles', $scope.allVehicles);
            });
        };
        $scope.getVehicles();

        /* For Job search */
        var loadsearchJobs = function (tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.query.page = pageable.page-1;
            $scope.query.sort = pageable.sort;
            $scope.query.size = pageable.size;

            if($scope.query.startDate > $scope.query.endDate) {
                swal("Error", "End Date should be greater than Start date", "error");
            }else {
                jobManager.getAllsearchJobs($scope.query,function (response) {
                    $scope.searchResults = response.data.content;
                    tableParams.data = $scope.searchResults;
                });
            }
        };

        $scope.searchFind = function () {
            jobManager.count($scope.query, function (count) {
                $scope.searchTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    }
                }, {
                    total:count,
                    counts: [],
                    getData: function (params) {
                        loadsearchJobs(params);
                    }
                });
            });

        };

        $scope.searchJob = function(){
            if($scope.query.startDate) {
                var startDate = new Date($scope.query.startDate);
                var startYear = startDate.getFullYear();
                var startMonth = startDate.getMonth() + 1;
                var startDay = startDate.getDate();
                $scope.query.startDate = startYear + '-' + startMonth + '-' + startDay;
            }
            if($scope.query.endDate){
                var endDate = new Date($scope.query.endDate);
                var endYear = endDate.getFullYear();
                var endMonth = endDate.getMonth() + 1;
                var endDay = endDate.getDate();
                $scope.query.endDate = endYear + '-' + endMonth + '-' + endDay;
            }
            $scope.searchFind();
        };

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        };

        $scope.print = function (eleId) {
            printManager.print(eleId);
        };
    })
    .controller('addorEditJobController', function ($scope, $rootScope, $state, vehicleManager, inventoryManager, $stateParams, jobManager, userManager) {
        $scope.job = {
            inventories:[]
        };
        console.log('Edit job here');
        $scope.allVehicles = [];
        $scope.titleName = 'Add Job';
        $scope.odometerReading = 0;
        $scope.cancel = function () {
            $state.go('jobs');
        };

        $scope.getVehicles = function () {
            vehicleManager.getVehicles({}, function (res) {
                $scope.allVehicles = res.content;
                $rootScope.$broadcast('vehicles', $scope.allVehicles);
                if($stateParams.id){
                    $scope.titleName = 'Edit Job';
                    jobManager.getJob($stateParams.id,function(data){
                        $scope.job = data;
                        $scope.getOdometerReading();
                    });
                }
            });
        };
        $scope.getOdometerReading = function () {
            for(var i=0; i<$scope.allVehicles.length;i++) {
                if($scope.allVehicles[i].id === $scope.job.vehicleId){
                    $scope.odometerReading =  $scope.allVehicles[i].odometerReading;
                }
            }
        };
        $scope.getVehicles();
        $scope.getInventories = function () {
            inventoryManager.getAllInventories({}, function (res) {
                $scope.allInventories = res.content;
            });
        };
        $scope.getInventories();

        $scope.getAllUsers = function () {
            userManager.getUserNames( function (res) {
                $scope.allUsers = res;
            })
        };
        $scope.getAllUsers();


        $scope.save = function () {
           /*if ($scope.job.inventories.length === 0) {
                swal("Error", "Please Select a Inventory", "error");
            } else if (!$scope.job.vehicleId) {
                swal("Error", "Please select a Vehicle", "error");
            } else {*/
            if ($stateParams.id) {
                jobManager.updateJob($scope.job, function (response) {
                    $state.go('jobs');
                });
            } else {
                jobManager.addJob($scope.job, function (response) {
                    $state.go('jobs');
                });
            }
            //}
        };
        $scope.addJobInventories = function(){
            if(!$scope.job.inventories){
                $scope.job.inventories = [];
            }
            for(var i=0;i<$scope.job.inventories.length; i++) {
                if (!$scope.job.inventories[i].inventoryId||
                    !$scope.job.inventories[i].quantity) {
                    swal("Error", "Please select values for inventory and quantity", "error");
                    return;
                }
            }
            $scope.job.inventories.push({
                inventoryId:undefined,
                quantity:undefined,
                unitCost: 0
            });
        };
        $scope.setUnitCost = function(inventory) {
            for(var i=0;i<$scope.allInventories.length; i++) {
                if($scope.allInventories[i].id === inventory.inventoryId){
                    inventory.unitCost = $scope.allInventories[i].unitCost;
                }
            }
        };
        $scope.calculateTotal = function(){
            var total = 0;
            for(var i=0;i<$scope.job.inventories.length; i++){
                total += ($scope.job.inventories[i].unitCost * $scope.job.inventories[i].quantity);
            }
            total += $scope.job.additionalCost;
            $scope.job.totalCost = total;
        }


        $scope.deleteJobInventory = function(index){
            if ($scope.job.inventories.length > 0) {
                $scope.job.inventories.splice(index, 1);
            }
        };
    }).factory("jobManager", function ($http) {
    return {
        addJob: function (job, callback) {
            $http.post('/api/v1/jobs/addJob', job)
                .then(function (response) {
                    callback(response);
                }, function (err) {
                });
        },
        getPendingJobs: function (pageable, callback) {
            $http({url: '/api/v1/jobs/getPendingJobs', method: "GET", params:pageable})
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getCompletedJobs: function (pageable, callback) {
            $http({url: '/api/v1/jobs/getCompletedJobs', method: "GET", params:pageable})
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getJob:function(id,callback){
            $http.get('/api/v1/jobs/getAJob/'+id)
                .then(function (response) {
                    callback(response.data);
                },function(err) {});
        },
        updateJob:function(job,callback){
            $http.put('/api/v1/jobs/updateJob',job)
                .then(function (response) {
                    callback(response);
                },function(err) {});
        },
        count:function (query,callback) {
            $http.post('/api/v1/jobs/getCount', query)
                .then(function (response) {
                    callback(response.data);
                },function(err) {});
        },
        deleteJob: function(id,callback) {
            swal({   title: "Are you sure?",   text: "You will not be able to recover this Job !",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false
            }, function() {
                $http.delete('/api/v1/jobs/delete/' + id)
                    .then(function (response) {
                        callback(response);
                        sweetAlert("Great", "Job successfully deleted", "success");
                    },function (error) {
                        sweetAlert("Oops...", "Error finding data!", "error" + angular.toJson(error));
                    });
            });
        },
        getAllsearchJobs: function (query,callback) {
            $http.post( '/api/v1/jobs/searchJobs', query)
                .then(function (response) {
                    callback(response);
                }, function(error){
                    swal("oops", error, "error");
                });
        }
    };
});
