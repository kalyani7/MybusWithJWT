/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.branchOfficeModule', ['ngTable', 'ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('BranchOfficesController', function($rootScope, $scope,$state, $http, $log, $filter,paginationService, NgTableParams, $location, branchOfficeManager) {
        $scope.headline = "Branch Offices";
        $scope.loading = false;
        $scope.currentPageOfOffices = [];
        $scope.count = 0;
        var pageable ;
        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.loading = true;
            branchOfficeManager.loadAll(pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)){
                    $scope.loading = false;
                    $scope.branchOffice = response.content;
                    tableParams.data = $scope.branchOffice;
                    $scope.count = response.totalElements;
                    // tableParams.total(response.totalElements);
                    $scope.currentPageOfOffices = $scope.branchOffice;
                }
            })
        };

        $scope.init = function(){
            branchOfficeManager.count($rootScope.currentuser.operatorId, function(branchOfficeCount){
               $scope.branchOfficeCount = branchOfficeCount;
                $scope.officeTableParams = new NgTableParams({
                        page: 1,
                        count:10,
                        sorting: {
                            name: 'asc'
                        }
                    },
                    {
                        counts:[20, 50, 100],
                        total:branchOfficeCount,
                        getData: function (params) {
                            loadTableData(params);
                        }
                    });
            });
        };
        $scope.init();

        $scope.addOffice = function () {
            $state.go('home.editbranchoffice');
        };

        $scope.edit = function(office){
            $state.go('home.editbranchoffice', {id: id})
            // $location.url('branchoffice/'+office.id,{'idParam':office.id});
        }
        $scope.delete = function(office){
            branchOfficeManager.deleteOffice(office.id);
        }

        $scope.$on('check',function(e,value){
            $scope.init();
        });
    })
    // ============================= Add/Edit ========================================
    .controller('EditBranchOfficeController', function($scope,$stateParams,userManager,$window,$log, cityManager, $location, cancelManager,branchOfficeManager ) {
        $scope.headline = "Edit Branch Office";
        $scope.id=$stateParams.id;
        cityManager.getActiveCityNames(function(data) {
            $scope.cities = data;
            console.log('list',$scope.cities)
            userManager.getUserNames(function(users) {
                $scope.users= users;
            });
        });
        $scope.office ={};
        $scope.cityName = null;
        $scope.managerName = null;

        $scope.selectFromCity = function(item){
            $scope.cityName = item.name;
            $scope.office.cityId= item.id;
        };

        $scope.selectManager = function(item, model, label, event){
            $scope.managerName = item.firstName;
            $scope.office.managerId= item.id;
        };
        $scope.launchUserAdd = function() {
            $state.go('home.user')
            // $location.url('/user/');
        };
        $scope.save = function() {
            if($scope.thisForm.$dirty){
                $scope.thisForm.submitted = true;
                if($scope.thisForm.$invalid) {
                    swal("Error!","Please fix the errors in the user form","error");
                    return;
                }
                branchOfficeManager.save($scope.office, function(data){
                    if($scope.office.id){
                        swal("success","BranchOffice Updated","success");
                    } else {
                        swal("success","BranchOffice created","success");
                    }
                });
            }
            // $location.url('/branchoffices');
            $state.go('home.branchoffices')
        };
        $scope.cancel = function(theForm) {
            cancelManager.cancel(theForm);
        };
        if($scope.id) {
            branchOfficeManager.load($scope.id, function(data) {
                $scope.office = data;
                $scope.cityName = data.attrs.cityName;
                $scope.managerName = data.attrs.managerName;
            });
        }
    })
    .factory('branchOfficeManager', function ($http, $log,$rootScope, services) {
        var branchOffices = {};
        return {
            loadAll: function ( pageable, callback) {
                services.get('/api/v1/branchOffices', pageable, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    swal("oops", error, "error");
                })
                // $http({url: '/api/v1/branchOffices', method: "GET", params: pageable})
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function(error){
                //         swal("oops", error, "error");
                //     });
            },
            loadNames: function (successCallback, errorCallback) {
                services.get('/api/v1/branchOffice/names', '', function (response) {
                    if (response) {
                        successCallback(response.data)
                        $rootScope.$broadcast('BranchOfficesLoadComplete');
                    }
                }, function (error) {
                    errorCallback(error)
                    $log.debug("error retrieving branchOffices");
                })
                // $http.get('/api/v1/branchOffice/names')
                //     .then(function (response) {
                //         callback(response.data);
                //         $rootScope.$broadcast('BranchOfficesLoadComplete');
                //     },function (error) {
                //         $log.debug("error retrieving branchOffices");
                //     });
            },
            save: function(branchOffice, callback) {
                if(branchOffice.id) {
                    services.put('/api/v1/branchOffice/'+branchOffice.id, branchOffice, function (response) {
                        if (response) {
                            callback(response.data);
                        }
                    }, function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    })
                    // $http.put('/api/v1/branchOffice/'+branchOffice.id,branchOffice).then(function(response){
                    //     if(angular.isFunction(callback)){
                    //         callback(response.data);
                    //     }
                    //     $rootScope.$broadcast('check');
                    // },function (err,status) {
                    //     sweetAlert("Error",err.message,"error");
                    // });
                } else {
                    services.post('/api/v1/branchOffice', branchOffice, function (response) {
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('check');
                    }, function (error, status) {
                        sweetAlert("Error",error.message,"error");
                    })
                    // $http.post('/api/v1/branchOffice',branchOffice).then(function(response){
                    //     if(angular.isFunction(callback)){
                    //         callback(response.data);
                    //     }
                    //         $rootScope.$broadcast('check');
                    // },function (err,status) {
                    //     sweetAlert("Error",err.message,"error");
                    // });
                }
            },
            count: function (id, callback) {
                services.get('/api/v1/branchOffices/count', '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving branchOffice count");
                })
                // $http.get('/api/v1/branchOffices/count')
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         $log.debug("error retrieving branchOffice count");
                //     });
            },
            load: function(officeId, callback) {
                services.get('/api/v1/branchOffice/' + officeId, '', function (response) {
                    if (response) {
                        callback(response.data);
                        $rootScope.$broadcast('check');
                    }
                }, function (error) {
                    $log.debug("error retrieving branchOffice");
                })
                // $http.get('/api/v1/branchOffice/'+officeId)
                //     .then(function (response) {
                //         callback(response.data);
                //         $rootScope.$broadcast('check');
                //     },function (error) {
                //         $log.debug("error retrieving branchOffice");
                //     });
            },
            deleteOffice: function(id) {
                services.delete('api/v1/branchOffice/' + id, function (response) {
                    if (response) {}
                })
                // swal({
                //     title: "Are you sure?",
                //     text: "Are you sure you want to delete this office?",
                //     type: "warning",
                //     showCancelButton: true,
                //     closeOnConfirm: false,
                //     confirmButtonText: "Yes, delete it!",
                //     confirmButtonColor: "#ec6c62"},function(){
                //     $http.delete('api/v1/branchOffice/'+id).then(function(response){
                //         $rootScope.$broadcast('check');
                //         swal("Deleted!", "Office was successfully deleted!", "success");
                //     },function () {
                //         swal("Oops", "We couldn't connect to the server!", "error");
                //     });
                // })
            }
        }
    });



