angular.module('myBus.expenseModule', ['ngTable', 'ui.bootstrap'])
    .controller("expenseController", function ($rootScope, $scope, $uibModal, NgTableParams, paginationService, expenseManager) {
        var pageable;
        $scope.query = "";

        $scope.init = function() {
            expenseManager.count($scope.query, function(expenseCount){
                $scope.expenseCount = expenseCount;
                $scope.expenseTableParams = new NgTableParams({
                        page: 1,
                        count:10,
                        sorting: {
                            name: 'asc'
                        }
                    },
                    {
                        counts:[20, 50, 100],
                        total:expenseCount,
                        getData: function (params) {
                            params.query = $scope.query;
                            loadTableData(params);
                        }
                    });
            });
        };

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response, query:tableParams.query};
            });
            $scope.loading = true;
            expenseManager.getExpense(pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.expense = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.expense;
                    $scope.currentPageOfExpense =  $scope.expense;
                }
            });
        };

        $scope.init('');

        $scope.searchFilter = function(){
            $scope.init('');
        }

        $scope.$on('check', function (e, value) {
            $scope.init();
        });

        $scope.delete = function(expenseID){
            expenseManager.deleteExpense(expenseID, function (data) {
                $scope.expense = data;
            });
        };

        $scope.handleClickAddExpense = function (size) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'expense-modal.html',
                controller: 'addExpenseController',
                size: size
            });
        };

        $scope.handleClickUpdateExpense = function (expenseId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'expense-modal.html',
                controller : 'updateExpenseController',
                resolve : {
                    expenseId : function(){
                        return expenseId;
                    }
                }
            });
        }
    })

    .controller("addExpenseController", function ($rootScope, $scope, $uibModal, expenseManager) {
        $scope.addExpense = function () {
            expenseManager.save($scope.expense, function (data) {
                $scope.expense = data;
                $rootScope.modalInstance.close(data);
            })
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

    })

    .controller("updateExpenseController", function ($rootScope, $scope, $uibModal, expenseManager, expenseId) {
        $scope.addExpense = function () {
            expenseManager.update($scope.expense, function (data) {
                $scope.expense = data;
                $rootScope.modalInstance.close(data);
            });
        };

        expenseManager.edit(expenseId, function (res) {
            $scope.expense = res;
        });

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })

.factory('expenseManager', function ($rootScope, $http, $log, services) {
    return {
        getExpense: function (pageable, callback) {
            services.get('/api/v1/expenseType/getAllExpenses', pageable, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error loading expense");
            })
            // $http({url:'/api/v1/expenseType/getAllExpenses',method:"GET",params:pageable})
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error loading expense");
            //     });
        },
        save: function (expense, callback) {
            services.post('/api/v1/expenseType/addtype', expense, function (response) {
                if (response) {
                    callback(response.data)
                    $rootScope.$broadcast('check');
                }
            }, function (err,status) {
                sweetAlert("Error",err.message,"error");
            })
            // $http.post('/api/v1/expenseType/addtype', expense).then(function (response) {
            //     if(angular.isFunction(callback)){
            //         callback(response.data);
            //     }
            //     $rootScope.$broadcast('check');
            // },function (err,status) {
            //     sweetAlert("Error",err.message,"error");
            // })
        },
        edit: function (id, callback) {
            services.get('api/v1/expenseType/get/' + id, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http({url: 'api/v1/expenseType/get/'+id, method: "GET"})
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         swal("oops", error, "error");
            //     })
        },
        update: function (expense, callback){
            services.put('/api/v1/expenseType/updatetype', '', expense, function (response) {
                if(angular.isFunction(callback)){
                    callback(response.data);
                }
                $rootScope.$broadcast('check');
            },function (err,status) {
                sweetAlert("Error",err.message,"error");
            })
            // $http.put('/api/v1/expenseType/updatetype', expense).then(function (response) {
            //     if(angular.isFunction(callback)){
            //         callback(response.data);
            //     }
            //     $rootScope.$broadcast('check');
            // },function (err,status) {
            //     sweetAlert("Error",err.message,"error");
            // });
        },
        count: function (query, callback) {
            services.get('/api/v1/expenseType/count?query=' + query, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving route count");
            })
            // $http.get('/api/v1/expenseType/count?query='+query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving route count");
            //     });
        },
        deleteExpense: function (expenseID, callback) {
            services.delete('api/v1/expenseType/delete/' + expenseID, function (response) {
                if (response) {
                    callback(data);
                    $rootScope.$broadcast('check');
                    swal("Deleted!", "Office was successfully deleted!", "success");
                }
            }, function (error) {
                swal("Oops", "We couldn't connect to the server!", "error");
            })
            // swal({
            //     title: "Are you sure?",
            //     text: "Are you sure you want to delete this expense?",
            //     type: "warning",
            //     showCancelButton: true,
            //     closeOnConfirm: false,
            //     confirmButtonText: "Yes, delete it!",
            //     confirmButtonColor: "#ec6c62"}, function () {
            //     $http.delete('api/v1/expenseType/delete/'+expenseID).then(function (data) {
            //         callback(data);
            //         $rootScope.$broadcast('check');
            //         swal("Deleted!", "Office was successfully deleted!", "success");
            //     },function (error) {
            //         swal("Oops", "We couldn't connect to the server!", "error");
            //     })
            // })
        }
    }
});
