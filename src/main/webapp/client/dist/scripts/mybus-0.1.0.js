/*! mybus - version:0.1.0 - 2016-02-07 * Copyright (c) 2016 Simha;*/
'use strict';

/* App Module */

var myBus = angular.module('myBus', [
  'ngRoute',
  'ngAnimate',
  'ngTouch',
  'ngTable',
  'ui.bootstrap',
  'unsavedChanges',
  'angularSpinner',
  'myBus.routesModules',
  'myBus.citiesModules',
  'myBus.expensesModules',
  'myBus.boardingPointModule',
  'myBus.personModules',
  'myBus.layoutModules',
  'myBus.layoutEditModules',
  'myBus.homeModule',
  'myBus.busDetailModule'
]);

myBus.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
        when('/dashboard', {
          templateUrl: 'partials/home.tpl.html',
          controller: 'HomeController'
        }).
        when('/cities', {
          templateUrl: 'partials/cities-list.tpl.html',
          controller: 'CitiesController'
        })
        .when('/routes', {
          templateUrl: 'partials/routes-list.tpl.html',
          controller: 'RoutesController'
        })
        .when('/persons', {
          templateUrl: 'partials/person.html',
          controller: 'PersonController'
        })
        .
        when('/states', {
          templateUrl: 'partials/states.html',
          controller: 'CitiesController'
        }).
        when('/expenses', {
          templateUrl: 'partials/payments-list.tpl.html',
          controller: 'ExpensesController'
        }).
        when('/city/:id', {
          templateUrl: 'partials/boardingpoints-list.tpl.html',
          controller: 'BoardingPointsListController'
        }).
        when('/layouts', {
          templateUrl: 'partials/buslayout.tpl.html',
          controller: 'BusLayoutController as busLayoutCtrl'
        }).
        when('/layouts/:id', {
          templateUrl: 'partials/buslayoutedit.tpl.html',
          controller: 'BusLayoutEditController as busLayoutEditCtrl'
        }).
        when('/busdetails', {
          templateUrl: 'partials/busdetails.tpl.html',
          controller: 'BusDetailsController'
        }).
        when('/users', {
          templateUrl: 'partials/users.tpl.html',
          controller: 'UsersController'
        }).
        when('/user', {
          templateUrl: 'partials/user-details.tpl.html',
          controller: 'UserEditController'
        }).
        when('/users-new', {
          templateUrl: 'partials/user-details.tpl.html',
          controller: 'UserAddController'
        }).
        when('/docs', {
          templateUrl: 'partials/api-docs.tpl.html',
          controller: 'APIDocsController'
        }).
        when('/account', {
          templateUrl: 'partials/account.tpl.html',
          controller: 'AccountController'
        })
        .otherwise({
          redirectTo: '/'
        });
  }]);

myBus.run(function ($rootScope, $location, appConfigManager, userManager) {
  appConfigManager.fetchAppSettings(function (err, cfg) {
    $rootScope.appConfigManager = appConfigManager;
  }, true);
  userManager.getCurrentUser(function (err) {
    if (!err) {
      userManager.getGroupsForCurrentUser();
    }
  });
  $rootScope.userManager = userManager;
  $rootScope.poiSearchText = '';
  $rootScope.searchPOIs = function () {
    $location.url('/businesses?name=' + $rootScope.poiSearchText);
  };
  //categoriesManager.reloadCategoryData();
  //classificationsManager.reloadClassificationData();
  //citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
});



/**
 * Created by skandula on 5/19/15.
 */

'use strict';
/*global angular,_*/

angular.module('myBus.boardingPointModule', [])
            .controller('BoardingPointsListController', function ($scope, $http, $log, ngTableParams, $routeParams,$modal, $filter, cityManager) {
            $log.debug('BoardingPointsListController');
            $scope.headline = "Boarding Points";
            $scope.cityId = $routeParams.id;
            $scope.currentPageOfBoardingPoints = [];
                var loadTableData = function (tableParams, $defer) {
                var data=cityManager.getCity($scope.cityId, function(data) {
                    $scope.city = data;
                    console.log("found city"+angular.toJson($scope.city));
                    var orderedData = tableParams.sorting() ? $filter('orderBy')($scope.city.boardingPoints, tableParams.orderBy()) : $scope.city.boardingPoints;
                    tableParams.total($scope.city.boardingPoints.length);
                    if (angular.isDefined($defer)) {
                        $defer.resolve(orderedData);
                    }
                    $scope.currentPageOfBoardingPoints = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                });

            };
            $scope.boardingPointContentTableParams = new ngTableParams({
                page: 1,
                count: 25,
                sorting: {
                    state: 'asc',
                    name: 'asc'
                }
            }, {
                total: $scope.currentPageOfBoardingPoints.length,
                getData: function ($defer, params) {
                loadTableData(params);
                }
            });
        //-------------------------------------------------------------------------------------------
         $scope.handleDeleteButtonClicked = function(id) {
                var modalInstance = $modal.open({
                templateUrl: 'delete-city-state-modal.html',
                controller: 'DeleteCityStateController',
                resolve: {
                    deleteCityId: function () {
                        return id;
                    }
                }
            })
        };
        $scope.handleClickAddBoardingPoint = function () {
            var modalInstance = $modal.open({
                templateUrl: 'add-boardingpoint-to-city-state-modal.html',
                controller: 'AddBoardingPointController',
                resolve: {
                    cityId:function(){
                        return $scope.cityId;
                    }
                }
            })
        },
        $scope. updateBpOnClick = function(id) {
                var modalInstance = $modal.open({
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
            console.log("delete btn clicked "+ id);
            var modalInstance = $modal.open({
                templateUrl: 'delete-boardingPt.html',
                controller: 'DeleteBpController',
                resolve:{
                    cityId:function() {
                        return $scope.cityId;
                    },
                    BpId:function(){
                        return id;
                    }
                }

            })
        }

    })
    // ========================== Modal - Boarding point controller =================================
    .controller('AddBoardingPointController', function ($scope, $modalInstance, $http,$log,$route,cityId, cityManager) {
        $scope.boardingPoint = {};
        $scope.ok = function () {
            if ($scope.boardingPoint.name === null || $scope.boardingPoint.contact === null || $scope.boardingPoint.landmark === null) {
                $log.error("null name or contact or landmark.  nothing was added.");
                $modalInstance.close(null);
            }
            cityManager. createBordingPoint(cityId,$scope.boardingPoint, function(data){
                $route.reload();
                $modalInstance.close(data);
            });
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.boardingPoint.name || '') !== '' &&
                ($scope.boardingPoint.landmark || '') !== '' &&
                ($scope.boardingPoint.contact || '') !== '';
        };
    })

    //======================Model - DeleteCityStateController=============================================
    .controller('DeleteCityStateController', function ($scope, $modalInstance, $http, $log,$route, deleteCityId, cityManager) {
        $scope.id = deleteCityId;
        $scope.ok = function (id) {
        cityManager.deleteCity(id,function(data) {
            $modalInstance.close(data);
        })
    }
        $scope.cancel = function () {
         $modalInstance.dismiss('cancel');
     };
        $scope.isInputValid = function () {
         return ($scope.person.name || '') !== '' &&
                ($scope.person.age || '') !== '' &&
                ($scope.person.phone || '') !== '';
     };
})
//======================Model - updateBpController=============================================
    .controller('UpdateBoardingPtController', function ($scope, $modalInstance, $http,$route,BpId,cityId, $log,cityManager) {
        $scope.setBpIntoView = function(cityId,BpId){
            cityManager.getBp(cityId,BpId,function(data){
                    $scope.boardingPoint=data;
            })
        };
        $scope.setBpIntoView (cityId,BpId);
        $scope.ok = function (BpId) {
                    cityManager.updateBp(cityId,$scope.boardingPoint, function(data) {
                        $route.reload();
                        $modalInstance.close(data);
                    })
        }
        $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
        };
    })
//======================Model - DeleteBpController=============================================
    .controller('DeleteBpController', function ($scope, $modalInstance,cityId,$route, $http,BpId,$log, cityManager) {
        $scope.ok = function () {
            cityManager.deleteBp(cityId,BpId ,function(data) {
                 $route.reload();
                $modalInstance.close(data);
            })
        }
        $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
        };
    });


"use strict";
/*global angular, _*/

angular.module('myBus.busDetailModule', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusDetailsController', function ($scope, $http, $log, ngTableParams, $modal, $filter, expensesManager, $location) {
    $log.debug('BusDetailsController loading');
    var busDetailsCtrl = this;

    $scope.busdetails = JSON.parse('{"buses":[{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"},{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"},{"type":"AC_SEMI_SLEEPER","route":"HYD - ONG","seats":42,"layout":"SEMI_SLEEPER"}]}');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Bus Details";

    $scope.goToBusLayout = function (name) {
        $location.url('/layouts/' + name);
    };

    return busDetailsCtrl;

  })

//"use strict";
/*global angular, _*/

