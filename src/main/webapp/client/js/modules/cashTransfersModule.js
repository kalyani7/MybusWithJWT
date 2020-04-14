"use strict";
/*global angular, _*/

angular.module('myBus.cashTransfersModule', ['ngTable', 'ui.bootstrap'])
    .controller("cashTransfersController",function($rootScope, $scope, $filter, $location,paginationService, $log,$uibModal, NgTableParams, branchOfficeManager, cashTransferManager, userManager){
        $scope.loading = false;
        $scope.approvedCashTransfers=[];
        $scope.pendingCashTransfers=[];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        $scope.offices = [];
        $scope.members =[];
        $scope.user = userManager.getUser();

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        $scope.currentPageOfCashTransfers=[];
        $scope.canAddCashTransfer = function() {
            var user = userManager.getUser();
            return user.admin || user.branchOfficeId;
        };
        $scope.userId = userManager.getUser().id;

        var pageable ;
        var loadPendingCashTransfers = function (tableParams) {
            $scope.loading = true;
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            cashTransferManager.pendingCashTransfers(pageable).then(function(response) {
                let pendingCashTransfers = response[0].data.content;
                let userNames = response[1].data;
                if (angular.isArray(pendingCashTransfers)) {
                    $scope.loading = false;
                    $scope.pendingCashTransfers = pendingCashTransfers;
                    angular.forEach($scope.pendingCashTransfers, function (cashTransfer) {
                        cashTransfer.attrs.createdBy = userNames[cashTransfer.createdBy];
                    });
                    $scope.loading = false;
                    tableParams.total(response[0].data.totalElements);
                    $scope.pendingCashTransfersCount = response[0].data.totalElements;
                    tableParams.data = $scope.pendingCashTransfers;
                    $scope.currentPageOfPendingCashTransfers = $scope.pendingCashTransfers;
                }
            });
        };
        var loadApprovedCashTransfers = function (tableParams) {
            $scope.loading = true;
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            cashTransferManager.approvedCashTransfers(pageable).then(function(response) {
                let approvedCashTransfers = response[0].data.content;
                let userNames = response[1].data;
                if (angular.isArray(approvedCashTransfers)) {
                    $scope.loading = false;
                    $scope.approvedCashTransfers = approvedCashTransfers;
                    angular.forEach($scope.approvedCashTransfers, function (cashTransfer) {
                        cashTransfer.attrs.createdBy = userNames[cashTransfer.createdBy];
                    });
                    $scope.loading = false;
                    tableParams.total(response[0].data.totalElements);
                    $scope.approvedCashTransfersCount = response[0].data.totalElements;
                    tableParams.data = $scope.approvedCashTransfers;
                    $scope.currentPageOfApprovedCashTransfers = $scope.approvedCashTransfers;
                }
            });
        };

        $scope.init = function() {
            cashTransferManager.pendingCount(function(pendingCashTransfersCount){
                $scope.pendingCashTransferTableParams = new NgTableParams({
                    page: 1,
                    size:10,
                    count:10,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[],
                    total:pendingCashTransfersCount,
                    getData: function (params) {
                        loadPendingCashTransfers(params);
                    }
                });
            });
            cashTransferManager.approvedCount(function(approvedCashTransfersCount){
                $scope.approvedCashTransfersTableParams = new NgTableParams({
                    page: 1,
                    size:10,
                    count:10,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[],
                    total:approvedCashTransfersCount,
                    getData: function (params) {
                        loadApprovedCashTransfers(params);
                    }
                });
            });
        };

        $scope.init();

        $scope.$on('UpdateHeader',function(e,value){
            $scope.init();
        });
        $scope.handleClickAddCashTransfer = function() {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-cashTransfer-modal.html',
                controller: 'editCashTransferController',
                resolve : {
                    cashTransferId : function(){
                        return null;
                    }
                }
            });
        };

        $scope.handleClickUpdateCashTransfer = function(cashTransferId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-cashTransfer-modal.html',
                controller: 'editCashTransferController',
                resolve : {
                    cashTransferId : function(){
                        return cashTransferId;
                    }
                }
            });
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.delete = function(cashTransferId) {
            cashTransferManager.delete(cashTransferId, function(data){
                $scope.init();
            });
        };
        $scope.approveOrRejectCashTransfer = function(cashTransfer, status){
            if(status == "Approve"){
                cashTransfer.status ="Approved";
            } else {
                cashTransfer.status ="Rejected";
            }
            cashTransferManager.save(cashTransfer, function(data){
                $rootScope.$broadcast('UpdateHeader');
                swal("Great", "cashTransfer is updated", "success");
            });
        }




        /* date picker functions */
        $scope.check = true;
        $scope.officeId = null;
        $scope.userSelect = null;
        $scope.today = function() {
            $scope.dt = new Date();
            $scope.dt2 = new Date();
        };

        $scope.today();
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.clear = function() {
            $scope.dt = new Date();
            $scope.dt2 = new Date();
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };

        $scope.toggleMin = function() {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function(year, month, day) {
            $scope.dt = new Date(year, month, day);
            $scope.dt2 = new Date(year, month, day);
        };
        $scope.monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        $scope.popup1= {
            opened: false
        };

        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0,0,0,0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
            return '';
        }

        /*Date picker 2 */
        $scope.open2 = function() {
            $scope.popup2.opened = true;
        };

        $scope.popup2 = {
            opened: false
        };

        $scope.query = {};
        var loadsearchExpenses = function (tableParams) {
            $scope.loading = true;
            cashTransferManager.search($scope.query, function(response){
                $scope.loading = false;
                $scope.searchExpenses = response;
                tableParams.data = $scope.searchExpenses;

            });
        };
        $scope.searchInit = function (){
            $scope.searchTableParams = new NgTableParams({
                page: 1, // show first page
                count:15,
                sorting: {
                    date: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    loadsearchExpenses(params);
                }
            });
        }


        $scope.search = function(){
            $scope.query = {
                "startDate" : $scope.dt.getFullYear()+"-"+[$scope.dt.getMonth()+1]+"-"+$scope.dt.getDate(),
                "endDate" :  $scope.dt2.getFullYear()+"-"+[$scope.dt2.getMonth()+1]+"-"+$scope.dt2.getDate(),
                "officeId" : $scope.officeId,
                "userId" : $scope.userSelect
            }
            $scope.searchInit();
        }

    })
    .controller("editCashTransferController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams,cashTransferId, cashTransferManager, userManager, branchOfficeManager){
        $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.user = userManager.getUser();

        $scope.cashTransfer = {
            date : new Date(),
            toUserId:'',
            fromUserId : $scope.user.id,
            amount:'',
            description:''
        };
        $scope.today();
        $scope.date = null;
        $scope.format = 'dd-MMMM-yyyy';

        $scope.users = [];
        userManager.getUserNames(function(data){
            $scope.users = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        if(cashTransferId) {
                $scope.setCashTransferIntoModal = function (cashTransferId) {
                    cashTransferManager.getCashTransferById(cashTransferId, function (data) {
                        $scope.cashTransfer = data;
                    });
                };
                $scope.setCashTransferIntoModal(cashTransferId);
        }

        $scope.add = function(){
            if(!$scope.cashTransfer.amount) {
                swal("Oops", "Cash transfer amount is required", "error");
                return;
            }
            if(!$scope.cashTransfer.toUserId) {
                swal("Oops", "Cash transfer toUser is required", "error");
                return;
            }
            $scope.cashTransfer.date = $scope.dt;
            cashTransferManager.save( $scope.cashTransfer, function (data) {
                swal("Great", "Saved successfully", "success");
            });

        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function() {

        };
        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };
        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
        }
        $scope.toggleMin = function() {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function(year, month, day) {

            $scope.dt = new Date(year, month, day);
        };
        $scope.popup1 = {
            opened: false
        };
        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0,0,0,0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
            return '';
        }


    })
    .factory('cashTransferManager', function ($rootScope, $q, $http, $log, $cookies, services) {
        var token = $cookies.get('token');
        var tokenType = $cookies.get('tokenType');
        var sendToken = tokenType + ' ' + token;
        var cashTransfer = {};
        return {
            /*load: function (pageable) {
                var deferred = $q.defer();
                $q.all([ $http({url:'/api/v1/cashTransfer/all',method: "GET",params: pageable}),
                    $http.get('/api/v1/branchOffice/names'),
                    $http.get('/api/v1/userNamesMap')]).then(
                function(results) {
                    deferred.resolve(results)
                },
                function(errors) {
                    deferred.reject(errors);
                },
                function(updates) {
                    deferred.update(updates);
                });
                return deferred.promise;

                /*
                $http({url:'/api/v1/cashTransfer/all',method: "GET",params: pageable})
                    .then(function (response) {
                        cashTransfer = response.data;
                        callback(cashTransfer);
                        $rootScope.$broadcast('cashTransfersInitComplete');
                    },function (error) {
                        $log.debug("error retrieving cashTransfers");
                    });

            },*/
            pendingCashTransfers: function (pageable) {
                var deferred = $q.defer();

                $q.all([$http({url: '/api/v1/cashTransfer/pending/all', method: "GET", params: pageable, headers: {"Authorization": sendToken}}),
                    $http({url: '/api/v1/userNamesMap', method: "GET", headers: {"Authorization": sendToken}})]).then(
                    function(results) {
                        deferred.resolve(results)
                    },
                    function(errors) {
                        deferred.reject(errors);
                    },
                    function(updates) {
                        deferred.update(updates);
                    });
                return deferred.promise;
            },
            approvedCashTransfers: function (pageable) {
                var deferred = $q.defer();
                $q.all([$http({url:'/api/v1/cashTransfer/nonpending/all',method:"GET", params: pageable, headers: {"Authorization": sendToken}}),
                    $http({url: '/api/v1/userNamesMap', method: "GET", headers: {"Authorization": sendToken}})]).then(
                    function(results) {
                        deferred.resolve(results)
                    },
                    function(errors) {
                        deferred.reject(errors);
                    },
                    function(updates) {
                        deferred.update(updates);
                    });
                return deferred.promise;
            },
            pendingCount: function (callback) {
                services.post('/api/v1/cashTransfer/pending/count', {}, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving payments count");
                })
                // $http.post('/api/v1/cashTransfer/pending/count',{})
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving payments count");
                //     });
            },
            approvedCount: function (callback) {
                services.post('/api/v1/cashTransfer/nonpending/count', {}, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving cashTransfers");
                })
                // $http.post('/api/v1/cashTransfer/nonpending/count',{})
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         $log.debug("error retrieving cashTransfers");
                //     });
            },
            getCashTransferById : function(cashTransferId, callback) {
                services.get("/api/v1/cashTransfer/" + cashTransferId, '', function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function(error){
                    swal("oops", error, "error");
                })
                // $http.get("/api/v1/cashTransfer/"+cashTransferId)
                //     .then(function(response){
                //         callback(response.data)
                //     },function(error){
                //         swal("oops", error, "error");
                //     })
            },
            delete: function (cashTransferId, callback) {
                services.delete('/api/v1/cashTransfer/' + cashTransferId, function (response) {
                    callback(response.data);
                    swal("Deleted!", "Cash Transfer has been deleted successfully!", "success");
                }, function (error) {
                    $log.debug("error retrieving cashTransfers");
                    swal("Oops", "We couldn't connect to the server!", "error");
                })
                // swal({
                //     title: "Are you sure?",
                //     text: "Are you sure you want to delete this Cash Transfer?",
                //     type: "warning",
                //     showCancelButton: true,
                //     closeOnConfirm: false,
                //     confirmButtonText: "Yes, delete it!",
                //     confirmButtonColor: "#ec6c62"},function(){
                //         $http.delete('/api/v1/cashTransfer/'+cashTransferId)
                //         .then(function (response) {
                //             callback(response.data);
                //             swal("Deleted!", "Cash Transfer has been deleted successfully!", "success");
                //         },function (error) {
                //             $log.debug("error retrieving cashTransfers");
                //             swal("Oops", "We couldn't connect to the server!", "error");
                //         });
                //         })
            },
            save: function(cashTransfer, callback) {
                if (!cashTransfer.id) {
                    services.post('/api/v1/cashTransfer/', cashTransfer, function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                        swal("Great", "Saved successfully", "success");
                        $rootScope.modalInstance.dismiss('success');
                        $rootScope.$broadcast('UpdateHeader');
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    })
                    // $http.post('/api/v1/cashTransfer/', cashTransfer)
                    //     .then(function (response) {
                    //     if (angular.isFunction(callback)) {
                    //         callback(response.data);
                    //     }
                    //     swal("Great", "Saved successfully", "success");
                    //     $rootScope.modalInstance.dismiss('success');
                    //     $rootScope.$broadcast('UpdateHeader');
                    //
                    // }, function (err, status) {
                    //     sweetAlert("Error", err.data.message, "error");
                    // });
                } else {
                    services.put('/api/v1/cashTransfer/', '', cashTransfer, function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                        $rootScope.$broadcast('UpdateHeader');
                        $rootScope.modalInstance.dismiss('success');
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    })
                    // $http.put('/api/v1/cashTransfer/', cashTransfer).then(function (response) {
                    //     if (angular.isFunction(callback)) {
                    //         callback(response.data);
                    //     }
                    //     $rootScope.$broadcast('UpdateHeader');
                    //     $rootScope.modalInstance.dismiss('success');
                    // }, function (err, status) {
                    //     sweetAlert("Error", err.data.message, "error");
                    // });
                }
            },
            getAllData: function () {
                return amount;
            },
            getOneById: function (id) {
                return _.first(_.select(amount, function (value) {
                    return value.id === id;
                }));
            },
            search: function(searchExpense, callback){
                services.post('/api/v1/cashTransfer/search', searchExpense, function (response) {
                    if (angular.isFunction(callback)) {
                        callback(response.data);
                    }
                }, function (err, status) {
                    sweetAlert("Error searching cash transfers", err.message, "error");
                })
                // $http.post('/api/v1/cashTransfer/search', searchExpense).then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error searching cash transfers", err.message, "error");
                // });
            },
        };
    });

