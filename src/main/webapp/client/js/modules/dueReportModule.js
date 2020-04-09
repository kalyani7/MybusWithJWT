/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.dueReportModule', ['ngTable','ui.bootstrap'])
    .controller('DueReportController', function($scope, dueReportManager, NgTableParams, $filter, $location, userManager) {
        $scope.headline = "Due Report";
        $scope.currentPageOfDues = [];
        $scope.loading = false;
        $scope.user = userManager.getUser();
        if(!$scope.user.admin) {
            $location.url('officeduereport/'+$scope.user.branchOfficeId);
        } else{
            var loadTableData = function (tableParams) {
                $scope.loading = true;
                dueReportManager.loadReports(function (data) {
                    $scope.loading = false;
                    if(angular.isArray(data)) {
                        $scope.allDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                        tableParams.total($scope.allDues.length);
                        $scope.currentPageOfDues = $scope.allDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    }
                });
            }
            $scope.duesTableParams = new NgTableParams({
                page: 1,
                count:99999,
                sorting: {
                    name: 'asc'
                }
            }, {
                total: $scope.currentPageOfDues.length,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        }

        $scope.goToDueReport = function(officeId) {
            console.log('relaod report..');
            $location.url('officeduereport/'+officeId);
        }
        $scope.gotoPayments = function(){
            $location.url('payments');
        }
    })
    .controller('OfficeDueReportController', function($scope, $rootScope, $stateParams, $uibModal, dueReportManager, branchOfficeManager, userManager, NgTableParams, $filter, $location, paginationService) {
        $scope.headline = "Office Due Report";
        $scope.currentPageOfDues = [];
        $scope.officeId = $stateParams.id;
        $scope.loading = false;
        $scope.officeDue = {};
        $scope.startDate = new Date();
        $scope.endDate = new Date();
        $scope.pnr = null;
        $scope.offices = [];
        $scope.selectedBookings = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });
        var pageable ;
        var loadTableDataByDate = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getBranchReport($scope.officeId,function (data) {
                $scope.loading = false;
                $scope.allDues = {};
                $scope.officeDue = data;
                if($scope.officeDue.duesByDate) {
                    $scope.officeDue.duesByDate = tableParams.sorting() ? $filter('orderBy')($scope.officeDue.duesByDate, tableParams.orderBy()) : $scope.officeDue.duesByDate;
                    tableParams.total($scope.officeDue.duesByDate.length);
                    $scope.currentPageOfOfficeDues = $scope.officeDue.duesByDate.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };
        var loadTableDataByService = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getReportByService(pageable,function (data) {
                $scope.loading = false;
                if(angular.isArray(data)) {
                    $scope.serviceDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    tableParams.total($scope.serviceDues.length);
                    $scope.currentPageOfServiceDues = $scope.serviceDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };
        var loadTableDataByAgent = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getReportByAgents(pageable,function (data) {
                $scope.loading = false;
                if(angular.isArray(data)) {
                    $scope.agentsDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    tableParams.total($scope.agentsDues.length);
                    $scope.currentPageOfAllAgentDues = $scope.agentsDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };

        $scope.officeDuesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableDataByDate(params);
            }
        });
        $scope.serviceDuesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                Id: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableDataByService(params);
            }
        });
        $scope.agentDuesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                Id: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableDataByAgent(params);
            }
        });

        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableDataByDate($scope.officeDuesTableParams);
            loadTableDataByService($scope.serviceDuesTableParams);
        });
        $scope.showDueReportByDate = function(dueDate) {
            $location.url('officeduereport/'+$scope.officeId+'/'+dueDate);
        };
        $scope.showDueReportByService = function(serviceNumber) {
            $location.url('officeduereportbyservice/'+serviceNumber);
        };

        $scope.showDueReportByAgent = function(agentName) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'officeDueReportByAgent.html',
                controller: 'OfficeDueByAgentController',
                size: 'lg',
                windowClass: 'ORBAgent',
                resolve: {
                    agentName: function () {
                        return agentName;
                    }
                }
            });
        };

        $scope.dueBookings = [];
        $scope.totalDue = 0;


            var loadTableData = function (tableParams) {
                $scope.loading = true;
                dueReportManager.searchDues($scope.startDate, $scope.endDate, $scope.branchOfficeId, function (data) {
                    $scope.loading = false;
                    // $scope.dueBookings = data;
                    if(angular.isArray(data)) {
                        $scope.searchAllDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                        tableParams.total($scope.searchAllDues.length);
                        $scope.dueBookings = $scope.searchAllDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                        $scope.totalDue = _.reduce($scope.dueBookings, function (memo, booking) {
                            return memo + booking.netAmt
                        }, 0);
                    }
                    // $scope.loading = false;
                    // if(angular.isArray(data)) {
                    //     $scope.allDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    //     tableParams.total($scope.allDues.length);
                    //     $scope.currentPageOfDues = $scope.allDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    // }
                });
            }
        $scope.search = function () {
            // console.log('dgiubnigdbibn')
            $scope.searchDuesTableParams = new NgTableParams({
                page: 1,
                count:99999,
                sorting: {
                    name: 'asc'
                }
            }, {
                total: $scope.dueBookings.length,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        }

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        $scope.searchByPNR = function() {
            dueReportManager.searchDuesByPNR($scope.pnr, function(data){
                $scope.duesByPNR= data;
            });
        }

        $scope.payBooking = function(bookingId) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    $scope.search();
                    $scope.duesByPNR=[];
                },function (error) {
                    alert("Error paying booking:" + error.data.message);
                });
            });
        }

        $scope.toggleBookingSelection = function(bookingId){
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        $scope.payBookings = function() {
            dueReportManager.payBookings($scope.selectedBookings, function(data) {
                $rootScope.$broadcast('UpdateHeader');
                $scope.search();
                $scope.selectedBookings = [];
                dueReportManager.showDuePaymentSummary(data);
            },function (error) {
                alert("Error paying booking:" + error.data.message);
            });
        }
    })
    .controller('showDuePaymentSummaryController', function($scope, $rootScope, paidBookings) {
        $scope.paidBookings = paidBookings;
        $scope.totalAmount = 0;
        for(var i=0; i<paidBookings.length; i++){
            $scope.totalAmount +=paidBookings[i].netAmt;
        }

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .controller('OfficeDueByDateReportController', function($scope, $rootScope, $stateParams, dueReportManager, userManager, NgTableParams, $filter) {
        $scope.headline = "Office Due Report";
        $scope.currentPageOfDues = [];
        $scope.officeId = $stateParams.id;
        $scope.date = $stateParams.date;
        $scope.loading = false;
        $scope.officeDue = {};
        $scope.currentPageOfDues = [];
        $scope.selectedBookings = [];


        var loadTableData = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getBranchReportByDate($scope.officeId,$scope.date,function (data) {
                $scope.loading = false;
                $scope.officeDue = data;
                if($scope.officeDue.bookings) {
                    $scope.officeDue.bookings = tableParams.sorting() ? $filter('orderBy')($scope.officeDue.bookings, tableParams.orderBy()) : $scope.officeDue.bookings;
                    tableParams.total($scope.officeDue.bookings.length);
                    $scope.currentPageOfDues = $scope.officeDue.bookings.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    $scope.agentNames = _.uniq($scope.currentPageOfDues, function (item) {return item.bookedBy });
                    $scope.filterArray = $scope.currentPageOfDues;
                }
            });
        };

        $scope.bookingByFilter = function (bookedBy) {
            if (bookedBy === undefined) {
                $scope.filterArray = $scope.currentPageOfDues;
            } else {
                $scope.filterArray = _.filter($scope.currentPageOfDues, function (item) {
                    return item.bookedBy === bookedBy.bookedBy;
                });
            }
        };

        $scope.duesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableData($scope.duesTableParams);
        });
        $scope.payBooking = function(bookingId) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    //$location.url('/officeduereport/'+officeId);
                    loadTableData($scope.duesTableParams);

                },function (error) {
                    swal("Oops...", "Error submitting the report", "error");
                });
            });
        }
        $scope.toggleBookingSelection = function(bookingId){
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        $scope.payBookings = function() {
            dueReportManager.payBookings($scope.selectedBookings, function(data) {
                $rootScope.$broadcast('ReloadOfficeDueReport');
                $scope.selectedBookings = [];
                dueReportManager.showDuePaymentSummary(data);
            },function (error) {
                alert("Error paying booking:" + error.data.message);
            });
        }

    })

    .controller('OfficeDueByServiceController', function($scope, $rootScope, $stateParams, dueReportManager, userManager, NgTableParams, $filter, $location) {
        $scope.headline = "Office Due Report By Service Number";
        $scope.currentPageOfDues = [];
        $scope.serviceNo = $stateParams.serviceNumber;
        $scope.loading = false;
        $scope.officeDue = {};
        $scope.currentPageOfDues = [];
        $scope.selectedBookings = [];
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getBranchReportByService($scope.serviceNo,function (data) {
                $scope.loading = false;
                $scope.officeDue = data;
                if(angular.isArray(data)) {
                    $scope.officeDue = tableParams.sorting() ? $filter('orderBy')($scope.officeDue, tableParams.orderBy()) : $scope.officeDue;
                    tableParams.total($scope.officeDue.length);
                    $scope.currentPageOfDues = $scope.officeDue.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    $scope.bookedData = data;
                }
            });
        }
        $scope.duesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableData($scope.duesTableParams);
        });
        $scope.payBooking = function(bookingId, officeId, serviceNumber) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    $location.url('/officeduereportbyservice/'+serviceNumber);
                },function (error) {
                    sweetAlert("Oops...", "Error submitting the report", "error");
                });
            });
        }
        $scope.toggleBookingSelection = function(bookingId){
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        $scope.payBookings = function() {
            dueReportManager.payBookings($scope.selectedBookings, function(data) {
                $rootScope.$broadcast('ReloadOfficeDueReport');
                $scope.selectedBookings = [];
                dueReportManager.showDuePaymentSummary(data);
            },function (error) {
                alert("Error paying booking:" + error.data.message);
            });
        }

    })

    .controller('OfficeDueByAgentController', function($scope, $rootScope, $stateParams, agentName, dueReportManager, userManager, NgTableParams,paginationService, $filter, $location) {
        $scope.headline = "Office Due Report By Agent";
        $scope.agentName = $stateParams.agentName;
        $scope.agentName = agentName;
        $scope.loading = false;
        $scope.agentDue = {};

        $scope.currentPageOfDuesByAgent = [];
        $scope.selectedBookings = [];
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getDueReportByAgent(agentName,function (data) {
                $scope.loading = false;
                $scope.agentDue = data;
                if($scope.agentDue) {
                    $scope.agentDue = tableParams.sorting() ? $filter('orderBy')($scope.agentDue, tableParams.orderBy()) : $scope.agentDue;
                    tableParams.total($scope.agentDue.length);
                    $scope.currentPageOfDuesByAgent = $scope.agentDue.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        }
        $scope.duesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDuesByAgent.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableData($scope.duesTableParams);
        });
        $scope.payBooking = function(bookingId, officeId, agentName) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    $location.url('officeduereportbyagent/'+agentName);

                },function (error) {
                    swal("Oops...", "Error submitting the report", "error");
                });
            });
        };
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        };
        $scope.toggleBookingSelection = function(bookingId){
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        };
        $scope.payBookings = function() {
            dueReportManager.payBookings($scope.selectedBookings, function(data) {
                $rootScope.$broadcast('ReloadOfficeDueReport');
                $scope.selectedBookings = [];
                dueReportManager.showDuePaymentSummary(data);
            },function (error) {
                alert("Error paying booking:" + error.data.message);
            });
        };
    })


    .factory('dueReportManager', function ($http, $rootScope, $log, $uibModal) {
        var pageable;

        return {
            loadReports:function(callback) {
                $http.get('/api/v1/dueReports')
                    .then(function (response) {
                        callback(response.data);
                        console.log(response)
                    },function (error) {
                        $log.debug("error loading due reports");
                        swal("Error",error.data.message,"error");
                    });
            },
            getBranchReport:function(id,callback) {
                $http.get('/api/v1/dueReport/office/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        swal("Error",error.data.message,"error");
                    });
            },
            getReportByService:function(pageable,callback) {
                $http.get('/api/v1/dueReport/officeDuesByService')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        swal("Error",error.data.message,"error");
                    });
            },
            getReportByAgents:function(pageable,callback) {
                $http.get('/api/v1/dueReport/officeDuesByAgent')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        swal("Error",error.data.message,"error");
                    });
            },
            getBranchReportByDate:function(id,date,callback) {
                $http.get('/api/v1/dueReport/office/'+id+'/'+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        swal("Error",error.data.message,"error");
                    });
            },
            getBranchReportByService:function(serviceNumber,callback) {
                $http.get('/api/v1/dueReport/dueBookingByService/'+serviceNumber)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        swal("Error",error.data.message,"error");
                    });
            },
            getDueReportByAgent:function(agentName,callback) {
                $http.get('/api/v1/dueReport/officeDuesByAgent/'+agentName)
                    .then(function (response) {
                        callback(response.data);
                    },function (err) {
                        $log.debug("error loading due report");
                        swal("Error",err.data.message,"error");
                    });
            },
            payBooking:function(id, callback, errorCallback) {
                $http.put('/api/v1/dueReport/payBookingDue/'+id)
                    .then(function (response) {
                        $rootScope.$broadcast('ReloadOfficeDueReport');
                        callback(response.data);
                    },function (error) {
                        errorCallback(error);
                    });
            },
            showDuePaymentSummary : function(paidBookings) {
                $rootScope.modalInstance = $uibModal.open({
                    templateUrl: 'partials/show-due-payment-summary.html',
                    controller:'showDuePaymentSummaryController',
                    resolve: {
                        paidBookings: function () {
                            return paidBookings;
                        }
                    }
                });
            },
            payBookings:function(ids, callback, errorCallback) {
                swal({title: "You want to Pay selected bookings now?",   text: "Are you sure?",   type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, pay now!",
                    closeOnConfirm: true }, function() {
                    $http.post('/api/v1/dueReport/payBookingDues/', ids).then(function (response) {
                        callback(response.data);
                    },function (error) {
                        errorCallback(error);
                    });
                });
            },

            searchDues:function(startDate, endDate, branchOfficeId, callback) {
                var st = startDate.getFullYear()+"-"+[startDate.getMonth()+1]+"-"+startDate.getDate();
                var end = endDate.getFullYear()+"-"+[endDate.getMonth()+1]+"-"+endDate.getDate();
                $http.get('/api/v1/dueReport/search?startDate='+st+'&endDate='+end+"&branchOfficeId="+ branchOfficeId)
                    .then(function (response) {
                        callback(response.data);
                        console.log(response)
                    },function (error) {
                        swal("Error",error.data.message,"error");
                    });
            },
            searchDuesByPNR:function(pnr, callback) {
                $http.get('/api/v1/dueReport/searchByPNR?pnr='+pnr)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {

                        swal("Error",error.data.message,"error");
                    });
            }
        }
    });



