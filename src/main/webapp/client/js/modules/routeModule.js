
"use strict";

 angular.module('myBus.routeModule', ['ui.bootstrap'])

// ============================================================================================================= //
// ====================================    Routes  Controller    =============================================== //
// ============================================================================================================= //

    .controller('RoutesController', function ($scope,$rootScope, $http,$state, $log, routesManager,$filter,paginationService,NgTableParams,$location) {
        $log.debug('RoutesController loading');
        $scope.headline = "Routes";
        var pageable;


        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count()};
            });
            routesManager.load(pageable).then(function(response){
                let routes = response[0].data;
                if (angular.isArray(routes)) {
                    $scope.allRoutes = routes;
                    $scope.cities = response[1].data;
                    angular.forEach($scope.allRoutes, function (route) {
                        angular.forEach($scope.cities, function (city) {
                            if (city.id == route.fromCityId) {
                                route.attrs.fromCity = city.name;
                            }
                            if (city.id == route.toCityId) {
                                route.attrs.toCity = city.name;
                            }
                        });
                    });
                    $scope.loading = false;
                    tableParams.total(response[0].data.totalElements);
                    $scope.count = response[0].data.totalElements;
                    tableParams.data = $scope.allRoutes;
                    $scope.currentPageOfRoutes =  $scope.allRoutes;
                }
            })
        };

        $scope.init = function(){
            routesManager.count(function(routesCount){
                $scope.routeContentTableParams = new NgTableParams({
                page: 1,
                count: 10,
                sorting: {
                    name: 'asc'
                }
                }, {
                    counts: [],
                    total: routesCount,
                    getData: function (params) {
                               loadTableData(params);
                    }
                     });
                });
        };
        $scope.init();
        $scope.$on('RoutesInitComplete', function (e, value) {
                $scope.init();
        });

        $scope.handleClickDeleteRoute = function(passId){
            routesManager.deleteRoute(passId);
        };

        $scope.handleClickUpdateRoute = function (routeId) {
          $state.go('createRoute',{id:routeId});
        };

    })
    // ============================================================================================================= //
    // ================================    Routes  Add and Update Controller   ===================================== //
    // ============================================================================================================= //
    .controller('AddUpdateRouteModalController', function ($document,$scope,$state, $http, $log,cityManager, routesManager,$rootScope, $stateParams) {

        $scope.cities = [];
        $scope.selectedViaCities = [];
        $scope.route = {
            name: null,
            viaCities: [],
            fromCityId: null,
            toCityId: null
        };
        var passId = $stateParams.id;
        $scope.title = "Add Route";

        if (passId){
            $scope.title = "Update Route";
            $scope.loadFromCities = function () {
                $scope.route = {};
                routesManager.getRoute(passId, function (data) {
                        $scope.route = data;
                    angular.forEach($scope.cities, function (fromCityName) {
                        if (fromCityName.id == $scope.route.fromCityId) {
                            $scope.routesFromCityName = fromCityName.name;
                        }
                    });
                    angular.forEach($scope.cities, function (toCityName) {
                        if (toCityName.id == $scope.route.toCityId) {
                            $scope.routesToCityName = toCityName.name;
                        }
                    });
                    angular.forEach($scope.route.viaCities, function (existingCityId) {
                        angular.forEach($scope.cities, function (city) {
                            if (existingCityId == city.id) {
                                $scope.selectedViaCities.push(city);
                            }
                        });
                    });
                });
            };
            $scope.loadFromCities();
        }
        else {
            console.log("Adding new route initiation");
        }
        $scope.cancel = function () {
            $state.go('routes');
        };
        $scope.addTheCity = function (selectedCity) {
            $scope.viaCities = [];
            if ($scope.route.viaCities.indexOf(selectedCity) == -1) {
                $scope.route.viaCities.push(selectedCity);

                for(var i =0;i < $scope.cities.length;i++){
                    $scope.cityDetails = _.find($scope.cities, function(city){
                        return selectedCity.toString() === city.id;
                    });
                }
                $scope.selectedViaCities.push($scope.cityDetails);
                for(var j = 0; j < $scope.route.viaCities.length; j++) {
                    $scope.selectedCities = _.without($scope.cities, _.findWhere($scope.cities, {id:$scope.route.viaCities[j]}));
                }
            }
            else {
                swal("Oops", "city already Added", "error");
            }
        };

        $scope.list = $scope.selectedViaCities;

        $scope.sorting = function (index) {
            $scope.selectedViaCities.splice(index, 1);
            $scope.route.viaCities = [];
            angular.forEach($scope.list, function (val, index) {
                $scope.route.viaCities.push(val.id);
            });
        }

            $scope.deleteCityFromList = function (cityId) {
            var index = $scope.route.viaCities.indexOf(cityId);
            if (index != -1) {
                $scope.route.viaCities.splice(index, 1);
                $scope.selectedViaCities.splice(index, 1);
            }
            else {
                swal("Oops", "city already removed from list", "error");
            }
        };
        $scope.routesFromManager = [];
        var pageable;
        $scope.onMouseLeave = function (Name) {
            routesManager.load(pageable).then(function (response) {
                $scope.routesFromManager = response[0].data.content;
            });
            angular.forEach($scope.routesFromManager, function (route) {
                if (route.name == Name) {
                    swal("Route already exist", "error");
                }
            });
        };
        $scope.loadCities = function () {
            cityManager.getActiveCityNames(function (data) {
                $scope.cities = data;
                $scope.selectedCities = data;
            });
        };
        $scope.loadCities();

        $scope.ok = function () {
            if (passId) {
                routesManager.updateRoute($scope.route, function (data) {
                    if (data) {
                        $state.go('routes');
                    }
                });
            } else {
                if ($scope.route.name === null || $scope.route.toCityId === null  ) {
                    swal("Error", "Please fill in all required fields", "error");
                    $log.error("nothing was added.");
                } else {
                    routesManager.createRoute($scope.route, function (data) {
                        if(data){
                            $state.go('routes');
                        }
                    });
                }
            }
        };
    })

    // ============================================================================================================= //
    // ====================================    Routes  Manager (Service)   ========================================= //
    // ============================================================================================================= //

    .factory('routesManager', function ($rootScope,$q, $http, $log) {
        var routes = {};
        return{
            /*getRoutes: function (pageable, callback) {
                $http({url:'/api/v1/routes',method: "GET",params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving Routes");
                    });
            },*/
            load: function (pageable) {
                var deferred = $q.defer();
                $q.all([$http({url: '/api/v1/routes', method: "GET", params: pageable}),
                    $http.get('/api/v1/activeCityNames')]).then(
                    function (results) {
                        deferred.resolve(results)
                    },
                    function (errors) {
                        deferred.reject(errors);
                    },
                    function (updates) {
                        deferred.update(updates);
                    });
                return deferred.promise;
            },
            getActiveRouteNames: function(callback) {
                $http.get('/api/v1/routes')
                    .then(function(response){
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving routes");
                    });
            },
            getRoute: function(routeId,callback){
                $http.get('/api/v1/route/'+routeId)
                    .then(function(response){
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },
            count: function (callback) {
                $http.get('/api/v1/routes/count')
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving route count");
                    });
            },
            createRoute: function(route,callback) {
                $http.post('/api/v1/route', route).then(function (response) {
                    callback(response.data);
                    swal("Added!", "Route was successfully Added!", "success");
                    $rootScope.$broadcast('RoutesInitComplete');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
                });
            },
            deleteRoute: function(routeId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this route?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){
                    $http.delete('/api/v1/route/' + routeId).then(function () {
                        $rootScope.$broadcast('RoutesInitComplete');
                        swal("Deleted!", "Route was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            },
            updateRoute: function(route,callback){
                $http.put('/api/v1/route/'+route.id,route).then(function(response){
                    callback(response.data);
                    swal("Updated!", "Route was successfully Updated!", "success");
                    $rootScope.$broadcast('RoutesInitComplete');
                },function(){
                    sweetAlert("Error","Error Updating Route");
                });
            }
        }
    });