angular.module('myBus.layoutModules', ['ngTable', 'ui.bootstrap'])


    // ==================================================================================================================
    // ====================================    BusLayoutController   ================================================
    // ==================================================================================================================

    .controller('BusLayoutController', function ($scope, $http, $log, ngTableParams, $modal, $filter, busLayoutManager, $location) {
        $log.debug('BusLayoutController loading');

        var busLayoutCtrl = this;

        busLayoutCtrl.currentPageOfLayouts = [];


        busLayoutCtrl.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        busLayoutCtrl.headline = "Layouts";

        busLayoutCtrl.goToBusLayout = function (busId) {
            $location.url('/layouts/' + busId);
        };

        busLayoutCtrl.deleteLayout = function (layout) {
            busLayoutManager.deleteLayout(layout.id);
            $location.url('/layouts');
        };

        var loadTableData = function (tableParams, $defer) {
            var data = busLayoutManager.getAllLayouts();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            busLayoutCtrl.layouts = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            busLayoutCtrl.currentPageOfLayouts = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('layoutsInitComplete', function (e, value) {
            loadTableData(busLayoutCtrl.layoutContentTableParams);
        });

        $scope.$on('layoutsDeleteComplete', function (e, value) {
            loadTableData(busLayoutCtrl.layoutContentTableParams);
        });

        busLayoutManager.fetchAllBusLayouts();

        busLayoutCtrl.addNewBusLayout = function(){
            $location.url('/layouts/' + 'create');
        }

        busLayoutCtrl.layoutContentTableParams = new ngTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: busLayoutCtrl.currentPageOfLayouts.length,
            getData: function ($defer, params) {
                $scope.$on('layoutsInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        return busLayoutCtrl;

})
"use strict";
/*global angular, _*/

angular.module('myBus.layoutEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusLayoutEditController', function ($rootScope, $scope, $http, $log, ngTableParams, $modal, $filter, busLayoutManager, $routeParams, $location, $cacheFactory) {
        $log.debug('BusLayoutController loading');
        var busLayoutEditCtrl = this;

        busLayoutEditCtrl.valid = false;

        busLayoutEditCtrl.totalSeats = 0;

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layout Details";

        busLayoutEditCtrl.busLayout = {
            rows : null,
            type: null,
            name : null,
            upper : null,
            lower : null,
            upperHeader : '',
            lowerHeader : ''
        };

        busLayoutEditCtrl.layouts  = [
              {id: 'SEMI_SLEEPER', name: 'SEMI_SLEEPER'},
              {id: 'AC_SEMI_SLEEPER', name: 'AC_SEMI_SLEEPER'},
              {id: 'SLEEPER', name: 'SLEEPER'}
            ];

       busLayoutEditCtrl.rows = [
            {id: '10', name: '10'},
            {id: '11', name: '11'},
            {id: '12', name: '12'},
            {id: '13', name: '13'},
            {id: '14', name: '14'},
            {id: '15', name: '15'},
            {id: '16', name: '16'},
            {id: '17', name: '17'}

           ];

      busLayoutEditCtrl.seats = [
             {id: 1, name: '1'},
              {id: 2, name: '2'},
              {id: 3, name: '3'},
              {id: 4, name: '4'}
           ];

      busLayoutEditCtrl.middleRows = [
             {id: 2, name: 'Middle Row After First Seat'},
             {id: 3, name: 'Middle Row After Second Seat'},
             {id: 4, name: 'Middle Row After Third Seat'}
           ];

      busLayoutEditCtrl.middleRowSeats = [
             {id: true, name: 'Yes'},
             {id: false, name: 'No'}
           ];


      var layOutId = $routeParams.id;

      if(layOutId !== ''){
        var cache = $cacheFactory.get($rootScope.id);
        if(cache){
            busLayoutEditCtrl.busLayout = cache.get(layOutId);
        }
        if(busLayoutEditCtrl.busLayout && busLayoutEditCtrl.busLayout.id !== ''){
            busLayoutEditCtrl.name = busLayoutEditCtrl.busLayout.name;
            busLayoutEditCtrl.type = $filter('filter')(busLayoutEditCtrl.layouts, {id: busLayoutEditCtrl.busLayout.type})[0];
            busLayoutEditCtrl.seatsPerRow = $filter('filter')(busLayoutEditCtrl.seats, {id: busLayoutEditCtrl.busLayout.seatsPerRow})[0];
            busLayoutEditCtrl.totalRows = $filter('filter')(busLayoutEditCtrl.rows, {id: busLayoutEditCtrl.busLayout.totalRows})[0];
            busLayoutEditCtrl.middleRow = $filter('filter')(busLayoutEditCtrl.middleRows, {id: busLayoutEditCtrl.busLayout.middleRowPosition})[0];
            busLayoutEditCtrl.middleRowSeat = $filter('filter')(busLayoutEditCtrl.middleRowSeats, {id: busLayoutEditCtrl.busLayout.middleRowLastSeat})[0];
            busLayoutEditCtrl.active = busLayoutEditCtrl.busLayout.active;
            var rows = [];
            angular.forEach(busLayoutEditCtrl.busLayout.rows, function(row, key) {
                var seats = [];
                angular.forEach(row, function(busseats, key) {
                    angular.forEach(busseats, function(busseat, key) {
                        if(busseat.number){
                            busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                        }
                        seats.push({number : busseat.number, [busseat.number]: busseat.number});
                    });
                });
                rows.push({seats: seats});
            });
            busLayoutEditCtrl.busLayout.rows = rows;
            busLayoutEditCtrl.valid = true;
        }else{
            busLayoutEditCtrl.busLayout = {
                rows : null,
                type: null,
                name : null,
                upper : null,
                lower : null,
                upperHeader : '',
                lowerHeader : ''
            }
        }
      }

        var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};

        function getName(id){
            return $filter('filter')(seatNames.seats, {id: id })[0];
        }

        busLayoutEditCtrl.getSeatName = function(seat){
            return seat.number;
        }

        function initialize(){
        	busLayoutEditCtrl.busLayout.name = null;
            busLayoutEditCtrl.busLayout.type = null;
            busLayoutEditCtrl.busLayout.rows = null;
            busLayoutEditCtrl.busLayout.upper = null;
            busLayoutEditCtrl.busLayout.lower = null;
            busLayoutEditCtrl.busLayout.isBig = false;
            busLayoutEditCtrl.busLayout.upperHeader = '';
            busLayoutEditCtrl.busLayout.lowerHeader = '';
            busLayoutEditCtrl.totalSeats = 0;
        }

        busLayoutEditCtrl.doLayout = function (){
            initialize();
            // layout css class
            var sleeper = false;
            if(busLayoutEditCtrl.type.id === 'SLEEPER'){
                sleeper = true;
                busLayoutEditCtrl.layoutCls = 'seat';
            }else{
                busLayoutEditCtrl.layoutCls = 'seat';
            }

            // building the rows and columns

            if(sleeper && busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                for(var k = 0; k < 2; k++){
                    if(k===0){
                       busLayoutEditCtrl.busLayout.upper = getSeats();
                       busLayoutEditCtrl.busLayout.upperHeader = 'Upper Deck Layout';
                    }else{
                       busLayoutEditCtrl.busLayout.lower = getSeats();
                       busLayoutEditCtrl.busLayout.lowerHeader = 'Lower Deck Layout';
                    }
                }
            }else if(busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                busLayoutEditCtrl.busLayout.rows = getSeats();
            }

        };

        function getSeats(middleseatpos, middleseat){
        //busLayoutEditCtrl.middleRow.id, busLayoutEditCtrl.middleRowSeat.id
            var rows = [], middleseatpos = 0, middleseat = 0;
            if(busLayoutEditCtrl.middleRow){
                middleseatpos = busLayoutEditCtrl.middleRow.id;
            }
            if(busLayoutEditCtrl.middleRowSeat){
                middleseat = busLayoutEditCtrl.middleRowSeat.id;
            }
            var cols = busLayoutEditCtrl.seatsPerRow.id;
            if(middleseatpos > 0){
                cols = parseInt(cols) +1;
            }

            if (cols > 4){
                busLayoutEditCtrl.busLayout.isBig = true;
            }

            for (var i = 1; i <= cols; i++){
                var seats = [];
                if(i === parseInt(middleseatpos)){
                    for (var j = 1; j <= busLayoutEditCtrl.totalRows.id; j++){
                        var number = getName(j).name+''+i;
                        if(middleseat === 1 && j === busLayoutEditCtrl.totalRows.id){
                            busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                            seats.push({number : number, [number]: number});
                        }else{
                            seats.push({number : null, [number]: null});
                        }
                    }
                }else{
                    for (var j = 1; j <= busLayoutEditCtrl.totalRows.id; j++){
                        var number = getName(j).name+''+i;
                        busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                        seats.push({number : number, [number]: number});
                    }
                }
                rows.push({seats :seats})
            }
            return rows;
        }

        busLayoutEditCtrl.goToLayouts = function(){
            $location.url('/layouts');
        };

        $scope.$on('layoutsCreateComplete', function (e, value) {
             busLayoutEditCtrl.goToLayouts();
        });

        busLayoutEditCtrl.saveLayout = function (){
            var rows = [];
            angular.forEach(busLayoutEditCtrl.busLayout.rows, function(row, key) {
                var seats = [];
                angular.forEach(row, function(busseats, key) {
                    angular.forEach(busseats, function(busseat, key) {
                        var seat = {
                            number : null,
                            displayName : null
                        };
                        seat.number = busseat.number;
                        seat.displayName = busseat[seat.number];
                        seats.push(seat);
                    });
                });
                rows.push({seats: seats});
            });
            var layoutToSave = {
                name : busLayoutEditCtrl.name,
                type: busLayoutEditCtrl.type.id,
                totalSeats : busLayoutEditCtrl.totalSeats,
                seatsPerRow : busLayoutEditCtrl.seatsPerRow.id,
                totalRows : busLayoutEditCtrl.totalRows.id,
                middleRowPosition : busLayoutEditCtrl.middleRow.id,
                middleRowLastSeat : busLayoutEditCtrl.middleRowSeat.id,
                rows: rows,
                active : busLayoutEditCtrl.active,
                id : busLayoutEditCtrl.busLayout.id
            };

            if(layoutToSave.id && layoutToSave.id !== 'create'){
                busLayoutManager.updateLayout(layoutToSave);
            }else{
                busLayoutManager.createLayout(layoutToSave);
            }
        };

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layouts";

        return busLayoutEditCtrl;

  })

"use strict";
/*global angular, _*/

angular.module('myBus.businessModule', ['ngAnimate', 'ngTouch', 'ngTable', 'angularFileUpload', 'ui.bootstrap'])


  //
  // ============================= Modal - Conditions  ===============================
  //
  .controller('BusinessesConditionsModalController', function ($scope, business, availableValues, $modalInstance, $http, $log) {
    $log.debug("modal popup -- business: " + angular.toJson(business));
    $scope.business = business;
    $scope.availableValues = availableValues;

    var createCompositeCondition = function (conditionObject, existingCondition) {
      if (!conditionObject) {
        return null;
      }
      var providerPrice, providerDescription, isPublished = null, composite = null;
      if (existingCondition) {
        providerPrice = existingCondition.providerPrice;
        providerDescription = existingCondition.providerDescription;
        isPublished = existingCondition.published;
      }
      composite = _.extend(conditionObject, {});
      composite.providerPrice = providerPrice;
      composite.providerDescription = providerDescription;
      composite.published = !!isPublished;
      return composite;
    };

    var createCompositeValuesList = function (business, allConditions) {
      var composites = [];
      _.each(allConditions, function (conditionObject) {
        var existingCondition, composite;
        if (business.conditionData) {
          existingCondition = _.find(business.conditionData, function (value) {
            return value.id === conditionObject.id;
          });
        }
        composite = createCompositeCondition(conditionObject, existingCondition);
        composites.push(composite);
      });
      $log.debug("composites (pre-sort) is: " + angular.toJson(composites));
      composites.sort(function (a, b) {
        return a > b;
      });
      return composites;
    };

    $scope.compositeDataModel = createCompositeValuesList($scope.business, $scope.availableValues);

    $scope.ok = function () {
      $log.debug("$scope.compositeDataModel is : " + angular.toJson($scope.compositeDataModel));
      if (!angular.isDefined($scope.business.id) || $scope.business.id === null) {
        $log.debug("business is not saved (no id), so dismissing dialog without saving to db.");
        $modalInstance.close($scope.compositeDataModel);
      } else {
        $http.post('/api/v1/businesses/' + $scope.business.id + '/conditions', {conditionData: $scope.compositeDataModel})
          .success(function (data) {
            $log.debug("successfully updated condition data.  " + angular.toJson(data));
            $modalInstance.close($scope.compositeDataModel);
          }).error(function (err) {
            $log.error("error updating condition data.  " + angular.toJson(err));
          });
      }
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  })






  //
  // ============================= Modal - Procedures  ===============================
  //
  .controller('BusinessesProceduresModalController', function ($scope, business, availableValues, $modalInstance, $http, $log) {
    $log.debug("modal popup -- business: " + angular.toJson(business));
    $scope.business = business;
    $scope.availableValues = availableValues;

    var createCompositeProcedure = function (procedureObject, existingProcedure) {
      if (!procedureObject) {
        return null;
      }
      var providerPrice, providerDescription, isPublished = null, composite = null;
      if (existingProcedure) {
        providerPrice = existingProcedure.providerPrice;
        providerDescription = existingProcedure.providerDescription;
        isPublished = existingProcedure.published;
      }
      composite = _.extend(procedureObject, {});
      composite.providerPrice = providerPrice;
      composite.providerDescription = providerDescription;
      composite.published = !!isPublished;
      return composite;
    };

    var createCompositeValuesList = function (business, allProcedures) {
      var composites = [];
      _.each(allProcedures, function (procedureObject) {
        var existingProcedure, composite;
        if (business.procedureData) {
          existingProcedure = _.find(business.procedureData, function (value) {
            return value.id === procedureObject.id;
          });
        }
        composite = createCompositeProcedure(procedureObject, existingProcedure);
        composites.push(composite);
      });
      $log.debug("composites (pre-sort) is: " + angular.toJson(composites));
      composites.sort(function (a, b) {
        return a > b;
      });
      return composites;
    };

    $scope.compositeDataModel = createCompositeValuesList($scope.business, $scope.availableValues);

    $scope.ok = function () {
      $log.debug("$scope.compositeDataModel is : " + angular.toJson($scope.compositeDataModel));
      if (!angular.isDefined($scope.business.id) || $scope.business.id === null) {
        $log.debug("business is not saved (no id), so dismissing dialog without saving to db.");
        $modalInstance.close($scope.compositeDataModel);
      } else {
        $http.post('/api/v1/businesses/' + $scope.business.id + '/procedures', {procedureData: $scope.compositeDataModel})
          .success(function (data) {
            $log.debug("successfully updated procedure data.  " + angular.toJson(data));
            $modalInstance.close($scope.compositeDataModel);
          }).error(function (err) {
            $log.error("error updating procedure data.  " + angular.toJson(err));
          });
      }
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  })






  //
  // ============================= Modal - NeighborhoodGeo Selection ===============================
  //
  .controller('BusinessesNeighborhoodGeoModalController', function ($scope, business, availableNeighborhoods, $modalInstance, $http, $log, citiesAndNeighborhoodsManager) {
    $log.debug("modal popup -- business: " + angular.toJson(business));
    $scope.business = business;

    var buildNeighborhoodsTree = function (neighArray) {
      if (!neighArray) {
        return neighArray;
      }
      angular.forEach(neighArray, function (val) {
        if (val) {
          delete val.geometry;
          val.selected = (business && !_.isEmpty(business.neighborhoodGeoIds) && _.contains(business.neighborhoodGeoIds, val.id));
          val.children = citiesAndNeighborhoodsManager.getChildrenByParentId(val.id);
          buildNeighborhoodsTree(val.children);
        }
      });
      return neighArray;
    };

    $scope.availableNeighborhoods = buildNeighborhoodsTree(availableNeighborhoods);

    $scope.selectedNeighborhoodGeoIds = [];
    $scope.selectedNeighborhoodNames = [];

    $scope.ok = function () {
      $scope.selectedNeighborhoodGeoIds = [];
      $scope.selectedNeighborhoodNames = [];
      var requestParams,
        collectSelectedIds = function (neighborhood) {
          if (neighborhood && neighborhood.selected) {
            $scope.selectedNeighborhoodGeoIds.push(neighborhood.id);
            $scope.selectedNeighborhoodNames.push(neighborhood.name);
          }
          if (neighborhood && !_.isEmpty(neighborhood.children)) {
            angular.forEach(neighborhood.children, collectSelectedIds);
          }
        };
      angular.forEach($scope.availableNeighborhoods, collectSelectedIds);

      requestParams = {
        neighborhoodGeoIds: $scope.selectedNeighborhoodGeoIds,
        neighborhoods: $scope.selectedNeighborhoodNames.sort(),
        neighborhoodsDisplayValue: $scope.selectedNeighborhoodNames.sort().join(', ')
      };
      if (!angular.isDefined($scope.business.id) || $scope.business.id === null) {
        $log.debug("business is not saved (no id), so dismissing dialog without saving to db.");
        $modalInstance.close(requestParams);
      } else {
        $http.put('/api/v1/businesses/' + $scope.business.id + '/neighborhoods', requestParams)
          .success(function (data) {
            $log.debug("successfully updated neighborhoods.  " + angular.toJson(data));
            $modalInstance.close(requestParams);
          }).error(function (err) {
            var errorMsg = "error updating neighborhoods.  " + angular.toJson(err);
            $log.error(errorMsg);
            alert(errorMsg);
          });
      }
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };


    var deselectAllInNeighborhoodTree = function (neighborhood) {
      if (neighborhood) {
        neighborhood.selected = false;
        if (!_.isEmpty(neighborhood.children)) {
          angular.forEach(neighborhood.children, deselectAllInNeighborhoodTree);
        }
      }
    };

    $scope.handleNeighborhoodSelectionChanged = function (neighborhoodGeo) {
      if (neighborhoodGeo && !neighborhoodGeo.selected && !_.isEmpty(neighborhoodGeo.children)) {
        deselectAllInNeighborhoodTree(neighborhoodGeo);
      }
    };

  })



  //
  // ============================= Modal - Generic Classification Selection ===============================
  //
  .controller('BusinessesGenericClassificationModalController', function ($scope, business, availableValues, businessPropertyName, numOfColumns, classificationDisplayName, $modalInstance, $log) {
    $log.debug("modal popup -- business: " + angular.toJson(business) + "\navailableValues: " + angular.toJson(availableValues) + "\nbusinessPropertyName: " + businessPropertyName);
    $scope.business = business;
    $scope.originalValueList = business[businessPropertyName] || [];
    var desiredNumberOfColumns = numOfColumns;
    $scope.numberOfColumnsOfValues = (availableValues || []).length >= desiredNumberOfColumns ? desiredNumberOfColumns : 1;
    $scope.classificationDisplayName = classificationDisplayName;

    $scope.getNumberArray = function (n) {
      return _.range(n);
    };

    function columnReorder(list, numOfCols) {
      var i, j,
        columnArrays = [],
        numberOfRows,
        rowsArrays = [],
        columnValues = [];

      numberOfRows = Math.ceil(list.length / numOfCols);
      for (i = 0; i < numOfCols; i += 1) {
        columnArrays[i] = list.slice(i * numberOfRows, (i + 1) * numberOfRows);
      }

      for (i = 0; i < numberOfRows; i += 1) {
        columnValues = [];
        for (j = 0; j < numOfCols; j += 1) {
          columnValues.push(columnArrays[j][i]);
        }
        rowsArrays.push(columnValues);
      }
      return rowsArrays;
    }

    $scope.availableValuesRaw = availableValues; // an array of objects, which have 'id' and 'name' fields
    $scope.availableValues = columnReorder(availableValues || [], $scope.numberOfColumnsOfValues);

    $scope.shouldBeChecked = function (value) {
      return _.contains($scope.originalValueList, value);
    };

    $scope.selectableValuesModel = {};
    angular.forEach($scope.availableValuesRaw, function (val) {
      $scope.selectableValuesModel[val.id] = $scope.shouldBeChecked(val.id);
    });

    $scope.ok = function () {
      var selectedValueNames = _.compact(_.map($scope.selectableValuesModel, function (val, key) {
        return val ? key : null;
      }));
      $log.debug("selectedValueNames is : " + angular.toJson(selectedValueNames));
      $modalInstance.close(selectedValueNames);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  })




  //
  // ======================== Edit Business / Image Upload =============================
  //
  .controller('BusinessController', function ($scope, $rootScope, $http, $log, ngTableParams, $location, $routeParams, categoriesManager, classificationsManager, states, $upload, businessNavHelper, $modal, geocoder, infoOverlay, citiesAndNeighborhoodsManager, googlePlaces) {

    $scope.headline = "Edit Business";
    $scope.businessId = $routeParams.id;
    $log.debug("businessId from routeParams is: " + $scope.businessId);
    $scope.business = {};
    $scope.saveButtonText = 'Save Business';
    $scope.isAdd = false;

    $scope.businessHelper = businessNavHelper;

    $scope.editIndex = parseInt($routeParams.idx, 10);
    $scope.isNotPublished = false;
    $scope.isPublished = true;
    $scope.states = states;
    $scope.totalResults = businessNavHelper.completeResults.length;

    $scope.catSelectionsModel = {};
    $scope.catTypeSelectionsModel = {};
    $scope.catSubTypeSelectionsModel = {};

    $scope.cityStateNeighborhoodGeo = null;
    $scope.isListOrHierarchy = classificationsManager.isListOrHierarchy;

    categoriesManager.reloadCategoryData();
    classificationsManager.reloadClassificationData();

    $scope.fetchNeighborhoodGeoForCityState = function (city, state, callback) {
      $log.debug("fetchNeighborhoodGeoForCityState: " + city + ", " + state);
      $http.get('/api/v1/city?city=' + city + '&state=' + state)
        .success(function (data) {
          //$log.debug("fetchNeighborhoodGeoForCityState - data was: " + angular.toJson(data));
          $scope.cityStateNeighborhoodGeo = angular.isArray(data) && data.length > 0 ? data[0] : (_.isEmpty(data) ? null : data);
          if (angular.isFunction(callback)) {
            callback(null, $scope.cityStateNeighborhoodGeo);
          }
        })
        .error(function (err) {
          $scope.cityStateNeighborhoodGeo = null;
          alert("error fetching city/state neighborhoodGeo information.");
          callback(err, null);
        });
    };

    $scope.getNeighborhoodNamesArray = function () {
      if ($scope.business && $scope.business.neighborhoodGeoIds) {
        return citiesAndNeighborhoodsManager.getNamesByIds($scope.business.neighborhoodGeoIds);
      }
      return [];
    };

    $scope.reloadBusiness = function () {
      $http.get('/api/v1/businesses/' + $scope.businessId + '?includeImportedDetails=true').success(function (business) {
        $log.info("Loaded business id: " + business.id + " - " + business.name);
        if (!business.hasOwnProperty('published')) {
          business.published = true;
        }
        $scope.business = business;
        $scope.updateCategoryModelsFromBusiness();
      }).error(function (error) {
        $log.error("Failed to get business by id: " + $scope.businessId);
      });
    };

    $scope.reloadBusiness();

    $scope.isClassificationInputEnabled = function (classificationId) {
      var associatedClassificationIds = [];
      if (_.isEmpty($scope.business.categoryIds)) {
        return false;
      }
      _.each($scope.business.categoryIds, function (categoryId) {
        var acIds = ($rootScope.categoriesMap[categoryId] || {}).associatedClassificationIds || [];
        associatedClassificationIds = associatedClassificationIds.concat(acIds);
      });
      associatedClassificationIds = _.uniq(associatedClassificationIds);
      return _.contains(associatedClassificationIds, classificationId);
    };

    $scope.rawPhotos = [];
    $scope.photos = [];
    $scope.refreshPhotos = function () {
      $scope._Index = 0;
      $http.get('/api/v1/businesses/' + $scope.businessId + '/images').success(function (images) {
        $scope.rawPhotos = images;
        $scope.photos = [];
        if (images) {
          angular.forEach(images, function (img) {
            var imgDesc = img.description && img.description !== '' ? img.description : img.name;
          $scope.photos.push({src: img.url, desc: imgDesc, id: img.id, is_system_default: img.is_system_default});
          });
        }
      });
    };
    $scope.refreshPhotos();

    $scope.neighborhoodOptions = [];
    $scope.getNeighborhoodOptions = function (state, city, callback) {
      if (city && state) {
        $scope.fetchNeighborhoodGeoForCityState(city, state, function (err, data) {
          if (err) {
            if (angular.isFunction(callback)) {
              callback(err, null);
            }
          } else if (!data || !$scope.cityStateNeighborhoodGeo) {
            $log.debug("No city/state data..");
            $scope.neighborhoodOptions = [];
            callback(null, $scope.neighborhoodOptions);
          } else {
            $log.debug("$scope.cityStateNeighborhoodGeo is " + angular.toJson($scope.cityStateNeighborhoodGeo));
            citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData($scope.cityStateNeighborhoodGeo.id, true, true, function (err, data) {
              $scope.neighborhoodOptions = citiesAndNeighborhoodsManager.getChildrenByParentId($scope.cityStateNeighborhoodGeo.id);
              if (angular.isFunction(callback)) {
                callback(err, data);
              }
            });
          }
        });
      }
    };

    $scope.editNeighborhoodsForBusiness = function (size) {
      $scope.getNeighborhoodOptions($scope.business.state, $scope.business.city, function (err, data) {
        if (err) {
          $log.error("error getting neighborhood options prior to modal popup");
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-neighborhoodsGeo-modal.html',
            controller: 'BusinessesNeighborhoodGeoModalController',
            size: size,
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableNeighborhoods: function () {
                return angular.isArray($scope.neighborhoodOptions) ? $scope.neighborhoodOptions.sort() : $scope.neighborhoodOptions;
              }
            }
          });

          modalInstance.result.then(function (data) {
            $scope.business.neighborhoods = data.neighborhoods || [];
            $scope.business.neighborhoodGeoIds = data.neighborhoodGeoIds || [];
            $scope.business.neighborhoodDisplayValue = $scope.business.neighborhoods.sort().join(', ');
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };

    $scope.getConditions = function (cb) {
      $http.get('/api/v1/conditions').success(function (data) {
        $scope.conditions = data || [];
        if (angular.isFunction(cb)) {
          cb(null, $scope.conditions);
        }
      }).error(function (error) {
        var errorMsg = "Error getting conditions. " + error;
        $log.info(errorMsg);
        if (angular.isFunction(cb)) {
          cb(error, null);
        }
      });
    };

    $scope.getProcedures = function (cb) {
      $http.get('/api/v1/procedures').success(function (data) {
        $scope.procedures = data || [];
        if (angular.isFunction(cb)) {
          cb(null, $scope.procedures);
        }
      }).error(function (error) {
        var errorMsg = "Error getting procedures. " + error;
        $log.info(errorMsg);
        if (angular.isFunction(cb)) {
          cb(error, null);
        }
      });
    };


    $scope.onClickEditConditions = function () {
      $scope.getConditions(function (err, conditions) {
        if (err) {
          $log.error(err);
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-conditions-modal.html',
            controller: 'BusinessesConditionsModalController',
            size: 'lg',
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableValues: function () {
                return conditions;
              }
            }
          });

          modalInstance.result.then(function (data) {
            $log.debug("reloading business after editing conditions");
            $scope.reloadBusiness();
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };

    $scope.onClickEditProcedures = function () {
      $scope.getProcedures(function (err, procedures) {
        if (err) {
          $log.error(err);
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-procedures-modal.html',
            controller: 'BusinessesProceduresModalController',
            size: 'lg',
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableValues: function () {
                return procedures;
              }
            }
          });

          modalInstance.result.then(function (data) {
            $log.debug("reloading business after editing procedures");
            $scope.reloadBusiness();
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };

    /**
     * returns a string or array of strings containing the name(s) of the classification type(s)
     * @param classificationId the string id of the classification
     * @param classificationTypeIds a string id or an array of string ids
     */
    $scope.getNameForClassificationTypeId = function (classificationId, classificationTypeIds) {
      if (!$rootScope.classificationTypes) {
        return null;
      }
      var classTypes = $rootScope.classificationTypes[classificationId],
        classTypeObjs = [];
      if (!classificationId || !classificationTypeIds) {
        return null;
      }
      if (angular.isArray(classificationTypeIds)) {
        classTypeObjs = _.map(classificationTypeIds, function (ctId) {
          return $rootScope.classificationTypesMap[ctId];
        });
        return _.pluck(_.compact(classTypeObjs), 'name');
      }
      return classTypes[classificationTypeIds];
    };

    $scope.getClassificationTypeNames = function (classificationId, classificationTypeIds) {
      if (_.isEmpty(classificationTypeIds) || !angular.isArray(classificationTypeIds)) {
        return [];
      }
      var namesUnsorted = $scope.getNameForClassificationTypeId(classificationId, classificationTypeIds);
      return _.compact(namesUnsorted || []).sort();
    };


    $scope.onClickEditMultipleClassificationProperty = function (classificationId, businessPropertyName, numOfColumns, size) {
      classificationsManager.reloadClassificationData(function (err, data) {
        if (err) {
          $log.error("error loading classification data. " + err);
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-generic-classification-modal.html',
            controller: 'BusinessesGenericClassificationModalController',
            size: size,
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableValues: function () {
                return $rootScope.classificationTypes[classificationId];
              },
              businessPropertyName: function () {
                return businessPropertyName;
              },
              numOfColumns: function () {
                return numOfColumns;
              },
              classificationDisplayName: function () {
                return $rootScope.classificationsMap[classificationId].name;
              }
            }
          });

          modalInstance.result.then(function (data) {
            $scope.business[businessPropertyName] = data;
            $scope.businessForm.$setDirty(true);
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };

    /**
     * Saves the current business and refreshes the page.
     * @param nextIndexToEdit an OPTIONAL parameter that is the index of the
     * next business to edit, assuming that the current business is saved successfully.
     */
    $scope.saveButtonClicked = function (nextIndexToEdit) {
      $log.debug("saveButtonClicked -- nextIndexToEdit = " + nextIndexToEdit);
      $http.put('/api/v1/businesses/' + $scope.businessId, $scope.business)
        .success(function (data) {
          $log.info("saved changes to business successfully");
          infoOverlay.displayInfo("Changes saved successfully");
          if (nextIndexToEdit || ($scope.editIndex && !_.isEmpty(businessNavHelper.completeResults))) {
            businessNavHelper.editBusinessAtIndex(nextIndexToEdit || $scope.editIndex);
          } else {
            businessNavHelper.editBusinessById($scope.businessId);
          }
        })
        .error(function () {
          infoOverlay.displayInfo("error saving changes to business.");
          $log.error("error saving changes to business.");
        });
    };
    $scope.deleteButtonClicked = function () {
      $http.delete('/api/v1/businesses/' + $scope.businessId)
        .success(function (data) {
          $log.info("deleted business " + $scope.businessId);
          $location.url('/businesses');
        })
        .error(function () {
          $log.error("error deleting the business.");
        });
    };

    $scope.previousBusiness = function () {
      if ($scope.businessForm.$dirty) {
        $log.debug("previous clicked -- dirty form, saving");
        $scope.saveButtonClicked($scope.editIndex - 1);
        return;
      } else {
        $log.debug("not dirty.....");
      }
      businessNavHelper.editBusinessAtIndex($scope.editIndex - 1);
    };

    $scope.nextBusiness = function () {
      if ($scope.businessForm.$dirty) {
        $log.debug("next clicked -- dirty form, saving");
        $scope.saveButtonClicked($scope.editIndex + 1);
        return;
      } else {
        $log.debug("not dirty...");
      }
      businessNavHelper.editBusinessAtIndex($scope.editIndex + 1);
    };

    $scope.goBackToFilteredResults = function () {
      var newLocation = businessNavHelper.urlWithQueryParams.split('/').slice(4).join('/');
      $log.debug("going back to: " + newLocation);
      $location.url(newLocation);
    };

    $scope.combinedCategoryInfo = function () {
      var validCatIds = _.select($scope.business.categoryIds, function (val) {
        return angular.isDefined($rootScope.categoriesMap[val]);
      }),
        validCatTypeIds = _.select($scope.business.categoryTypeIds, function (val) {
          return angular.isDefined($rootScope.categoryTypesMap[val.substring(25)]);
        }),
        validCatSubTypeIds = _.select($scope.business.categorySubTypeIds, function (val) {
          return angular.isDefined($rootScope.categorySubTypesMap[val.substring(50)]);
        }),
        all = _.union(validCatIds, validCatTypeIds, validCatSubTypeIds).sort(),
        allFiltered = [],
        i,
        j,
        val1,
        val2,
        isSubstring;
      for (i = 0; i < all.length; i++) {
        isSubstring = false;
        val1 = all[i];
        for (j = 0; j < all.length; j++) {
          val2 = all[j];
          if (i !== j && val2.indexOf(val1) === 0) {
            isSubstring = true;
            break;
          }
        }
        if (!isSubstring) {
          allFiltered.push(val1);
        }
      }
      return allFiltered;
    };

    $scope.updateBusinessCategoriesFromTreeModel = function (id) {
      $log.debug("updateBusinessCategoriesFromTreeModel...");
      var selectedCategoryIds = [],
        selectedCategoryTypeIds = [],
        selectedCategorySubTypeIds = [];
      _.each($scope.catSelectionsModel, function (val, key) {
        if (val) {
          selectedCategoryIds.push(key);
        }
      });
      _.each($scope.catTypeSelectionsModel, function (val, key) {
        if (val) {
          selectedCategoryTypeIds.push(key);
        }
      });
      _.each($scope.catSubTypeSelectionsModel, function (val, key) {
        if (val) {
          selectedCategorySubTypeIds.push(key);
        }
      });
      if (id.length === 24) {
        $log.debug("cat value is " + $scope.catSelectionsModel[id]);
        if (!$scope.catSelectionsModel[id]) { // if category is unchecked
          _.each($scope.catTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catTypeSelectionsModel[key] = false;
            }
          });
          _.each($scope.catSubTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catSubTypeSelectionsModel[key] = false;
            }
          });
        }
      } else if (id.length === 49) {
        $log.debug("cat type value is " + $scope.catTypeSelectionsModel[id]);
        if (!$scope.catTypeSelectionsModel[id]) { // if category type is unchecked
          _.each($scope.catSubTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catSubTypeSelectionsModel[key] = false;
            }
          });
        }
      } else if (id.length === 74) {
        $log.debug("cat sub type value is " + $scope.catSubTypeSelectionsModel[id]);
      } else {
        $log.warn("unknown cat id type.  unexpected length");
      }

      $scope.business.categorySubTypeIds = [];
      _.each($scope.catSubTypeSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categorySubTypeIds.push(key);
        }
      });

      $scope.business.categoryTypeIds = [];
      _.each($scope.catTypeSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categoryTypeIds.push(key);
        }
      });

      $scope.business.categoryIds = [];
      _.each($scope.catSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categoryIds.push(key);
        }
      });
    };

    $scope.updateCategoryModelsFromBusiness = function () {
      _.each($scope.business.categoryIds, function (id) {
        $scope.catSelectionsModel[id] = true;
      });
      _.each($scope.business.categoryTypeIds, function (id) {
        $scope.catTypeSelectionsModel[id] = true;
      });
      _.each($scope.business.categorySubTypeIds, function (id) {
        $scope.catSubTypeSelectionsModel[id] = true;
      });
    };

    var gt = ' <span class="glyphicon glyphicon-chevron-right"></span> ';

    $scope.displayValueForCategory = function (categoryId) {
      return $rootScope.categoriesMap[categoryId].name;
    };

    $scope.displayValueForCategoryType = function (expandedCategoryTypeId) {
      var categoryId = expandedCategoryTypeId.substring(0, 24),
        categoryTypeId = expandedCategoryTypeId.substring(25),
        catName = $rootScope.categoriesMap[categoryId].name,
        catTypeName = $rootScope.categoryTypesMap[categoryTypeId].name;
      return catName + gt + catTypeName;
    };

    $scope.displayValueForCategorySubType = function (expandedCategorySubTypeId) {
      var categoryId = expandedCategorySubTypeId.substring(0, 24),
        categoryTypeId = expandedCategorySubTypeId.substring(25, 49),
        categorySubTypeId = expandedCategorySubTypeId.substring(50),
        catName = $rootScope.categoriesMap[categoryId].name,
        catTypeName = $rootScope.categoryTypesMap[categoryTypeId].name,
        catSubTypeName = $rootScope.categorySubTypesMap[categorySubTypeId].name;
      return catName + gt + catTypeName + gt + catSubTypeName;
    };

    $scope.displayValueForCategoryInfo = function (expandedId) {
      if (expandedId) {
        if (expandedId.length === 24) {
          return $scope.displayValueForCategory(expandedId);
        } else if (expandedId.length === 49) {
          return $scope.displayValueForCategoryType(expandedId);
        } else if (expandedId.length === 74) {
          return $scope.displayValueForCategorySubType(expandedId);
        }
      }
      return '';
    };


    $scope.codeAddress = function () {
      $log.debug("geocoding address....");
      var b = $scope.business,
        addressQuery = ((b.addr1 || '') + ' ' + (b.city || '') + ' ' + (b.state || '') + ' ' + (b.zip || '')).trim();
      geocoder.codeAddress(addressQuery, function (err, data) {
        if (err) {
          $log.debug("error geocoding. " + err);
        } else {
          $log.debug("geocode result is: " + angular.toJson(data));
          $scope.business.addr1 = data.streetNumber + ' ' + data.street;
          $scope.business.city = data.city || $scope.business.city;
          $scope.business.state = data.state || $scope.business.state;
          $scope.business.zip = data.zip || $scope.business.zip;
          $scope.business.lat = data.lat;
          $scope.business.long = data.long;
          $scope.lookupNeighborhoodOfBusinessByLatLong();
          $scope.$apply();
        }

        // next, lookup info in google places
        if ($scope.business.place_id) {
          googlePlaces.getDetailsForPlace($scope.business.place_id, function (err, details) {
            if (err) {
              $log.error("Error fetching details from google places.  " + err);
              return;
            }
            $log.debug("Place details successfully retrieved for place_id " + $scope.business.place_id + "\n" + angular.toJson(details));
            $log.debug("rating is " + details.rating);
            $scope.business.imported_data = $scope.business.imported_data || {};
            $scope.business.imported_data.google_places = details;
            $scope.business.rating = details.rating;
            $scope.business.rating_google = details.rating;
            if (details.geometry && details.geometry.location) {
              $scope.business.lat = details.geometry.location.k;
              $scope.business.long = details.geometry.location.B || details.geometry.location.D;
            }
            $scope.$apply();
          });
        }

      });
    };


    $scope.lookupNeighborhoodOfBusinessByLatLong = function () {
      geocoder.getNeighborhoodsForLonLat($scope.business.long, $scope.business.lat, function (err, data) {
        if (err) {
          $log.debug("Unable to determine neighborhood for this business. " + angular.toJson(err));
        } else {
          if (_.isEmpty(data)) {
            $log.debug("no neighborhoods were found at lon/lat " + $scope.business.long + ', ' + $scope.business.lat);
          } else {
            $log.debug("neighborhoods were found! \n" + angular.toJson(data));
            $scope.business.neighborhoods = [data[0].name];
            $scope.business.neighborhoodGeoIds = geocoder.xformToNeighborhoodGeoIds(data);
          }
        }
      });
    };

    $scope.newImage = {
      businessId: $scope.businessId,
      name: '',
      description: ''
    };

    $scope.isUploadInProgress = false;
    $scope.uploadAlerts = [];

    $scope.closeUploadAlert = function (index) {
      $scope.uploadAlerts.splice(index, 1);
    };

    // https://github.com/danialfarid/angular-file-upload
    $scope.onFileSelect = function ($files) {
      $scope.isUploadInProgress = true;
      //$files: an array of files selected, each file has name, size, and type.
      for (var i = 0; i < $files.length; i++) {
        var file = $files[i];
        $scope.uploadAlerts.push({type: 'info', msg: 'Beginning to upload file ' + file.name + ' at ' + (new Date())});
        $scope.upload = $upload.upload({
          url: '/api/v1/businesses/' + $scope.businessId + '/images', //'server/upload/url', //upload.php script, node.js route, or servlet url
          method: 'POST',
          // headers: {'header-key': 'header-value'},
          // withCredentials: true,
          data: {myObj: $scope.newImage},
          file: file // or list of files: $files for html5 only
          /* set the file formData name ('Content-Desposition'). Default is 'file' */
          //fileFormDataName: myFile, //or a list of names for multiple files (html5).
          /* customize how data is added to formData. See #40#issuecomment-28612000 for sample code */
          //formDataAppender: function(formData, key, val){}
        }).progress(function (evt) {
          $log.debug('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
        }).success(function (data, status, headers, config) {
          // file is uploaded successfully
          $scope.isUploadInProgress = false;
          $log.debug(data);
          var uploadedFilename = '';
          if (angular.isArray(data) && data.length > 0) {
            uploadedFilename = data[0].name;
          } else if (data.hasOwnProperty('name')) {
            uploadedFilename = data.name;
          }
          $scope.uploadAlerts.push({type: 'success', msg: 'File ' + uploadedFilename + ' uploaded successfully at ' + (new Date())});
          $scope.refreshPhotos();
        }).error(function (err) {
          $scope.isUploadInProgress = false;
          $scope.uploadAlerts.push({type: 'error', msg: 'Error uploading file at ' + new Date()});
          $log.error("error uploading: " + err);
        });
      }
    };


    // ---------------- photo slider stuff -------------------

    // initial image index
    $scope._Index = 0;

    // if a current image is the same as requested image
    $scope.isActive = function (index) {
      return $scope._Index === index;
    };

    // show prev image
    $scope.showPrev = function () {
      $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.photos.length - 1;
    };

    // show next image
    $scope.showNext = function () {
      $scope._Index = ($scope._Index < $scope.photos.length - 1) ? ++$scope._Index : 0;
    };

    // show a certain image
    $scope.showPhoto = function (index) {
      $scope._Index = index;
    };

  })


//
// ============================= Business Resolve Controller===================================
//
.controller('BusinessResolveController', function($scope, $rootScope, $http, $log, $filter, ngTableParams, $location, $routeParams, appUtils, businessNavHelper, geocoder) {
  $scope.businessId = $routeParams.id;
  $log.debug("businessId from routeParams is: " + $scope.businessId);
  $scope.business = {};
  $scope.yelpResponse = {};
  $scope.foursquareResponse = {};
  $scope.headline = "Resolve Business" ;
  
  $http.get('/api/v1/resolvenBusiness/' + $scope.businessId + '?includeImportedDetails=true').success(function (response) {
      $log.info("Loaded business id: " + response.business.id + " - " + response.business.name);
      $scope.headline = "Resolve Business:"+response.business.name ;
      
      $scope.business = response.business;
      $scope.business.createdAt = response.business.createdAt.year+"-"+response.business.createdAt.monthOfYear+"-"+response.business.createdAt.dayOfMonth;
      $scope.business.updatedAt = response.business.updatedAt.year+"-"+response.business.updatedAt.monthOfYear+"-"+response.business.updatedAt.dayOfMonth;
      $scope.resolved = response.resolved;
      $scope.factualId = response.factualId;
      $scope.yelpResponse = response.yelp;
      $scope.foursquareResponse = response.foursquare;      
    }).error(function (error) {
      $log.error("Failed to get business by id: " + $scope.businessId);
    });
  
})


  //
  // ============================= Add ========================================
  //
  .controller('BusinessAddController', function($scope, $rootScope, $http, $log, ngTableParams, $location, $routeParams, categoriesManager, classificationsManager, states, $upload, businessNavHelper, $modal, geocoder, infoOverlay, citiesAndNeighborhoodsManager) {
    $scope.headline = "Add New Business";
    $scope.saveButtonText = 'Save Business';
    $scope.isAdd = true;
    $scope.business = {published: false, categoryIds: [], categoryTypeIds: [], categorySubTypeIds: []};
    $scope.states = states;
    $scope.isNotPublished = false;
    $scope.isPublished = true;

    $scope.catSelectionsModel = {};
    $scope.catTypeSelectionsModel = {};
    $scope.catSubTypeSelectionsModel = {};

    $scope.businessHelper = businessNavHelper;
    categoriesManager.reloadCategoryData();
    classificationsManager.reloadClassificationData();

    $scope.saveButtonClicked = function () {
      $http.post('/api/v1/businesses', $scope.business)
        .success(function (data) {
          infoOverlay.displayInfo("Changes saved successfully");
          $log.info("created new business successfully");
          $location.url('/businesses');
        })
        .error(function () {
          infoOverlay.displayInfo("error adding new business");
          $log.error("error adding new business");
        });
    };

    $scope.isClassificationInputEnabled = function (classificationId) {
      var associatedClassificationIds = [];
      if (_.isEmpty($scope.business.categoryIds)) {
        return false;
      }
      _.each($scope.business.categoryIds, function (categoryId) {
        var acIds = ($rootScope.categoriesMap[categoryId] || {}).associatedClassificationIds || [];
        associatedClassificationIds = associatedClassificationIds.concat(acIds);
      });
      associatedClassificationIds = _.uniq(associatedClassificationIds);
      return _.contains(associatedClassificationIds, classificationId);
    };

    $scope.cityStateNeighborhoodGeo = null;

    $scope.fetchNeighborhoodGeoForCityState = function (city, state, callback) {
      $log.debug("fetchNeighborhoodGeoForCityState: " + city + ", " + state);
      $http.get('/api/v1/city?city=' + city + '&state=' + state)
        .success(function (data) {
          $scope.cityStateNeighborhoodGeo = angular.isArray(data) && data.length > 0 ? data[0] : (_.isEmpty(data) ? null : data);
          if (angular.isFunction(callback)) {
            callback(null, $scope.cityStateNeighborhoodGeo);
          }
        })
        .error(function (err) {
          $scope.cityStateNeighborhoodGeo = null;
          alert("error fetching city/state neighborhoodGeo information.");
          callback(err, null);
        });
    };

    $scope.getNeighborhoodNamesArray = function () {
      if ($scope.business && $scope.business.neighborhoodGeoIds) {
        return citiesAndNeighborhoodsManager.getNamesByIds($scope.business.neighborhoodGeoIds);
      }
      return [];
    };

    $scope.photos = [];
    $scope.neighborhoodOptions = [];
    $scope.getNeighborhoodOptions = function (state, city, callback) {
      if (city && state) {
        $scope.fetchNeighborhoodGeoForCityState(city, state, function (err, data) {
          //$log.debug("data from fetchNeighborhoodGeoForCityState is " + angular.toJson(data));
          //$log.debug(" ... and $scope.cityStateNeighborhoodGeo is " + angular.toJson($scope.cityStateNeighborhoodGeo));
          if (err) {
            if (angular.isFunction(callback)) {
              callback(err, null);
            }
          } else if (!data || _.isEmpty($scope.cityStateNeighborhoodGeo)) {
            $log.debug("No city/state data..");
            $scope.neighborhoodOptions = [];
            callback(null, $scope.neighborhoodOptions);
          } else {
            $log.debug("$scope.cityStateNeighborhoodGeo is " + angular.toJson($scope.cityStateNeighborhoodGeo));
            citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData($scope.cityStateNeighborhoodGeo.id, true, true, function (err, data) {
              $scope.neighborhoodOptions = citiesAndNeighborhoodsManager.getChildrenByParentId($scope.cityStateNeighborhoodGeo.id);
              if (angular.isFunction(callback)) {
                callback(null, data);
              }
            });
          }
        });
      } else {
        $scope.neighborhoodOptions = null;
      }
    };

    $scope.lookupNeighborhoodOfBusinessByLatLong = function () {
      geocoder.getNeighborhoodsForLonLat($scope.business.long, $scope.business.lat, function (err, data) {
        if (err) {
          $log.debug("Unable to determine neighborhood for this business. " + angular.toJson(err));
        } else {
          if (_.isEmpty(data)) {
            $log.debug("no neighborhoods were found at lon/lat " + $scope.business.long + ', ' + $scope.business.lat);
          } else {
            $log.debug("neighborhoods were found! \n" + angular.toJson(data));
            $scope.business.neighborhoods = [data[0].name];
            $scope.business.neighborhoodGeoIds = geocoder.xformToNeighborhoodGeoIds(data);
          }
        }
      });
    };

    $scope.codeAddress = function () {
      $log.debug("geocoding address....");
      var b = $scope.business,
        addressQuery = ((b.addr1 || '') + ' ' + (b.city || '') + ' ' + (b.state || '') + ' ' + (b.zip || '')).trim();
      geocoder.codeAddress(addressQuery, function (err, data) {
        if (err) {
          $log.debug("error geocoding. " + err);
        } else {
          $log.debug("geocode result is: " + angular.toJson(data));
          $scope.business.addr1 = data.streetNumber + ' ' + data.street;
          $scope.business.city = data.city;
          $scope.business.state = data.state;
          $scope.business.zip = data.zip;
          $scope.business.lat = data.lat;
          $scope.business.long = data.long;
          $scope.lookupNeighborhoodOfBusinessByLatLong();
          $scope.$apply();
        }
      });
    };

//    $scope.addNewNeighborhoodIfNeeded = function () {
//      if ($scope.business.neighborhoods && $scope.business.neighborhoods.length > 0 && $scope.business.city && $scope.business.state) {
//        angular.forEach($scope.business.neighborhoods, function (val) {
//          if (val !== '(PENDING)') {
//            $http.post('/api/v1/neighborhoods', {city: $scope.business.city, state: $scope.business.state, neighborhood: val});
//          }
//        });
//      }
//    };

    $scope.editNeighborhoodsForBusiness = function (size) {
      $scope.getNeighborhoodOptions($scope.business.state, $scope.business.city, function (err, data) {
        if (err) {
          $log.error("error getting neighborhood options prior to modal popup");
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-neighborhoodsGeo-modal.html',
            controller: 'BusinessesNeighborhoodGeoModalController',
            size: size,
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableNeighborhoods: function () {
                return angular.isArray($scope.neighborhoodOptions) ? $scope.neighborhoodOptions.sort() : $scope.neighborhoodOptions;
              }
            }
          });
          modalInstance.result.then(function (data) {
            $scope.business.neighborhoods = data.neighborhoods || [];
            $scope.business.neighborhoodGeoIds = data.neighborhoodGeoIds || [];
            $scope.business.neighborhoodDisplayValue = $scope.business.neighborhoods.sort().join(', ');
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };


    $scope.onClickEditMultipleClassificationProperty = function (classificationId, businessPropertyName, numOfColumns, size) {
      classificationsManager.reloadClassificationData(function (err, data) {
        if (err) {
          $log.error("error loading classification data. " + err);
        } else {
          var modalInstance = $modal.open({
            templateUrl: 'business-generic-classification-modal.html',
            controller: 'BusinessesGenericClassificationModalController',
            size: size,
            resolve: {
              business: function () {
                return $scope.business;
              },
              availableValues: function () {
                return $rootScope.classificationTypes[classificationId];
              },
              businessPropertyName: function () {
                return businessPropertyName;
              },
              numOfColumns: function () {
                return numOfColumns;
              },
              classificationDisplayName: function () {
                return $rootScope.classificationsMap[classificationId].name;
              }
            }
          });

          modalInstance.result.then(function (data) {
            $scope.business[businessPropertyName] = data;
            $scope.businessForm.$setDirty(true);
          }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
          });
        }
      });
    };

    $scope.combinedCategoryInfo = function () {
      var validCatIds = _.select($scope.business.categoryIds, function (val) {
          return angular.isDefined($rootScope.categoriesMap[val]);
        }),
        validCatTypeIds = _.select($scope.business.categoryTypeIds, function (val) {
          return angular.isDefined($rootScope.categoryTypesMap[val.substring(25)]);
        }),
        validCatSubTypeIds = _.select($scope.business.categorySubTypeIds, function (val) {
          return angular.isDefined($rootScope.categorySubTypesMap[val.substring(50)]);
        }),
        all = _.union(validCatIds, validCatTypeIds, validCatSubTypeIds).sort(),
        allFiltered = [],
        i,
        j,
        val1,
        val2,
        isSubstring;
      for (i = 0; i < all.length; i++) {
        isSubstring = false;
        val1 = all[i];
        for (j = 0; j < all.length; j++) {
          val2 = all[j];
          if (i !== j && val2.indexOf(val1) === 0) {
            isSubstring = true;
            break;
          }
        }
        if (!isSubstring) {
          allFiltered.push(val1);
        }
      }
      return allFiltered;
    };


    var gt = ' <span class="glyphicon glyphicon-chevron-right"></span> ';

    $scope.displayValueForCategory = function (categoryId) {
      return $rootScope.categoriesMap[categoryId].name;
    };

    $scope.displayValueForCategoryType = function (expandedCategoryTypeId) {
      var categoryId = expandedCategoryTypeId.substring(0, 24),
        categoryTypeId = expandedCategoryTypeId.substring(25),
        catName = $rootScope.categoriesMap[categoryId].name,
        catTypeName = $rootScope.categoryTypesMap[categoryTypeId].name;
      return catName + gt + catTypeName;
    };

    $scope.displayValueForCategorySubType = function (expandedCategorySubTypeId) {
      var categoryId = expandedCategorySubTypeId.substring(0, 24),
        categoryTypeId = expandedCategorySubTypeId.substring(25, 49),
        categorySubTypeId = expandedCategorySubTypeId.substring(50),
        catName = $rootScope.categoriesMap[categoryId].name,
        catTypeName = $rootScope.categoryTypesMap[categoryTypeId].name,
        catSubTypeName = $rootScope.categorySubTypesMap[categorySubTypeId].name;
      return catName + gt + catTypeName + gt + catSubTypeName;
    };

    $scope.displayValueForCategoryInfo = function (expandedId) {
      if (expandedId) {
        if (expandedId.length === 24) {
          return $scope.displayValueForCategory(expandedId);
        } else if (expandedId.length === 49) {
          return $scope.displayValueForCategoryType(expandedId);
        } else if (expandedId.length === 74) {
          return $scope.displayValueForCategorySubType(expandedId);
        }
      }
      return '';
    };


    $scope.updateBusinessCategoriesFromTreeModel = function (id) {
      $log.debug("updateBusinessCategoriesFromTreeModel...");
      var selectedCategoryIds = [],
        selectedCategoryTypeIds = [],
        selectedCategorySubTypeIds = [];
      _.each($scope.catSelectionsModel, function (val, key) {
        if (val) {
          selectedCategoryIds.push(key);
        }
      });
      _.each($scope.catTypeSelectionsModel, function (val, key) {
        if (val) {
          selectedCategoryTypeIds.push(key);
        }
      });
      _.each($scope.catSubTypeSelectionsModel, function (val, key) {
        if (val) {
          selectedCategorySubTypeIds.push(key);
        }
      });
      if (id.length === 24) {
        $log.debug("cat value is " + $scope.catSelectionsModel[id]);
        if (!$scope.catSelectionsModel[id]) { // if category is unchecked
          _.each($scope.catTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catTypeSelectionsModel[key] = false;
            }
          });
          _.each($scope.catSubTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catSubTypeSelectionsModel[key] = false;
            }
          });
        }
      } else if (id.length === 49) {
        $log.debug("cat type value is " + $scope.catTypeSelectionsModel[id]);
        if (!$scope.catTypeSelectionsModel[id]) { // if category type is unchecked
          _.each($scope.catSubTypeSelectionsModel, function (val, key) {
            if (key.indexOf(id) === 0) {
              $scope.catSubTypeSelectionsModel[key] = false;
            }
          });
        }
      } else if (id.length === 74) {
        $log.debug("cat sub type value is " + $scope.catSubTypeSelectionsModel[id]);
      } else {
        $log.warn("unknown cat id type.  unexpected length");
      }

      $scope.business.categorySubTypeIds = [];
      _.each($scope.catSubTypeSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categorySubTypeIds.push(key);
        }
      });

      $scope.business.categoryTypeIds = [];
      _.each($scope.catTypeSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categoryTypeIds.push(key);
        }
      });

      $scope.business.categoryIds = [];
      _.each($scope.catSelectionsModel, function (val, key) {
        if (val) {
          $scope.business.categoryIds.push(key);
        }
      });
    };


  })


  //
  // ============================= List All ===================================
  //
  .controller('BusinessesController', function($scope, $rootScope, $http, $log, $filter, ngTableParams, $location, $routeParams, appUtils, businessNavHelper, geocoder) {

    $scope.headline = "Businesses";
    $scope.businesses = [];
    $scope.businessesCount = null;
    $scope.filterCategoryId = $routeParams.categoryId;
    $scope.nameSearch = $routeParams.name;
    $scope.isViewForMissingFields = $location.path() == '/businesses-missing-fields';

    function translatePublishedRouteParam() {
      if ('true' === $routeParams.published) {
        return 'true';
      } else if ('false' === $routeParams.published) {
        return 'false';
      } else {
        return 'all';
      }
    }
    $scope.publishedFilterValue = translatePublishedRouteParam();
    $scope.publishedOptions = [
      {id: 'all', desc: "All (Published and Pending)"},
      {id: 'true', desc: "Published only"},
      {id: 'false', desc: "Pending Review only"}
    ];

    $scope.businessContentTableParams = new ngTableParams({
      page: $routeParams.pageNumber || 1,
      count: $routeParams.pageSize || 100,
      sorting: {
        name: 'asc'
      }
    }, {
      total: $scope.businesses.length,
      getData: function ($defer, params) {
        businessNavHelper.urlWithQueryParams = $location.absUrl();
        var queryParameters = {};
        if ($scope.filterCategoryId !== null && !_.isUndefined($scope.filterCategoryId)) {
          queryParameters.categoryId = $scope.filterCategoryId;
        }
        if ($scope.publishedFilterValue === 'true') {
          queryParameters.published = true;
        } else if ($scope.publishedFilterValue === 'false') {
          queryParameters.published = false;
        } else {
          queryParameters.published = 'all';
        }
        if ($scope.nameSearch) {
          queryParameters.name = $scope.nameSearch;
        }
         if (params.page()) {
          queryParameters.pageNumber = $routeParams.pageNumber || 1;
          queryParameters.pageSize = $routeParams.pageSize || 1000;
        }
        if (params.sorting()) {
          queryParameters.orderBy = params.orderBy()[0].slice(1);
          queryParameters.orderDir = params.orderBy()[0].slice(0, 1) === '+' ? 'asc' : 'desc';
        }
        businessNavHelper.searchParams = queryParameters;
        queryParameters.isPortal = true;
        if ($scope.isViewForMissingFields) {
          queryParameters.includeImageStats = true;
          queryParameters.includeSubCatCount = true;
        }
        var queryString = appUtils.queryStringFromObject(queryParameters);
        $http.get('/api/v1/businesses' + queryString)
          .success(function (data) {
            businessNavHelper.completeResults = data;
            params.total(data.length);
            $scope.businessesCount = data.length;
            $scope.businesses = data.slice((params.page() - 1) * params.count(), params.page() * params.count());
            $defer.resolve($scope.businesses);
          }).error(function (error) {
            $log.info("Error getting businesses. " + error);
            params.total(0);
            $scope.businessesCount = null;
            $scope.businesses = [];
            businessNavHelper.completeResults = [];
          });
      }
    });

    $scope.editBusiness = function (businessId, rowIndex) {
      $log.debug("editBusiness.");
      var index = $scope.businessContentTableParams.count() * ($scope.businessContentTableParams.page() - 1) + rowIndex;
      businessNavHelper.editBusinessAtIndex(index);
    };

    $scope.resolveBusiness = function (businessId, rowIndex) {
        $log.debug("resolveBusiness.");
        var index = $scope.businessContentTableParams.count() * ($scope.businessContentTableParams.page() - 1) + rowIndex;
        businessNavHelper.resolveBusinessAtIndex(index);
    };
    $scope.formatCoords = function (business) {
      if (business && business.lat && business.long) {
        return '[' + business.long + ', ' + business.lat + ']';
      }
      return '---';
    };

    $scope.getHrefForGoogleMapWithLonLat = function (neighborhood) {
      return geocoder.getHrefForGoogleMapWithLonLat(neighborhood);
    };

    $scope.applyFilter = function () {
      var queryParams = {},
        locationURL = $scope.isViewForMissingFields ? '/businesses-missing-fields' : '/businesses',
        queryString;

      if ($scope.publishedFilterValue === 'true') {
        queryParams.published = true;
      } else if ($scope.publishedFilterValue === 'false') {
        queryParams.published = false;
      } else {
        queryParams.published = 'all';
      }
      if ($scope.nameSearch) {
        queryParams.name = $scope.nameSearch;
      }
      if ($scope.businessContentTableParams.sorting()) {
        queryParams.orderBy = $scope.businessContentTableParams.orderBy()[0].slice(1);
        queryParams.orderDir = $scope.businessContentTableParams.orderBy()[0].slice(0, 1) === '+' ? 'asc' : 'desc';
      }

      if ($scope.filterCategoryId !== null && !_.isUndefined($scope.filterCategoryId)) {
        $log.debug("filtering businesses on categoryId " + $scope.filterCategoryId);
        queryParams.categoryId = $scope.filterCategoryId;
      }
      queryString = appUtils.queryStringFromObject(queryParams);
      $location.url(locationURL + queryString);
    };

    $scope.formatNeighborhoodsList = function (neighborhoods) {
      if (neighborhoods && angular.isArray(neighborhoods)) {
        return neighborhoods.sort().join(', ');
      }
      return '';
    };

  });




