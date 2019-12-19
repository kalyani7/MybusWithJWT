//"use strict";
/*global angular, _*/

angular.module('myBus.layoutModules', ['ngTable', 'ui.bootstrap'])


// ==================================================================================================================
// ====================================    BusLayoutController   ================================================
// ==================================================================================================================

    .controller('BusLayoutController', function ($scope, $http, $log, NgTableParams, $filter, busLayoutManager, $location, $state, paginationService) {
        $log.debug('BusLayoutController loading');

        $scope.currentPageOfLayouts = [];


        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layouts";

        $scope.goToBusLayout = function (busId) {
            $location.url('/layouts/' + busId);
        };

        $scope.deleteLayout = function (layout) {
            busLayoutManager.deleteLayout(layout.id);
            $location.url('/layouts');
        };

        $scope.$on('layoutsDeleteComplete', function (e, value) {
            loadTableData($scope.layoutContentTableParams);
        });

        $scope.addNewBusLayout = function () {
            $state.go("addLayouts");
        }

        var pageable;

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function (response) {
                pageable = {
                    page: tableParams.page(),
                    size: tableParams.count(),
                    sort: response,
                };
            });
            busLayoutManager.getAllLayouts(pageable, function (response) {
                if (angular.isArray(response)) {
                    $scope.layouts = response;
                    $scope.count = response.length;
                    tableParams.data = $scope.layouts;
                    $scope.currentPageOfLayouts = $scope.layouts;
                }
            });
        };

        $scope.init = function () {
            $scope.layoutContentTableParams = new NgTableParams({
                page: 1, // show first page
                size: 10,
                sorting: {
                    date: 'desc'
                }
            }, {
                counts: [20, 50, 100],
                total: $scope.currentPageOfLayouts.length,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };

        $scope.init();

    })
    .controller('BusLayoutEditController', function ($rootScope, $window, $scope, $http, $log, NgTableParams, $filter, busLayoutManager, $stateParams, $location, $cacheFactory, $state) {
        $log.debug('BusLayoutController loading');
        $scope.valid = false;

        $scope.totalSeats = 0;

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layout Details";

        $scope.busLayout = {
            rows: null,
            type: null,
            name: null,
            upper: null,
            lower: null,
            upperHeader: '',
            lowerHeader: ''
        };

        $scope.upperHeader = "Upper Deck";
        $scope.lowerHeader = "Lower Deck";


        $scope.lowerDeck = [];
        $scope.upperDeck = [];
        $scope.oldRows=0;
        $scope.totalRows=0;

        $scope.initRows = function () {
            if($scope.oldRows<$scope.totalRows){
                for (var i =$scope.oldRows; i < $scope.totalRows; i++) {
                    $scope.lowerDeck.push({noOfSeats: 0, seats: [], rowType: '', window:Boolean});
                    if ($scope.hasUpperDeck) {
                        $scope.upperDeck.push({noOfSeats: 0, seats: [], rowType: '', window:Boolean});
                    }
                }
            }else{
                for (var i =$scope.oldRows; i > $scope.totalRows; i--) {
                    $scope.lowerDeck.pop();
                    if ($scope.hasUpperDeck) {
                        $scope.upperDeck.pop();
                    }
                }
                if (!$scope.hasUpperDeck) {
                    $scope.oldRows=$scope.totalRows;
                }
            }
            $scope.oldRows=$scope.totalRows;

        };
        $scope.initUpperDeck = function () {
            $scope.upperDeck = [];
            for (var i =0; i < $scope.totalRows; i++) {
                if ($scope.hasUpperDeck) {
                    $scope.upperDeck.push({noOfSeats: 0, seats: [], rowType: '', window:Boolean});
                }
            }
        };

        $scope.getNumber = function (r, num) {
            var n = parseInt(num);
            r.seats = [];
            if (n > 0) {
                for (var i = 0; i < num; i++) {
                    r.seats.push({number: "", displayName: '',  seatStatus: "AVAILABLE"});
                }
            }
        };

        var layOutId = $stateParams.id;
        if (layOutId) {
            busLayoutManager.layoutById(layOutId, function (data) {
                $scope.busLayout = data.data;
                if ($scope.busLayout) {
                    $scope.name = $scope.busLayout.name;
                    $scope.totalRows = $scope.busLayout.totalRows;
                    $scope.totalSeats = $scope.busLayout.totalSeats;
                    $scope.active = $scope.busLayout.active;
                    $scope.lowerDeck = $scope.busLayout.lowerDeck;
                    $scope.hasUpperDeck = $scope.busLayout.hasUpperDeck;
                    $scope.upperDeck = $scope.busLayout.upperDeck;
                    $scope.oldRows = $scope.totalRows;
                }
            });
        }


        $scope.saveLayout = function () {
            var totalSeats=0;
            $scope.lowerDeck.forEach(function(r){
                totalSeats+=parseInt(r.noOfSeats);
            });
            if($scope.hasUpperDeck) {
                $scope.upperDeck.forEach(function (r) {
                    totalSeats += parseInt(r.noOfSeats);
                });
            }
            var layoutToSave = {
                name: $scope.name,
                totalSeats:totalSeats,
                totalRows:  $scope.lowerDeck.length,
                hasUpperDeck:$scope.hasUpperDeck,
                lowerDeck: $scope.lowerDeck,
                active: $scope.active,
                id : $scope.busLayout.id
            };

            if ($scope.hasUpperDeck) {
                layoutToSave.upperDeck = $scope.upperDeck;
            }

            if (layOutId) {
                busLayoutManager.updateLayout(layoutToSave);
                $state.go('layouts');
            } else {
                busLayoutManager.createLayout(layoutToSave, function (data) {
                    if (data) {
                        $state.go('layouts');
                    }
                });
            }
        };

        $scope.cancel = function () {
            $state.go('layouts');
        };

    })
    .factory('busLayoutManager', function ($rootScope, $http, $log) {
        return {
            getAllLayouts: function (pageable, callback) {
                $http({url: '/api/v1/layouts', method: "GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        swal("oops", error, "error");
                    });
            },
            getActiveLayoutNames: function () {
                return $http({
                    method: 'GET',
                    /*url:'/api/v1/documents/layout?fields=id,name'*/
                    url: '/api/v1/layouts'
                });
            },
            createLayout: function (layout, callback) {
                $http.post('/api/v1/layout', layout)
                    .then(function (data) {
                        callback(data);
                        swal("Great", "Layout has been successfully created", "success");
                        $rootScope.$broadcast('layoutsCreateComplete');
                    }, function (err) {
                        var errorMsg = "error adding new layout info. " + (err && err.error ? err.error : '');
                        $log.error(errorMsg);
                    });
            },
            layoutById: function (id, callback) {
                $http.get('/api/v1/layout/' + id).then(function (data) {
                    callback(data);
                    $rootScope.$broadcast('layoutsCreateComplete');
                });
            },
            updateLayout: function (layout, callback) {
                $http.put('/api/v1/layout', layout).then(function (data) {
                    // callback(data);
                    swal("Great", ":Layout has been successfully Update", "success");
                    $rootScope.$broadcast('layoutsCreateComplete');
                });
            },
            deleteLayout: function (id, callback) {
                swal({
                    title: "Are you sure?", text: "You will not be able to recover this Job !",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false
                }, function () {
                    $http.delete('/api/v1/layout/' + id)
                        .then(function (response) {
                            // callback(response);
                            swal("Great", "Layout has been successfully deleted", "success");
                            $rootScope.$broadcast('layoutsDeleteComplete');
                        }, function (error) {
                            swal("Oops...", "Error finding data!", "error" + angular.toJson(error));
                        });
                });
            }
        };
    });