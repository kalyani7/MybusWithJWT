"use strict";
/*global angular, _*/

angular.module('myBus.expensesIncomesReportsModule', ['ngTable', 'ui.bootstrap'])
    .controller('expensesIncomesReportsCtrl', function ($scope,$rootScope,NgTableParams,$stateParams,$uibModal, $filter, $location, printManager,userManager,paymentManager,paginationService,paymentsReportsManager) {
        $scope.payments = [];
        $scope.totalExpense = 0;
        $scope.totalIncome = 0;
        $scope.loading = false;
        $scope.user = userManager.getUser();
        $scope.currentPageOfPayments=[];
        $rootScope.urlDate = $stateParams.date;

        $scope.print = function(eleId) {
            printManager.print(eleId);
        }
        $scope.parseDate = function(){
            $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getUTCMonth()+1))).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
        }
        $scope.reportsByDate = function(date){
            var dateObj = date;
            var month = dateObj.getMonth() + 1;
            var day = dateObj.getDate();
            var year = dateObj.getFullYear();
            var newdate = year + "-" + month + "-" + day;
            $location.url('expensesincomesreports/' + newdate);
        }
        $scope.today = function() {
            //var date =
            //date.setDate(date.getDate()-1);
            $scope.dt = new Date();
            $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
            $scope.parseDate();
            $scope.reportsByDate($scope.dt)
        };
        if(!$scope.urlDate) {
            $scope.today();
        } else {
            $scope.dt = new Date($scope.urlDate);
            $scope.todayDate = new Date();
            $scope.tomorrow = new Date($scope.todayDate.getTime() + (24 * 60 * 60 * 1000));
        }

        $scope.loading = false;
        $scope.currentPageOfReports = [];
        $scope.loading = true;

        $scope.clear = function() {
            $scope.dt = null;
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };
        $scope.$watch('dt', function(newValue, oldValue) {
            $scope.reportsByDate($scope.dt);
        });

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        $scope.isAdmin = function() {
            var currentUser = userManager.getUser();
            return currentUser.admin;
        }

        $scope.nextPaymentDay = function() {
                var dt = $scope.dt;
                dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
                $scope.dt.setTime(dt.getTime());
            if ($scope.dt >= $scope.tomorrow) {
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else {
                $scope.reportsByDate($scope.dt)
                $scope.init();
            }
        }
        $scope.previousPaymentDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
            $scope.dt = dt;
            if ($scope.dt >= $scope.tomorrow) {
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else {
                $scope.reportsByDate($scope.dt)
                $scope.init();
            }
        }

        var loadTableData = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            var dateObj = $scope.dt;
            var month = dateObj.getMonth() + 1;
            var day = dateObj.getDate();
            var year = dateObj.getFullYear();
            var newdate = year + "/" + month + "/" + day;
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentsReportsManager.getPayments(newdate,pageable, function(response) {
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.payments = response.content;
                    $scope.totalExpense = 0;
                    $scope.totalIncome = 0;
                    angular.forEach($scope.payments, function (payment) {
                        if (payment.type=='EXPENSE'){
                            $scope.totalExpense = $scope.totalExpense + payment.amount;
                        }
                        else if(payment.type=='INCOME') {
                            $scope.totalIncome = $scope.totalIncome + payment.amount;
                        }
                        else{
                            console.log('Unknown payment type')
                        }
                    })
                }
                tableParams.total(response.totalElements);
                $scope.count = response.totalElements;
                tableParams.data = $scope.payments;
                $scope.currentPageOfPayments = $scope.payments;
            });
        };

        $scope.init = function() {
            $scope.totalExpense = 0;
            $scope.totalIncome = 0;
            $scope.paymentTableParams = new NgTableParams({
                page: 1,
                count:999999,
                sorting: {
                    status: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };
        $scope.init();

        $scope.serviceReportsPopUp = function (formId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'serviceReportPopUp-form-modal.html',
                controller:'serviceReportsPopUpController',
                resolve : {
                    formId : function(){
                        return formId;
                    }
                }
            })
        }

        $scope.bookingDuePopUpExpenses = function(bookingId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'booking-popup-modal.html',
                controller : 'popUpBookingControllerExpenses',
                resolve : {
                    bookingId : function(){
                        return bookingId;
                    }
                }
            });
        };
    })
    .controller("popUpBookingControllerExpenses", function($scope,$rootScope, serviceReportsManager , bookingId){
        $scope.booking = {};
        serviceReportsManager.getBooking(bookingId,function (data) {
            $scope.booking = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .controller("serviceReportsPopUpController", function($scope,$rootScope, serviceReportsManager , formId){
        $scope.service = {};
        serviceReportsManager.getForm(formId,function (data) {
            $scope.service = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .factory('paymentsReportsManager', function ($http, $log) {
        return {
            getPayments: function (date,pageable, callback) {
            $http({url:'/api/v1/payments/day?date=' + date,method:"GET",params:pageable})
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error loading payments");
                });
            }
        }
    });