'use strict';
/*global angular, _*/

angular.module('myBus.classificationModule', [])

  // ==================================================================================================================
  //         Categories (List view)
  // ==================================================================================================================
  .controller('CategoriesController', function ($scope, $http, $log, $location, categoriesManager, classificationsManager, $modal) {
    $log.debug('CategoriesController loading');

    $scope.headline = "Categories";

    categoriesManager.reloadCategoryData();
    classificationsManager.reloadClassificationData();

    $scope.editCategory = function (categoryId) {
      $log.debug('editCategory(' + categoryId + ')');
      $location.url('/categories/' + categoryId);
    };

    $scope.editClassification = $scope.editCategory;

    var reloadAppropriateData = function (categoryOrClassification) {
      if (categoryOrClassification) {
        var visibleFlag = angular.isDefined(categoryOrClassification.visible) ? categoryOrClassification.visible : true;
        return visibleFlag ? categoriesManager.reloadCategoryData() : classificationsManager.reloadClassificationData();
      }
      categoriesManager.reloadCategoryData();
      classificationsManager.reloadClassificationData();
    };

    $scope.deleteCategoryClicked = function (category) {
      var visibleFlag = angular.isDefined(category.visible) ? category.visible : true
        , entityType = (visibleFlag ? "category" : "classification")
        , categoryId = category.id;
      $http.delete('/api/v1/classifications/' + categoryId)
        .success(function () {
          $log.info("successfully deleted " + entityType + " '" + categoryId + "'");
          reloadAppropriateData(category);
        })
        .error(function () {
          $log.error("error deleting the category: " + categoryId);
          reloadAppropriateData(category);
        });
    };

    $scope.addCategoryClicked = function () {
      var categoryName = prompt("Category name:");
      if (categoryName && categoryName !== '') {
        $http.post('/api/v1/classifications', {name: categoryName})
          .success(function (data) {
            $log.info("created new classification successfully");
            categoriesManager.reloadCategoryData();
          })
          .error(function () {
            $log.error("error adding new classification");
          });
      }
    };

    $scope.handleEditCategoryNameClicked = function (category) {
      var visibleFlag = !!category.visible
        , entityType = (visibleFlag ? "category" : "classification")
        , newName = prompt("Enter new " + entityType + " name", category.name);
      if (newName && newName !== category.name) {
        $log.debug("changing " + entityType + " name from " + category.name + " to " + newName);
        $http.put('/api/v1/classifications/' + category.id, {name: newName, visible: visibleFlag})
          .success(function () {reloadAppropriateData(category);})
          .error(function () {reloadAppropriateData(category);});
      }
    };

    $scope.dataTypeDisplayName = classificationsManager.dataTypeDisplayName;
    $scope.isListOrHierarchy = classificationsManager.isListOrHierarchy;

    $scope.addClassificationClicked = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'classification-add-modal.html',
        controller: 'ClassificationAddModalController',
        size: size,
        resolve: {
          business: function () {
            return $scope.business;
          },
          availableNeighborhoods: function () {
            return angular.isArray($scope.neighborhoodOptions) ? $scope.neighborhoodOptions.sort() : $scope.neighborhoodOptions;
          }
        }
      });
      modalInstance.result.then(function (data) {
        classificationsManager.reloadClassificationData();
      }, function () {
        $log.debug('Modal dismissed at: ' + new Date());
      });
    };

  })

  // ==================================================================================================================
  //         Category Types View
  // ==================================================================================================================
  .controller('CategoryTypesController', function ($scope, $rootScope, $http, $log, $location, $routeParams, categoriesManager, classificationsManager, infoOverlay) {
    $log.debug('CategoryTypesController loading');

    $scope.updateTypesMap = function () {
      $scope.categoryId = $routeParams.id;
      $scope.isCategory = !!$rootScope.categoriesMap[$scope.categoryId];
      $scope.currentCategory = $rootScope.categoriesMap[$scope.categoryId] || ($rootScope.classificationsMap ? $rootScope.classificationsMap[$scope.categoryId] : {});
      $scope.headline = $scope.isCategory ? 'Category Types' : $scope.currentCategory.name + ' Values';

      $scope.typesMap = $scope.isCategory ? $rootScope.categoryTypes : $rootScope.classificationTypes;

      if ($scope.currentCategory) {
        angular.forEach($scope.currentCategory.associatedClassificationIds, function (val) {
          $scope.classificationSelectionsModel[val] = true;
        });
      }
    };

    categoriesManager.reloadCategoryData($scope.updateTypesMap);
    classificationsManager.reloadClassificationData($scope.updateTypesMap);

    $scope.categoryId = $routeParams.id;
    $scope.classificationSelectionsModel = {};

    var refreshTypesMap = function () {
      if ($scope.isCategory) {
        categoriesManager.reloadCategoryData($scope.updateTypesMap);
      } else {
        classificationsManager.reloadClassificationData($scope.updateTypesMap);
      }
    };

    $scope.handleClassificationCheckboxChanged = function (classificationId) {
      var newAssociatedClassificationIds = [];
      _.each($scope.classificationSelectionsModel, function (val, key) {
        if (val) {
          newAssociatedClassificationIds.push(key);
        }
      });
      $scope.currentCategory.associatedClassificationIds = newAssociatedClassificationIds;
    };

    $scope.handleSaveClassificationAssociationsClicked = function () {
      $log.debug("handleSaveClassificationAssociationsClicked");
      $http.put('/api/v1/categories/' + $scope.categoryId, {associatedClassificationIds: $scope.currentCategory.associatedClassificationIds})
        .success(function (data) {
          infoOverlay.displayInfo("Changes saved successfully");
          $scope.associatedClassificationForm.$setPristine();
        })
        .error(function (error) {
          var errorMsg = "Error saving changes to associated classifications.  " + (error.error || '');
          infoOverlay.displayErrorInfo(errorMsg);
          $log.error(errorMsg);
        });
    };

    $scope.deleteCategoryTypeClicked = function (categoryTypeId) {
      if (!categoryTypeId || categoryTypeId === '') {
        $log.error("No categoryTypeId was specified.");
        return;
      }
      $http.delete('/api/v1/classifications/' + $scope.categoryId + '/' + categoryTypeId)
        .success(function (data) {
          $log.info("successfully deleted the category type type with id of '" + categoryTypeId + "'");
          refreshTypesMap();
        })
        .error(function () {
          $log.error("error deleting the category type with id of '" + categoryTypeId + "'");
          refreshTypesMap();
        });
    };

    $scope.editCategoryType = function (categoryTypeId) {
      if ($scope.isCategory || (angular.isDefined($scope.currentCategory.maxDepth) && $scope.currentCategory.maxDepth > 1)) {
        $log.debug('editCategoryType(' + $scope.categoryId + ', ' + categoryTypeId + ')');
        $location.url('/categoriessub/' + $scope.categoryId + '/' + categoryTypeId);
      }
    };

    $scope.addCategoryTypeClicked = function () {
      var typeName = prompt("Name of category type:");
      if (typeName && typeName !== '') {
        $http.post('/api/v1/classifications/' + $scope.categoryId, {name: typeName})
          .success(function (data) {
            $log.info("successfully added new category type : " + typeName + "\n" + JSON.stringify(data));
            refreshTypesMap();
          })
          .error(function () {
            $log.error("error adding a new category type with name " + typeName);
            refreshTypesMap();
          });
      }
    };

    $scope.handleEditCategoryTypeNameClicked = function (categoryType) {
      var newName = prompt("Enter new category type name", categoryType.name);
      if (newName && newName !== categoryType.name) {
        $log.debug("changing category type name from " + categoryType.name + " to " + newName);
        $http.put('/api/v1/classifications/' + $scope.categoryId + "/" + categoryType.id, {name: newName})
          .success(refreshTypesMap)
          .error(refreshTypesMap);
      }
    };


  })

  // ==================================================================================================================
  //         Category Sub-Types View
  // ==================================================================================================================
  .controller('CategorySubTypesController', function ($scope, $rootScope, $http, $log, $routeParams, categoriesManager) {
    $log.debug('CategorySubTypesController loading');

    $scope.headline = "Category Sub-Types";

    if (!$rootScope.categories) {
      categoriesManager.reloadCategoryData();
    }

    $scope.categoryId = $routeParams.categoryId;
    $scope.currentCategory = $rootScope.categoriesMap[$scope.categoryId];

    $scope.categoryTypeId = $routeParams.categoryTypeId;
    $scope.currentCategoryType = $rootScope.categoryTypesMap[$scope.categoryTypeId];

    $scope.currentCategorySubTypes = $rootScope.categorySubTypes[$scope.categoryTypeId];

    $scope.deleteCategorySubTypeClicked = function (categorySubTypeId) {
      if (!categorySubTypeId || categorySubTypeId === '') {
        $log.error("No categorySubTypeId was specified.");
        return;
      }
      $http.delete('/api/v1/classifications/' + $scope.categoryId + '/' + $scope.categoryTypeId + '/' + categorySubTypeId)
        .success(function (data) {
          $log.info("successfully deleted the category sub-type type with id of '" + categorySubTypeId + "'");
          categoriesManager.reloadCategoryData();
        })
        .error(function () {
          $log.error("error deleting the category sub-type with id of '" + categorySubTypeId + "'");
        });
    };

    $scope.addCategorySubTypeClicked = function () {
      var typeName = prompt("Name of category sub-type:");
      if (typeName && typeName !== '') {
        $http.post('/api/v1/classifications/' + $scope.categoryId + '/' + $scope.categoryTypeId, {name: typeName})
          .success(function (data) {
            $log.info("successfully added new category sub-type : " + typeName + "\n" + JSON.stringify(data));
            categoriesManager.reloadCategoryData();
          })
          .error(function () {
            $log.error("error adding a new category sub-type with name " + typeName);
          });
      }
    };

    $scope.handleEditCategorySubTypeNameClicked = function (categorySubType) {
      var newName = prompt("Enter new category sub-type name", categorySubType.name);
      if (newName && newName !== categorySubType.name) {
        $log.debug("changing category type name from " + categorySubType.name + " to " + newName);
        $http.put('/api/v1/classifications/' + $scope.categoryId + "/" + $scope.categoryTypeId + "/" + categorySubType.id, {name: newName})
          .success(function (data) {
            categoriesManager.reloadCategoryData();
          })
          .error(function (error) {
            categoriesManager.reloadCategoryData();
          });
      }
    };

  })



  // ==================================================================================================================
  //         MODAL - Add Classification
  // ==================================================================================================================
  .controller('ClassificationAddModalController', function ($scope, $http, $log, $modalInstance, classificationsManager) {

    $scope.classification = { name: '', visible: false, dataType: null };
    $scope.dataTypes = classificationsManager.dataTypes;

    $scope.ok = function () {
      if ($scope.classification.name.trim() === '') {
        alert("'name' is required.");
        return;
      }
      if (!$scope.classification.dataType) {
        alert("You must choose a data type");
        return;
      }
      $http.post('/api/v1/classifications', $scope.classification)
        .success(function () {
          $modalInstance.close($scope.classification);
        })
        .error(function (err) {
          var errMsg = "Error adding classification. " + err.error;
          $log.error(errMsg);
          alert(errMsg);
        });
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });




"use strict";
/*global angular, _*/

angular.module('myBus.citiesModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    CitiesController   ================================================
  // ==================================================================================================================
    .controller('CitiesController', function ($scope, $http, $log, ngTableParams, $modal, $filter, cityManager, $location) {
        $log.debug('CitiesController loading');
        $scope.headline = "Cities";
        $scope.allCities = [];
        $scope.currentPageOfCities = [];

        var loadTableData = function (tableParams, $defer) {
            var data = cityManager.getAllCities();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.allCities = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfCities = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };
        $scope.$on('updateCityCompleteEvent', function (e, value) {
            cityManager.fetchAllCities();
            //loadTableData($scope.cityContentTableParams);
        });

        $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
            loadTableData($scope.cityContentTableParams);
        });

        $scope.goToBoardingPointsList = function (id) {
            $location.url('/city/' + id);
        };

        $scope.cityContentTableParams = new ngTableParams({
            page: 1,
            count:25,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfCities.length,
            getData: function ($defer, params) {
                $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });
        cityManager.fetchAllCities();

//---------------------------------------------------------------------------------------------------------------------
    $scope.handleClickAddStateCity = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            $scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
    $scope.handleClickUpdateStateCity = function(cityId){
        var modalInstance = $modal.open({
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

    .controller('UpdateStateCityModalController', function ($scope, $modalInstance, $http, $log, cityManager, passId) {
        console.log("in UpdateStateCityModalController");
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
                $modalInstance.close(null);
            }
            cityManager.updateCity($scope.city, function (data) {
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                ($scope.city.state || '') !== '';
        };
    })

//
    // ========================== Modal - Add City, State  =================================
    //
    .controller('AddStateCityModalController', function ($scope, $modalInstance,$route, $http, $log, cityManager) {
        $scope.city = {
            name: null,
            state: null
        };
        $scope.ok = function () {
            if ($scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $modalInstance.close(null);
            }
            cityManager.createCity($scope.city, function(data){
                $route.reload();
                $modalInstance.close(data);
            });
        };
        
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                    ($scope.city.state || '') !== '';
        };

    });



"use strict";
/*global angular, _*/

angular.module('myBus.expensesModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    ExpensesController   ================================================
  // ==================================================================================================================

  .controller('ExpensesController', function ($scope, $http, $log, ngTableParams, $modal, $filter, expensesManager, $location) {
    $log.debug('ExpensesController loading');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Expenses";

    $scope.allExpenses = [];
    $scope.currentPageOfExpenses = [];
    $scope.expensesManager = expensesManager;

    var loadTableData = function (tableParams, $defer) {
      var data = expensesManager.getAllData()
        , orderedData = null;

      orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
      $scope.allExpenses = orderedData;
      tableParams.total(data.length);
      if (angular.isDefined($defer)) {
        $defer.resolve(orderedData);
      }
      $scope.currentPageOfExpenses = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };

    $scope.$on('expensesInitComplete', function (e, value) {
      loadTableData($scope.expensesContentTableParams);
    });


    $scope.goToExpensesList = function (id) {
      $location.url('/expenses/' + id);
    };
        
    $scope.expensesContentTableParams = new ngTableParams({
      page: 1,
      count: 50,
      sorting: {
        date: 'asc',
        description: 'asc'
      }
    }, {
      total: $scope.currentPageOfExpenses.length,
      getData: function ($defer, params) {
        $scope.$on('expensesInitComplete', function (e, value) {
          loadTableData(params);
        });
      }
    });
    expensesManager.fetchAllExpenses();

    $scope.handleClickAddStateCity = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'neighborhood-add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
            //$scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
  })

'use strict';
/*global angular, _*/

angular.module('myBus.homeModule', ['ngTable', 'ui.bootstrap'])
  .controller('HomeController', function($scope, $http, $log, $modal, $filter, $location) {
    $scope.headline = "Srikrishna Travels - Admin Portal";
  });
"use strict";
/*global angular, _*/

angular.module('myBus.neighborhoodsModule', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    NeighborhoodsController   ================================================
  // ==================================================================================================================

  .controller('NeighborhoodsController', function ($scope, $http, $log, ngTableParams, $modal, $filter, citiesAndNeighborhoodsManager, geocoder, $location) {
    $log.debug('NeighborhoodsController loading');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Cities";

    $scope.allCities = [];
    $scope.currentPageOfCities = [];
    $scope.citiesAndNeighborhoodsManager = citiesAndNeighborhoodsManager;

    var loadTableData = function (tableParams, $defer) {
      var data = citiesAndNeighborhoodsManager.getAllCities()
        , orderedData = null;

      angular.forEach(data, function (val) {
        val.childrenCount = citiesAndNeighborhoodsManager.countChildrenById(val.id);
      });

      orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
      $scope.allCities = orderedData;
      tableParams.total(data.length);
      if (angular.isDefined($defer)) {
        $defer.resolve(orderedData);
      }
      $scope.currentPageOfCities = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };

    $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
      loadTableData($scope.cityContentTableParams);
    });


    $scope.formatCoords = function (city) {
      if (city && city.lat && city.long) {
        return '[' + city.long + ', ' + city.lat + ']';
      }
      return '---';
    };

    $scope.goToNeighborhoodsList = function (id) {
      $location.url('/cities/' + id);
    };

    $scope.getHrefForGoogleMapWithLonLat = function (neighborhood) {
      return geocoder.getHrefForGoogleMapWithLonLat(neighborhood);
    };


    $scope.handleEditLatLonClicked = function (city) {
      var modalInstance = $modal.open({
        templateUrl: 'edit-lat-lon-modal.html',
        controller: 'EditCityLatLonModalController',
        //size: 'sm',
        resolve: {
          city: function () {
            return city;
          }
        }
      });
      modalInstance.result.then(function (data) {
        $log.debug("results from modal EditCityLatLonModalController: " + angular.toJson(data));
        $http.put('/api/v1/city/' + data.id, data)
          .success(function (suc) {
            $log.debug("city changes saved.");
            _.each([$scope.allCities, $scope.currentPageOfCities], function (arr) {
              _.find(arr, function (val) {
                if (val.id === data.id) {
                  val.lat = data.lat;
                  val.long = data.long;
                  return true;
                }
                return false;
              });
            });
          })
          .error(function (err) {
            $log.debug("error saving changes to city: " + err);
          });
      }, function () {
        $log.debug('Modal dismissed at: ' + new Date());
      });
    };



    $scope.cityContentTableParams = new ngTableParams({
      page: 1,
      count: 50,
      sorting: {
        state: 'asc',
        name: 'asc'
      }
    }, {
      total: $scope.currentPageOfCities.length,
      getData: function ($defer, params) {
        $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
          loadTableData(params);
        });
      }
    });


    citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();

    $scope.handleClickAddStateCity = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'neighborhood-add-city-state-modal.html',
        controller: 'AddStateCityModalController',
        size: size,
        resolve: {
          neighborhoodId: function () {
            return null;
          }
        }
      });
      modalInstance.result.then(function (data) {
        $log.debug("results from modal: " + angular.toJson(data));
        citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
        //$scope.cityContentTableParams.reload();
      }, function () {
        $log.debug('Modal dismissed at: ' + new Date());
      });
    };




    $scope.deleteNeighborhoodFromCity = function (neighborhoodId, neighborhoodName) {
      $log.debug("delete neighborhood " + neighborhoodName + " w/ id " + neighborhoodId);
      $http.delete('/api/v1/neighborhoods/' + neighborhoodId + '?neighborhood=' + neighborhoodName)
        .success(function (data) {
          $log.debug("deleted neighborhood " + neighborhoodName);
          $scope.cityContentTableParams.reload();
        }).error(function (error) {
          var errorMsg = "error deleting neighborhood. " + error;
          alert(errorMsg);
          $log.error(errorMsg);
        });
    };

  })



  //
  // ========================== Modal - Add City, State  =================================
  //
  .controller('AddStateCityModalController', function ($scope, $modalInstance, $http, $log, geocoder) {

    $scope.neighborhood = {
      name: null,
      geo_name: null,
      city: null,
      state: null,
      level: 0,
      position: 1,
      path: '',
      parent_path: null,
      parent_name: null
    };
    /*
     "geometry": {
     "type": "Point",
     "coordinates": [
     -73.959721999999999298,
     40.790278000000000702
     ]
     },
     */

    $scope.ok = function () {
      if ($scope.neighborhood.city === null || $scope.neighborhood.state === null) {
        $log.error("null city or state.  nothing was added.");
        $modalInstance.close(null);
      }

      geocoder.codeAddress($scope.neighborhood.city + ', ' + $scope.neighborhood.state, function (err, data) {
        if (err) {
          $log.error("Error finding lat/lon for specified city. " + angular.toJson(err));
        } else {
          $log.debug("successfully geocoded city/state:  " + angular.toJson(data));
          $scope.neighborhood.lat = data.lat;
          $scope.neighborhood.long = data.long;
          $scope.neighborhood.city = data.city;
          $scope.neighborhood.name = data.city;
          $scope.neighborhood.geo_name = data.city;
          $log.debug("new neighborhood to add is:  " + angular.toJson($scope.neighborhood));

          $http.post('/api/v1/neighborhoodGeo', $scope.neighborhood)
            .success(function (data) {
              $log.info("added new neighborhood info: " + angular.toJson($scope.neighborhood));
              $modalInstance.close(data);
            })
            .error(function (err) {
              var errorMsg = "error adding new category info. " + (err && err.error ? err.error : '');
              $log.error(errorMsg);
              alert(errorMsg);
            });
        }
      });


    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
      return ($scope.neighborhood.city || '') !== '' &&
        ($scope.neighborhood.state || '') !== '';
    };


  })

  //
  // ========================== Modal - Add neighborhood to an existing city =================================
  //
  .controller('NeighborhoodAddToExistingStateCityModalController', function ($scope, $modalInstance, $http, $log, neighborhood) {
    $scope.parentNeighborhood = neighborhood;
    $scope.neighborhood = {
      parentId: neighborhood.id,
      city: neighborhood.city,
      state: neighborhood.state,
      parent_name: neighborhood.name,
      level: (neighborhood.level + 1),
      parent_path: neighborhood.path,
      position: 1
    };

    $scope.ok = function () {
      if (!$scope.neighborhood.name || $scope.neighborhood.name.trim() === '') {
        $log.error("Blank name!  No neighborhood will be added.");
        $modalInstance.close(null);
      }
      $scope.neighborhood.path = $scope.neighborhood.name.toLowerCase();

      $http.post('/api/v1/neighborhoodGeo', $scope.neighborhood)
        .success(function (data) {
          $log.info("added new neighborhood info: " + angular.toJson($scope.neighborhood));
          $modalInstance.close(data);
        })
        .error(function () {
          $log.error("error adding new category info.");
        });
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
      return ($scope.neighborhood.city || '') !== '' &&
        ($scope.neighborhood.state || '') !== '' &&
        ($scope.neighborhood.name || '') !== '';
    };


  })




  //
  // ========================== Modal - Edit Lat/Lon for City =================================
  //
  .controller('EditCityLatLonModalController', function ($scope, $modalInstance, $http, $log, $location, geocoder, city) {

    $log.debug("EditCityLatLonModalController - city: " + angular.toJson(city));

    $scope.cityInfo = {};
    $scope.cityInfo.id = city.id;
    $scope.cityInfo.name = city.name;
    $scope.cityInfo.lat = city.lat;
    $scope.cityInfo.long = city.long;

    $scope.ok = function () {
      $modalInstance.close($scope.cityInfo);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.getHrefForGoogleMapWithLonLat = function () {
      return geocoder.getHrefForGoogleMapWithLonLat($scope.cityInfo);
    };

    $scope.isInputValid = function () {
      return ($scope.cityInfo.lat === null && $scope.cityInfo.long === null)
        || ($scope.cityInfo.lat === '' && $scope.cityInfo.long === '')
        || (parseFloat($scope.cityInfo.lat) && parseFloat($scope.cityInfo.long));
    };


  })







  // ==================================================================================================================
  // =================================     NeighborhoodsListController    =============================================
  // ==================================================================================================================

  .controller('NeighborhoodsListController', function ($scope, $routeParams, $http, $location, $log, ngTableParams, $modal, $filter, citiesAndNeighborhoodsManager) {

    //$log.debug('NeighborhoodsListController loading');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.allNeighborhoods = [];
    $scope.currentPageOfNeighborhoods = [];
    $scope.currentNeighborhoodId = $routeParams.id;
    $scope.currentNeighborhood = null;
    $scope.breadCrumbs = [];

    $scope.headline = "";

    $scope.citiesAndNeighborhoodsManager = citiesAndNeighborhoodsManager;

    $scope.poiCountEnabled = $location.search()['poiCount'];
    $scope.recalculatePOICount = function () {
      $log.debug("recalculatePOICount");
      var updateOperations = [];
      $scope.citiesAndNeighborhoodsManager.getAllData().forEach(function (nei) {
        if (nei.name === '(PENDING)') {
          return;
        }
        updateOperations.push(function (cb) {
          $log.debug("getting POI count for " + nei.name);
          $http.get('/api/v1/npc?neighGeoId=' + nei.id + '&isPortal=1')
            .success(function (data) {
              $log.debug("poi count response: " + angular.toJson(data));
              nei.poiCount = data.poi_count;
              cb(null, data);
            })
            .error(function (err) {
              nei.poiCount = err;
              cb("error. " + err, err);
            });
        });
      });
      async.series(updateOperations, function () {
        $log.debug("all done with POI count update.");
      });
    };

    var updateHeadline = function () {
      if ($scope.currentNeighborhood) {
        $scope.headline = "Neighborhoods for " + $scope.currentNeighborhood.name;
      } else {
        $scope.headline = "Neighborhoods...";
      }
    };

    var updateBreadcrumbModel = function () {
      var cityAndNeighborhoodChain = citiesAndNeighborhoodsManager.getBreadcrumbDescendants($scope.currentNeighborhood, []);
      cityAndNeighborhoodChain.pop();
      $scope.breadCrumbs = cityAndNeighborhoodChain;
    };

    var loadTableData = function (tableParams, $defer) {
      var data = citiesAndNeighborhoodsManager.getChildrenByParentId($scope.currentNeighborhoodId)
        , orderedData = null;

      angular.forEach(data, function (val) {
        val.childrenCount = citiesAndNeighborhoodsManager.countChildrenById(val.id);
      });

      orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
      $scope.allNeighborhoods = orderedData;
      tableParams.total(data.length);
      if (angular.isDefined($defer)) {
        $defer.resolve(orderedData);
      }
      $scope.currentPageOfNeighborhoods = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };

    $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
      $scope.currentNeighborhood = citiesAndNeighborhoodsManager.getOneById($scope.currentNeighborhoodId);
      updateHeadline();
      loadTableData($scope.neighborhoodContentTableParams);
      updateBreadcrumbModel();
    });


    $scope.handleDeleteButtonClicked = function (id) {
      if (!id) {
        var errorMsg = "no id was specified.  neighborhood will not be deleted.";
        $log.error(errorMsg);
        alert(errorMsg);
        return;
      }
      $http.delete('/api/v1/neighborhoodGeo/' + id)
        .success(function (data) {
          $location.url('/cities/' + ($scope.currentNeighborhood.parentId || ''));
        })
        .error(function (error) {
          alert("error deleting neighborhood.  " + angular.toJson(error));
        });
    };

    $scope.formatGeoJSON = function (neighborhood) {
      if (neighborhood && neighborhood.geometry && neighborhood.geometry.type === "Polygon" && _.isArray(neighborhood.geometry.coordinates)) {
        return (neighborhood.geometry.coordinates[0].length - 1) + " sided polygon";
      }
      return '---';
    };

    $scope.goToNeighborhoodsList = function (neighborhood) {
      $location.url('/cities/' + neighborhood.id);
    };


    $scope.neighborhoodContentTableParams = new ngTableParams({
      page: 1,
      count: 50,
      sorting: {
        state: 'asc',
        name: 'asc'
      }
    }, {
      total: $scope.currentPageOfNeighborhoods.length,
      getData: function ($defer, params) {
        $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
          updateHeadline();
          loadTableData(params, $defer);
        });
      }
    });

    citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();

    $scope.handleClickAddNeighborhood = function () {
      var modalInstance = $modal.open({
        templateUrl: 'add-neighborhood-to-city-state-modal.html',
        controller: 'NeighborhoodAddToExistingStateCityModalController',
        size: 'sm',
        resolve: {
          neighborhood: function () {
            return $scope.currentNeighborhood;
          }
        }
      });
      modalInstance.result.then(function (data) {
        $log.debug("results from modal: " + angular.toJson(data));
        citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData($scope.currentNeighborhoodId);
      }, function () {
        $log.debug('Modal dismissed at: ' + new Date());
      });
    };


  });

