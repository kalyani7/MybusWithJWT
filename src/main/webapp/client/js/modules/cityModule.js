"use strict";
/*global angular, _*/

angular.module('myBus.cityModule', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    CitiesController   =======================================================
  // ==================================================================================================================


    .controller('CitiesController', function ($scope, $uibModal, $http, $log, NgTableParams, $filter, cityManager, $location, $rootScope,paginationService) {
        $scope.headline = "Cities";
        $scope.currentPageOfCities = [];
        $scope.loading =false;
        $scope.cities = {};
        $scope.count = 0;
        var pageable ;

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
            pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
        });
            $scope.loading = true;
            cityManager.getCities(pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response)) {
                    $scope.loading = false;
                    $scope.cities = response;
                    // tableParams.total(response.length);
                    $scope.count = response.length;
                    tableParams.data = $scope.cities;
                    $scope.currentPageOfCities =  $scope.cities;
                }
            });
        };

        $scope.init = function() {
            cityManager.count(function(citiesCount){
                $scope.citiesCount = citiesCount;
            $scope.cityContentTableParams = new NgTableParams({
                page: 1,
                size:10,
                count:10,
                sorting: {
                    name: 'asc'
                },
            }, {
                counts:[],
                total: citiesCount,
                getData: function (params) {
                    loadTableData(params);
                }

                 });
            });
        };
        $scope.init();


        $scope.$on('CityCompleteEvent', function (e, value) {
            $scope.init();
        });

        $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
            loadTableData($scope.cityContentTableParams);
        });

        $scope.goToBoardingPointsList = function (id) {
            $state.go('home.city', {id: id});
            // $location.url('/city/' + id);
        };

