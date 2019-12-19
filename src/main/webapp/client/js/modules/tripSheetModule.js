'use strict';
/*global angular,_*/


angular.module('myBus.tripSheetModule', ['ngTable', 'ui.bootstrap'])
    .controller("tripSheetController", function ($scope, $rootScope, $log, NgTableParams, $location, $uibModal, $state, paginationService, tripSheetManager, vehicleManager, staffManager) {
        $scope.headline = 'Trip Sheet List';

        $scope.headline = 'Trip Sheets';
        vehicleManager.getVehicles({}, function (response) {
            $scope.vehicles = response.content;
        });

        staffManager.getStaffList({}, function (response) {
            $scope.staffList = response.content;
        });

        $scope.query = {};
        var pageable;

        var loadTripSheetParams = function (tableParams) {
            paginationService.pagination(tableParams, function (response) {
                pageable = {
                    page: tableParams.page(),
                    size: tableParams.count(), sort: response, query: tableParams.query
                };
            });
            $scope.query.page = pageable.page;
            $scope.query.size = pageable.size;
            $scope.loading = true;
            tripSheetManager.getTripSheets($scope.query, function (response) {
                $scope.invalidCount = 0;
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.tripSheets = response.content;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.tripSheets;
                    $scope.tripSheetList = $scope.tripSheets;
                }
            });
        };

        $scope.searchTripSheetReports = function () {
            tripSheetManager.count($scope.query, function (tripSheetCount) {
                $scope.tripSheetTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    }
                }, {
                     counts: [],
                    total: tripSheetCount,
                    getData: function (params) {
                        params.query = $scope.query;
                        loadTripSheetParams(params);
                    }
                });
            });

        };

        $scope.searchTripSheetReports();

        $scope.addTripSheet = function () {
            $state.go('addTripSheet');
        };

        $scope.editTripSheet = function (id) {
            $state.go('editTripSheet', {id: id});
        };

        $scope.deleteTripSheet = function (id) {
            tripSheetManager.deleteTripSheet(id);
        };

        $scope.$on('DeleteTripSheetCompleted', function (e, value) {
            $scope.searchTripSheetReports();
        });
    })
    .controller("addEditTripSheetController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, tripSheetManager, staffManager, vehicleManager, suppliersManager, expenseManager, bankManager) {

        $rootScope.tripSheetId = $stateParams.id;

        var tripSheetId = $stateParams.id;

        $scope.OnClick = function (evt) {
            $rootScope.$broadcast("nameChanged", tripSheetId);
        };
        $scope.OnClick();

        $scope.headline = 'Add Trip Sheet';
        vehicleManager.getVehicles({}, function (response) {
            $scope.vehicles = response.content;
        });

        staffManager.getStaffList({}, function (response) {
            $scope.staffList = response.content;
        });

        $scope.query = {};
        $scope.receiptQuery = {startDate: new Date(), endDate: new Date()};
        $scope.tripExpenseQuery = {startDate: new Date(), endDate: new Date()};
        $scope.tripBankQuery = {startDate: new Date(), endDate: new Date()};

        $scope.reloadTripSheet = function (query) {
            if ($stateParams.id) {
                $scope.headline = 'Update Trip Sheet';
                tripSheetManager.getTripSheetWithId(query, $stateParams.id, function (data) {
                    if (data) {
                        $scope.driverName = data.driverName;
                        $scope.tripSheet = data.tripSettlement;
                        $scope.receipts = data.tsReceipt;
                        $scope.tsBankTransfers = data.tsBankTransfer;
                        $scope.tsPayments = data.tsPayment;
                        $scope.tsOtherExpenses = data.tsOtherExpenses;
                        $scope.tripExpenses = data.tripExpenses;
                        $scope.totalReceiptsSum = 0;
                        $scope.totalTripAmount = 0;
                        $scope.totalTripAdvance = 0;
                        $scope.totalExpenseAmount = 0;
                        $scope.totalBankAmount = 0;
                        $scope.totalCashExpenseAmount = 0;
                        $scope.totalCashReceiptAdvanceAmount = 0;
                        if($scope.receipts) {
                            for (var T = 0; T < $scope.receipts.length; T++) {
                                $scope.totalTripAmount += $scope.receipts[T].totalFare;
                                $scope.totalTripAdvance += $scope.receipts[T].advanceReceived;
                                $scope.totalTripBalance += $scope.receipts[T].balance;
                                if (($scope.receipts[T].totalFare - $scope.receipts[T].advanceReceived) > 0) {
                                    $scope.totalReceiptsSum += ($scope.receipts[T].totalFare - $scope.receipts[T].advanceReceived);
                                }
                                if($scope.receipts[T].paymentMode === 'Cash'){
                                    $scope.totalCashReceiptAdvanceAmount += $scope.receipts[T].advanceReceived;
                                    console.log('cash', $scope.totalCashReceiptAdvanceAmount);
                                }
                            }
                        }
                        if($scope.tripExpenses){
                            for (var e = 0; e < $scope.tripExpenses.length; e++) {
                                $scope.totalExpenseAmount += $scope.tripExpenses[e].amount;
                                if($scope.tripExpenses[e].paymentType === 'CASH'){
                                        $scope.totalCashExpenseAmount += $scope.tripExpenses[e].amount;
                                }
                            }
                        }
                        if($scope.tsBankTransfers){
                            for (var b = 0; b < $scope.tsBankTransfers.length; b++) {
                                $scope.totalBankAmount += $scope.tsBankTransfers[b].amount;
                            }
                        }
                    }
                });
            }
        };
        $scope.reloadTripSheet($scope.query);

        $scope.tripSheet = {
            startDate: new Date(),
            endDate: new Date()
        };


        $scope.saveTripSheet = function () {
            $scope.tripSheet.grandTotalPayments = $scope.tripSheet.dieselExpenses + $scope.tripSheet.tripExpenses + $scope.tripSheet.tripOtherExpenses + $scope.totalReceiptsSum + $scope.tripSheet.balanceReceived;
            if (!$scope.tripSheet.vehicleId) {
                swal("error", "Please select a Truck", "error");
            } else if (!$scope.tripSheet.driverId) {
                swal("error", "Please select a driver", "error");
            } else {
                if ($stateParams.id) {
                    tripSheetManager.updateTripSheet($scope.tripSheet, function (data) {
                        swal("success", "Trip Sheet successfully updated", "success");
                        $state.go('tripsheet');
                        $scope.tripSheet.grandTotalPayments = 0;
                    });
                } else {
                    tripSheetManager.createTripSheet($scope.tripSheet, function (data) {
                        swal("success", "Trip Sheet successfully added", "success");
                        $state.go('tripsheet');
                    });
                }
            }
        };

        $scope.filterTripReceipts = function(){
            tripSheetManager.searchTripReceipts($scope.receiptQuery,function(response){
                $scope.receipts = response.content;
                $scope.totalReceiptsSum = 0;
                $scope.totalTripAmount = 0;
                $scope.totalTripAdvance = 0;
                if($scope.receipts) {
                    for (var T = 0; T < $scope.receipts.length; T++) {
                        $scope.totalTripAmount += $scope.receipts[T].totalFare;
                        $scope.totalTripAdvance += $scope.receipts[T].advanceReceived;
                        $scope.totalTripBalance += $scope.receipts[T].balance;
                        if (($scope.receipts[T].totalFare - $scope.receipts[T].advanceReceived) > 0) {
                            $scope.totalReceiptsSum += ($scope.receipts[T].totalFare - $scope.receipts[T].advanceReceived);
                        }
                    }
                }
            });
        };

        $scope.filterTripExpenses = function(){
            tripSheetManager.searchTripExpenses($scope.tripExpenseQuery,function(response){
                $scope.tripExpenses = response.content;
                $scope.totalExpenseAmount = 0;
                if($scope.tripExpenses){
                    for (var e = 0; e < $scope.tripExpenses.length; e++) {
                        $scope.totalExpenseAmount += $scope.tripExpenses[e].amount;
                    }
                }
            });
        };

        $scope.filterBankTransfers = function(){
            $scope.reloadTripSheet($scope.tripBankQuery);
            tripSheetManager.searchBankTransfers($scope.tripBankQuery,function(response){
                $scope.tsBankTransfers = response.content;
                $scope.totalBankAmount = 0;
                if($scope.tsBankTransfers){
                    for (var b = 0; b < $scope.tsBankTransfers.length; b++) {
                        $scope.totalBankAmount += $scope.tsBankTransfers[b].amount;
                    }
                }
            });
        };

        $scope.receiptData = function () {
            $state.go('tripSheetReceipts');
        };
        $scope.editReceipt = function (id) {
            $state.go('tripSheetReceipts', {id: id});
        };
        $scope.deleteReceipt = function (id) {
            tripSheetManager.deleteTripSettlementReceipt(id);
        };
        $scope.$on('DeleteTripSettlementReceiptCompleted', function (e, value) {
            $scope.reloadTripSheet($scope.query);
        });

        $scope.paymentData = function () {
            $state.go('tripSheetPayments');
        };
        $scope.editTsPayment = function (id) {
            $state.go('tripSheetPayments', {id: id});
        };
        $scope.deleteTsPayment = function (id) {
            tripSheetManager.deleteTripPayments(id);
        };
        $scope.$on('DeleteTripPaymentsCompleted', function (e, value) {
            $scope.reloadTripSheet($scope.query);
        });

        $scope.bankCollectionData = function () {
            $state.go('tripSheetBankCollection');
        };
        $scope.editBankCollection = function (id) {
            $state.go('tripSheetBankCollection', {id: id});
        };
        $scope.deleteBankCollection = function (id) {
            tripSheetManager.deleteBankTransfer(id);
        };
        $scope.$on('DeleteTripSettlementBankTransferCompleted', function (e, value) {
            $scope.reloadTripSheet($scope.query);
        });

        $scope.otherTripExpensesData = function () {
            $state.go('tripSheetOtherExpenses');
        };
        $scope.editOtherData = function (id) {
            $state.go('tripSheetOtherExpenses', {id: id});
        };
        $scope.deleteOtherData = function (id) {
            tripSheetManager.deleteTsOtherExpenses(id);
        };
        $scope.$on('DeleteOtherTripCompleted', function (e, value) {
            $scope.reloadTripSheet($scope.query);
        });

        $scope.cancelTripSheet = function () {
            $state.go('tripsheet');
        };

        $scope.addTripExpense = function () {
            $state.go('tripSheetTripExpenses');
        };

        $scope.editTripExpense = function (id) {
            $state.go('tripSheetTripExpenses', {id:id});
        };
        $scope.deleteTripExpense = function (id) {
            tripSheetManager.deleteTripExpenses(id);
        };
        $scope.$on('DeleteTripExpense', function (e, value) {
            $scope.reloadTripSheet($scope.query);
        });

        suppliersManager.getSuppliersBypartyType("DEBTORS",function (response) {
            $scope.suppliers = response;
        });

        expenseManager.getExpense({}, function (data) {
            if (data) {
                $scope.expenseTypes = data.content;
            }
        });

        bankManager.getBank(function (data) {
            if(data){
                $scope.bankAccounts = data;
            }
        });

    })
    .controller("receiptsTripSheetController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, tripSheetManager, bankManager, suppliersManager) {
        $scope.receiptId = $stateParams.id;
        $scope.$on("nameChanged", function (evt, tripSheetId) {
            $scope.getTripSheetId = tripSheetId;
        });


        if ($stateParams.id) {
            tripSheetManager.getTripSettlementReceipt($scope.receiptId, function (data) {
                $scope.tsReceipt = data.data;
            });
        }
        $scope.saveReceiptData = function () {
            if ($stateParams.id) {
                $scope.tsReceipt.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.updateTripSettlementReceipt($scope.tsReceipt, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            } else {
                $scope.tsReceipt.tripSettlementId = $rootScope.tripSheetId;
                if($scope.tsReceipt.paymentMode === 'Cash'){
                    $scope.tsReceipt.bankId = null;
                }
                tripSheetManager.saveTripSettlementReceipts($scope.tsReceipt, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            }
        };

        $scope.cancelReceiptData = function () {
            $state.go('editTripSheet', {id: $rootScope.tripSheetId});
        };

        bankManager.getBank(function (data) {
            if(data){
                $scope.bankAccounts = data;
            }
        })

        suppliersManager.getSuppliersBypartyType('DEBTORS',function (data) {
            if(data){
                $scope.suppliers = data;
            }
        })

        $scope.calculateBalance = function (totalAmount, advanceAmount) {
            $scope.tsReceipt.balance = totalAmount;
            if (advanceAmount) {
                $scope.tsReceipt.balance = totalAmount - advanceAmount;
            }
        };
    })
    .controller("bankTripSheetController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, tripSheetManager, bankManager) {
        $scope.transferId = $stateParams.id;

        bankManager.getBank(function (data) {
            $scope.allBankes = data;
        });

        if ($stateParams.id) {
            tripSheetManager.getBankTransferData($scope.transferId, function (data) {
                $scope.tsBankTransfer = data.data;
            });
        }

        $scope.tsBankTransfer = {date: new Date()};
        $scope.bankCollectionData = function () {
            if ($stateParams.id) {
                $scope.tsBankTransfer.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.updateBankTransfer($scope.tsBankTransfer, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            } else {
                $scope.tsBankTransfer.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.saveTripSettlementBank($scope.tsBankTransfer, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            }
        };

        $scope.cancelBankCollectionData = function () {
            $state.go('editTripSheet', {id: $rootScope.tripSheetId});
        };
    })
    .controller("paymentsTripSheetController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, suppliersManager, tripSheetManager) {
        $scope.paymentId = $stateParams.id;

        suppliersManager.getSuppliers(function (response) {
            $scope.suppliers = response;
        });

        if ($stateParams.id) {
            tripSheetManager.getTripPayment($scope.paymentId, function (data) {
                $scope.tsPayment = data.data;
            });
        }

        $scope.tsPayment = {date: new Date()}
        $scope.savePaymentData = function () {
            if ($stateParams.id) {
                $scope.tsPayment.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.updateTripPayment($scope.tsPayment, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            } else {
                $scope.tsPayment.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.saveTripPayment($scope.tsPayment, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId})
                });
            }
        };

        $scope.cancelPaymentData = function () {
            $state.go('editTripSheet', {id: $rootScope.tripSheetId})
        };
    })
    .controller("otherTripSheetController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, tripSheetManager, suppliersManager) {
        $scope.otherId = $stateParams.id;

        if ($stateParams.id) {
            tripSheetManager.getTsOtherExpenses($scope.otherId, function (data) {
                $scope.tsOther = data.data;
            });
        }
        $scope.saveOtherData = function () {
            if ($stateParams.id) {
                $scope.tsOther.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.updateTsOtherExpenses($scope.tsOther, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            } else {
                $scope.tsOther.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.saveTsOtherExpenses($scope.tsOther, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            }
        };

        $scope.cancelOtherData = function () {
            $state.go('editTripSheet', {id: $rootScope.tripSheetId});
        };
    })
    .controller("tripExpensesController", function ($scope, $rootScope, $log, $stateParams, NgTableParams, $location, $uibModal, $state, tripSheetManager, suppliersManager, expenseManager) {
        $scope.otherId = $stateParams.id;

        $scope.tripExpense = {
            date: new Date(),
        };


        expenseManager.getExpense({}, function (data) {
            if (data) {
                $scope.expenseTypes = data.content;
            }
        });

        suppliersManager.getSuppliersBypartyType('CREDITORS', function (data) {
            if (data) {
                $scope.suppliers = data;
            }
        });


        if ($stateParams.id) {
            tripSheetManager.getTripExpense($scope.otherId, function (data) {
                $scope.tripExpense = data.data;
            });
        }
        $scope.saveTripData = function () {
            if ($stateParams.id) {
                $scope.tripExpense.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.updateTripExpenses($scope.tripExpense, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            } else {
                $scope.tripExpense.tripSettlementId = $rootScope.tripSheetId;
                tripSheetManager.saveTripExpense($scope.tripExpense, function () {
                    $state.go('editTripSheet', {id: $rootScope.tripSheetId});
                });
            }
        };

        $scope.cancelTripData = function () {
            $state.go('editTripSheet', {id: $rootScope.tripSheetId});
        };

        $scope.calculateAmount = function (quantity, qAmount) {
            $scope.tripExpense.amount = quantity * qAmount;
        };
    })

    .factory('tripSheetManager', function ($http, $log, $rootScope) {
        return {
            createTripSheet: function (tripSheet, callback) {
                $http.post('/api/v1/tripSettlement/saveTripSettlement', tripSheet).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('CreateTripSheetCompleted');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            count: function (filter, callback) {
                $http.post('/api/v1/tripSettlement/count', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving tripSettlement count");
                    });
            },
            getTripSheets: function (filter, callback) {
                $http.post('/api/v1/tripSettlement/search', filter)
                    .then(function (response) {
                        callback(response.data);
                        // $rootScope.$broadcast('FetchingUserNamesComplete');
                    }, function (error) {
                        $log.debug("error retrieving Trip Sheet");
                    });
            },
            getTripSheetWithId: function (query, tripSettlementId, callback) {
                $http.post("/api/v1/tripSettlement/getTripSettlement/" + tripSettlementId, query).then(function (response) {
                    callback(response.data);
                });
            },
            updateTripSheet: function (tripSheet, callback, errorcallback) {
                $http.put('/api/v1/tripSettlement/update/' + tripSheet.id, tripSheet).then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('UpdateUserCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteTripSheet: function (tripSettlementId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this TripSheet?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/tripSettlement/delete/' + tripSettlementId).then(function (response) {
                        $rootScope.$broadcast('DeleteTripSheetCompleted');
                        swal("Deleted!", "Trip Sheet was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },

            saveTripSettlementReceipts: function (tsReceipt, callback) {
                $http.post('/api/v1/tripSettlementsReceipts/saveTripSettlementReceipts', tsReceipt).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('CreateTripSheetCompleted');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            getTripSettlementReceipt: function (receiptId, callback) {
                $http.get("/api/v1/tripSettlementsReceipts/getTripSettlementReceipt/" + receiptId).then(function (response) {
                    callback(response);
                });
            },
            updateTripSettlementReceipt: function (tsReceipt, callback, errorcallback) {
                $http.put('/api/v1/tripSettlementsReceipts/updateTripSettlementReceipt/' + tsReceipt.id, tsReceipt).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('UpdateUserCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteTripSettlementReceipt: function (tripSettlementReceiptId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this TripSettlementReceipt?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/tripSettlementsReceipts/delete/' + tripSettlementReceiptId).then(function (response) {
                        $rootScope.$broadcast('DeleteTripSettlementReceiptCompleted');
                        swal("Deleted!", "Receipt was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },

            saveTripSettlementBank: function (tsReceipt, callback) {
                $http.post('/api/v1/bankTransfers/saveBankTransferData', tsReceipt).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('CreateTripSheetCompleted');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            getBankTransferData: function (transferId, callback) {
                $http.get("/api/v1/bankTransfers/getBankTransferData/" + transferId).then(function (response) {
                    callback(response);
                });
            },
            updateBankTransfer: function (tsBankTransfer, callback, errorcallback) {
                $http.put('/api/v1/bankTransfers/updateBankTransfer/' + tsBankTransfer.id, tsBankTransfer).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('UpdateUserCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteBankTransfer: function (transferId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this TripSettlementBankTransfer?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/bankTransfers/delete/' + transferId).then(function (response) {
                        $rootScope.$broadcast('DeleteTripSettlementBankTransferCompleted');
                        swal("Deleted!", "Bank Transfer was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },

            saveTripPayment: function (tsPayment, callback) {
                $http.post('/api/v1/tripPayments/saveTripPayment', tsPayment).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('CreateTripSheetPaymentsCompleted');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            getTripPayment: function (paymentId, callback) {
                $http.get("/api/v1/tripPayments/getTripPayment/" + paymentId).then(function (response) {
                    callback(response);
                });
            },
            updateTripPayment: function (tsPayment, callback, errorcallback) {
                $http.put('/api/v1/tripPayments/updateTripPayment/' + tsPayment.id, tsPayment).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('UpdatePaymentCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteTripPayments: function (transferId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this TripPayments?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/tripPayments/delete/' + transferId).then(function (response) {
                        $rootScope.$broadcast('DeleteTripPaymentsCompleted');
                        swal("Deleted!", "Trip Payment was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },

            saveTsOtherExpenses: function (tsOther, callback) {
                $http.post('/api/v1/tsOtherExpenses/addTSExpense', tsOther).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('CreateTripSheetPaymentsCompleted');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            getTsOtherExpenses: function (expenseId, callback) {
                $http.get("/api/v1/tsOtherExpenses/getTSOtherExpense/" + expenseId).then(function (response) {
                    callback(response);
                });
            },
            updateTsOtherExpenses: function (tsOther, callback, errorcallback) {
                $http.put('/api/v1/tsOtherExpenses/update/' + tsOther.id, tsOther).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('UpdatePaymentCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteTsOtherExpenses: function (expenseId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this Other Expense?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/tsOtherExpenses/delete/' + expenseId).then(function (response) {
                        $rootScope.$broadcast('DeleteOtherTripCompleted');
                        swal("Deleted!", "Other Expense was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },
            saveTripExpense: function (data, callback) {
                $http.post('/api/v1/tripExpenses/add', data).then(function (response) {
                    callback(response);
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            getTripExpense: function (expenseId, callback) {
                $http.get("/api/v1/tripExpenses/get/" + expenseId).then(function (response) {
                    callback(response);
                });
            },
            updateTripExpenses: function (details, callback, errorcallback) {
                $http.put('/api/v1/tripExpenses/update/' + details.id, details).then(function (response) {
                    callback(response);
                    $rootScope.$broadcast('UpdatePaymentCompleted');
                }, function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            deleteTripExpenses: function (id) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this Trip Expense?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"
                }, function () {

                    $http.delete('api/v1/tripExpenses/delete/' + id).then(function (response) {
                        $rootScope.$broadcast('DeleteTripExpense');
                        swal("Deleted!", "Trip Expense was successfully deleted!", "success");
                    }, function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                });
            },
            searchTripReceipts:function (filter, callback) {
                $http.post('/api/v1/tripSettlementsReceipts/search', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving data");
                    });
            },
            searchTripExpenses:function (filter, callback) {
                $http.post('/api/v1/tripExpenses/getAll', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving data");
                    });
            },
            searchBankTransfers:function (filter, callback) {
                $http.post('/api/v1/bankTransfers/getBankTransfers', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving data");
                    });
            },
        };
    });