"use strict";
/*global angular, _*/

angular.module('myBus.personModules', ['ngTable', 'ui.bootstrap'])
    .controller('PersonController', function ($rootScope, $http,$scope,$modal, personService) {
        $scope.persons = [];

        $scope.displayPersons = function(data){
            $scope.persons = data;
        };

        $scope.loadPersons = function () {
            personService.loadPersons($scope.displayPersons)
        };

        $scope.loadPersons();

        $scope.addPersonOnClick = function(){
            var modalInstance = $modal.open({
                templateUrl: 'add-person-modal.html',
                controller: 'AddPersonModalController'
            });
        };

        $scope.deletePersonOnClick = function(personId){
            var modalInstance=$modal.open({
                templateUrl: 'delete-person-modal.html',
                controller: 'DeletePersonModalController',
                resolve:{
                    deleteId:function(){
                        return personId;
                    }
                }
            })


        };

        $scope.updatePersonOnClick = function(personId){
            console.log("Loading");
            var modalInstance = $modal.open({
                templateUrl: 'update-person-modal.html',
                controller: 'UpdatePersonModalController',
                resolve: {
                    fetchId: function () {
                        return personId;
                    }
                }

            });
        };
        //watch event handler
        $scope.$on('loadPersonsEvent', function (e, value) {
            $scope.loadPersons();
        });


    })

    .controller('AddPersonModalController',function($scope,$modalInstance,$http,$log,$route,personService){
        $scope.person = {
            name: null,
            age: null,
            phone : null
        };
        $scope.ok = function () {
            if ($scope.person.name === null || $scope.person.age === null || $scope.person.phone == null) {
                $log.error("Empty person data.  nothing was added.");
                $modalInstance.close(null);
            }
            personService.createPersons($scope.person, function(data){
                $route.reload();
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

            return ($scope.person.name || '') !== '' &&
                ($scope.person.age || '') !== '' &&
                ($scope.person.phone || '') !== '';

        };

    })

    .controller('UpdatePersonModalController',function($scope,$modalInstance,$http,$route,$log,personService,fetchId, cityManager){
        $scope.person = {};
        $scope.personId=fetchId;
        $scope.citySelected = null;
        $scope.addLivingCity = function(cityId){
            if($scope.person.citiesLived.indexOf(cityId) == -1) {
                $scope.person.citiesLived.push(cityId);
            }else {
                console.log("city already added");
            }

        };
        $scope.removeLivingCity = function(cityId){
            var index = $scope.person.citiesLived.indexOf(cityId);
            if(index!= -1) {
                $scope.person.citiesLived.splice(index, 1);
            }else {
                console.log("city already removed");
            }

        };
        $scope.displayPersons = function(data){
            $scope.person = data;
        };
        $scope.cities = [];
        cityManager.getCities(function(data) {
            $scope.cities = data;
        });

        $scope.setPersonIntoView = function(fetchId){
            personService.findByIdPerson(fetchId,$scope.displayPersons);

        };
        $scope.setPersonIntoView(fetchId);

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.firstCallBack = function(){
            console.log("executing function1");
        }

        $scope.ok = function (fetchId) {
            if ($scope.person.name === null || $scope.person.age === null || $scope.person.phone == null) {
                $log.error("Empty person data.  nothing was added.");
                $modalInstance.close(null);
            }
                personService.updatePerson($scope.person, function(data){
                    console.log("we are at OK");
                    $route.reload();
                    $modalInstance.close(data);
                });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.person.name || '') !== '' &&
                ($scope.person.age || '') !== '' &&
                ($scope.person.phone || '') !== '';
        };
    })
//-- -----------------------------for delete model popup-----------------------
    .controller('DeletePersonModalController',function($scope,$modalInstance,$http,$route,$log,personService,deleteId){
        $scope.person = {};
        $scope.displayPersons = function(data){$scope.person = data;
        };

        $scope.loadPersons = function () {
            personService.loadPersons($scope.displayPersons)
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.ok = function (personId) {

            personService.deletePerson(deleteId,$scope.loadPersons);
            $route.reload();
            $modalInstance.close();


        };
        $scope.$on('loadPersonsEvent', function (e, value) {
            $scope.loadPersons();
        });
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    });
/**
 * Created by svanik on 1/19/2016.
 */
"use strict";

angular.module('myBus.routesModules', ['ui.bootstrap'])

    // ==================================================================================================================
    // ====================================    RoutesController   ================================================
    // ==================================================================================================================

    .controller('RoutesController', function ($scope, $http,$modal, $log, routesManager,$filter,ngTableParams,$location,cityManager) {

        $log.debug('RoutesController loading');
        $scope.headline = "Routes";
        $scope.allRoutes = [];
        $scope.currentPageOfRoutes = [];

        var loadTableData = function (tableParams, $defer) {
            var data = routesManager.getAllRoutes();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.allRoutes = orderedData;

            cityManager.getCities(function(data){
                $scope.cities = data;

                angular.forEach($scope.allRoutes,function(route) {
                    // each route
                    angular.forEach($scope.cities,function(city){
                        // for each city
                        if (city.id == route.fromCity) {
                            route.fromCity = city.name;
                        }

                        if (city.id == route.toCity) {
                            route.toCity = city.name;
                        }
                    });

                });

            });
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfRoutes = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('RoutesInitComplete', function (e, value) {
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

        $scope.routeContentTableParams = new ngTableParams({
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

        $scope.handleClickDeleteRoute = function(routeId){
            var modalInstance = $modal.open({
                templateUrl : 'delete-route-modal.html',
                controller : 'DeleteRouteModalController',
                resolve : {
                    passId : function(){
                        return routeId;
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
    })

    .controller('AddRouteModalController', function ($scope, $modalInstance, $http, $log, cityManager,routesManager) {
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

        $scope.ok = function () {
            if ($scope.route.name === null || $scope.route.toCity === null  ) {
                $log.error("nothing was added.");
                $modalInstance.close(null);
            }
            routesManager.createRoute($scope.route, function (data) {
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

        };
    })

    .controller('DeleteRouteModalController',function($scope,$http,routesManager,$modalInstance,$log,passId) {
                $scope.routeId = passId;

                $scope.ok = function (passId) {
                    routesManager.deleteRoute(passId);
                    $modalInstance.close();
                    console.log("Route deleted");
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };

    });



"use strict";
/*global angular, _*/

angular.module('myBus.userModule', ['ngTable', 'ui.bootstrap'])


  //
  // ======================== Edit User =====================================
  //
  .controller('UserEditController', function ($scope, $location, $http, $log, $modal, infoOverlay) {
    $scope.headline = "Edit User";
    $scope.isAdd = false;

    $scope.user = {customData: {}};
    $scope.businessesMinimal = [];

    $scope.fetchUser = function () {
      var userMail = $location.search().email;
      $http.get('/api/v1/account?email=' + userMail)
        .success(function (userData) {
          $scope.user = userData;
        })
        .error(function (err) {
          $log.error("Error fetching user.  href: " + userHref + ".  Error: " + err);
          $scope.user = {};
        });
    };

    $scope.fetchUser();

    $scope.fetchBusinessesMinimal = function () {
      $http.get('/api/v1/businessesMinimal')
        .success(function (data) {
          $scope.businessesMinimal = data;
        })
        .error(function (err) {
          $log.error("Error fetching businesses (minimal). " + err);
          $scope.businessesMinimal = [];
        });
    };

    $scope.fetchBusinessesMinimal();

    $scope.selectedBusinesses = function () {
      var selectedBusinesses = _.select($scope.businessesMinimal, function (bus) {
        return _.contains($scope.user.customData.businessIds, bus.id);
      });
      return _.sortBy(selectedBusinesses, 'displayName');
    };

    $scope.disassociateBusiness = function (businessId) {
      var idx = _.indexOf($scope.user.customData.businessIds, businessId);
      if (idx >= 0) {
        $scope.user.customData.businessIds.splice(idx, 1);
      }
      $scope.userForm.$setDirty(true);
    };

    $scope.handleAssociateBusinessesClicked = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'associated-businesses-modal.html',
        controller: 'UserAssociatedBusinessesModalController',
        size: size,
        resolve: {
          selectedIds: function () {
            return $scope.user.customData.businessIds;
          },
          businessList: function () {
            return $scope.businessesMinimal;
          }
        }
      });

      modalInstance.result.then(function (data) {
        $scope.user.customData.businessIds = data;
        $scope.userForm.$setDirty(true);
      }, function () {
        $log.debug('Modal dismissed at: ' + new Date());
      });

    };

    $scope.saveButtonClicked = function () {
      var customData = {email: $scope.user.email, businessIds: $scope.user.customData.businessIds};
      	$http.put('/api/v1/accountCustomData', customData)
        .success(function (data) {
          infoOverlay.displayInfo("Changes saved successfully");
        })
        .error(function (err) {
          var errorMsg = 'Error saving changes to account. ' + angular.toJson(err);
          $log.error(errorMsg);
          infoOverlay.displayInfo(errorMsg);
        });
    };

  })


  //
  // ============================= Add ========================================
  //
  .controller('UserAddController', function($scope) {
    $scope.headline = "Add New User";
    $scope.isAdd = true;
  })


  //
  // ============================= List All ===================================
  //
  .controller('UsersController', function($scope, $http, $log, $filter, ngTableParams, $location, usSpinnerService) {

    $scope.headline = "Users";
    $scope.users = [];
    $scope.userCount = 0;

    $scope.startSpin = function(){
      usSpinnerService.spin('spinner-1');
    };
    $scope.stopSpin = function(){
      usSpinnerService.stop('spinner-1');
    };

    $scope.userContentTableParams = new ngTableParams(
      // merge default params with url
      angular.extend({
        page: 1,            // show first page
        count: 25,          // count per page
        sorting: {
          surname: 'asc'     // initial sorting
        }
      }, $location.search()), {
        total: $scope.users.length,
        getData: function($defer, params) {
          $location.search(params.url()); // put params in url
          $http.get('/api/v1/accounts')
            .success(function (data) {
              $scope.userCount = data.length || 0;
              //$log.debug("user data: " + angular.toJson(data));
              var orderedData = params.sorting ? $filter('orderBy')(data, params.orderBy()) : data;
              params.total(data.length);
              $scope.users = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
              _.each($scope.users, function (u) {
                if (angular.isArray(u.businesses)) {
                  u.businesses.sort();
                }
              });
              $defer.resolve($scope.users);
              $scope.stopSpin();
            }).error(function (error) {
              $log.info("Error getting users. " + error);
              $scope.stopSpin();
            });
        }
    });

    $scope.editUser = function (userEmail) {
      $location.url('/user?email=' + userEmail);
    };

  })

  //
  // ============================= Modal - Conditions  ===============================
  //
  .controller('UserAssociatedBusinessesModalController', function ($scope, selectedIds, businessList, $modalInstance) {
    $scope.selectedIds = selectedIds;
    $scope.businessList = businessList;

    (function updateSelectedFlagInBusinessList() {
      if ($scope.selectedIds && $scope.selectedIds.length > 0) {
        _.each($scope.businessList, function (bus) {
          if (_.contains($scope.selectedIds, bus.id)) {
            bus.selected = true;
          }
        });
      }
    }());

    $scope.ok = function () {
      var selectedBusinessIds = [];
      _.select(businessList, function (bus) {
        if (bus.selected) {
          selectedBusinessIds.push(bus.id);
        }
      });
      $modalInstance.close(selectedBusinessIds);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });


/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes: ng-really-message="Are you sure"? ng-really-click="takeAction()" function
 */
angular.module('myBus').directive('ngReallyClick', [function() {
  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      element.bind('click', function() {
        var message = attrs.ngReallyMessage || 'Are you sure?';
        if (message && confirm(message)) {
          scope.$apply(attrs.ngReallyClick);
        }
      });
    }
  }
}]);
// https://coderwall.com/p/fyuxhg

angular.module('myBus')
  .directive('stateOptions', function (states) { //states value injected into directive context
    return {
      restrict: 'E',
      replace: true,
      scope: true,  //we want a separate child scope
      template: '<select ng-options="state.abbreviation as state.displayName for state in states" class="form-control"><option value="">--- Choose State ---</option></select>',
      require: '^ngModel',
      link: function(scope, element, attrs) {
        scope.states = states;
      }
    };
  });
var app = angular.module('myBus');

/**
 * The 'arrayNone' filter will format the array as a comma-separated list of values, or else 'None' if it is empty.
 * You can override the 'None' string with your own custom string with the 'textForNone' param.
 * You can override the separator with the 'separatorString' param
 * If the parameter is not an array, it is returned as-is.
 */
app.filter('arrayNone', function () {
  return function (val, textForNone, separatorString) {
    if (angular.isUndefined(val) || val === null) {
      return textForNone || 'None';
    }
    if (!angular.isArray(val)) {
      return val;
    }
    if (val.length === 0) {
      return textForNone || 'None';
    }
    return val.join(separatorString || ", ");
  };
});
var app = angular.module('myBus');

app.filter('unsafe', function ($sce) {
  return function (val) {
    return $sce.trustAsHtml(val);
  };
});
"use strict";

angular.module('myBus')
  .value('states',
    function (rawStates) {
      return _.map(rawStates, function (name, abbreviation) {
        return {abbreviation: abbreviation, name: name, displayName: (abbreviation + ' - ' + name)};
      });
    }({
      'AP': 'Andhra Pradesh',
      'AR': 'Arunachal Pradesh',
      'AS': 'Assam',
      'BH': 'Bihar',
      'CG': 'Chhattisgarh'
    }));
"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('appConfigManager', function ($http, $log) {

  var appConfigProperties = null;

  return {

    fetchAppSettings: function (callback, forceRefresh) {
      if (appConfigProperties === null || forceRefresh) {
        $http.get('/api/v1/appconfig')
            .success(function (data) {
              appConfigProperties = data;
              $log.debug("App configuration properties loaded:\n" + angular.toJson(appConfigProperties));
              return angular.isFunction(callback) && callback(null, data);
            })
            .error(function (err, status) {
              var errorMsg = 'Error getting app settings from server. Status code ' + status + ".  " + angular.toJson(err);
              $log.error(errorMsg);
              return angular.isFunction(callback) && callback(errorMsg, null);
            });
      } else {
        return angular.isFunction(callback) && callback(null, appConfigProperties);
      }
    },

    areConditionsEnabled: function () {
      return appConfigProperties && appConfigProperties['conditions.enabled'];
    },

    areProceduresEnabled: function () {
      return appConfigProperties && appConfigProperties['procedures.enabled'];
    }
  };
});
"use strict";

var portalApp = angular.module('myBus');

portalApp.factory('appUtils', function () {

  return {
    queryStringFromObject: function (obj) {
      if (_.size(obj) <= 0) {
        return '';
      }
      var queryString = '',
        isFirst = true;
      _.each(obj, function (value, key) {
        if (isFirst) {
          queryString += '?';
        } else {
          queryString += '&';
        }
        isFirst = false;
        queryString += key + '=' + value;
      });
      return queryString;
    }
  };
});



//'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('busLayoutManager', function ($rootScope, $http, $log, $window, $cacheFactory) {

  var layouts = {};

  return {
    fetchAllBusLayouts: function () {
        $log.debug("fetching layouts data ...");
          $http.get('/api/v1/layouts')
          .success(function (data) {
                layouts = data;
                $rootScope.$broadcast('layoutsInitComplete');
                var cache = null;
                if($cacheFactory.get($rootScope.id)){
                    cache = $cacheFactory.get($rootScope.id);
                }else{
                    cache = $cacheFactory($rootScope.id);
                }
                angular.forEach(layouts, function(layout, key) {
                  cache.put(layout.id, layout);
                })

          })
          .error(function (error) {
            $log.debug("error retrieving layouts");
          });
    },

    getAllData: function () {
      return layouts;
    },

    refreshCache: function() {

    },

    getLayouts: function (callback) {
          $log.debug("fetching layouts data ...");
          $http.get('/api/v1/layouts')
              .success(function (data) {
              $rootScope.$broadcast('layoutsCreateComplete');
                callback(data);
              })
              .error(function (error) {
                $log.debug("error retrieving cities");
              });
    },

    getAllLayouts: function () {
        return layouts;
    },

    createLayout : function (layout, callback) {
        $http.post('/api/v1/layout', layout)
          .success(function (data) {
            $rootScope.$broadcast('layoutsCreateComplete');
            //callback(data);
          })
          .error(function (err) {
            var errorMsg = "error adding new layout info. " + (err && err.error ? err.error : '');
            $log.error(errorMsg);
            alert(errorMsg);
          });
    },
    updateLayout: function(layout,callback) {
     $http.put('/api/v1/layout',layout).success(function (data) {
       //callback(data);
       $rootScope.$broadcast('layoutsCreateComplete');
     });
   },
    deleteLayout: function(name) {
     $http.delete('/api/v1/layout/'+name).success(function (data) {
       $rootScope.$broadcast('layoutsDeleteComplete');
     });
   }
  };
});



"use strict";

var portalApp = angular.module('myBus');

portalApp.service('businessNavHelper', function ($log, $location, $window, $http) {
  return {
    searchParams: {},
    urlWithQueryParams: '',
    completeResults: [],

    editBusinessAtIndex: function (idx) {
      $log.debug("requesting to edit business index " + idx + ".  There are " + this.completeResults.length + " total businesses.");
      var businessId = this.completeResults[parseInt(idx, 10)].id;
      $log.debug("editBusiness w/ id of " + businessId + " and index of " + idx);
//      $location.url('/businesses/' + businessId + '?idx=' + idx);
      $window.open('/console#/businesses/' + businessId + '?idx=' + idx);
    },

    resolveBusinessAtIndex: function (idx) {
        $log.debug("requesting to resolve business index " + idx + ".  There are " + this.completeResults.length + " total businesses.");
        var businessId = this.completeResults[parseInt(idx, 10)].id;
        $log.debug("editBusiness w/ id of " + businessId + " and index of " + idx);
//        $location.url('/businesses/' + businessId + '?idx=' + idx);
        $window.open('/api/v1/businesses/resolveBusinesses?id=' + businessId + '?idx=' + idx);
    },
    editBusinessById: function (businessId) {
      $location.url('/businesses/' + businessId);
    },

    isWebSiteLinkValid: function (url) {
      return url && url.match(/https?:\/\/\S+\.\S+/i);
    },

    handleWebSiteLinkClicked: function (url) {
      if (this.isWebSiteLinkValid(url)) {
        $window.open(url);
      } else {
        $log.warn("invalid website address.  Not opening a new window.  url is: '" + url + "'");
      }
    },

    deleteImage: function (business, imageId, callback) {
      $log.debug("delete image " + imageId + " from business " + business.id);
      var url = '/api/v1/businesses/' + business.id + '/images/' + imageId;
      $http.delete(url, {})
        .success(function (data) {
          return angular.isFunction(callback) && callback(null, business.id);
        })
        .error(function (err) {
          return angular.isFunction(callback) && callback("Error deleting image for business. " + err, null);
        });
    },

    setImageAsPrimary: function (business, imageId, callback) {
      $log.debug("set image " + imageId + " as primary for business " + business.id);
      var url = '/api/v1/businesses/' + business.id + '/images/' + imageId + '/primary';
      $http.put(url, {})
        .success(function (data) {
          business.primaryImageId = imageId;
          return angular.isFunction(callback) && callback(null, business.id);
        })
        .error(function (err) {
          return angular.isFunction(callback) && callback("Error setting primary image for business. " + err, null);
        });
    }
  };
});
"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('categoriesManager', function ($rootScope, $http, $log) {

  return {
    reloadCategoryData: function (callback) {
      var i, j, k, category, tmpCatTypes, tmpCatType, tmpCatSubTypes, tmpCatSubType;
      $log.info("Fetching all categories.");
      $http.get('/api/v1/classifications').success(function (data) {
        $rootScope.categories = _.sortBy(data, 'name');
        $rootScope.categoriesMap = {};
        $rootScope.categoryTypes = {};
        $rootScope.categoryTypesMap = {};
        $rootScope.categorySubTypes = {};
        $rootScope.categorySubTypesMap = {};
        if ($rootScope.categories) {
          for (i = 0; i < $rootScope.categories.length; i += 1) {
            category = data[i];
            $rootScope.categoriesMap[category.id] = category;
            tmpCatTypes = (category && category.types) ? category.types : [];
            $rootScope.categoryTypes[category.id] = tmpCatTypes;
            for (j = 0; j < tmpCatTypes.length; j += 1) {
              tmpCatType = tmpCatTypes[j];
              $rootScope.categoryTypesMap[tmpCatType.id] = tmpCatType;
              tmpCatSubTypes = (tmpCatType && tmpCatType.subtypes) ? tmpCatType.subtypes : [];
              $rootScope.categorySubTypes[tmpCatType.id] = tmpCatSubTypes;
              for (k = 0; k < tmpCatSubTypes.length; k += 1) {
                tmpCatSubType = tmpCatSubTypes[k];
                $rootScope.categorySubTypesMap[tmpCatSubType.id] = tmpCatSubType;
              }
            }
          }
        }

        if (angular.isFunction(callback)) {
          callback(null, data);
        }
      }).error(function (error) {
        $log.error("Error getting categories. " + error);
        if (angular.isFunction(callback)) {
          callback(error);
        }
      });
    }
  };
});
'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('citiesAndNeighborhoodsManager', function ($rootScope, $http, $log) {

  var rawDataWithGeo = null
    , rawChildDataWithGeoMap = {}
    , hasFullDataSet = false
    , PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

  return {
    fetchAllCityAndNeighborhoodData: function (parentId, excludeGeo, skipFullDataReloadIfAlreadyExists, callback) {
      if (parentId) {
        if (hasFullDataSet && skipFullDataReloadIfAlreadyExists) {
          $log.debug("skipping children neighborhood data download...");
          if (angular.isFunction(callback)) {
            callback(null, this.getChildrenByParentId(parentId));
          } else {
            $rootScope.$broadcast('cityAndNeighborhoodInitComplete');
          }
          return;
        }
        $log.debug("fetching city and neighborhood data for parentId " + parentId + "...");

        $http.get('/api/v1/neighborhoodGeo/' + parentId + '/children?isPortal=1' + (excludeGeo === false || excludeGeo === null || _.isUndefined(excludeGeo) ? '' : '&geo=true'))
          .success(function (data) {
            rawChildDataWithGeoMap[parentId] = data;
            if (angular.isFunction(callback)) {
              callback(null, data);
            } else {
              $rootScope.$broadcast('cityAndNeighborhoodInitComplete');
            }
          })
          .error(function (error) {
            $log.debug("error retrieving city/neighborhood data for parentId " + parentId + ". " + angular.toJson(error));
            rawChildDataWithGeoMap[parentId] = null;
            if (angular.isFunction(callback)) {
              callback(error, null);
            }
          });
      } else {
        // fetch EVERYTHING.
        if (hasFullDataSet && skipFullDataReloadIfAlreadyExists) {
          $log.debug("skipping full neighborhood data download...");
          if (angular.isFunction(callback)) {
            callback(null, rawDataWithGeo);
          } else {
            $rootScope.$broadcast('cityAndNeighborhoodInitComplete');
          }
          return;
        }
        $log.debug("fetching ALL city and neighborhood data...");
        $http.get('/api/v1/neighborhoodGeos?isPortal=1' + (excludeGeo === false || excludeGeo === null || _.isUndefined(excludeGeo) ? '' : '&geo=true'))
          .success(function (data) {
            hasFullDataSet = true;
            rawDataWithGeo = data;
            if (angular.isFunction(callback)) {
              callback(null, rawDataWithGeo);
            } else {
              $rootScope.$broadcast('cityAndNeighborhoodInitComplete');
            }
          })
          .error(function (error) {
            $log.debug("error retrieving all city/neighborhood data. " + angular.toJson(error));
            rawDataWithGeo = null;
            if (angular.isFunction(callback)) {
              callback(error, null);
            }
          });
      }
    },

    getAllData: function () {
      return rawDataWithGeo;
    },

    getAllCities: function () {
      return _.select(rawDataWithGeo, function (value) {
        return value && value.level === 0;
      });
    },

    countChildrenById: function (parentId) {
      if (rawChildDataWithGeoMap[parentId]) {
        return rawChildDataWithGeoMap[parentId].length;
      }
      return _.reduce(rawDataWithGeo, function (sum, val) {
        if (val) {
          if (val && val.parentId === parentId) {
            return sum + 1;
          }
          return sum;
        }
      }, 0);
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
      return _.first(_.select(rawDataWithGeo, function (value) {
        return value.id === id;
      }));
    },

    /**
     * returns an array of the order of city -> borough -> neighborhood level 1 ... level N,
     * which can be used for breadcrumbs.  This modifies the descendantsArray and also returns it.
     * @param childObj - neighborhood object to find all parents of.  the last element in the array will be this object
     * @param descendantsArray
     * @returns {*}
     */
    getBreadcrumbDescendants: function (childObj, descendantsArray) {
      var parentObj = null;
      if (childObj) {
        descendantsArray.unshift(childObj);
        if (childObj.parentId) {
          parentObj = this.getOneById(childObj.parentId);
          this.getBreadcrumbDescendants(parentObj, descendantsArray);
        }
      }
      return descendantsArray;
    },

    getNamesByIds: function (neighborhoodGeoIds) {
      if (!rawDataWithGeo) {
        return [];
      }
      if (!angular.isArray(neighborhoodGeoIds)) {
        return [];
      }
      var names = [];
      angular.forEach(rawDataWithGeo, function (val) {
        if (val && val.id/* && val.id !== PENDING_NEIGHBORHOOD_NAME*/) {
          if (_.contains(neighborhoodGeoIds, val.id)) {
            names.push(val.name);
          }
        }
      });
      return names.sort();
    }

  };
});



'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');
portalApp.factory('cityManager', function ($rootScope, $http, $log, $window) {
  var cities = {}
      , rawChildDataWithGeoMap = {};
  return {
    fetchAllCities: function () {
      $log.debug("fetching cities data ...");
      $http.get('/api/v1/cities')
          .success(function (data) {
            cities = data;
            $rootScope.$broadcast('cityAndBoardingPointsInitComplete');
          })
          .error(function (error) {
            $log.debug("error retrieving cities");
          });
    },
    getCities: function (callback) {
      $log.debug("fetching cities data ...");
      $http.get('/api/v1/cities')
          .success(function (data) {
            callback(data);
          })
          .error(function (error) {
            $log.debug("error retrieving cities");
          });
    },
    getAllData: function () {
      return cities;
    },
    getAllCities: function () {
      return cities;
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
    createCity : function (city, callback) {
      $http.post('/api/v1/city', city)
          .success(function (data) {
            callback(data);
            this.fetchAllCities();
          })
          .error(function (err) {
            var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
            $log.error(errorMsg);
            alert(errorMsg);
          });
    },
    getCity: function (id, callback) {
      $http.get('/api/v1/city/' + id)
          .success(function (data) {
              callback(data);
              $rootScope.$broadcast('BoardingPointsInitComplete');
          })
          .error(function (error) {
            alert("error finding city. " + angular.toJson(error));
          });
    },
    deleteCity: function(id, callback) {
      $http.delete('/api/v1/city/' + id)
          .success(function (data) {
            callback(data);
            $window.location = "#/cities";
          })
          .error(function (error) {
            alert("error finding city. " + angular.toJson(error));
          });
    },
    updateCity: function(city,callback) {
      $http.put('/api/v1/city/'+city.id,city).success(function (data) {
        callback(data);
        $rootScope.$broadcast('updateCityCompleteEvent');
      }).error(function (error) {
              alert("error updating city. " + angular.toJson(error));
          })
    },
    //----------------------------------------------------------------------
    createBordingPoint: function (cityId,boardingPoint, callback) {
      $http.post('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
      }).error(function () {
        alert("Error saving Bp data");
      });
    },
    updateBp: function(cityId,boardingPoint,callback) {
      $http.put('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
       // $rootScope.$broadcast('updateBpCompleteEvent');
      }).error(function () {
        alert("Error updating Bp data");
      });
    },
    deleteBp: function(cityId,BpId,callback) {
      $http.delete('/api/v1/city/'+cityId+'/boardingpoint/'+BpId).success(function (data) {
        callback(data);
        //$rootScope.$broadcast('deleteBpCompleteEvent');
      }).error(function () {
        alert("Error deleting Bp data");
      });
    },
    getBp: function (id,BpId, callback) {
      $http.get('/api/v1/city/'+id+'/boardingpoint/'+BpId)
          .success(function (data) {
            callback(data);
          })
          .error(function (error) {
            alert("error finding city and Bp. " + angular.toJson(error));
          });
    },
  }
});



