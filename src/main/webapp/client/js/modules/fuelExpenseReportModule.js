"use strict";
/*global angular, _*/

angular.module('myBus.fuelExpenseReportModule', ['ngTable','ui.bootstrap'])
        .controller("fuelExpenseReportsCtrl",function($scope,$rootScope,NgTableParams,$stateParams,$uibModal, $filter, $location,
                                                      fuelExpensesManager,paginationService,$state, suppliersManager, vehicleManager){
            var pageable;
            $scope.loading = false;
            $rootScope.urlDate = $stateParams.date ;
            $scope.date = $stateParams.date;
            $scope.fillingStations = [];
            $scope.dayTotalBill = 0;
            $scope.selectedSupplier="All";
            $scope.urlDate = fuelExpensesManager.getDate();
            $scope.parseDate = function(){
                $scope.date = $scope.dt.getFullYear() + "-" + ('0' + (parseInt($scope.dt.getMonth() + 1))).slice(-2) + "-" + ('0' + $scope.dt.getDate()).slice(-2);
            };
            $scope.reportsByDate = function(date){
                var dateObj = date;
                var month =''+(dateObj.getMonth() + 1);
                if(month.length < 2) month = '0' + month;
                var day =''+dateObj.getDate();
                if (day.length < 2) day = '0' + day;
                var year = dateObj.getFullYear();
                var newdate = year + "-" + month + "-" + day;
                $location.url('fuelexpensereports/' + newdate);
            }
            $scope.today = function() {
                var yesterday = new Date();
                yesterday.setDate(yesterday.getDate() -1);
                $scope.dt = yesterday;
                $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
                fuelExpensesManager.setDate($scope.dt);
                $scope.parseDate();
                $scope.reportsByDate($scope.dt);
            };
            $scope.selectedDate=function(){
                var dt = $scope.dt;
                fuelExpensesManager.setDate($scope.dt);
                if ($scope.dt >= $scope.tomorrow) {
                    swal("Oops...", "U've checked for future, Check Later", "error");
                }
                else {
                    $scope.reportsByDate($scope.dt)
                    $scope.init();
                }
            }

            if(!$scope.urlDate) {
                $scope.today();
            } else {
                $scope.dt = new Date($scope.urlDate);
                $scope.todayDate = new Date();
                $scope.tomorrow = new Date($scope.todayDate.getTime() + (24 * 60 * 60 * 1000));
            }
            $scope.nextDay = function() {
                var dt = $scope.dt;
                dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
                 $scope.dt.setTime(dt.getTime());
                fuelExpensesManager.setDate($scope.dt);
                if ($scope.dt >= $scope.tomorrow) {
                    swal("Oops...", "U've checked for future, Check Later", "error");
                }
                else {
                    $scope.reportsByDate($scope.dt)
                    $scope.init();
                }
            }
            $scope.previousDay = function() {
                var dt = $scope.dt;
                dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
                $scope.dt = dt;
                fuelExpensesManager.setDate($scope.dt);
                if ($scope.dt >= $scope.tomorrow) {
                    swal("Oops...", "U've checked for future, Check Later", "error");
                }
                else {
                    $scope.reportsByDate($scope.dt)
                    $scope.init();
                }
            }

            suppliersManager.getSuppliers(function (response) {
                if (angular.isArray(response)) {
                    // $scope.loading = false;
                    $scope.suppliers = response;
                }
            });

            vehicleManager.getVehicles({}, function (res) {
                $scope.vehicles = res.content;
            });

            var loadTableData = function (tableParams) {
                var dateObj = $scope.dt;
                var month =''+ (dateObj.getMonth() + 1);
                if(month.length < 2){
                    month = '0' + month;
                }
                var day =''+ dateObj.getDate();
                if (day.length < 2) {
                    day = '0' + day;
                }
                var year = dateObj.getFullYear();
                var newDate = year + "-" + month + "-" + day;
                fuelExpensesManager.getFuelExpenseReports(newDate, function(response){
                    $scope.allReports = response.content;
                    $scope.fuelExpenses = $scope.allReports;
                    for(var i=0;i<$scope.fuelExpenses.length;i++){
                        if($scope.fuelExpenses[i].paid){
                            $scope.fuelExpenses[i].paid='Yes';
                        }else{
                            $scope.fuelExpenses[i].paid='No';
                        }
                    }
                    for(var i=0;i<$scope.fuelExpenses.length;i++){
                        if($scope.fuelExpenses[i].fillup){
                            $scope.fuelExpenses[i].fillup='Yes';
                        }else{
                            $scope.fuelExpenses[i].fillup='No';
                        }
                    }
                    if(tableParams.sorting()){
                        if(tableParams.orderBy()[0]){
                            var orderBy = tableParams.orderBy()[0].slice(1);
                            var orderDir = tableParams.orderBy()[0].slice(0, 1) === '+' ? 1 : -1;
                            $scope.allReports = _.sortBy($scope.allReports, function(report){
                                return report[orderBy];
                            });
                            if(orderDir === -1){
                                $scope.allReports = $scope.allReports.reverse();
                            }
                        }
                    }
                    $scope.dayTotalBill = _.reduce($scope.allReports, function(memo, bill) { return memo + bill.cost}, 0);
                });
            };
            $scope.init = function() {
                $scope.fuelExpensesParams = new NgTableParams({
                    page: 1,
                    count:9999,
                }, {
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            };
            $scope.init();

            $scope.editFuelExpenses = function(id){
                $state.go('editfuelexpensereports',{id:id});

            };
            $scope.addOrUpdateFuelExpense = function() {
                $state.go('addfuelexpensereports');
            }
            $scope.deleteFuelExpense = function (id) {
                fuelExpensesManager.deleteFuelExpense(id,function(){
                    $scope.init();
                });
            }

            $scope.search = function(){
                if($scope.fromDate) {
                    var startDate = new Date($scope.fromDate);
                    var startYear = startDate.getFullYear();
                    var startMonth = startDate.getMonth() + 1;
                    var startDay = startDate.getDate();
                    $scope.startDate = startYear + '-' + startMonth + '-' + startDay;
                }
                if($scope.toDate){
                    var endDate = new Date($scope.toDate);
                    var endYear = endDate.getFullYear();
                    var endMonth = endDate.getMonth() + 1;
                    var endDay = endDate.getDate();
                    $scope.endDate = endYear + '-' + endMonth + '-' + endDay;
                }

                $scope.query = {
                    "startDate" : $scope.startDate,
                    "endDate" :  $scope.endDate,
                    "supplierId" : $scope.supplierId,
                    "vehicleId" : $scope.vehicleId
                }
                $scope.searchInit();
            }
            $scope.filterBySupplier=function(supplierid){
                $scope.fuelExpenses = [];
                if(supplierid === "All"){
                    $scope.fuelExpenses = $scope.allReports;
                    $scope.dayTotalBill = _.reduce($scope.fuelExpenses, function(memo, bill) { return memo + bill.cost}, 0);
                } else {
                    for(var i =0; i< $scope.allReports.length;i++) {
                            if($scope.allReports[i].supplierId==supplierid ){
                                $scope.fuelExpenses.push($scope.allReports[i]);
                            }
                        $scope.dayTotalBill = _.reduce($scope.fuelExpenses, function(memo, bill) { return memo + bill.cost}, 0);
                    }
                }
            }

            // $scope.query = {};
            $scope.totalBill = 0;

            var searchFuelBills = function (tableParams) {
                $scope.loading = true;
                fuelExpensesManager.search($scope.query, function (response) {
                    $scope.loading = false;
                    $scope.fuelBills = response.content;
                    for (var i = 0; i < $scope.fuelBills.length; i++) {
                        if ($scope.fuelBills[i].paid) {
                            $scope.fuelBills[i].paid = 'Yes';
                        } else {
                            $scope.fuelBills[i].paid = 'No';
                        }
                    }
                    for (var i = 0; i < $scope.fuelBills.length; i++) {
                        if ($scope.fuelBills[i].fillup) {
                            $scope.fuelBills[i].fillup = 'Yes';
                        } else {
                            $scope.fuelBills[i].fillup = 'No';
                            if (response) {
                                $scope.loading = false;
                                $scope.fuelBills = response.content;
                                for (var i = 0; i < $scope.fuelBills.length; i++) {
                                    var supplierObj = _.find($scope.suppliers, function (supplier) {
                                        return supplier.id.toString() === $scope.fuelBills[i].supplierId;
                                    });
                                    if (supplierObj) {
                                        $scope.fuelBills[i].supplierName = supplierObj.name;
                                    }

                                    var vehicleObj = _.find($scope.vehicles, function (vehicle) {
                                        return vehicle.id.toString() === $scope.fuelBills[i].vehicleId;
                                    });
                                    if (vehicleObj) {
                                        $scope.fuelBills[i].regNo = vehicleObj.regNo;
                                    }

                                }
                                tableParams.data = $scope.fuelBills;
                                $scope.totalBill = _.reduce($scope.fuelBills, function (memo, bill) {
                                    return memo + bill.cost
                                }, 0);
                            }

                            $scope.totalBill = _.reduce($scope.fuelBills, function (memo, bill) {
                                return memo + bill.cost
                            }, 0);

                            tableParams.data = $scope.fuelBills;
                            $scope.totalBill = _.reduce($scope.fuelBills, function (memo, bill) {
                                return memo + bill.fuelCost
                            }, 0);
                        }
                    }
                });
            }

            $scope.searchInit = function () {
                fuelExpensesManager.getCount($scope.date, function (count) {
                    $scope.searchTableParams = new NgTableParams({
                        page: 1, // show first page
                        count: 15,
                        sorting: {
                            date: 'asc'
                        },
                    }, {
                        counts: [],
                        total:count,
                        getData: function (params) {
                            searchFuelBills(params);
                        }
                    });
                });
            };

            $scope.exportToExcel = function (tableId, fileName) {
                paginationService.exportToExcel(tableId, fileName);

            }
            $scope.printArea = function () {
                var w = window.open();
                w.document.write(document.getElementsByClassName('print')[0].innerHTML);
                w.print();
                w.close();
            }
            $scope.refreshServiceNames=function(urlDate){
                var date=new Date(urlDate);
                var Year = date.getFullYear();
                var month =''+ (date.getMonth() + 1);
                if(month.length < 2){
                    month = '0' + month;
                }
                var Day =''+ date.getDate();
                if (Day.length < 2) {
                    Day = '0' + Day;
                }
                urlDate = Year + '-' + month + '-' + Day;
            fuelExpensesManager.updateServiceName(urlDate,function(){
                $scope.init();})
            }

        })
        .controller("editFuelExpenseReportController",function ($scope,$rootScope, fuelExpensesManager,serviceReportsManager,vehicleManager,suppliersManager,$stateParams,$state ) {
            $scope.titleName="Add FuelExpenses";
            $scope.fuelExpense={}
            $scope.serviceExpense = {};
            $scope.suppliers = [];
            $scope.vehicles = [];
            $scope.selectedListing = {};
            $scope.serviceList = [];
            $scope.fuelExpense.date=fuelExpensesManager.getDate();

            if($stateParams.id) {
                $scope.titleName = 'Edit FuelExpenses';
                fuelExpensesManager.getFuelExpense($stateParams.id, function (data) {
                    $scope.fuelExpense = data;
                    $scope.fuelExpense.date = new Date(data.date);
                    // $scope.fuelExpense.date=new Date();
                });
            }

                suppliersManager.getSuppliers(function (response) {
                    if (angular.isArray(response)) {
                        // $scope.loading = false;
                        $scope.suppliers = response;
                    }
                })
                vehicleManager.getVehicles({}, function (res) {
                    $scope.vehicles = res.content;
                });
                $scope.listingChanged = function () {
                    $scope.serviceExpense.serviceListingId = $scope.selectedListing.id;
                    $scope.serviceExpense.vehicleNumber = $scope.selectedListing.vehicleRegNumber;
                };

                $scope.addFuelExpense = function () {
                    // if (!$scope.fuelExpense.contact) {
                    //     swal("Error", "Please Enter the contactNumber", "error");
                    //     return;
                    // }
                    if (!$scope.fuelExpense.date) {
                        swal("Error", "Please enter date", "error");
                        return;
                    }
                    if (!$scope.fuelExpense.vehicleId) {
                        swal("Error", "Please enter vehicleId", "error");
                        return;
                    }
                    if (!$scope.fuelExpense.supplierId) {
                        swal("Error", "Please enter supplierId", "error");
                        return;
                    }
                    if (!$scope.fuelExpense.odometer) {
                        swal("Error", "Please enter odometerReading", "error");
                        return;
                    }
                    if (!$scope.fuelExpense.quantity) {
                        swal("Error", "Please enter the quantity", "error");
                        return;
                    }
                    if (!$scope.fuelExpense.rate) {
                        swal("Error", "Please enter the rate", "error");
                        return;
                    }
                    else {
                        var today = $scope.fuelExpense.date;
                        // var curDate = today.getFullYear() + "-" + [today.getMonth() + 1] + "-" + today.getDate();
                        var month =''+ (today.getMonth() + 1);
                        if(month.length < 2){
                            month = '0' + month;
                        }
                        var day =''+ today.getDate();
                        if (day.length < 2) {
                            day = '0' + day;
                        }
                        var year = today.getFullYear();
                        var curDate = year + "-" + month + "-" + day;
                        $scope.fuelExpense.dateString = curDate;
                        if($stateParams.id){
                            fuelExpensesManager.updateFuelExpense($scope.fuelExpense,function(data){
                                if(data.status){
                                    sweetAlert("Great", "Your vehicle "+data.vehicle+" have a remainder ", "error");
                                }
                                $state.go('fuelexpensereports');
                            })
                        }else{
                        fuelExpensesManager.addFuelExpense($scope.fuelExpense, function (data) {
                           if(data.status){
                               sweetAlert("Great", "Your vehicle "+data.vehicle+" have a remainder ", "error");
                               $rootScope.$broadcast('reloadReminders');
                               $state.go('fuelexpensereports');

                           }else{
                               $state.go('fuelexpensereports');
                           }

                        })
                    }
                    }
                };
                    $scope.getFuelCost = function () {
                    if ($scope.fuelExpense) {
                        $scope.fuelExpense.cost = $scope.fuelExpense.quantity * $scope.fuelExpense.rate;
                    }
                    return $scope.fuelExpense.cost;
                }
                $scope.cancel = function () {
                    $state.go('fuelexpensereports');
                };
        }).factory('fuelExpensesManager',function ($http,$log,$rootScope) {
            var date;
            return{
                setDate : function(date){
                    this.date = date;
                },
                getDate : function(){
                    return this.date;
                },
                search: function (query,callback) {
                    $http.post('/api/v1/FuelExpense/search', query).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching expenses", err.message, "error");
                    });
                },addFuelExpense: function (fuelExpense, callback) {
                    $http.post('/api/v1/FuelExpense/addFuelExpense',fuelExpense)
                        .then(function (response) {
                                 sweetAlert("Great","Your Fuel Consumption successfully added", "success");
                                callback(response.data);
                        },function (error) {
                            $log.debug("error adding Fuel Expense");
                        });
                },updateFuelExpense: function (fuelExpense, callback) {
                    $http.put('/api/v1/FuelExpense/updateFuelExpense', fuelExpense)
                        .then(function (response) {
                             sweetAlert("Great", "Your Fuel Consumption successfully updated", "success");
                            callback(response.data);
                        }, function (error) {
                            $log.debug("error updating Fuel Expense");
                        });
                },getFuelExpenseReports: function (date,callback) {
                    $http.get('/api/v1/FuelExpense/getAllByDate?date=' + date)
                        .then(function (response) {
                            callback(response.data);
                        },function (error) {
                            $log.debug("error retrieving Fuel Expense reports");
                        });
                },getFuelExpense:function(id,callback){
                    $http.get('/api/v1/FuelExpense/getFuelExpense/'+id)
                        .then(function (response) {
                            callback(response.data);
                        },function(err) {});
                },deleteFuelExpense:function(id,callback){
                    swal({   title: "Are you sure?",   text: "You will not be able to recover this FuelExpense !",
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Yes, delete it!",
                        closeOnConfirm: false
                    }, function() {
                        $http.delete('/api/v1/FuelExpense/deleteFuelExpense/' + id)
                            .then(function (response) {
                                callback(response);
                                sweetAlert("Great", "Fuel Expense successfully deleted", "success");
                            },function (error) {
                                sweetAlert("Oops...", "Error finding data!", "error" + angular.toJson(error));
                            });
                    });
                },getCount:function(date,callback){
                    $http.get('/api/v1/FuelExpense/getCount?date='+date)
                        .then(function (response) {
                            callback(response.data);
                        },function(err) {});
                },updateServiceName:function(date,callback) {
                    $http.get('/api/v1/FuelExpense/updateServiceName?date=' + date)
                        .then(function (response) {
                            callback(response.data);
                        }, function (err) { });
                }
            };
        });
