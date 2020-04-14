'use strict';
/*global angular,_*/

angular.module('myBus.remindersModule', ['ngTable', 'ui.bootstrap'])
    .controller("RemindersController", function ($rootScope, $scope, $uibModal, NgTableParams, $location, paginationService, fullTripManager, $state, $stateParams, remainderManager, userManager, printManager) {
        $scope.headline = "Reminders";

        $scope.query = {
            isCompleted : false,
        }

        $scope.getUserNames = function () {
            userManager.getUserNames(function (res) {
                $scope.userNames = res;
            });
        };
        $scope.getUserNames();

        var pageable ;
        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.query.page = pageable.page-1;
            $scope.query.sort = pageable.sort;
            $scope.query.size = pageable.size;

            $scope.loading = true;
                remainderManager.loadAll($scope.query, function (response) {
                    $scope.invalidCount = 0;
                    if (angular.isArray(response.content)) {
                        for(var i=0;i<response.content.length;i++){
                            if(response.content[i].completed){
                                response.content[i].completed='Yes';
                            }else{
                                response.content[i].completed='No';
                            }
                        }
                        $scope.loading = false;
                        $scope.reminders = response.content;
                        tableParams.data = $scope.reminders;
                        $scope.count = response.totalElements;
                        $scope.currentPageReminders = $scope.reminders;
                    }
                });

        };

        $scope.init = function(){
            remainderManager.count($scope.query,function(remindersCount){
                $scope.remaindersCount = remindersCount;
                $scope.remainderTableParams = new NgTableParams({
                        page: 1,
                        count:10,
                        sorting: {
                            name: 'asc'
                        }
                    },
                    {
                        counts:[20, 50, 100],
                        total:remindersCount,
                        getData: function (params) {
                            loadTableData(params);
                        }
                    });
            });
        };


        $scope.init();

        $scope.remainderByStatus = function(status){
            if(status){
                $scope.showTab = status;
                $scope.query.isCompleted = status;
                $scope.init();
            }else{
                $scope.query.isCompleted = status;
                $scope.init();
            }
        };

        $scope.addRemainder = function () {
            $state.go('home.addreminders');
        };

        $scope.edit = function(remainder){
            $state.go('home.editreminders', {id:remainder.id} );
        };

        $scope.delete = function(remainder){
            remainderManager.deleteRemainder(remainder.id, function (response) {
                $scope.init();
            });
        };

        //For Search Reminders

        var loadsearchReminders = function (tableParams){
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
                remainderManager.loadAll($scope.query,function (response) {
                    for(var i=0;i<response.content.length;i++){
                        if(response.content[i].completed){
                            response.content[i].completed='Yes';
                        }else{
                            response.content[i].completed='No';
                        }
                    }
                    $scope.searchResults = response.content;
                    tableParams.data = $scope.searchResults;
                });
            }
        };

        $scope.initSearch = function(){
            remainderManager.count($scope.query,function(remindersCount){
                $scope.remaindersCount = remindersCount;
                $scope.remainderSearchTableParams = new NgTableParams({
                        page: 1,
                        count:10,
                        sorting: {
                            name: 'asc'
                        }
                    },
                    {
                        counts:[20, 50, 100],
                        total:remindersCount,
                        getData: function (params) {
                            loadsearchReminders(params);
                        }
                    });
            });
        };

        $scope.searchReminder = function () {
            if($scope.query.endDate) {
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

            $scope.initSearch();
        };

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        $scope.print = function (eleId) {
            printManager.print(eleId);
        };


    })

    .controller("EditremindersController", function ($rootScope, $scope, NgTableParams, $location, paginationService, $state, $stateParams, userManager, remainderManager, cancelManager) {

        $scope.headline = "Add Reminder";

        $scope.reminders = {};

        $scope.getUserNames = function () {
            userManager.getUserNames(function (res) {
                $scope.userNames = res;
            });

        };

        $scope.getUserNames();

            if($stateParams.id) {
                $scope.headline = "Edit Reminder"
                    remainderManager.edit($stateParams.id, function (res) {
                        $scope.reminders = res;
                    });
            }


        $scope.saveDet = function() {
                if($scope.remainderDetails.dispatchDate) {
                    swal("Error!","Please fix the errors in the user form","error");
                    return;
                }
                if($stateParams.id){
                    remainderManager.update($scope.reminders, function (data) {
                        swal("success","Remainder Updated","success");
                    });
                }else {
                    remainderManager.save($scope.reminders, function (data) {
                        swal("success","Remainder created","success");
                    });
                }
            // $location.url('/reminders');
                $state.go('home.reminders')
        };

        $scope.cancel = function() {
            $state.go('reminders');
        };

    })
    .factory('remainderManager', function ($http, $log,$rootScope, services) {
        var reminders = {};
    return {

        loadAll: function (query, callback) {
            services.post('/api/v1/reminders/getAll', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http.post('/api/v1/reminders/getAll', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function(error){
            //         swal("oops", error, "error");
            //     });
        },
        save: function (remainder, callback) {
            services.post('/api/v1/reminders/addReminder/', remainder, function (response) {
                if (response) {
                    if(angular.isFunction(callback)){
                        callback(response.data);
                    }
                    $rootScope.$broadcast('check');
                }
            }, function (err,status) {
                sweetAlert("Error",err.message,"error");
            })
          // $http.post('/api/v1/reminders/addReminder/', remainder).then(function (response) {
          //           if(angular.isFunction(callback)){
          //               callback(response.data);
          //           }
          //           $rootScope.$broadcast('check');
          //       },function (err,status) {
          //           sweetAlert("Error",err.message,"error");
          //       });
            },
        update: function (remainder, callback) {
            services.put('/api/v1/reminders/updateReminder/', '', remainder, function (response) {
                if (response) {
                    if(angular.isFunction(callback)){
                        callback(response.data);
                    }
                    $rootScope.$broadcast('check');
                }
            }, function (err, status) {
                sweetAlert("Error",err.message,"error");
            })
            // $http.put('/api/v1/reminders/updateReminder/', remainder).then(function (response) {
            //     if(angular.isFunction(callback)){
            //         callback(response.data);
            //     }
            //     $rootScope.$broadcast('check');
            // },function (err,status) {
            //     sweetAlert("Error",err.message,"error");
            // });
        },

        edit: function (id, callback) {
            services.get('api/v1/reminders/get/' + id, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http({url: 'api/v1/reminders/get/'+id, method: "GET"})
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         swal("oops", error, "error");
            //     });
        },

            count: function (query, callback) {
            services.post('/api/v1/reminders/getCount', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving branchOffice count");
            })
            // $http.post('/api/v1/reminders/getCount', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     },function (error) {
            //         $log.debug("error retrieving branchOffice count");
            //     });
        },

        deleteRemainder: function (id, callback) {
            services.delete('api/v1/reminders/delete/' + id, function (response) {
                if (response) {
                    callback(response);
                    $rootScope.$broadcast('check');
                    swal("Deleted!", "Remainder was successfully deleted!", "success");
                }
            }, function () {
                swal("Oops", "We couldn't connect to the server!", "error");
            })
            // swal({
            //     title: "Are you sure?",
            //     text: "Are you sure you want to delete this remainder?",
            //     type: "warning",
            //     showCancelButton: true,
            //     closeOnConfirm: false,
            //     confirmButtonText: "Yes, delete it!",
            //     confirmButtonColor: "#ec6c62"}, function () {
            //         $http.delete('api/v1/reminders/delete/'+id).then(function (response) {
            //             callback(response);
            //             $rootScope.$broadcast('check');
            //             swal("Deleted!", "Remainder was successfully deleted!", "success");
            //         },function () {
            //             swal("Oops", "We couldn't connect to the server!", "error");
            //         });
            //     });
        }
    }
});