"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('classificationsManager', function ($rootScope, $http, $log) {

  return {
    isListOrHierarchy: function (classification) {
      return classification && (_.isUndefined(classification.dataType) || classification.dataType === 'list');
    },
    dataTypes: [
      {id: 'boolean', name: 'Boolean'},
      {id: 'float', name: 'Float'},
      {id: 'integer', name: 'Integer'},
      {id: 'list', name: 'List of Values'},
      {id: 'string', name: 'String'}
    ],
    dataTypeDisplayName: function (classification) {
      var displayName = '';
      if (classification) {
        if (classification.dataType) {
          switch (classification.dataType) {
            case 'boolean': return 'Boolean';
            case 'string': return 'String';
            case 'integer': return 'Integer';
            case 'float': return 'Float';
            case 'list': return (classification.maxDepth && classification.maxDepth > 1 ? 'Hierarchy' : 'List') + ' of Values';
            default: return 'Unknown';
          }
        }
        if (classification.visible) {
          displayName = 'Hierarchy of Values';
        } else {
          displayName = (classification.maxDepth && classification.maxDepth > 1 ? 'Hierarchy' : 'List') + ' of Values';
        }
      }
      return displayName;
    },
    reloadClassificationData: function (callback) {
      $log.info("Fetching all classifications.");
      $http.get('/api/v1/classifications?visible=false').success(function (data) {
        $rootScope.classifications = _.sortBy(data, 'name');
        $rootScope.classificationsMap = {};
        $rootScope.classificationTypes = {};
        $rootScope.classificationTypesMap = {};
        $rootScope.classificationSubTypes = {};
        $rootScope.classificationSubTypesMap = {};
        if ($rootScope.classifications) {
          for (var i = 0; i < $rootScope.classifications.length; i++) {
            var classification = data[i];
            $rootScope.classificationsMap[classification.id] = classification;
            var tmpClassificationTypes = (classification && classification.types) ? classification.types : [];
            $rootScope.classificationTypes[classification.id] = tmpClassificationTypes;
            for (var j = 0; j < tmpClassificationTypes.length; j++) {
              var tmpClassificationType = tmpClassificationTypes[j];
              $rootScope.classificationTypesMap[tmpClassificationType.id] = tmpClassificationType;
              var tmpClassificationSubTypes = (tmpClassificationType && tmpClassificationType.subtypes) ? tmpClassificationType.subtypes : [];
              $rootScope.classificationSubTypes[tmpClassificationType.id] = tmpClassificationSubTypes;
              for (var k = 0; k < tmpClassificationSubTypes.length; k++) {
                var tmpClassificationSubType = tmpClassificationSubTypes[k];
                $rootScope.categorySubTypesMap[tmpClassificationSubType.id] = tmpClassificationSubType;
              }
            }
          }
        }

        //$log.debug("rootscope classifications: \n" + JSON.stringify($rootScope.classifications, null, 3) + '\n\n');
        //$log.debug("rootscope classificationTypes: \n" + JSON.stringify($rootScope.classificationTypes, null, 3) + '\n\n');
        //$log.debug("rootscope classificationSubTypes: \n" + JSON.stringify($rootScope.classificationSubTypes, null, 3) + '\n\n');

        if (angular.isFunction(callback)) {
          callback(null, data);
        }
      }).error(function (error) {
        $log.error("Error getting categories. " + error);
        if (angular.isFunction(callback)) {
          callback(error);
        }
      });
    }
  };
});
'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('expensesManager', function ($rootScope, $http, $log) {

  var expenses = {};

  return {
    fetchAllExpenses: function () {
        $log.debug("fetching expenses data ...");
          $http.get('/api/v1/expenses')
          .success(function (data) {
             expenses = data;
             $rootScope.$broadcast('expensesInitComplete');
          })
          .error(function (error) {
            $log.debug("error retrieving expenses");
          });
    },

    getAllData: function () {
      return expenses;
    },
    getOneById: function (id) {
      return _.first(_.select(expenses, function (value) {
        return value.id === id;
      }));
    }
  };
});



