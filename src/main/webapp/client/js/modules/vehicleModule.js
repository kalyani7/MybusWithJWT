'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', ['ngTable', 'ui.bootstrap'])
.controller('VehicleController', function ($scope,$rootScope, $state,$http, $log,paginationService, $uibModal, $filter,$stateParams, vehicleManager, $location, NgTableParams, jobManager) {
        $log.debug('vehicleController');
        $scope.count = 0;
        $scope.loading = false;
        $scope.id = $stateParams.id;
        $scope.query = {};
        $scope.query.vehicleId = $stateParams.id;
        var pageable;
        $scope.searchQuery = {};

        var loadTableParams = function(tableParams){
            var getQuery = {};
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.loading = true;
            getQuery.page = pageable.page;
            getQuery.size = pageable.size;
            getQuery.sort = pageable.sort;
            getQuery.type = $scope.searchQuery.type;
            getQuery.searchText = $scope.searchQuery.searchText;
            vehicleManager.getVehicles(getQuery, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)){
                    $scope.loading = false;
                    $scope.vehicles = response.content;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.vehicles;
                    $scope.currentPageOfVehicles = $scope.vehicles;
                }
            });
        };


        $scope.init = function () {
            vehicleManager.count($scope.searchQuery, function(vehicleCount) {
                $scope.vehicleContentTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    },
                }, {
                    counts: [20, 50, 100],
                    total: vehicleCount,
                    getData: function (params) {
                        params.searchQuery = $scope.searchQuery;
                        loadTableParams(params);
                    }
                });
            });

        };

        $scope.init('');

        $scope.addVehicleOnClick = function (){
            $state.go('createvehicle');
        };

        $scope.deleteVehicleOnClick = function(id) {
            vehicleManager.deleteVehicle(id,function(data) {
            });
        };
        $scope.updateVehicleOnClick = function(id) {
            $location.url('vehicle/'+id);
        };

        $scope.viewJob = function (id) {
            $state.go('jobViewByVehicleId', {id:id});
        };

        $scope.jobViewByVehicle= function () {
            jobManager.getAllsearchJobs($scope.query,function (response) {
                $scope.jobListByVehicle=response.data.content;
            });
        };


    })
    .controller('EditVehicleController', function ($scope,$state,$stateParams, $rootScope, $http,$log, vehicleManager, Upload) {
        $scope.headline = "Add Vehicle";
        $scope.vehicles= [];
        $scope.vehicleId = $stateParams.id;

        $scope.getVehicle = function(){
            vehicleManager.getVehicleById($scope.vehicleId, function(vehicle) {
                $scope.vehicle = vehicle;
                if(vehicle.taxPayments!=null){
                    for(var i=0;i<vehicle.taxPayments.length;i++){
                        $scope.vehicle.taxPayments[i].taxExpiry=new Date(vehicle.taxPayments.paymentDate)
                    }
                }else{
                    $scope.vehicle.taxPayments=[];
                    $scope.vehicle.taxPayments.push({ state :undefined ,taxExpiry:new Date()});
                }
            });
        };

        if($scope.vehicleId){
            $scope.headline = "Update Vehicle";
            $scope.getVehicle();

        } else {
            $scope.vehicle = {
                insuranceExpiry: new Date(),
                permitExpiry : new Date(),
                fitnessExpiry : new Date(),
                pollutionExpiry : new Date(),
                authExpiry: new Date(),
                taxExpiry: new Date(),
                taxPayments: [{
                    state:undefined,
                    taxExpiry: new Date()
                }]
            };
        }



        $scope.addNewState = function() {
            var length = $scope.vehicle.taxPayments.length;
            if( !$scope.vehicle.taxPayments[length-1].state ||!$scope.vehicle.taxPayments[length-1].taxExpiry ){
                swal("Error!", "Please Select State And TaxExpiry Date ", "error");

            }else{
                var newItemNo = $scope.vehicle.taxPayments.length+1;
                $scope.vehicle.taxPayments.push({ state :undefined ,taxExpiry:new Date()});
            }
        };

        $scope.removeState = function(index) {
            var newItemNo = $scope.vehicle.taxPayments.length-1;
            if ( newItemNo !== 0 ) {
                $scope.vehicle.taxPayments.pop();
            }
        };

        $scope.save = function(){
            vehicleManager.createVehicle($scope.vehicle, function (data) {
                $state.go('vehicles');
            }, function (error) {
                console.log('error');
            });
        };
        $scope.cancel = function() {
            $state.go('vehicles');
        };
        $scope.uploadRc = function(file){
            var url = '/api/v1/rcUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.uploadFc = function(file){
            var url = '/api/v1/fcUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.permitUpload = function(file){
            var url = '/api/v1/permitUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.authUpload = function(file){
            var url = '/api/v1/authUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.insuranceUpload = function(file){
            var url = '/api/v1/insuranceUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.pollutionUpload = function(file){
            var url = '/api/v1/pollutionUpload/';
            vehicleManager.uploadVehicleDocument(url,$scope.vehicleId,file,function(response){
                if(response){
                    $scope.getVehicle();
                }
            });
        };
        $scope.removeRC = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "rcCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.removePollutionCopy = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "pollutionCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.removeInsuranceCopy = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "insuranceCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.removeAuthCopy = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "authCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.removePermitCopy = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "permitCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.removeFcCopy = function(key){
            $scope.data = {};
            $scope.data.key = key;
            $scope.data.field = "fcCopy";
            vehicleManager.removeUpload($scope.vehicleId,$scope.data,function(response){
                if(response){
                    $scope.getVehicle();
                    $scope.file = null;
                }
            });
        };
        $scope.saveUrl = function(url){
            console.log("url....",url);
          $scope.url = url;
        };
    }).factory('vehicleManager', function ($rootScope, $http, $log,$q,Upload, $cookies, services) {
    var token = $cookies.get('token');
    var tokenType = $cookies.get('tokenType');
    var sendToken = tokenType + ' ' + token;
        var vehicles = {}
            , rawChildDataWithGeoMap = {};
        var data = '';
        return {
            getVehicles: function ( pageable, callback) {
                $http.post('/api/v1/vehicles', pageable).then(function (response) {
                    callback(response.data);
                },function (err,status) {
                    swal("oops", error, "error");
                });
            },
            getExpiringVehicles: function (pageable,today) {
                // console.log(pageable,today)
                var deferred = $q.defer();
                $q.all([$http({url:'/api/v1/vehicles/expiring',method:"GET", params: pageable, headers: {"Authorization": sendToken}}),
                    $http({url: '/api/v1/serviceReport/haltedServices?date=' + today, method: "GET", headers: {"Authorization": sendToken}}),
                    $http({url:'/api/v1/reminders/getUpcoming',method:"GET",params: pageable, headers: {"Authorization": sendToken} })]).then(
                    function(results) {
                        // console.log(results, 'results')
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
            getAllData: function () {
                return vehicles;
            },
            getAllVehicles: function () {
                return vehicles;
            },
            count: function (query,callback) {
                $http.post('/api/v1/vehicle/count', query).then(function (response) {
                    callback(response.data);
                },function (err,status) {
                    swal("oops", error, "error");
                });
            },
            createVehicle: function (vehicle, callback, error) {
                $http.post('/api/v1/vehicle', vehicle).then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast("reloadVehicleInfo");
                    swal("Great", "Your vehicle has been successfully added", "success");
                },function (err,status) {
                    sweetAlert("Error Saving Vehicle info",err.data.message,"error");
                });
            },
            getVehicleById: function (id,callback) {
                $log.debug("fetching vehicle data ...");
                $http.get('/api/v1/vehicle/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            updateVehicle: function (id,vehicle,callback) {
                $log.debug("fetching vehicle data ...");
                $http.put('/api/v1/vehicle/'+id,vehicle)
                    .then(function (response) {
                        callback(response.data);
                        sweetAlert("Great","Your vehicle has been successfully updated", "success");
                        $log.debug(" vehicle data is updated ...");
                        $rootScope.$broadcast('reloadVehicleInfo');
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            deleteVehicle: function(id,callback) {
                swal({   title: "Are you sure?",   text: "You will not be able to recover this vehicle !",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false }, function() {
                    $http.delete('/api/v1/vehicle/' + id)
                        .then(function (response) {
                            callback(response.data);
                            sweetAlert("Great", "Your Vehicle has been successfully deleted", "success");
                            $rootScope.$broadcast('reloadVehicleInfo');
                        },function (error) {
                            sweetAlert("Oops...", "Error finding vehicle data!", "error" + angular.toJson(error));
                        });
                });
            },
            uploadVehicleDocument:function(url,vehicleId,file,callback){
                Upload.upload({
                    url: url+vehicleId,
                    data: {
                        files: file,
                    },
                }).then(function (success) {
                    if (success) {
                        swal({
                            title: "Wow!",
                            text: "File uploaded successfully!",
                            type: "success",
                        });
                        callback(success);
                    }
                });
            },
            removeUpload:function (vehicleId,data,callback) {
                swal({   title: "Are you sure?",   text: "You will not be able to recover this vehicle !",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false }, function() {
                    $http.put('/api/v1/vehicle/removeFile/'+vehicleId,data)
                        .then(function (response) {
                            sweetAlert("Error","File deleted","error");
                            callback(response.data);
                        },function (err,status) {
                            sweetAlert("Error",err.message,"error");
                        });
                });
            }
        }

    });

