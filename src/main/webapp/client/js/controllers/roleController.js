/**
 * Created by svanik on 1/19/2016.
 */
"use strict";

angular.module('myBus.roleModules', ['ui.bootstrap'])

    // ==================================================================================================================
    // ====================================    RoutesController   ================================================
    // ==================================================================================================================

    .controller('RoleController', function ($scope,$modal, $log, roleManager,$filter,NgTableParams,$location) {

        $log.debug('RoleController loading');
        $scope.headline = "Roles";
        $scope.allRoles = [];
        $scope.currentPageOfRoles = [];

        var loadTableData = function (tableParams, $defer) {
            var data = roleManager.getAllRoles();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.allRoles = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfRoles = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('RolesInitComplete', function (e, value) {
            loadTableData($scope.routeContentTableParams);
        });

        $scope.$on('CreateRouteCompleted',function(e,value){
            routesManager.fetchAllRoutes();
        });

        $scope.$on('FetchingRoutesComplete',function(e,value){
            routesManager.fetchAllRoutes();
        });

        $scope.$on('DeleteRouteCompleted',function(e,value) {
            routesManager.fetchAllRoutes();
        });

        $scope.$on('UpdateRouteCompleted',function(e,value) {
            routesManager.fetchAllRoutes();
        });

        $scope.routeContentTableParams = new NgTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfRoutes.length,
            getData: function ($defer, params) {
                $scope.$on('RoutesInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        routesManager.fetchAllRoutes();

        $scope.handleClickDeleteRoute = function(passId){
            routesManager.deleteRoute(passId);
        };

       $scope.handleClickAddNewRoute = function(cityId){
            var modalInstance = $modal.open({
                templateUrl : 'add-route-modal.html',
                controller : 'AddRouteModalController',
                resolve : {
                    passId : function(){
                        return cityId;
                    }
                }
            });
        };

        $scope.handleClickUpdateRoute = function(routeId){
            var modalInstance = $modal.open({
                templateUrl : 'update-route-modal.html',
                controller : 'UpdateRouteModalController',
                resolve : {
                    passId : function(){
                        return routeId;
                    }
                }
            });
        };
    })

    .controller('UpdateRouteModalController', function ($document,$scope, $modalInstance, $http, $log,cityManager, routesManager, passId,$rootScope) {

        $scope.cities= [];
        $scope.selectedViaCities = [];
        $scope.selectedViaCity = {};

        $scope.loadFromCities = function(){
            cityManager.getCities(function(data){
                $scope.cities = data;
                $scope.route = {};

                routesManager.getRoute(passId,function(data){
                    $scope.route = data;
                    angular.forEach($scope.cities,function(fromCityName){
                        if(fromCityName.id == $scope.route.fromCity){
                           $scope.routesFromCityName = fromCityName.name;
                        }
                    });
                    angular.forEach($scope.cities,function(toCityName){
                        if(toCityName.id == $scope.route.toCity){
                            $scope.routesToCityName = toCityName.name;
                        }
                    });
                    angular.forEach($scope.route.viaCities,function(existingCityId) {
                        angular.forEach($scope.cities,function(city){

                            if(existingCityId == city.id){
                                $scope.selectedViaCities.push(city);
                            }
                        });
                    });
                });
            });
        };
        $scope.loadFromCities();

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.ok = function () {
            routesManager.updateRoute($scope.route,function(data) {
               $modalInstance.close(data);
            });
        };

        $scope.updateFromCity = function(item){
            $scope.routesFromCityName = item.name;
            $scope.route.fromCity= item.id;
            console.log("items:"+angular.toJson(item));
            console.log("id:"+$scope.route.fromCity);
        };

        $scope.updateToCity = function(item){
            $scope.routesToCityName = item.name;
            $scope.route.toCity= item.id;
            console.log("items:"+angular.toJson(item));
            console.log("id:"+$scope.route.toCity);
        };

        $scope.addTheCity = function(selectedCity){
            if($scope.route.viaCities.indexOf(selectedCity) == -1){
                $scope.route.viaCities.push(selectedCity);
                cityManager.getCity(selectedCity,function(data){
                    $scope.selectedViaCities.push(data);
                });
            }else{
                console.log("city already added");
            }
        };

        $scope.deleteCityFromList = function(cityId){
            var index = $scope.route.viaCities.indexOf(cityId);
            if(index != -1 ){
                $scope.route.viaCities.splice(index,1);
                $scope.selectedViaCities.splice(index,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };

        $scope.moveCallback = function(event,index1,item){
            $scope.selectedViaCities.splice(index1,1);
            console.log($scope.route.viaCities.splice(index1,1));
            console.log("City moved" + angular.toJson($scope.selectedViaCities));
        };

        $scope.insertedCallback = function(index,item){
            $scope.route.viaCities.splice(index,0,item.id);
            console.log("index2:"+index);
            console.log("item2:"+item.id);
            return true;
        };
    })

    .controller('AddRouteModalController', function ($scope, $modalInstance, $http, $log, cityManager,routesManager) {

        $scope.fromCitySelected = {};
        $scope.route = {
            name : null,
            viaCities : [],
            fromCity : null,
            toCity : null
        };
        $scope.cities = [];
        $scope.loadFromToCities = function(){
            cityManager.getCities(function(data){
                $scope.cities = data;
            });
        }();

        $scope.selectFromCity = function(item){
            $scope.fromCityName = item.name;
            $scope.route.fromCity= item.id;
            console.log("items:"+angular.toJson(item));
            console.log("id:"+$scope.route.fromCity);
        };

        $scope.selectToCity = function(item, model, label, event){
            $scope.toCityId = item.name;
            $scope.route.toCity= item.id;
        };

        $scope.selectedViaCityId = {};
        $scope.citiesFromService = [];

        $scope.addCityToViaCities = function(viaCityId){
            if($scope.route.viaCities.indexOf(viaCityId)== -1) {
                $scope.route.viaCities.push(viaCityId);
                cityManager.getCity(viaCityId, function (data) {
                    $scope.citiesFromService.push(data);
                });
            }else{
                console.log("city already exist");
            }
        };

        $scope.deleteViaCityFromList = function(cityId){
            var index = $scope.route.viaCities.indexOf(cityId);
            if(index != -1 ){
                $scope.route.viaCities.splice(cityId,1);
                $scope.citiesFromService.splice(index,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };

        $scope.moveCallback = function(event,index1,item){
            console.log("index:"+index1);
            console.log("Before slicing..    " +$scope.route.viaCities);
            $scope.citiesFromService.splice(index1,1);
            console.log($scope.route.viaCities.splice(index1,1));
            console.log("event:"+event);
            console.log("City moved" + angular.toJson($scope.citiesFromService));
            console.log("City id    " +$scope.route.viaCities);
        };

        $scope.insertedCallback = function(index,item){
            $scope.route.viaCities.splice(index,0,item.id);
            console.log("index2:"+index);
            console.log("item2:"+item.id);
            return true;
        };

        $scope.ok = function () {
            if ($scope.route.name === null || $scope.route.toCity === null  ) {
                $log.error("nothing was added.");
                $modalInstance.close(null);
            }
            console.log(angular.toJson($scope.fromCitySelected));
            routesManager.createRoute($scope.route, function (data) {
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

        };
        $scope.routesFromManager=[];
        $scope.onMouseLeave = function(routeName){
            routesManager.getRoutes(function(data){
                $scope.routesFromManager=data;
            });
            angular.forEach($scope.routesFromManager,function(route){
                if(route.name==routeName){
                    swal("oops!","Route already exist","error");
                }
            })
        }
    });