//---------------------------------------------------------------------------------------------------------------------
    $scope.handleClickAddStateCity = function (size) {
        $rootScope.modalInstance = $uibModal.open({
            templateUrl: 'add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        $rootScope.modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            $scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
        $scope.handleClickDeleteStateCity = function(passId){
            cityManager.deleteCity(passId);
        };
    $scope.handleClickUpdateStateCity = function(cityId){
        $rootScope.modalInstance = $uibModal.open({
            templateUrl : 'update-city-state-modal.html',
            controller : 'UpdateStateCityModalController',
            resolve : {
                passId : function(){
                    return cityId;
                }
            }
        });
    };

  })
    // ========================== Modal - Update City, State  =================================

    .controller('UpdateStateCityModalController', function ($scope, $state, $uibModal, $http, $log, cityManager, passId, $rootScope) {
        $scope.city = {};
        $scope.displayCity = function(data){
            $scope.city = data;
        };
        $scope.setCityIntoModal = function(passId){
            cityManager.getCity(passId,$scope.displayCity);
        };
        $scope.setCityIntoModal(passId);
        $scope.ok = function () {
            if ($scope.city.id === null || $scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager.updateCity($scope.city, function (data) {
                $state.transitionTo('home.cities');
                $rootScope.modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                ($scope.city.state || '') !== '';
        };
    })

//
    // ========================== Modal - Add City, State  =================================
    //
    .controller('AddStateCityModalController', function ($scope, $rootScope,$uibModal,$state, $http, $log, cityManager) {
        $scope.city = {
            name: null,
            state: null
        };
        $scope.ok = function () {
            if ($scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager.createCity($scope.city, function(data){
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            });
        };
        
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                    ($scope.city.state || '') !== '';
        };

    }).factory('cityManager', function ($rootScope, $http, $log, $location, services) {
        var cities = {}
            , rawChildDataWithGeoMap = {}, totalCount = 0;
        return {
            getCities: function ( pageable, callback) {
                services.get('/api/v1/cities', pageable, function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving agents");
                })
                // $http({url:'/api/v1/cities',method: "GET",params: pageable})
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         $log.debug("error retrieving agents");
                //     });
            },
            count: function (callback) {
                services.get('/api/v1/cities/count', '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving route count");
                })
                // $http.get('/api/v1/cities/count')
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function (error) {
                //         $log.debug("error retrieving route count");
                //     });
            },
            getActiveCityNames:function(callback) {
                services.get('/api/v1/activeCityNames', '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    $log.debug("error retrieving cities");
                })
                // $http.get('/api/v1/activeCityNames')
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         $log.debug("error retrieving cities");
                //     });
            },
            getAllCities: function () {
                return cities;
            },
            getTotalCount : function() {
                return totalCount;
            },
            getChildrenByParentId: function (parentId) {
                if (!parentId) {
                    return [];
                }
                if (rawChildDataWithGeoMap[parentId]) {
                    return rawChildDataWithGeoMap[parentId];
                }
                return _.select(rawDataWithGeo, function (value) {
                    return value && value.parentId === parentId;
                });
            },

            getOneById: function (id) {
                return _.first(_.select(cities, function (value) {
                    return value.id === id;
                }));
            },
            getCityName: function (id) {
                return _.first(_.select(cities, function (value) {
                    return value.id === id;
                })).name;
            },
            createCity : function (city, callback) {
                services.post('/api/v1/city', city, function (response) {
                    callback(response.data);
                    swal("Great", "Your City has been successfully added", "success");
                    $rootScope.$broadcast('CityCompleteEvent');
                }, function(err,status) {
                    sweetAlert("Error",err.data.message,"error");
                })
                // $http.post('/api/v1/city',city)
                //     .then(function (response) {
                //         callback(response.data);
                //         swal("Great", "Your City has been successfully added", "success");
                //         $rootScope.$broadcast('CityCompleteEvent');
                //     },function(err,status) {
                //         sweetAlert("Error",err.data.message,"error");
                //     });
            },
            getCity: function (id, callback) {
                services.get('/api/v1/city/' + id, '', function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('BoardingPointsInitComplete');
                }, function (error) {
                    alert("error finding city. " + angular.toJson(error));
                })
                // $http.get('/api/v1/city/' + id)
                //     .then(function (response) {
                //         callback(response.data);
                //         $rootScope.$broadcast('BoardingPointsInitComplete');
                //     },function (error) {
                //         alert("error finding city. " + angular.toJson(error));
                //     });
            },
            deleteCity: function(id) {
                services.delete('/api/v1/city/' + id, function (response) {
                    if (response) {
                        sweetAlert("Great", "Your City has been successfully deleted", "success");
                        $location.url("/cities");
                        $rootScope.$broadcast('CityCompleteEvent');
                    }
                }, function (error) {
                    sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                })
                // swal({   title: "Are you sure?",   text: "You will not be able to recover this City !",   type: "warning",
                //     showCancelButton: true,
                //     confirmButtonColor: "#DD6B55",
                //     confirmButtonText: "Yes, delete it!",
                //     closeOnConfirm: false }, function() {
                //     $http.delete('/api/v1/city/' + id)
                //         .then(function (response) {
                //             //callback(response.data);
                //             sweetAlert("Great", "Your City has been successfully deleted", "success");
                //            $location.url("/cities");
                //             $rootScope.$broadcast('CityCompleteEvent');
                //         },function (error) {
                //             sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                //         });
                // });
            },
            updateCity: function(city,callback) {
                services.put('/api/v1/city/' + city.id, status, city, function (response) {
                    if (response) {
                        callback(response.data);
                        sweetAlert("Great","Your City has been successfully updated", "success");
                        $rootScope.$broadcast('CityCompleteEvent');
                    }
                }, function (error) {
                    console.log(JSON.stringify(error));
                    sweetAlert("Oops..", "Error updating City data!", "error" + angular.toJson(error));
                })
                // $http.put('/api/v1/city/'+city.id,city).then(function (response) {
                //     callback(response.data);
                //     sweetAlert("Great","Your City has been successfully updated", "success");
                //     $rootScope.$broadcast('CityCompleteEvent');
                // },function (error) {
                //     console.log(JSON.stringify(error));
                //     sweetAlert("Oops..", "Error updating City data!", "error" + angular.toJson(error));
                // })
            },
            //----------------------------------------------------------------------
            createBordingPoint: function (cityId,boardingPoint,callback) {
                services.post('/api/v1/city/'+cityId+'/boardingpoint', boardingPoint, function (response) {
                    if (response) {
                        callback(response.data);
                        sweetAlert("Great","Your BoardingPoint has been successfully added", "success");
                    }
                }, function (err,status) {
                    sweetAlert("Error",err.data.message,"error");
                })
                // $http.post('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).then(function (response) {
                //     callback(response.data);
                //     sweetAlert("Great","Your BoardingPoint has been successfully added", "success");
                // },function (err,status) {
                //     sweetAlert("Error",err.data.message,"error");
                // });
            },
            updateBp: function(cityId,boardingPoint,callback) {
                services.put('/api/v1/city/'+cityId+'/boardingpoint', '', boardingPoint, function (response) {
                    if (response) {
                        callback(response.data);
                        sweetAlert("Great","Your BoardingPoint has been successfully updated", "success");
                    }
                }, function () {
                    sweetAlert("Oops...", "Error updating Bp data!", "error");
                })
                // $http.put('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).then(function (response) {
                //     callback(response.data);
                //     sweetAlert("Great","Your BoardingPoint has been successfully updated", "success");
                //     // $rootScope.$broadcast('updateBpCompleteEvent');
                // },function () {
                //     sweetAlert("Oops...", "Error updating Bp data!", "error");
                // });
            },
            deleteBp: function(cityId,BpId,callback) {
                services.delete('/api/v1/city/'+cityId+'/boardingpoint/'+BpId, function (response) {
                    if (response) {
                        callback(response.data);
                        sweetAlert("Great", "Your BoardingPoint has been successfully deleted", "success");
                        $rootScope.$broadcast('deleteBpCompleteEvent');
                    }
                }, function (error) {
                    sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                })
                // swal({   title: "Are you sure?",   text: "You will not be able to recover this BoardingPoint !",   type: "warning",
                //     showCancelButton: true,
                //     confirmButtonColor: "#DD6B55",
                //     confirmButtonText: "Yes, delete it!",
                //     closeOnConfirm: false }, function() {
                //     $http.delete('/api/v1/city/'+cityId+'/boardingpoint/'+BpId)
                //         .then(function (response) {
                //             callback(response.data);
                //             sweetAlert("Great", "Your BoardingPoint has been successfully deleted", "success");
                //             $rootScope.$broadcast('deleteBpCompleteEvent');
                //         },function (error) {
                //             sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                //         });
                // });
            },
            getBp: function (id,BpId, callback) {
                services.get('/api/v1/city/'+id+'/boardingpoint/'+BpId, '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    sweetAlert("Oops...", "Error finding BoardingPoint data!", "error" + angular.toJson(error));
                })
                // $http.get('/api/v1/city/'+id+'/boardingpoint/'+BpId)
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         sweetAlert("Oops...", "Error finding BoardingPoint data!", "error" + angular.toJson(error));
                //     });
            },
            getBoardingPoints: function (cityId,callback) {
                services.get('/api/v1/city/'+cityId+'/boardingpoint/', '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    sweetAlert("Oops...", "Error loading BoardingPoint data!", "error" + angular.toJson(error));
                })
                // $http.get('/api/v1/city/'+cityId+'/boardingpoint/')
                //     .then(function (response) {
                //         callback(response.data);
                //     },function (error) {
                //         sweetAlert("Oops...", "Error loading BoardingPoint data!", "error" + angular.toJson(error));
                //     });
            }
        }
    }).controller('BoardingPointsListController', function ($scope, $http,$uibModal, $log, NgTableParams,$state,$stateParams, $filter, cityManager, $rootScope) {
        $log.debug('BoardingPointsListController');
        $scope.headline = "Boarding Points";
        $scope.cityId = $stateParams.id;
        $scope.currentPageOfBoardingPoints = [];
        var loadTableData = function (tableParams, $defer) {
            cityManager.getCity($scope.cityId, function(data) {
                $scope.city = data;
                var orderedData = tableParams.sorting() ? $filter('orderBy')($scope.city.boardingPoints, tableParams.orderBy()) : $scope.city.boardingPoints;
                tableParams.total($scope.city.boardingPoints.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfBoardingPoints = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.boardingPointContentTableParams = new NgTableParams({
            page: 1,
            count: 25,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfBoardingPoints.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        //--------------------------------City deletion-----------------------------------------------------------
        $scope.handleDeleteButtonClicked = function(id) {
            cityManager.deleteCity(id);
        },
        //-----------------------------------------------------------------------------------------------------------
        $scope.handleClickAddBoardingPoint = function () {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-boardingpoint-to-city-state-modal.html',
                controller: 'AddBoardingPointController',
                resolve: {
                    cityId:function(){
                        return $scope.cityId;
                    },
                    city :function(){
                        return $scope.city;
                    }
                }
            })
        },
        $scope. updateBpOnClick = function(id) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'update-boardingPt.html',
                controller: 'UpdateBoardingPtController',
                resolve: {
                    cityId: function () {
                        return $scope.cityId;
                    },
                    BpId:function(){
                        return id;
                    }
                }
            })
        },
        $scope. deleteBpOnClick = function(id) {
            cityManager.deleteBp($scope.cityId,id,function(data){
                console.log("in deleteBP"+data.name);
                $state.go($state.$current, null, { reload: true });
            });
        }
    })

    // ========================== Modal - Boarding point controller =================================
    .controller('AddBoardingPointController', function ($scope, $uibModal,$state, $http,$log,city, cityManager, $rootScope) {
        $scope.boardingPoint = {};
        $scope.city = city;
        $scope.ok = function () {
            if ($scope.boardingPoint.name === null || $scope.boardingPoint.contact === null || $scope.boardingPoint.landmark === null) {
                $log.error("null name or contact or landmark.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager. createBordingPoint(city.id,$scope.boardingPoint, function(data){
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            });
        };
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.boardingPoint.name || '') !== '' &&
                ($scope.boardingPoint.landmark || '') !== '' &&
                ($scope.boardingPoint.contact || '') !== '';
        };
    })
//======================Model - updateBpController=============================================
    .controller('UpdateBoardingPtController', function ($scope, $uibModal, $state,$http,BpId,cityId, $log,cityManager, $rootScope) {
        $scope.setBpIntoView = function(cityId,BpId){
            cityManager.getBp(cityId,BpId,function(data){
                $scope.boardingPoint=data;
            })
        };
        $scope.setBpIntoView (cityId,BpId);
        $scope.ok = function (BpId) {
            cityManager.updateBp(cityId,$scope.boardingPoint, function(data) {
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            })
        }
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    });



