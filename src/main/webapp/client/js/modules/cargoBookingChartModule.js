"use strict";
/*global angular, _*/

angular.module('myBus.cargoBookingChart', ['ngTable', 'ui.bootstrap', 'angularMoment', 'n3-line-chart'])
    .controller("CargoBookingChartController", function ($rootScope, $scope, moment, cargoBookingChartManager) {
        $scope.headline = 'CargoBookingChart';

        cargoBookingChartManager.getAllcargoBookingChart(function(response) {
            $scope.cargoBookingChart = response;
            $scope.data = {};
            var baranchName;
            for (var i = 0; i < $scope.cargoBookingChart.length; i++) {
                baranchName = $scope.cargoBookingChart[i].branchName;
                var dataset1 = [];
                for (var j = 0; j < $scope.cargoBookingChart[i].Bookings.length; j++) {
                    var dateFormate = new Date($scope.cargoBookingChart[i].Bookings[j].date);
                    $scope.cargoBookingChart[i].Bookings[j].date = dateFormate;
                    dataset1.push($scope.cargoBookingChart[i].Bookings[j]);
                }
                $scope.data[baranchName] = dataset1;
            }

            // Line Chart
            $scope.options = {

                series:[],

                axes: {
                    x: {
                        key: 'date',
                        type: 'date'
                    },
                    y: {
                        min: 0,
                        // max: 10000,
                    }
                },

                zoom: {
                    x: true,
                    // y: true,
                    key: 'altKey'
                },
                margin: {
                    top: 40,
                    right: 40,
                    bottom: 40,
                    left: 40
                },

                grid: {
                    x: true,
                    y: true,
                    doubleClickEnabled: true
                },

                pan: {
                    x: function(newDomain) {
                        return newDomain
                    },
                    x2: true,
                    y: false,
                    y2: false

                }

            };

            for (var l = 0; l < $scope.cargoBookingChart.length; l++) {
                var seriesData = [];
                seriesData.push({
                    axis: "y",
                    dataset: $scope.cargoBookingChart[l].branchName,
                    key: "BookingsTotal",
                    label: $scope.cargoBookingChart[l].branchName,
                    interpolation: {mode: 'cardinal', tension: 0.7},
                    color: "rgb(126, 181, 63)",
                    type: [
                        'line',
                        'dot',
                        'area'
                    ],
                    id: $scope.cargoBookingChart[l]._id,
                    visible: false
                });
                $scope.options.series.push(seriesData[0]);
            }

            $scope.options.series[5].visible = true;
            for (var c = 0; c < $scope.options.series.length; c++) {
                var num = Math.round(0xffffff * Math.random());
                var r = num >> 16;
                var g = num >> 8 & 255;
                var b = num & 255;
                $scope.options.series[c].color = 'rgb(' + r + ', ' + g + ', ' + b + ')';
            }

            // Column Chatr

            $scope.optionsColumn = {

                series:[],

                axes: {
                    x: {
                        key: 'date',
                        type: 'date'
                    },
                    y: {
                        min: 0,
                        // max: 10000,
                    }
                },

                zoom: {
                    x: true,
                    // y: true,
                    key: 'altKey'
                },
                margin: {
                    top: 40,
                    right: 40,
                    bottom: 40,
                    left: 40
                },

                grid: {
                    x: true,
                    y: true,
                    doubleClickEnabled: true
                },

                pan: {
                    x: function(newDomain) {
                        return newDomain
                    },
                    x2: true,
                    y: false,
                    y2: false

                }

            };

            for (var colum = 0; colum < $scope.cargoBookingChart.length; colum++) {
                var seriesData = [];
                seriesData.push({
                    axis: "y",
                    dataset: $scope.cargoBookingChart[colum].branchName,
                    key: "BookingsTotal",
                    label: $scope.cargoBookingChart[colum].branchName,
                    interpolation: {mode: 'cardinal', tension: 0.7},
                    color: "rgb(126, 181, 63)",
                    type: [
                        'column'
                    ],
                    id: $scope.cargoBookingChart[colum]._id,
                    visible: false
                });
                $scope.optionsColumn.series.push(seriesData[0]);
            }

            $scope.optionsColumn.series[5].visible = true;
            for (var columcolor = 0; columcolor < $scope.optionsColumn.series.length; columcolor++) {
                var num = Math.round(0xffffff * Math.random());
                var r = num >> 16;
                var g = num >> 8 & 255;
                var b = num & 255;
                $scope.optionsColumn.series[columcolor].color = 'rgb(' + r + ', ' + g + ', ' + b + ')';
            }
        });
    })
    .factory('cargoBookingChartManager', function ($rootScope, $q, $http, services) {
        var cashTransfer = {};
        return {
            getAllcargoBookingChart: function (callback) {
                services.get('/api/v1/shipment/groupCargoBookings', '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                }, function (error) {
                    swal("oops", error, "error");
                })
                // $http({url: '/api/v1/shipment/groupCargoBookings', method: "GET"})
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function(error){
                //         swal("oops", error, "error");
                //     });
            },
        };
    });