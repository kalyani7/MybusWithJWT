'use strict';
/*global angular,_*/

angular.module('myBus.dailyTripModule', ['ngTable', 'ui.bootstrap'])
.controller('AddDailyTripController',function ($scope,vehicleManager,$rootScope,staffManager,serviceReportsManager,DailyTripManager,$stateParams,PreferredStaffManager,$state) {
    $scope.titleName = 'Add Daily trip';
    $scope.trip = {};
    $scope.dt = new Date();
    $scope.staffDetails = [];
    $scope.loading=false;


    vehicleManager.getVehicles({}, function (res) {
        $scope.allVehicles = res.content;
        $rootScope.$broadcast('vehicles', $scope.allVehicles);
    });

    $scope.getStaffForDailyTrips = function(staffIds){
        $scope.query = {staffIds:staffIds};
        PreferredStaffManager.getStaffForDailyTrips($scope.query,function(response){
            $scope.allStaff= response;
            $scope.allStaffDuplicate = response;
        });
    };
    $scope.getServices = function(){
        $scope.date = $scope.dt.getFullYear() + "-" + ('0' + (parseInt($scope.dt.getMonth() + 1))).slice(-2) + "-" + ('0' + $scope.dt.getDate()).slice(-2);
        $scope.loading=true;
        DailyTripManager.getServices($scope.date,function (response) {
            $scope.loading=false;
            $scope.serviceList = _.sortBy(response, function (o) {
                return o.serviceName;
            });
        });
    };
    $scope.getAllStaff = function(){
        staffManager.getStaffList(null, function (response) {
            $scope.allStaff= response.content;
            $scope.allStaffDuplicate= response.content;
            if($stateParams.tripId){
                DailyTripManager.getTrip($stateParams.tripId,function (response) {
                    $scope.trip = response;
                    var vehicle = _.find($scope.allVehicles, function (vehicle) {
                        return vehicle.id.toString() === $scope.trip.vehicleId;
                    });
                    if (vehicle) {
                        $scope.vehicleRegNo = vehicle;
                    }
                    $scope.dt = new Date($scope.trip.tripDate);
                    $scope.query = {staffIds:$scope.trip.staffIds};
                    $scope.staffDetails = [];
                    for(var i =0; i < $scope.trip.staffIds.length; i++) {
                        var newStaffObj = _.find($scope.allStaff, function (s) {
                            return s.id === $scope.trip.staffIds[i];
                        });
                        $scope.staffDetails.push(newStaffObj);
                    }
                    $scope.date = $scope.dt.getFullYear() + "-" + ('0' + (parseInt($scope.dt.getMonth() + 1))).slice(-2) + "-" + ('0' + $scope.dt.getDate()).slice(-2);
                    serviceReportsManager.getServices($scope.date, function (data) {
                        $scope.serviceList = _.sortBy(data.data, function (o) {
                            return o.serviceName;
                        });
                        var service = _.find($scope.serviceList, function (service) {
                           return  service.serviceName === $scope.trip.serviceName;
                        });
                        if (service) {
                            $scope.trip.service = service;
                        }
                    });
                });
            }else{
                $scope.$watch('dt', function (newValue, oldValue) {
                    $scope.getServices();
                });
            }
        });
    };
    $scope.addStaff = function () {
        if (!$scope.trip.staffIds) {
            $scope.trip.staffIds = [];
        }
        if($scope.trip.newStaffId === undefined){
            sweetAlert("Error", "Please select Staff", "error");
        }
        $scope.trip.staffIds.push($scope.trip.newStaffId.id);
        $scope.trip.newStaffId = null;

        $scope.staffDetails = [];
        for(var i =0; i < $scope.trip.staffIds.length; i++) {
            var newStaffObj = _.find($scope.allStaffDuplicate, function (s) {
                return s.id === $scope.trip.staffIds[i];
            });
            if(newStaffObj === undefined){
                sweetAlert("Error", "Please select Staff", "error");
            }else {
                $scope.staffDetails.push(newStaffObj);
            }
        }

        for(var j = 0; j < $scope.trip.staffIds.length; j++) {
            $scope.allStaff = _.without($scope.allStaff, _.findWhere($scope.allStaff, {id:$scope.trip.staffIds[j]}));
        }
    };
    $scope.deleteStaff = function (staff, index) {
        $scope.staffDetails.splice(index, 1);
        $scope.trip.staffIds = _.without($scope.trip.staffIds, staff.id);
        $scope.allStaff.unshift(staff);
    };
    $scope.save = function(){
        $scope.trip.date = $scope.date;
        $scope.trip.tripDate = $scope.dt;
        if ($stateParams.tripId) {
            if($scope.trip.vehicleId.id){
                $scope.trip.vehicleId = $scope.trip.vehicleId.id;
            }
            if($scope.trip.service.id){
                $scope.trip.serviceName = $scope.trip.service.serviceName;
                $scope.trip.serviceNumber =$scope.trip.service.serviceNumber;
            }
            DailyTripManager.updateDailyTrip($scope.trip, function (response) {
            });
            swal("success", "trip Updated", "success");
            $state.go('dailytrips');
        } else {
            $scope.trip.vehicleId = $scope.trip.vehicleId.id;
            $scope.trip.serviceName = $scope.trip.service.serviceName;
            $scope.trip.serviceNumber =$scope.trip.service.serviceNumber;
            DailyTripManager.addDailyTrip($scope.trip, function (response) {
            });
            swal("success", "trip added", "success");
            $state.go('dailytrips');
        }
    };
    $scope.getStaffFromLastTrip = function(vehicleId){
        if($scope.isChecked){
            $scope.tripQuery = {vehicleId:vehicleId,date:$scope.date};
            DailyTripManager.getStaffFromLastTrip($scope.tripQuery,function(response){
                $scope.trip.staffIds = response;
                for(var i =0; i < $scope.trip.staffIds.length; i++) {
                    var newStaffObj = _.find($scope.allStaff, function (s) {
                        return s.id === $scope.trip.staffIds[i];
                    });
                    $scope.staffDetails.push(newStaffObj);
                }
            });
        }
    };
    $scope.cancel = function(){
        $state.go('dailytrips');
    };
    $scope.getAllStaff();

}).controller('DailyTripsController',function ($scope,DailyTripManager,$state,staffManager,vehicleManager,paginationService,NgTableParams) {
    $scope.query = {};
    $scope.dt = new Date();
    $scope.searchQuery = {};
    var pageable;
    staffManager.getStaffList(null, function (response) {
           $scope.staffList= response.content;
    });
    vehicleManager.getVehicles({}, function (res) {
        $scope.vehiclesList = res.content;
    });
    var loadTableParams = function (tableParams){
        paginationService.pagination(tableParams, function(response){
            pageable = {page:tableParams.page(),
                size:tableParams.count(),
                sort:response
            };
        });
        $scope.query.page = pageable.page-1;
        $scope.query.size = pageable.size;
        DailyTripManager.getAllDailyTrips($scope.query,function(response){
            $scope.dailyTrips = response.content;
            tableParams.data = $scope.dailyTrips;

        });
    };
    $scope.init = function() {
        $scope.date = $scope.dt.getFullYear() + "-" + ('0' + (parseInt($scope.dt.getMonth() + 1))).slice(-2) + "-" + ('0' + $scope.dt.getDate()).slice(-2);
        $scope.query = {date:$scope.date};
        DailyTripManager.getDailyTripsCount($scope.query,function (count) {
            $scope.dailyTripsTableParams = new NgTableParams({
                page: 1, // show first page
                size: 10,
                sorting: {
                    createdAt: 'desc'
                }
            }, {
                counts: [20, 50, 100],
                total: count,
                getData: function (params) {
                    loadTableParams(params);
                }
            });
        });
    };
    $scope.nextDay = function() {
        var yesterday = new Date();
        yesterday.setDate(yesterday.getDate() -1);
        $scope.dt1 = yesterday;
        $scope.tomorrow = new Date($scope.dt1.getTime() + (24 * 60 * 60 * 1000));
        $scope.dt2 = $scope.dt;
        var dt = $scope.dt2;
        dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
        $scope.dt = new Date($scope.dt2.setTime(dt.getTime()));
        if ($scope.dt >= $scope.tomorrow) {
            swal("Oops...", "U've checked for future, Check Later", "error");
        }
    };
    $scope.previousDay = function() {
        var dt = $scope.dt;
        dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
        $scope.dt = new Date(dt);
    };
    $scope.selectedDate=function(){
        var dt = $scope.dt;
        $scope.dt = new Date(dt);
    }

    $scope.$watch('dt', function(newValue, oldValue) {
        $scope.init();
    });
    $scope.addDailyTrip = function (id) {
        $state.go('addDailyTrip',{tripId:id});
    };
    $scope.deleteDailyTrip = function (id) {
        DailyTripManager.deleteDailyTrip(id,function (response) {});
        $scope.init();
    };
    $scope.search = function(){
        if($scope.fromDate) {
            var startDate = new Date($scope.fromDate);
            var startYear = startDate.getFullYear();
            var startMonth = startDate.getMonth() + 1;
            var startDay = startDate.getDate();
            $scope.searchQuery.fromDate = startYear + '-' + startMonth + '-' + startDay;
        }
        if($scope.toDate){
            var endDate = new Date($scope.toDate);
            var endYear = endDate.getFullYear();
            var endMonth = endDate.getMonth() + 1;
            var endDay = endDate.getDate();
            $scope.searchQuery.toDate = endYear + '-' + endMonth + '-' + endDay;
        }
        DailyTripManager.getAllDailyTrips($scope.searchQuery,function(response){
            $scope.searchDailyTrips = response.content;
        });
    };
    $scope.init();


}).factory('DailyTripManager',function ($http) {
    return{
        addDailyTrip:function (trip, callback) {
            $http.post('/api/v1/dailyTrips/addDailytrip', trip)
                .then(function (response) {
                    callback(response);
                }, function (err) {});
        },
        updateDailyTrip:function (trip, callback) {
            $http.put('/api/v1/dailyTrips/updateDailyTrip', trip)
                .then(function (response) {
                    callback(response);
                }, function (err) {});
        },
        getAllDailyTrips: function (query,callback) {
            $http.post('/api/v1/dailyTrips/getAllDailyTrips', query)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        deleteDailyTrip:function (id,callback) {
            $http.delete('/api/v1/dailyTrips/deleteDailyTrip/'+id)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getTrip: function (id,callback) {
            $http.get('/api/v1/dailyTrips/getDailyTrip/'+id)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getDailyTripsCount:function (query,callback) {
            $http.post('/api/v1/dailyTrips/getDailyTripsCount', query)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getStaffFromLastTrip:function (tripQuery,callback) {
            $http.post('/api/v1/dailyTrips/getStaffFromLastTrip',tripQuery)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        },
        getServices:function (date,callback) {
            $http.get('/api/v1/dailyTrips/getServices/'+date)
                .then(function (response) {
                    callback(response.data);
                }, function(error){
                    swal("oops", error, "error");
                });
        }
    }

});