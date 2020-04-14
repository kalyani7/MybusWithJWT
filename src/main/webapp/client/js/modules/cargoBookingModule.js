"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingListController", function ($rootScope, $scope, $uibModal, NgTableParams, userManager, cargoBookingManager, $location, branchOfficeManager, paginationService) {
        $scope.headline = "Cargo Bookings";
        $scope.cargoBookings = null;
        $scope.cargoBookingsTable = null;
        $scope.filter = {startDate: new Date(), endDate: new Date()};
        $scope.tableParams = {};
        $scope.members = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });

        cargoBookingManager.getShipmentTypes(function (types) {
            $scope.shipmentTypes = types;
        });
        var loadCargoBookings = function (tableParams, filter) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            $scope.loading = true;
            if (!filter) {
                filter = {};
            }
            filter.page = tableParams.page();
            filter.size = tableParams.count();
            filter.sort = sortProps;

            cargoBookingManager.findCargoBookings(filter, function (cargoBookings) {
                if (angular.isArray(cargoBookings)) {
                    $scope.loading = false;
                    tableParams.data = cargoBookings;
                    $scope.tableParams = tableParams;
                        $scope.cargoBookings = cargoBookings;

                }
            });
        };
        $scope.init = function (filter) {
            cargoBookingManager.count(filter, function (shipmentCount) {
                $scope.cargoBookingsTable = new NgTableParams({
                    page: 1, // show first page
                    count: 100,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts: [100, 200, 500],
                    total: shipmentCount,
                    getData: function (params) {
                        loadCargoBookings(params, filter);
                    }
                });
            });
        };
        $scope.init();
        $scope.gotoBooking = function (bookingId) {
            // $location.url('viewcargobooking/' + bookingId);
            $state.go('home.viewcargobooking', {id: id})
        }
        $scope.search = function () {
            $scope.init($scope.filter);
        }

        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.init();
            });
        }
        $scope.initiateServiceAllotment = function (bookingId) {
            swal({
                title: "Assign Service",
                text: "Please provide provide a service number",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "Collecting person name or identification"
            }, function (serviceNumber) {
                if (serviceNumber === false) return false;
                if (serviceNumber === "") {
                    swal.showInputError("ServiceNumber is required");
                    return false
                }
                cargoBookingManager.allotService(bookingId, serviceNumber, function (data) {
                    swal("Nice!", data.shipmentNumber + " has been alloted to service" + data.vehicleId, "success");
                    $scope.init();
                });
            });
        }

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        /**
         * This can be called when CargoBooking is paid from the details screen
         */
        $rootScope.$on('UpdateCargoBookingList', function (e, value) {
            $scope.init();
        });
        $scope.addComment = function (bookingId) {
            cargoBookingManager.getCargoBooking(bookingId,function (cargoBooking) {
                cargoBookingManager.addComment(bookingId,cargoBooking.reviewComment,function (data) {
                    swal("Great!");
                    $scope.init();
                });
            });

        }
    }).controller("CargoBookingLookupController", function ($rootScope, $scope, $location, $uibModal, cargoBookingManager, userManager, bookings) {
        $scope.headline = "Cargo Bookings";
        $scope.bookings = bookings;
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.gotoBooking = function (bookingId) {
            $rootScope.modalInstance.dismiss('cancel');
            // $location.url('viewcargobooking/' + bookingId);
            $state.go('home.viewcargobooking', {id: id})
        }
    })
    .controller("CargoBookingHeaderController", function ($rootScope, $scope, cargoBookingManager, $location) {
        $scope.headline = "Cargo Bookings";
        $scope.bookingId;
        $scope.search = function () {
            if (!$scope.bookingId) {
                sweetAlert("Error", "Enter the bookingId for search", "error");
                return;
            }
            cargoBookingManager.lookupCargoBooking($scope.bookingId);
        }
        $scope.newBooking = function () {
            // $location.url('newbooking');
            $state.go('home.newbooking');
        }
    }).controller("DeliverBookingController", function ($rootScope, $scope, cargoBookingManager, $location, bookingId) {
        $scope.reviewComment = null;
        $scope.showError = false;
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.cargoBookingId = bookingId;

        $scope.deliverCargoBooking = function () {
            if (!$scope.reviewComment) {
                $scope.showError = true;
                return;
            }
            cargoBookingManager.deliverCargoBooking($scope.cargoBookingId, $scope.reviewComment, function () {
                // $location.url('cargobookings');
                $state.go('home.cargobookings')
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }
    })
    .controller("CargoUnloadingSheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, paginationService) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.filter.toBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        $scope.unload = function () {
            cargoBookingManager.unloadBookings($scope.selectedBookings, function (success) {
                //reload the bookings for unload
                $scope.selectedBookings = [];
                $scope.searchBookingForUnload();
            })
        }
        $scope.searchBookingForUnload = function () {
            cargoBookingManager.getBookingsForUnloading($scope.filter, function (response) {
                $scope.cargoBookings = response;
                // console.log("$scope.cargoBookings", $scope.cargoBookings);
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
            })
        }
        $scope.searchBookingForUnload();
        $scope.gotoBooking = function (bookingId) {
            // $location.url('viewcargobooking/' + bookingId);
            $state.go('home.viewcargobooking', {id: id})
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

    })

    .controller("CargoLoadingSheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, vehicleManager, paginationService) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.query = {};
        $scope.filter.fromBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        $scope.loadToVehicle = function (truckId) {
            $scope.selected = truckId;
            if(!$scope.selected) {
                swal("Error", "Please select a vehicle number to load", "error");
                return;
            }
            if($scope.selectedBookings.length === 0) {
                swal("Error", "Please select a bookings to load", "error");
                return;
            }

            cargoBookingManager.loadBookings($scope.selected, $scope.selectedBookings, function (success) {
                //reload the bookings for load
                $scope.selectedBookings = [];
                $scope.searchBookingForLoading();
            })
        };
        $scope.searchBookingForLoading = function () {
            cargoBookingManager.getBookingsForLoading($scope.filter, function (response) {
                $scope.allBookings = response;
                $scope.cargoBookings = response;
                $scope.fromCityNames = _.uniq($scope.cargoBookings, function (item) {return item.attrs.fromBranchOfficeName });
               // $scope.citysFilter = '';
                // console.log("$scope.citysFilter", $scope.citysFilter);
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
            })
        };
        $scope.searchBookingForLoading();

        $scope.filterByLoad = function(status){
            console.log('status  '+ status);
            $scope.cargoBookings = [];
            if(status === "All"){
                $scope.cargoBookings = $scope.allBookings;
            } else if(status === "Loaded"){
                for(var i =0; i< $scope.allBookings.length;i++) {
                    if($scope.allBookings[i].vehicleId !== null){
                        // $scope.cargoBookings = [];
                        $scope.cargoBookings.push($scope.allBookings[i]);
                    }
                }
            } else {
                for(var i =0; i< $scope.allBookings .length;i++) {
                    if(!$scope.allBookings[i].vehicleId ){
                        // $scope.cargoBookings = [];
                        $scope.cargoBookings.push($scope.allBookings[i]);
                    }
                }
            }
        };

        $scope.gotoBooking = function (bookingId) {
            // $location.url('viewcargobooking/' + bookingId);
            $state.go('home.viewcargobooking', {id: id})
        };

        $scope.getAllVehicles = function () {
            vehicleManager.getVehicles({}, function (response) {
                $scope.vehicles = response.content;
            })
        }
        $scope.getAllVehicles();

        $scope.vehicleMy = function (vehicle) {
            console.log("vehice", vehicle);
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }
        $scope.addComment = function (bookingId) {
            cargoBookingManager.getCargoBooking(bookingId,function (cargoBooking) {
                cargoBookingManager.addComment(bookingId,cargoBooking.reviewComment,function (data) {
                    swal("Great!");
                    $scope.searchBookingForLoading();
                });
            });
        }
    })
    .controller("CargoDeliverySheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, paginationService,vehicleManager,NgTableParams) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.query = {};
        $scope.cashBalances = [];
        $scope.branchCashBalances = [];
        var pageable;
        $scope.filter.toBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        vehicleManager.getVehicles({}, function (res) {
            $scope.allVehicles = res.content;
        });


        $scope.searchBookingForDelivery = function () {
            $scope.branchCashBalances = [];
            cargoBookingManager.getBookingsForDelivery($scope.filter, function (response) {
                $scope.cargoBookings = response;
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
                userManager.getUserCashbalances({},function(response){
                    $scope.cashBalances = response;
                    for(var i=0;i<$scope.cashBalances.length;i++){
                        var obj = {};
                        if($scope.cashBalances[i].branchOfficeId === $scope.filter.toBranchId){
                            if($scope.cashBalances[i].amountToBePaid > 0) {
                                obj.fullName = $scope.cashBalances[i].fullName;
                                obj.balance = $scope.cashBalances[i].amountToBePaid;
                                $scope.branchCashBalances.push(obj);
                            }
                        }
                    }
                });
            });
        };

        $scope.searchBookingForDelivery();
        $scope.gotoBooking = function (bookingId) {
            // $location.url('viewcargobooking/' + bookingId);
            $state.go('home.viewcargobooking', {id: id})
        };
        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.searchBookingForDelivery();
            });
        };

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        };
        $scope.addComment = function (bookingId) {
            cargoBookingManager.getCargoBooking(bookingId,function (cargoBooking) {
                cargoBookingManager.addComment(bookingId,cargoBooking.reviewComment,function (data) {
                    swal("Great!");
                    $scope.searchBookingForDelivery();
                });
            });
        };

        var loadsearchdeliveredbookings = function (tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.query.page = pageable.page-1;
            $scope.query.sort = pageable.sort;
            $scope.query.size = pageable.size;
            cargoBookingManager.getDeliveredCargoBookings($scope.query,function(response){
                console.log(response)
                $scope.deliveredBookings = response.data.content;
                tableParams.data = $scope.deliveredBookings;
            });
        };

        $scope.searchFind = function () {
            cargoBookingManager.countDeliveredBookings($scope.query, function (count) {
                $scope.searchParams = new NgTableParams({
                    page: 1, // show first page
                    count: 100,
                    sorting: {
                        deliveredOn: 'asc'
                    }
                }, {
                    counts: [100,300,500, 700],
                    total:count,
                    getData: function (params) {
                        loadsearchdeliveredbookings(params);
                    }
                });
            });

        };

        $scope.searchDeliveredBookings = function(){
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
            $scope.searchFind();
        };
    })
    .controller("CargoCancellationsController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, paginationService,vehicleManager,NgTableParams) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.cancelledCargoBookings = [];
        $scope.query = {};
        $scope.pendingCargoBookings = [];
        $scope.data = {};
        var pageable;
        $scope.currentUser = userManager.getUser();

        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        userManager.getUserNames(function (data) {
            $scope.members = data;
        });
        vehicleManager.getVehicles({}, function (res) {
            $scope.allVehicles = res.content;
        });

        $scope.query.startDate = new Date()
        $scope.query.endDate = new Date()

        var loadsearchcancellationpendingbookings = function (tableParams, query){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.loading = true;
            if (!query) {
                query = {};
            }
            query.page = pageable.page-1;
            query.sort = pageable.sort;
            query.size = pageable.size;
            cargoBookingManager.getCancellationPendingBookings(query, function (response) {
                $scope.pendingCargoBookings = response.content;
                tableParams.data = $scope.pendingCargoBookings;
            });
        };

        var loadsearchcancellationbookings = function (tableParams, query){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            // var sortingProps = tableParams.sorting();
            // var sortProps = ""
            // for (var prop in sortingProps) {
            //     sortProps += prop + "," + sortingProps[prop];
            // }
            $scope.loading = true;
            if (!query) {
                query = {};
            }
            query.page = pageable.page-1;
            query.sort = pageable.sort;
            query.size = pageable.size;
            cargoBookingManager.searchCancelledBookings(query, function (response) {
                $scope.cancelledCargoBookings = response;
                tableParams.data = $scope.cancelledCargoBookings;
            });
        };
        $scope.init = function (query) {
            //Cancellation Pending Bookings
            cargoBookingManager.countCancellationPendingCargoBookings(query, function (count) {
                $scope.pendingCancellationParams = new NgTableParams({
                    page: 1, // show first page
                    count: 20,
                    sorting: {
                        deliveredOn: 'asc'
                    }
                }, {
                    counts: [100,300,500, 700],
                    total:count,
                    getData: function (params) {
                        loadsearchcancellationpendingbookings(params, query);
                    }
                });
            });
            //Cancelled Bookings
            cargoBookingManager.countCancelledBookings(query, function (count) {
                $scope.searchCancelledParams = new NgTableParams({
                    page: 1, // show first page
                    count: 20,
                    sorting: {
                        deliveredOn: 'asc'
                    }
                }, {
                    counts: [100,300,500, 700],
                    total:count,
                    getData: function (params) {
                        loadsearchcancellationbookings(params, query);
                    }
                });
            });
        }

        $scope.init()

        $scope.search = function () {
            $scope.init($scope.query)
        }

        $scope.exportToExcel = function (tableId, fileName) {
            console.log('kdgjbkjdnbkndbknbkndkjgnkgjnb')
            paginationService.exportToExcel(tableId, fileName);
        };

        var loadsearchdeliveredbookings = function (tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response
                };
            });
            $scope.query.page = pageable.page-1;
            $scope.query.sort = pageable.sort;
            $scope.query.size = pageable.size;
            cargoBookingManager.getDeliveredCargoBookings($scope.query,function(response){
                $scope.deliveredBookings = response.data.content;
                tableParams.data = $scope.deliveredBookings;
            });
        };

        $scope.searchFind = function () {
            cargoBookingManager.countDeliveredBookings($scope.query, function (count) {
                $scope.searchParams = new NgTableParams({
                    page: 1, // show first page
                    count: 100,
                    sorting: {
                        deliveredOn: 'asc'
                    }
                }, {
                    counts: [100,300,500, 700],
                    total:count,
                    getData: function (params) {
                        loadsearchdeliveredbookings(params);
                    }
                });
            });

        };

        $scope.searchDeliveredBookings = function(){
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
            $scope.searchFind();
        };
        $scope.saveShipmentId = function(id){
            $scope.shipmentId = id;
        };
        $scope.approveCancellation = function(){
            $scope.data.shipmentId = $scope.shipmentId;
            $scope.data.reason = $scope.reason;
            cargoBookingManager.approveCancellation($scope.data,function(response){
                $scope.init();
            });
        };
    })

    .controller("CargoBookingController", function ($rootScope, $scope, $stateParams, $uibModal, cargoBookingManager, suppliersManager, userManager, branchOfficeManager, $location) {
        $scope.headline = "Cargo Booking";
        $scope.shipmentTypes = [];
        $scope.users = [];
        $scope.shipment = {'paymentType': 'Paid'};
        $scope.shipment.dispatchDate = new Date();
        $scope.filter;
        $scope.suppliers = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.shipment.fromBranchId = userManager.getUser().branchOfficeId;
        });

        suppliersManager.getSuppliers(function (data) {
            $scope.suppliers = data;
        });

        $scope.getTotalPrice = function () {
            $scope.shipment.totalCharge = 0;
            for (var index = 0; index < $scope.shipment.items.length; index++) {
                if ($scope.shipment.items[index].charge) {
                    $scope.shipment.totalCharge += parseFloat($scope.shipment.items[index].charge);
                }
            }
            if ($scope.shipment.loadingCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.loadingCharge);
            }
            if ($scope.shipment.unloadingCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.unloadingCharge);
            }
            if ($scope.shipment.otherCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.otherCharge);
            }
            return $scope.shipment.totalCharge;
        }
        $scope.printArea = function () {
            var w = window.open();
            w.document.write(document.getElementsByClassName('report_left_inner')[0].innerHTML);
            w.print();
            w.close();
        }
        if ($stateParams.id) {
            cargoBookingManager.getCargoBooking($stateParams.id, function (cargoBooking) {
                $scope.shipment = cargoBooking;
            });
        }
        userManager.getUserNames(function (users) {
            $scope.users = users;
        });

        cargoBookingManager.getShipmentTypes(function (types) {
            $scope.shipmentTypes = types;
            var paidType = _.find($scope.shipmentTypes, function (type) {
                if (type.shipmentCode === 'P') {
                    return type.id;
                }
            });
        });

        //set the user to current user
        $scope.shipment.forUser = userManager.getUser().id;
        $scope.currentUser = userManager.getUser();

        $scope.copyDetails = function () {
            $scope.shipment.receiverName = $scope.shipment.senderName;
            $scope.shipment.receiverNo = $scope.shipment.senderNo;
        }
        $scope.addItem = function () {
            if (!$scope.shipment.items) {
                $scope.shipment.items = [];
            }
            $scope.shipment.items.push({'index': $scope.shipment.items.length + 1});
        }
        $scope.addItem();

        $scope.deleteItem = function (item) {
            $scope.shipment.items.splice(item.index - 1, 1);
            for (var index = 0; index < $scope.shipment.items.length; index++) {
                $scope.shipment.items[index].index = index + 1;
            }
        }
        $scope.cancelShipmentForm = function () {
            $location.url();
        }
        $scope.dt = new Date();
        $scope.copySenderDetails = function () {
            if ($scope.shipment.copySenderDetails) {
                $scope.shipment.toContact = $scope.shipment.fromContact;
                $scope.shipment.toName = $scope.shipment.fromName;
                $scope.shipment.toEmail = $scope.shipment.fromEmail;
            }
        }
        $scope.saveCargoBooking = function () {
            if ($scope.shipment.paymentType === 'OnAccount' && !$scope.shipment.supplierId) {
                swal("Error", "Please select the account name", "error");
                return;
            }
            if (!$scope.shipment.fromContact || $scope.shipment.fromContact.toString().length < 10) {
                swal("Error", "Invalid contact number for From", "error");
                return;
            }

            if (!$scope.shipment.toContact || $scope.shipment.toContact.toString().length < 10) {
                swal("Error", "Invalid contact number for To", "error");
                return;
            }
            cargoBookingManager.createShipment($scope.shipment, function (response) {
                // $location.url('viewcargobooking/' + response.id);
                $state.go('home.viewcargobooking', {id: response.id})
                $rootScope.$broadcast('UpdateHeader');
            });
        }

        $scope.lookUpCargoBooking = function () {
            if (!this.filter) {
                swal("Error", "Search text is missing", "error");
            } else {
                cargoBookingManager.lookupCargoBooking(this.filter);
            }
        }
        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.init();
            });
        }
        $scope.addComment = function (bookingId) {
            cargoBookingManager.addComment(bookingId,$scope.reviewComment,function (data) {
                swal("Great!");
            });
        }

        $scope.cancelCargoBooking = function (bookingId) {
            cargoBookingManager.cancelCargoBooking(bookingId, function () {
                // $location.url('cargobookings');
                $state.go('home.cargobookings')
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }
        /**
         * Module to find the contact details from the previous booking using the contact number
         * @param contactType -- 'from' or 'to'
         *
         */
        $scope.getDetailsForContact = function (contactType) {
            if (contactType === 'from') {
                cargoBookingManager.findContactInfoFromPreviousBookings(contactType, $scope.shipment.fromContact, function (data) {
                    $scope.shipment.fromName = data.name;
                    $scope.shipment.fromEmail = data.email;
                });
            } else if (contactType === 'to') {
                cargoBookingManager.findContactInfoFromPreviousBookings(contactType, $scope.shipment.toContact, function (data) {
                    $scope.shipment.toName = data.name;
                    $scope.shipment.toEmail = data.email;
                });
            }
        }
        $scope.sendSMS = function (shipmentId) {
            cargoBookingManager.sendSMSForCargoBooking(shipmentId);
        };
}).factory('cargoBookingManager', function ($rootScope, $q, $uibModal, $http, $log, $location, services) {
    return {
        findContactInfoFromPreviousBookings: function (contactType, contact, successCallback, errorCallback) {
            services.get('/api/v1/shipment/findContactInfo?contactType=' + contactType + "&contact=" + contact, '', function (response) {
                if (angular.isFunction(successCallback)) {
                    successCallback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching cargo contact info", error.message, "error");
            })
            // $http.get('/api/v1/shipment/findContactInfo?contactType=' + contactType + "&contact=" + contact)
            //     .then(function (response) {
            //         if (angular.isFunction(callback)) {
            //             callback(response.data);
            //         }
            //     }, function (err, status) {
            //         sweetAlert("Error searching cargo contact info", err.message, "error");
            //     });
        },
        findCargoBookings: function (filter, callback) {
            services.post('/api/v1/shipments', filter, function (response) {
                if (angular.isFunction(callback)) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error searching cargo booking", error.message, "error");
            })
            // $http.post('/api/v1/shipments', filter)
            //     .then(function (response) {
            //         if (angular.isFunction(callback)) {
            //             callback(response.data);
            //         }
            //     }, function (err, status) {
            //         sweetAlert("Error searching cargo booking", err.message, "error");
            //     });
        },
        getCargoBooking: function (id, callback) {
            services.get("/api/v1/shipment/" + id, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http.get("/api/v1/shipment/" + id)
            //     .then(function (response) {
            //         callback(response.data)
            //     }, function (error) {
            //         swal("oops", error, "error");
            //     })
        },
        getShipmentTypes: function (successCallback, errorCallback) {
            services.get('/api/v1/shipment/types', '', function (response) {
                if (response) {
                    successCallback(response.data)
                }
            }, function (error) {
                errorCallback(error)
            })
            // $http.get("/api/v1/shipment/types")
            //     .then(function (response) {
            //         callback(response.data)
            //     }, function (error) {
            //         swal("oops", error, "error");
            //     })
        }, createShipment: function (cargoBooking, successcallback) {
            services.post('/api/v1/shipment', cargoBooking, function (response) {
                if (response) {
                    successcallback(response.data)
                }
            }, function (error) {
                swal("oops", error.data.message, "error");
            })
            // $http.post("/api/v1/shipment", cargoBooking)
            //     .then(function (response) {
            //         successcallback(response.data)
            //     }, function (error) {
            //         swal("oops", error.data.message, "error");
            //     })
        },
        count: function (filter, callback) {
            services.post('/api/v1/shipments/count', filter, function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (error) {
                $log.debug("error retrieving shipments count");
            })
            // $http.post('/api/v1/shipments/count', filter)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving shipments count");
            //     });
        },
        cancelCargoBooking: function (bookingId, callback) {
            swal({
                title: "Do you want to cancel this booking now?", text: "Are you sure?", type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, cancel!",
                closeOnConfirm: true
            }, function () {
                services.put('/api/v1/shipment/cancel/' + bookingId, status, bookingId, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error canceling the booking " + error);
                })
                // $http.put('/api/v1/shipment/cancel/' + bookingId)
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error canceling the booking " + error);
                //     });
            });
        },
        initiateDeliverCargoBooking: function (bookingId, callback) {
            swal({
                title: "Delivery comment",
                text: "Please provide delivery comment:",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "Collecting person name or identification"
            }, function (deliveryComment) {
                if (deliveryComment === false) return false;
                if (deliveryComment === "") {
                    swal.showInputError("Identification is required");
                    return false
                }
                services.put('/api/v1/shipment/deliver/' + bookingId, status, deliveryComment, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving shipments count");
                })
                // $http.put('/api/v1/shipment/deliver/' + bookingId, deliveryComment)
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving shipments count");
                //     });
            });
        },
        lookupCargoBooking: function (LRNumber) {
            services.get("/api/v1/shipment/search/byLR?LRNumber=" + LRNumber, '', function (response) {
                if (response) {
                    if (response.data.length === 0) {
                        swal("Error", "No Cargo Bookings found", "error");
                    } else if (response.data.length === 1) {
                        $location.url('viewcargobooking/' + response.data[0].id);
                    } else {
                        //console.log("found more than one booking " + JSON.stringify(response.data.length));
                        $rootScope.modalInstance = $uibModal.open({
                            templateUrl: 'cargobookings-modal.html',
                            controller: 'CargoBookingLookupController',
                            resolve: {
                                bookings: function () {
                                    return response.data;
                                }
                            }
                        });
                    }
                }
            }, function (error) {
                swal("oops", error.data.message, "error");
            })
            // $http.get("/api/v1/shipment/search/byLR?LRNumber=" + LRNumber)
            //     .then(function (response) {
            //         if (response.data.length === 0) {
            //             swal("Error", "No Cargo Bookings found", "error");
            //         } else if (response.data.length === 1) {
            //             $location.url('viewcargobooking/' + response.data[0].id);
            //         } else {
            //             //console.log("found more than one booking " + JSON.stringify(response.data.length));
            //             $rootScope.modalInstance = $uibModal.open({
            //                 templateUrl: 'cargobookings-modal.html',
            //                 controller: 'CargoBookingLookupController',
            //                 resolve: {
            //                     bookings: function () {
            //                         return response.data;
            //                     }
            //                 }
            //             });
            //         }
            //     }, function (error) {
            //         swal("oops", error.data.message, "error");
            //     });
        },
        sendSMSForCargoBooking: function (shipmentId) {
            services.post("/api/v1/shipment/sendSMS/" + shipmentId, '', function (response) {
                if (response) {
                    console.log('sent SMS');
                }
            }, function (error) {
                swal("oops", error.data.message, "error");
            })
            // $http.post("/api/v1/shipment/sendSMS/" + shipmentId)
            //     .then(function (response) {
            //         console.log('sent SMS');
            //     }, function (error) {
            //         swal("oops", error.data.message, "error");
            //     });
        },
        getBranchSummary: function (filter, callback) {
            services.post('/api/v1/shipment/branchSummary', filter, function (response) {
                if (angular.isFunction(callback)) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error searching branch summary", error.message, "error");
            })
            // $http.post('/api/v1/shipment/branchSummary', filter)
            //     .then(function (response) {
            //         if (angular.isFunction(callback)) {
            //             callback(response.data);
            //         }
            //     }, function (err, status) {
            //         sweetAlert("Error searching branch summary", err.message, "error");
            //     });
        },
        getBookingsForUnloading: function (filter, callback) {
            services.post('/api/v1/shipment/search/unloading', filter, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching for unloading bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/search/unloading', filter)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for unloading bookings", error.message, "error");
            //     });
        },
        getBookingsForLoading: function (filter, callback) {
            services.post('/api/v1/shipment/search/loading', filter, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching for unloading bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/search/loading', filter)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for unloading bookings", error.message, "error");
            //     });
        },
        loadBookings: function (vehicleId, bookingIds, callback) {
            services.post('/api/v1/shipment/assignVehicle/' + vehicleId, bookingIds, function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error unloading bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/assignVehicle/'+vehicleId, bookingIds)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error unloading bookings", error.message, "error");
            //     });
        },
        unloadBookings: function (bookingIds, callback) {
            services.post('/api/v1/shipment/unload', bookingIds, function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error unloading bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/unload', bookingIds)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error unloading bookings", error.message, "error");
            //     });
        }, getBookingsForDelivery: function (filter, callback) {
            services.post('/api/v1/shipment/search/undelivered', filter, function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error searching for undelivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/search/undelivered', filter)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for undelivered bookings", error.message, "error");
            //     });
        },
        addComment: function (bookingId,reviewComment,callback) {
            swal({
                title: "Comment",
                text: "Please provide comment:",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "Add comment",
                inputValue:reviewComment
            }, function (comment) {
                if (comment === false) return false;
                if (comment === "") {
                    swal.showInputError("provide comment");
                    return false
                }
                services.put('/api/v1/shipment/addCommentToBooking/' + bookingId, status, comment, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving shipments count");
                })
                // $http.put('/api/v1/shipment/addCommentToBooking/' + bookingId, comment)
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving shipments count");
                //     });
            });
        },
        getDeliveredCargoBookings: function (query, callback) {
            services.post('/api/v1/shipment/deliveredBookings', query, function (response) {
                if (response) {
                    callback(response)
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/deliveredBookings', query)
            //     .then(function (response) {
            //         callback(response);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        },
        countDeliveredBookings:function (query, callback) {
            services.post('/api/v1/shipment/countDeliveredBookings', query, function (response) {
                if (response) {
                    callback(response.data);
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/countDeliveredBookings', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        },
        searchCancelledBookings:function (query, callback) {
            services.post('/api/v1/shipment/search/cancelled', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/search/cancelled', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        },
        countCancelledBookings:function (query, callback) {
            services.post('/api/v1/shipment/count/cancelled', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/count/cancelled', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        },
        countCancellationPendingCargoBookings:function (query, callback) {
            services.post('/api/v1/shipment/count/cancellationPendingShipments', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.post('/api/v1/shipment/count/cancellationPendingShipments', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        },
        getCancellationPendingBookings:function (query, callback) {
            services.post('/api/v1/shipment/get/pendingShipments', query, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                sweetAlert("Error in fetching data", error.message, "error");
            })
            // $http.post('/api/v1/shipment/get/pendingShipments', query)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         sweetAlert("Error in fetching data", error.message, "error");
            //     });
        },
        approveCancellation:function(data,callback){
            services.put('/api/v1/shipment/approveCancellation', '', data, function (response) {
                if (response) {
                    callback(response);
                }
            }, function (error) {
                sweetAlert("Error searching for delivered bookings", error.message, "error");
            })
            // $http.put('/api/v1/shipment/approveCancellation',data)
            //     .then(function (response) {
            //         callback(response);
            //     }, function (error) {
            //         sweetAlert("Error searching for delivered bookings", error.message, "error");
            //     });
        }

    }
});
