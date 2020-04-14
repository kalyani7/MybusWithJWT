"use strict";

angular.module('myBus.inventoriesModule', ['ngTable', 'ui.bootstrap'])
    .controller("addInventoryController",function($scope,inventoryManager,suppliersManager,$stateParams,$state){
        $scope.inventory = {};
        $scope.others = {name: "others"};
        $scope.pageTitle = "Add Inventory";
        suppliersManager.getSuppliers(function(response){
            $scope.suppliers = response;
            $scope.suppliers.push($scope.others);
        });
        if($stateParams.id){
            $scope.pageTitle = "Edit Inventory";
            inventoryManager.getInventory($stateParams.id,function(data) {
                $scope.inventory = data;
                if ($scope.inventory.supplierType === 'others'){
                    $scope.inventory.supplierType = _.find($scope.suppliers, function (supplier) {
                        return supplier.id === $scope.inventory.supplierId;
                    });
                } else {
                    $scope.inventory.supplierType = _.find($scope.suppliers, function (supplier) {
                        return supplier.id === $scope.inventory.supplierType;
                    });
                }
            });
        }
        $scope.save = function(){
            if ($scope.inventory.supplierType.name === 'others') {
                $scope.inventory.supplierType = $scope.inventory.supplierType.name;
            }else{
                $scope.inventory.supplierType = $scope.inventory.supplierType.id;
            }
            if (!$scope.inventory.uniqueId) {
                swal("Error", "Please enter a UniqueId", "error");
            }else {
                if ($stateParams.id) {
                    inventoryManager.updateInventory($scope.inventory, function (response) {
                        $state.go('home.inventories');
                    });
                } else {
                    inventoryManager.addInventory($scope.inventory, function (response) {
                        $state.go('home.inventories');
                    });
                }
            }
        };
        $scope.cancel = function(){
            $state.go('home.inventories');
        };

    }).controller("InventoriesController",function($scope,inventoryManager,$state,NgTableParams,paginationService){
        var pageable;

        var loadTableParams = function(tableParams){
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(),
                    size:tableParams.count(),
                    sort:response,
                    query:tableParams.inventoryName
                };
            });
            inventoryManager.getAllInventories(pageable,function(response){
                if(angular.isArray(response.content)){
                    $scope.inventories = response.content;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.inventories;
                }
            });
        };
        $scope.init = function(inventoryName){
            inventoryManager.count(inventoryName,function(count) {
                $scope.inventoryTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    sorting: {
                        date: 'desc'
                    }
                }, {
                    counts: [20, 50, 100],
                    total: count,
                    getData: function (params) {
                        params.inventoryName = inventoryName;
                        loadTableParams(params);
                    }
                });
            });
        };
        $scope.editInventory = function(id){
            $state.go('home.add-editInventory', {id:id});
        };
        $scope.init('');
        $scope.deleteInventory = function(id){
          inventoryManager.deleteInventory(id,function(response){
              $scope.init('');
          });
        };
        $scope.viewJob = function(inventoryId){
            $state.go('home.viewJobsByInventory', {inventoryId:inventoryId});
        };
    }).controller("viewJobsByInventoryController",function($scope,$stateParams,jobManager){
        $scope.getQuery = {inventoryId:$stateParams.inventoryId};
        jobManager.getAllsearchJobs($scope.getQuery,function (response) {
            $scope.inventoryJobs = response.data.content;
            $scope.jobsCount = $scope.inventoryJobs.length;
        });
    }).factory("inventoryManager",function($http, services){
        return{
            addInventory: function(inventory, callback) {
                services.post('/api/v1/inventory/addInventory', inventory, function (response) {
                    if (response) {
                        callback(response)
                    }
                }, function(err) {})
                // $http.post('/api/v1/inventory/addInventory',inventory)
                //     .then(function (response) {
                //         callback(response);
                //         },function(err) {});
            },
            getAllInventories: function (pageable, callback) {
                services.get('/api/v1/inventory/getAllInventories', pageable, function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function (error) {
                    swal("oops", error, "error");
                })
                // $http({url: '/api/v1/inventory/getAllInventories', method: "GET", params: pageable})
                //     .then(function (response) {
                //         callback(response.data);
                //     }, function(error){
                //         swal("oops", error, "error");
                //     });
            },
            getInventory: function(id, callback) {
                services.get('/api/v1/inventory/get/' + id, '', function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function(err) {})
                // $http.get('/api/v1/inventory/get/'+id)
                //     .then(function (response) {
                //         callback(response.data);
                //     },function(err) {});
            },
            updateInventory: function(inventory, callback) {
                services.put('/api/v1/inventory/updateInventory', inventory, '', function (response) {
                    if (response) {
                        callback(response)
                    }
                }, function (error) {})
                // $http.put('/api/v1/inventory/updateInventory',inventory)
                //     .then(function (response) {
                //         callback(response);
                //     },function(err) {});
            },
            count:function (query, callback) {
                services.get('/api/v1/inventory/getCount?query=' + query, '', function (response) {
                    if (response) {
                        callback(response.data)
                    }
                }, function (error) {})
                // $http.get('/api/v1/inventory/getCount?query=' + query)
                //     .then(function (response) {
                //         callback(response.data);
                //     },function(err) {});
            },
            deleteInventory: function(id,callback) {
                services.delete('/api/v1/inventory/delete/' + id, function (response) {
                    if (response) {
                        callback(response);
                        sweetAlert("Great", "successfully deleted", "success");
                    }
                }, function (error) {
                    sweetAlert("Oops...", "Error finding data!", "error" + angular.toJson(error));
                })
                // swal({   title: "Are you sure?",   text: "You will not be able to recover this inventory !",
                //     type: "warning",
                //     showCancelButton: true,
                //     confirmButtonColor: "#DD6B55",
                //     confirmButtonText: "Yes, delete it!",
                //     closeOnConfirm: false
                // }, function() {
                //     $http.delete('/api/v1/inventory/delete/' + id)
                //         .then(function (response) {
                //             callback(response);
                //             sweetAlert("Great", "successfully deleted", "success");
                //         },function (error) {
                //             sweetAlert("Oops...", "Error finding data!", "error" + angular.toJson(error));
                //         });
                // });
            }
        }
    });