'use strict';

var portalApp = angular.module('myBus');

portalApp.factory('geocoder', function ($log, $http) {

  var _geocoder = null;

  var createResultDTO = function (result) {
    var arrAddress = result.address_components,
      itemRoute = '',
      itemLocality = '',
      itemCountry = '',
      itemPostalCode = '',
      itemStreetNumber = '',
      itemState = "",
      lat = result.geometry.location.k,
      long = result.geometry.location.B || result.geometry.location.D;

    $log.debug("creating DTO from geocode result: " + angular.toJson(result));
    angular.forEach(arrAddress, function (address_component, i) {

      if (address_component.types[0] === "route") {
        $log.debug("route:" + address_component.long_name);
        itemRoute = address_component.long_name;
      }

      if (    (_.contains(address_component.types, 'locality') && _.contains(address_component.types, 'political'))
           || (_.contains(address_component.types, 'sublocality_level_1') && _.contains(address_component.types, 'sublocality') && _.contains(address_component.types, 'political'))) {
        $log.debug("city:" + address_component.long_name);
        itemLocality = address_component.long_name;
      }

      if (address_component.types[0] === "country") {
        $log.debug("country:" + address_component.long_name);
        itemCountry = address_component.long_name;
      }

      if (address_component.types[0] === "postal_code") {
        $log.debug("pc:" + address_component.long_name);
        itemPostalCode = address_component.long_name;
      } else if (address_component.types[0] === "postal_code_prefix" && !itemPostalCode) {
        $log.debug("pc:" + address_component.long_name);
        itemPostalCode = address_component.long_name;
      }

      if (address_component.types[0] === "administrative_area_level_1") {
        $log.debug("state:" + address_component.short_name);
        itemState = address_component.short_name;
      }

      if (address_component.types[0] === "street_number") {
        $log.debug("street_number:" + address_component.long_name);
        itemStreetNumber = address_component.long_name;
      }
    });
    return {
      lat: lat,
      long: long,
      streetNumber: itemStreetNumber,
      street: itemRoute,
      country: itemCountry,
      state: itemState,
      zip: itemPostalCode,
      city: itemLocality
    };
  };

  return {

    getGeocoder : function () {
      if (_geocoder === null) {
        _geocoder = new google.maps.Geocoder();
      }
      return _geocoder;
    },


    codeAddress: function (addressQuery, callback) {
      $log.debug("geocoding address: " + addressQuery);
      this.getGeocoder().geocode({ 'address': addressQuery }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
          if (angular.isFunction(callback)) {
            callback(null, createResultDTO(results[0]));
          }
        } else {
          if (angular.isFunction(callback)) {
            callback(status, null);
          }
          var errorMsg = 'Geocode was not successful for the following reason: ' + status;
          $log.error(errorMsg);
          alert(errorMsg);
        }
      });
    },

    /**
     * @param callback params are err and data, where data is an array
     */
    getNeighborhoodsForLonLat: function (lon, lat, callback) {
      var url = '/api/v1/neighborhoodGeo?lat=' + lat + '&long=' + lon;
      $http.get(url)
        .success(function (data) {
          callback(null, data);
        })
        .error(function (err) {
          callback(err, null);
        });
    },

    /**
     * changes an array of results from a query to neighborhoodsGeo into a list of neighborhood ids
     * @param results
     */
    xformToNeighborhoodGeoIds: function (results) {
      $log.debug("xformToNeighborhoodGeoIds -- " + angular.toJson(results));
      if (_.isEmpty(results) || !angular.isArray(results)) {
        return [];
      }
      var retval = [];
      angular.forEach(results, function (val) {
        retval.push(val.id);
      });
      return retval;
    },

    getHrefForGoogleMapWithLonLat: function (cityNeighborhoodGeo) {
      var zoomLevel = 13;
      if (!cityNeighborhoodGeo || !cityNeighborhoodGeo.long || !cityNeighborhoodGeo.lat) {
        return 'javascript:void(0);';
      }
      return 'http://maps.google.com/maps?q=' + cityNeighborhoodGeo.lat + '+' + cityNeighborhoodGeo.long + '&z=' + zoomLevel + '&ll=' + cityNeighborhoodGeo.lat + ',' + cityNeighborhoodGeo.long;
    }

  };
});

