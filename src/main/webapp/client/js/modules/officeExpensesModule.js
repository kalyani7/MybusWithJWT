"use strict";
/*global angular, _*/

angular.module('myBus.officeExpensesModule', ['ngTable', 'ui.bootstrap'])
    .controller("OfficeExpensesController", function ($rootScope, $scope, $filter, $location, $log, $uibModal, printManager, branchOfficeManager,
                                                      NgTableParams, officeExpensesManager, userManager, suppliersManager, paginationService,$state,$stateParams) {

        $scope.loading = false;
        $scope.headline = "Office Expenses";
        $scope.query = {"status": null};
        $scope.user = userManager.getUser();
        $scope.approvedExpenses = [];
        $scope.pendingExpenses = [];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        $scope.selectedPayments = [];
        $scope.suppliers = [];
        var user = userManager.getUser();
        $scope.currentUser = user.fullName;

        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });

        suppliersManager.getSuppliers(function (data) {
            $scope.suppliers = data;
        });

        officeExpensesManager.loadExpenseTypes(function (data) {
            $scope.expenseTypes = data.data;
        });

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        $scope.canAddExpense = function () {
            return user.admin || user.branchOfficeId;
        }
        var loadPendingExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page: tableParams.page(), size: tableParams.count(), sort: sortProps};
            officeExpensesManager.pendingOfficeExpenses(pageable, function (response) {
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.pendingExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.pendingTotal = response.totalElements;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.pendingExpenses;
                }
            });
        };

        var loadApprovedExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page: tableParams.page(), size: tableParams.count(), sort: sortProps};
            officeExpensesManager.approvedOfficeExpenses(pageable, function (response) {
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.approvedExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.approvedExpenses;
                }
            });
        };

        $scope.init = function () {
            officeExpensesManager.count(true, function (expensesCount) {
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    count: 10,
                    sorting: {
                        date: 'desc'
                    },
                }, {
                    counts: [10, 50, 100],
                    total: expensesCount,
                    getData: function (params) {
                        loadPendingExpenses(params);
                    }
                });
            });
            officeExpensesManager.count(false, function (count) {
                $scope.approvedTableParams = new NgTableParams({
                    page: 1, // show first page
                    count: 15,
                    sorting: {
                        date: 'desc'
                    },
                }, {
                    counts: [10, 50, 100],
                    total: count,
                    getData: function (params) {
                        loadApprovedExpenses(params);
                    }
                });
            });
        };
        $scope.init();
        $scope.handleClickAddExpense = function () {
            // $rootScope.modalInstance = $uibModal.open({
            //     templateUrl: 'add-expense-modal.html',
            //     controller: 'EditExpenseController',
            //     resolve: {
            //         expenseId: function () {
            //             return null;
            //         }
            //     }
            // });
            $state.go('home.addOfficeExpenses');
        };
        $rootScope.$on('UpdateHeader', function (e, value) {
            $scope.init();
        });



        $scope.handleClickUpdateExpense = function (expenseId) {
            if(expenseId){
                $state.go('home.updateOfficeExpenses', {id: expenseId});
            }
        };
        $scope.delete = function (expenseId) {
            officeExpensesManager.delete(expenseId, function (data) {
                $scope.init();
            });
        };

        $scope.query = {};
        var loadsearchExpenses = function (tableParams) {
            $scope.loading = true;
            officeExpensesManager.searchExpenses($scope.query, function (response) {
                $scope.loading = false;
                $scope.searchExpenses = response;
                tableParams.data = $scope.searchExpenses;
            });
        };
        $scope.searchInit = function () {
            $scope.searchTableParams = new NgTableParams({
                page: 1, // show first page
                count: 15,
                sorting: {
                    date: 'asc'
                },
            }, {
                counts: [],
                total:$scope.count,
                getData: function (params) {
                    loadsearchExpenses(params);
                }
            });
        }

        $scope.print = function (eleId) {
            printManager.print(eleId);
        }

        $scope.search = function () {
            $scope.query = {
                "startDate": $scope.dt.getFullYear() + "-" + [$scope.dt.getMonth() + 1] + "-" + $scope.dt.getDate(),
                "endDate": $scope.dt2.getFullYear() + "-" + [$scope.dt2.getMonth() + 1] + "-" + $scope.dt2.getDate(),
                "officeId": $scope.officeId,
                "expenseType": $scope.expenseType,
                "userId": $scope.userSelect
            }
            $scope.searchInit();
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        $scope.togglePaymentSelection = function (paymentId) {
            var idx = $scope.selectedPayments.indexOf(paymentId);
            if (idx > -1) {
                $scope.selectedPayments.splice(idx, 1);
            } else {
                $scope.selectedPayments.push(paymentId);
            }
        }
        $scope.approveOrRejectExpense = function (status) {
            officeExpensesManager.approveOrRejectExpenses($scope.selectedPayments, status, function (data) {
                $rootScope.$broadcast('UpdateHeader');
                $scope.selectedPayments = [];
                swal("Great", "Expense is updated", "success");
            });
        }

    })
    .controller("EditExpenseController", function ($rootScope, $scope, $uibModal, $location, officeExpensesManager, userManager, branchOfficeManager, suppliersManager, vehicleManager, FileUploader, S3UploadManager, Upload, $state, $stateParams) {

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.title = "Add Office Expenses";
        $scope.checkType = function (check) {
            if (check === true) {
                $scope.type = true;
            }
            else {
                $scope.type = false;
            }
        };
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });

        suppliersManager.getSuppliers(function (suppliers) {
            $scope.suppliers = suppliers;
        });
        vehicleManager.getVehicles(null, function (data) {
            $scope.vehicles = data.content;
        });
        $scope.user = userManager.getUser();
        $scope.expense = {'branchOfficeId': $scope.user.branchOfficeId};
        $scope.today();
        $scope.expenseTypes = [];

        officeExpensesManager.loadExpenseTypes(function (data) {
            $scope.expenseTypes = data.data;
        });
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        if ($stateParams.id) {
            $scope.title = "Update Office Expenses";
             // $scope.setExpenseIntoModal = function (expaneId) {
                officeExpensesManager.getExpenseById($stateParams.id, function (data) {
                    $scope.expense = data;
                });
             // };

        }

        $scope.add = function () {
            if ($scope.expense.expenseType === 'SALARY') {
                if (!$scope.expense.fromDate || !$scope.expense.toDate) {
                    swal("Error!", "Please enter the dates for Salary", "error");
                    return;
                }
            }
            if ($scope.expense.expenseType === 'SALARY' || $scope.expense.expenseType === 'VEHICLE MAINTENANCE'
                || $scope.expense.expenseType === 'TRIP ADVANCE' || $scope.expense.expenseType === 'DIESEL') {
                if (!$scope.expense.vehicleId) {
                    swal("Error!", "Please select the vehicle number", "error");
                    return;
                }
            }

            if ($stateParams.id) {
                officeExpensesManager.save($scope.expense, function (data) {
                    swal("Great", "Saved successfully", "success");
                });
            }
            else {
                $scope.expense.date = $scope.dt;
                officeExpensesManager.save($scope.expense, function (data) {
                    swal("Great", "Saved successfully", "success");
                    // $location.url('/officeexpenses');
                    $state.go('home.officeexpenses')
                });
            }
        };

    }).factory('officeExpensesManager', function ($rootScope, $http, $log, services) {
    return {
        loadExpenseTypes: function (callback) {
            services.get('/api/v1/officeExpenses/types', '', function (response) {
                if (response) {
                    callback(response)
                }
            }, function (error) {
                $log.debug("error retrieving expense types");
            })
            // $http({url: '/api/v1/officeExpenses/types', method: "GET"})
            //     .then(function (response) {
            //         callback(response);
            //     }, function (error) {
            //         $log.debug("error retrieving expense types");
            //     });
        },
        pendingOfficeExpenses: function (pageable, callback) {
            services.get('/api/v1/officeExpenses/pending', pageable, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving expenses");
            })
            // $http({url: '/api/v1/officeExpenses/pending', method: "GET", params: pageable})
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving expenses");
            //     });
        },
        approvedOfficeExpenses: function (pageable, callback) {
            services.get('/api/v1/officeExpenses/approved', pageable, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving expenses");
            });
            // $http({url: '/api/v1/officeExpenses/approved', method: "GET", params: pageable})
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving expenses");
            //     });
        },
        searchExpenses: function (searchExpense, callback) {
            services.post('/api/v1/officeExpenses/search', searchExpense, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (err, status) {
                sweetAlert("Error searching expenses", err.message, "error");
            })
            // $http.post('/api/v1/officeExpenses/search', searchExpense).then(function (response) {
            //     if (angular.isFunction(callback)) {
            //         callback(response.data);
            //     }
            // }, function (err, status) {
            //     sweetAlert("Error searching expenses", err.message, "error");
            // });
        },

        count: function (pending, callback) {
            services.get('/api/v1/officeExpenses/count?pending=' + pending, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving expenses count");
            })
            // $http.get('/api/v1/officeExpenses/count?pending=' + pending)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving expenses count");
            //     });
        },
        delete: function (expenseId, callback) {
            services.delete('/api/v1/officeExpense/' + expenseId, function (response) {
                if (response) {
                    callback(response.data);
                    swal("Great", "Saved Deleted", "success");
                }
            }, function (error) {
                $log.debug("error deleting expense");
                sweetAlert("Error", err.message, "error");
            })
            // swal({
            //     title: "Are you sure?",
            //     text: "Are you sure you want to delete this expense?",
            //     type: "warning",
            //     showCancelButton: true,
            //     closeOnConfirm: false,
            //     confirmButtonText: "Yes, delete it!",
            //     confirmButtonColor: "#ec6c62"
            // }, function () {
            //
            //     $http.delete('/api/v1/officeExpense/' + expenseId)
            //         .then(function (response) {
            //             callback(response.data);
            //             swal("Great", "Saved Deleted", "success");
            //         }, function (error) {
            //             $log.debug("error deleting expense");
            //             sweetAlert("Error", err.message, "error");
            //         });
            // })

        },
        getExpenseById: function (id, callback) {
            $log.debug("fetching expense data ...");
            services.get('/api/v1/officeExpense/' + id, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (err, status) {
                sweetAlert("Error", err.message, "error");
            })
            // $http.get('/api/v1/officeExpense/' + id)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (err, status) {
            //         sweetAlert("Error", err.message, "error");
            //     });
        },
        save: function (officeExpense, callback) {
            if (!officeExpense.id) {
                services.post('/api/v1/officeExpense/', officeExpense, function (response) {
                    if (response) {
                        callback(response.data);
                        $rootScope.$broadcast('UpdateHeader');
                        swal("Great", "Saved successfully", "success");
                    }
                }, function (error) {
                    sweetAlert("Error", error.data.message, "error");
                })
                // $http.post('/api/v1/officeExpense/', officeExpense).then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //         $rootScope.$broadcast('UpdateHeader');
                //         swal("Great", "Saved successfully", "success");
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error", err.data.message, "error");
                // });
            } else {
                services.put('/api/v1/officeExpense/', '', officeExpense, function (response) {
                    if (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Updated successfully", "success");
                        }
                    }
                }, function (err, status) {
                    sweetAlert("Error", err.data.message, "error");
                })
                // $http.put('/api/v1/officeExpense/', officeExpense).then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //         $rootScope.$broadcast('UpdateHeader');
                //         swal("Great", "Updated successfully", "success");
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error", err.data.message, "error");
                // });
            }
        },

        approveOrRejectExpenses: function (paymentIds, approve, callback) {
            services.post('/api/v1/officeExpenses/approveOrReject/' + approve, paymentIds, function (response) {
                if (response) {
                    if (angular.isFunction(callback)) {
                        callback(response.data);
                        $rootScope.$broadcast('UpdateHeader');
                        $rootScope.modalInstance.dismiss('success');
                    }
                }
            }, function (err, status) {
                sweetAlert("Error", err.data.message, "error");
            })
            // $http.post('/api/v1/officeExpenses/approveOrReject/' + approve, paymentIds).then(function (response) {
            //     if (angular.isFunction(callback)) {
            //         callback(response.data);
            //         $rootScope.$broadcast('UpdateHeader');
            //         $rootScope.modalInstance.dismiss('success');
            //     }
            // }, function (err, status) {
            //     sweetAlert("Error", err.data.message, "error");
            // });
        }
    };
}).factory('printManager', function () {
    return {
        print: function (eleId) {
            var mywindow = window.open('', 'PRINT', 'height=400,width=800');
            mywindow.document.write('<html><head><title>' + document.title + '</title> <style>table{border: 1px}</style>');
            mywindow.document.write('</head><body >');
            mywindow.document.write('<h1>' + document.title + '</h1>');
            mywindow.document.write(document.getElementById(eleId).innerHTML);
            mywindow.document.write('</body></html>');

            mywindow.document.close(); // necessary for IE >= 10
            mywindow.focus(); // necessary for IE >= 10*/

            mywindow.print();
            mywindow.close();
            return true;
        }
    };
}).factory('S3UploadManager', function ($rootScope, $http, $log, services) {
    return {
        getUploads: function (id, callback) {
            services.get('/api/v1/getUploads/?id=' + id, '', function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (err, status) {
                sweetAlert("Error", err.data.message, "error");
            })
            // $http.get('/api/v1/getUploads/?id='+id)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (err, status) {
            //         sweetAlert("Error", err.data.message, "error");
            //     });
        }
    }
});