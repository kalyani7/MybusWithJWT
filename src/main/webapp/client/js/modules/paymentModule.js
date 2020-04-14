"use strict";
/*global angular, _*/

angular.module('myBus.paymentModule', ['ngTable', 'ui.bootstrap'])
    .controller("PaymentController",function($rootScope, $scope, $filter, $location, $log,$uibModal, NgTableParams,serviceReportsManager, paymentManager, userManager,
                                             branchOfficeManager, paginationService){
        $scope.loading = false;
        $scope.query = {"status":null};
        $scope.user = userManager.getUser();
        $scope.approvedPayments=[];
        $scope.pendingPayments=[];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        $scope.selectedPayments = [];
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });
        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        $scope.canAddPayment = function() {
            var user = userManager.getUser();
            return user.admin || user.branchOfficeId;
        }
        var loadPendingPayments = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentManager.pendingPayments(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.pendingPayments = response.content;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.pendingPayments;
                }
            });
        };

        var loadApprovedPayments = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentManager.approvedPayments(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.approvedPayments = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.approvedPayments;
                }
            });
        };
        $scope.init = function() {
            paymentManager.count(true, function(paymentsCount){
                $scope.pendingTotal = paymentsCount;
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[10,50,100],
                    total:paymentsCount,
                    getData: function (params) {
                        loadPendingPayments(params);
                    }
                });
            });
            paymentManager.count(false, function(count){
                $scope.approvedTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:15,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[10,50,100],
                    total:count,
                    getData: function (params) {
                        loadApprovedPayments(params);
                    }
                });
            });
        };

        $scope.init();
        $scope.searchFilter = function(){
            $scope.init();
        }
        $scope.handleClickAddPayment = function() {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-payment-modal.html',
                controller : 'EditPaymentController',
                resolve : {
                    paymentId : function(){
                        return null;
                    }
                }
            });
        };

        $rootScope.$on('UpdateHeader',function (e,value) {
            $scope.init();
        });

        $scope.handleClickUpdatePayment = function(paymentId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-payment-modal.html',
                controller : 'EditPaymentController',
                resolve : {
                    paymentId : function(){
                        return paymentId;
                    }
                }
            });
        };
        $scope.BookingDuePopUpPayments = function(bookingId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'booking-popup-modal.html',
                controller : 'popUpBookingControllerPayments',
                resolve : {
                    bookingId : function(){
                        return bookingId;
                    }
                }
            });
        };
        $scope.delete = function(paymentId) {
            paymentManager.delete(paymentId, function(data){
                $scope.init();
            });
        };
        $scope.popUp = function (formId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'service-form-modal.html',
                controller:'popUpController',
                resolve : {
                    formId : function(){
                        return formId;
                    }
                }
            })
        }
        $scope.togglePaymentSelection = function(paymentId){
            var idx = $scope.selectedPayments.indexOf(paymentId);
            if (idx > -1) {
                $scope.selectedPayments.splice(idx, 1);
            } else {
                $scope.selectedPayments.push(paymentId);
            }
        }
        $scope.approveOrRejectPayment = function(status){
            paymentManager.approveOrRejectPayments($scope.selectedPayments, status, function(data){
                $rootScope.$broadcast('UpdateHeader');
                $scope.selectedPayments = [];
                swal("Great", "Payment is updated", "success");
            });
        }


        $scope.officeSelect = null;
        $scope.userSelect = null;

        $scope.query = {};
        var loadSearchPayments = function (tableParams) {
            $scope.loading = true;
            paymentManager.searchPayments($scope.query, function(response){
                $scope.loading = false;
                $scope.searchPayments = response;
                tableParams.data = $scope.searchPayments;
            });
        };
        $scope.searchInit = function (){
            $scope.searchPaymentsTableParams = new NgTableParams({
                page: 1,
                count:15,
                sorting: {
                    date: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    loadSearchPayments(params);
                }
            });
        }
        $scope.search = function(){
            $scope.query = {
                "startDate" : $scope.stDt.getFullYear()+"-"+[$scope.stDt.getMonth()+1]+"-"+$scope.stDt.getDate(),
                "endDate" :  $scope.endDt.getFullYear()+"-"+[$scope.endDt.getMonth()+1]+"-"+$scope.endDt.getDate(),
                "type" :  $scope.typeSelect,
                "officeId" : $scope.officeSelect,
                "userId" : $scope.userSelect
            }
            $scope.searchInit();
        }

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }
    })
    .controller("popUpController", function($scope,$rootScope, serviceReportsManager , formId){
        $scope.service = {};
            serviceReportsManager.getForm(formId,function (data) {
                $scope.service = data;
            })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        })

    .controller("EditPaymentController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams, paymentManager, userManager, branchOfficeManager,paymentId) {
        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.user = userManager.getUser();

        $scope.payment = {'type': 'EXPENSE', 'branchOfficeId': $scope.user.branchOfficeId};
        $scope.today();
        $scope.date = null;
        $scope.format = 'dd-MMMM-yyyy';

        $scope.offices = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.showType = function () {
            console.log($scope.payment);
        };

        if (paymentId) {
            $scope.setPaymentIntoModal = function (paymentId) {
                paymentManager.getPaymentById(paymentId, function (data) {
                    $scope.payment = data;
                });
            };
            $scope.setPaymentIntoModal(paymentId);
        }

        $scope.add = function () {
            if (paymentId) {
                if ($scope.addNewExpenseForm.$invalid) {
                    swal("Error!", "Please fix the errors in the form", "error");
                    return;
                }
                paymentManager.save(paymentId, $scope.payment, function (data) {
                    swal("Great", "Saved successfully", "success");
                });
            }

            $scope.payment.date = $scope.dt;
            paymentManager.save($scope.payment, function (data) {
                swal("Great", "Saved successfully", "success");
                // $location.url('/payments');
                $state.go('home.payments')
            });
        }

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function () {

        }
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

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function () {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function (year, month, day) {
            $scope.dt = new Date(year, month, day);
        };
        $scope.popup1 = {
            opened: false
        };
        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
        };
    })
    .controller("popUpBookingControllerPayments", function($scope,$rootScope, serviceReportsManager , bookingId){
        $scope.booking = {};
        serviceReportsManager.getBooking(bookingId,function (data) {
            $scope.booking = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .factory('paymentManager', function ($rootScope, $http, $log, services) {
        var payments = {};
        return {
            pendingPayments: function (pageable, callback) {
                services.get('/api/v1/payments/pending', pageable, function (response) {
                    if (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }
                }, function (error) {
                    $log.debug("error retrieving payments");
                });
                // $http({url:'/api/v1/payments/pending',method:"GET", params: pageable})
                //     .then(function (response) {
                //         payments = response.data;
                //         callback(payments);
                //         $rootScope.$broadcast('paymentsInitComplete');
                //     }, function (error) {
                //         $log.debug("error retrieving payments");
                //     });
            },
            approvedPayments: function (pageable, callback) {
                services.get('/api/v1/payments/approved', pageable, function (response) {
                    if (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }
                }, function (error) {
                    $log.debug("error retrieving payments");
                })
                // $http({url:'/api/v1/payments/approved',method:"GET", params: pageable})
                //     .then(function (response) {
                //         payments = response.data;
                //         callback(payments);
                //         $rootScope.$broadcast('paymentsInitComplete');
                //     }, function (error) {
                //         $log.debug("error retrieving payments");
                //     });
            },
            count: function (pendingPayments, callback) {
                services.get('/api/v1/payments/count?pending=' + pendingPayments, '', function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function (error) {
                    $log.debug("error retrieving payments count");
                })
                // $http.get('/api/v1/payments/count?pending='+pendingPayments)
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving payments count");
                //     });
            },
            countVehicleExpenses: function (query, callback) {
                services.get('/api/v1/vehicleExpenses/count', '', function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function (error) {
                    $log.debug("error retrieving payments count");
                })
                // $http.get('/api/v1/vehicleExpenses/count')
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving payments count");
                //     });
            },
            getVehicleExpenses: function (pageable, callback) {
                services.get('/api/v1/vehicleExpenses', pageable, function (response) {
                    if (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }
                }, function (error) {
                    $log.debug("error retrieving payments");
                })
                // $http({url:'/api/v1/vehicleExpenses',method:"GET", params: pageable})
                //     .then(function (response) {
                //         payments = response.data;
                //         callback(payments);
                //         $rootScope.$broadcast('paymentsInitComplete');
                //     }, function (error) {
                //         $log.debug("error retrieving payments");
                //     });
            },
            delete: function (paymentId, callback) {
                services.delete('/api/v1/payment/' + paymentId, function (response) {
                    callback(response.data);
                    swal("Great", "Saved Deleted", "success");
                }, function (error) {
                    $log.debug("error deleting payment");
                    sweetAlert("Error",err.message,"error");
                })
                // $http.delete('/api/v1/payment/' + paymentId)
                //     .then(function (response) {
                //         callback(response.data);
                //         swal("Great", "Saved Deleted", "success");
                //     }, function (error) {
                //         $log.debug("error deleting payment");
                //         sweetAlert("Error",err.message,"error");
                //     });
            },
            getPaymentById: function (id,callback) {
                $log.debug("fetching payment data ...");
                services.get('/api/v1/payment/' + id, function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function (err,status) {
                    sweetAlert("Error",err.message,"error");
                })
                // $http.get('/api/v1/payment/'+id)
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (err,status) {
                //         sweetAlert("Error",err.message,"error");
                //     });
            },
            searchPayments: function(searchPayments, callback){
                services.post('/api/v1/payment/search', searchPayments, function (response) {
                    if (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }
                }, function (err, status) {
                    sweetAlert("Error searching payments", err.message, "error");
                })
                // $http.post('/api/v1/payment/search', searchPayments)
                //     .then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error searching payments", err.message, "error");
                // });
            },
            save: function (payment, callback) {
                if (!payment.id) {
                    services.post('/api/v1/payment/', payment, function (response) {
                        if (response) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    });
                    // $http.post('/api/v1/payment/', payment).then(function (response) {
                    //     if (angular.isFunction(callback)) {
                    //         callback(response.data);
                    //         $rootScope.$broadcast('UpdateHeader');
                    //         swal("Great", "Saved successfully", "success");
                    //         $rootScope.modalInstance.dismiss('success');
                    //     }
                    // }, function (err, status) {
                    //     sweetAlert("Error", err.data.message, "error");
                    // });
                } else {
                    services.put('/api/v1/payment/', '', payment, function (response) {
                        if (response) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    })
                    // $http.put('/api/v1/payment/', payment).then(function (response) {
                    //     if (angular.isFunction(callback)) {
                    //         callback(response.data);
                    //         $rootScope.$broadcast('UpdateHeader');
                    //         swal("Great", "Saved successfully", "success");
                    //         $rootScope.modalInstance.dismiss('success');
                    //     }
                    // }, function (err, status) {
                    //     sweetAlert("Error", err.data.message, "error");
                    // });
                }
            },
            getAllData: function () {
                return payments;
            },
            getOneById: function (id) {
                return _.first(_.select(expenses, function (value) {
                    return value.id === id;
                }));
            },
            approveOrRejectPayments:function(paymentIds, approve, callback) {
                services.post('/api/v1/payment/approveOrReject/'+approve, paymentIds, function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('UpdateHeader');
                    $rootScope.modalInstance.dismiss('success');
                }, function (err, status) {
                    sweetAlert("Error", err.data.message, "error");
                })
                // $http.post('/api/v1/payment/approveOrReject/'+approve, paymentIds).then(function (response) {
                //     if (angular.isFunction(callback)) {
                //         callback(response.data);
                //         $rootScope.$broadcast('UpdateHeader');
                //         $rootScope.modalInstance.dismiss('success');
                //     }
                // }, function (err, status) {
                //     sweetAlert("Error", err.data.message, "error");
                // });
            }
        }
    });