'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('googlePlaces', function ($log) {
  var manhattan = new google.maps.LatLng(40.7903, -73.9597)
    , mapElement = document.getElementById('map')
    , googleMap = new google.maps.Map(mapElement, {
      center: manhattan,
      zoom: 12
    })
    , service = new google.maps.places.PlacesService(googleMap);

  return {
    getDetailsForPlace: function (placeId, callback) {
      $log.debug("searching for google place_id " + placeId);
      var request = {
        placeId: placeId
      };
      service.getDetails(request, function (place, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
          callback(null, place);
        } else {
          callback('Error retrieving place details. ' + angular.toJson(status));
        }
      });
    }
  };
});

'use strict';
/*global angular, $*/

var portalApp = angular.module('myBus');

portalApp.factory('infoOverlay', function () {
  var hideOverlay, displayOverlay;

  hideOverlay = function () {
    $('#infoOverlay').fadeOut();
  };

  displayOverlay = function (info, cssClassToAdd, cssClassToRemove) {
    hideOverlay();
    $('#infoOverlayDetails').html(info);
    $('#infoOverlay').removeClass(cssClassToRemove).addClass(cssClassToAdd).fadeIn();
    setTimeout(hideOverlay, 1800);
  };

  return {

    hideInfo: function () {
      hideOverlay();
    },

    displayInfo: function (info) {
      displayOverlay(info, 'bg-info', 'bg-danger');
    },

    displayErrorInfo: function (info) {
      displayOverlay(info, 'bg-danger', 'bg-info');
    }
  };
});




var portalApp = angular.module('myBus');
portalApp.factory('personService', function ($http, $log) {

    return {
        loadPersons: function (callback) {
            $http.get('/api/v1/persons')
                .success(function (data) {
                    callback(data);
                }).error(function () {
                    alert("Error getting the data from the server");
                });

        },

        createPersons: function (person, callback) {
            $http.post('/api/v1/person', person).success(function (data) {
                callback(data);
                }).error(function () {
                alert("Error saving the data");
            });

        },
        deletePerson:function(personId,callback){
            $http.delete('/api/v1/person/'+personId).success(function(data){
                callback();
            });

        },
        findByIdPerson:function(personId,callback) {
            $http.get('/api/v1/person/' + personId).success(function (data) {
                callback(data);
            });
        },

        updatePerson: function(person,callback) {
            $http.put('/api/v1/person/'+person.id,person).success(function (data) {
                callback(data);
                //$rootScope.$broadcast('updatePersonCompleteEvent');
            });
        }
    }

});


/**
 * Created by svanik on 1/20/2016.
 */

var portalApp = angular.module('myBus');

portalApp.factory('routesManager', function ($rootScope, $http, $log, $window) {

    var routes = {};

    return{
        fetchAllRoutes: function () {
            $log.debug("fetching routes data ...");
            $http.get('/api/v1/routes')
                .success(function (data) {
                    routes = data;
                    $rootScope.$broadcast('RoutesInitComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving cities");
                });
        },

        getRoutes: function (callback) {
            $log.debug("fetching routes data ...");
            $http.get('/api/v1/routes')
                .success(function (data) {
                    callback(data);
                    $rootScope.$broadcast('FetchingRoutesComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving cities");
                });
        },

        getRoute: function(routeId,callback){
             $http.get('/api/v1/route/'+routeId).success(function(data){
                 callback(data);
             })
             .error(function (error) {
                  $log.debug("error retrieving cities");
             });
        },

        getAllRoutes: function () {
            return routes;
        },

        createRoute: function(route,callback){
            $http.post('/api/v1/route',route).success(function(data){
                callback(data);
                $rootScope.$broadcast('CreateRouteCompleted');
                //this.fetchAllRoutes();
            })
                .error(function (err) {
                    var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                    $log.error(errorMsg);
                    alert(errorMsg);
                });
        },

        deleteRoute: function(routeId){
            $http.delete('/api/v1/route/'+routeId).success(function(data){
               $rootScope.$broadcast('DeleteRouteCompleted');
            })
                .error(function(){
                    alert("Error deleting Route");
                });
        },

        updateRoute: function(route,callback){
            $http.put('/api/v1/route/'+route.id,route).success(function(data){
                $rootScope.$broadcast('UpdateRouteCompleted');
            })
                .error(function(){
                    alert("Error Updating Route");
                });
        }

    }

});
"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('userManager', function ($http, $log) {

  var GRP_READ_ONLY = "Read-only"
    , GRP_AUTHOR = "Author"
    , GRP_PUBLISHER = "Publisher"
    , GRP_ADMIN = "Admin"
    , GRP_DEVELOPER = "Developer"
    , GRP_BUSINESS_ADMIN = "Business Admin"
    , currentUser = null
    , currentGroups = null
    , hasRoleReadOnly = null
    , hasRoleAuthor = null
    , hasRolePublisher = null
    , hasRoleAdmin = null
    , hasRoleDeveloper = null
    , hasRoleBusinessAdmin = null;

  return {
    getCurrentUser: function (callback, forceRefresh) {
      if (currentUser === null || forceRefresh) {
        $http.get('/api/v1/user/me')
          .success(function (user) {
            currentUser = user;
            return angular.isFunction(callback) && callback(null, user);
          })
          .error(function (err, status) {
            $log.error('Error getting current user. Status code ' + status + ".  " + angular.toJson(err));
            //angular.isFunction(callback) && callback(err);
            document.location = "/"; // redirect to login
          });
      } else {
        return angular.isFunction(callback) && callback(null, currentUser);
      }
    },
    getUser: function(){
      return currentUser;
    },

    getGroupsForCurrentUser: function (callback, forceRefresh) {
      if (currentGroups === null || forceRefresh) {
        $http.get('/api/v1/user/groups')
          .success(function (groups) {
            currentGroups = groups;
            return angular.isFunction(callback) && callback(null, groups);
          })
          .error(function (err) {
            $log.error('Error getting current user\'s groups. ' + angular.toJson(err));
            return angular.isFunction(callback) && callback(err);
          });
      } else {
        return angular.isFunction(callback) && callback(null, currentGroups);
      }
    },

    isReadOnly: function () {
      if (hasRoleReadOnly === null && currentGroups) {
        hasRoleReadOnly = _.any(currentGroups, function (grp) {
          return GRP_READ_ONLY === grp.name;
        });
      }
      return hasRoleReadOnly;
    },

    isDeveloper: function () {
      if (hasRoleDeveloper === null && currentGroups) {
        hasRoleDeveloper = _.any(currentGroups, function (grp) {
          return GRP_DEVELOPER === grp.name;
        });
      }
      return hasRoleDeveloper;
    },

    isAuthor: function () {
      if (hasRoleAuthor === null && currentGroups) {
        hasRoleAuthor = _.any(currentGroups, function (grp) {
          return GRP_AUTHOR === grp.name;
        });
      }
      return hasRoleAuthor;
    },

    isPublisher: function () {
      if (hasRolePublisher === null && currentGroups) {
        hasRolePublisher = _.any(currentGroups, function (grp) {
          return GRP_PUBLISHER === grp.name;
        });
      }
      return hasRolePublisher;
    },

    isAdmin: function () {
      if (hasRoleAdmin === null && currentGroups) {
        hasRoleAdmin = _.any(currentGroups, function (grp) {
          return GRP_ADMIN === grp.name;
        });
      }
      return hasRoleAdmin;
    },

    isBusinessAdmin: function (businessId) {
      var isBusAdm = false;
      if (hasRoleBusinessAdmin === null && currentGroups) {
        hasRoleBusinessAdmin = _.any(currentGroups, function (grp) {
          return GRP_BUSINESS_ADMIN === grp.name;
        });
      }
      if (hasRoleBusinessAdmin) {
        if (businessId) {
          isBusAdm = currentUser && currentUser.customData && _.contains(currentUser.customData.businessIds, businessId);
        } else {
          isBusAdm = true;
        }
      }
      return isBusAdm;
    },

    canAddPOI: function () {
      return this.isAuthor() || this.isPublisher() || this.isAdmin();
    },

    canEditPOI: function (businessId) {
      return this.isPublisher() || this.isAdmin() || this.isBusinessAdmin(businessId);
    },

    canAddOrEditPOI: function (isAdd, businessId) {
      return isAdd ? this.canAddPOI() : this.canEditPOI(businessId);
    },

    canViewAPIDocs: function () {
      return this.isDeveloper() || this.isAdmin() || this.isPublisher() || this.isAuthor();
    },

    canViewBusinesses: function () {
      return this.isReadOnly() || this.isAuthor() || this.isPublisher() || this.isAdmin() || this.isBusinessAdmin();
    }
  };
});

angular.module('templates.app', []);


angular.module('templates.common', []);

