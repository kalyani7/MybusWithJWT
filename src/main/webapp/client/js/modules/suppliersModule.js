"use strict";
/*global angular, _*/

angular.module('myBus.suppliers', ['ngTable', 'ui.bootstrap'])
.controller("SuppliersListController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,paginationService, suppliersManager){
	$scope.headline = "Suppliers";
	$scope.suppliers = [];
	var loadTableData = function (tableParams) {
		$scope.loading = true;
        suppliersManager.getSuppliers(function(response){
			if(angular.isArray(response)){
				$scope.loading = false;
				$scope.suppliers = response;
			}
		});
	 };

    $scope.init = function() {
        $scope.tableParams = new NgTableParams({
            sorting: {
                name: 'asc'
            }
        }, {
            counts: [],
            getData: function (params) {
                loadTableData(params);
            }
        });
    };

    $scope.init();
    $scope.$on('reloadSuppliers', function (e, value) {
        loadTableData();
    });
    $scope.$on('reloadFillingStations', function (e, value) {
        $scope.init();
    });
	$scope.handleEdit = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'edit-supplier-modal.html',
	        controller : 'EditSupplierController',
	        resolve : {
                supplierId : function(){
	                return id;
	            }
	        }
	    });
	};

	$scope.delete = function (id) {
		suppliersManager.deleteSupplier(id, function(response){});
	};

})
// ========================== Modal - Update Amenity  =================================

.controller('EditSupplierController', function ($scope, $rootScope, $uibModal, $http, $log, suppliersManager, supplierId) {
	$scope.supplier = {};
	$scope.save =function(){
		if(supplierId){
            suppliersManager.updateSupplier($scope.supplier, function(data){
                $scope.supplier = data;
                $rootScope.modalInstance.close(data);
            });
		} else {
            suppliersManager.addSupplier($scope.supplier, function(data){
                $scope.supplier = data;
                $rootScope.modalInstance.close(data);
            });
		}
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };
	if(supplierId) {
        suppliersManager.getSupplier(supplierId,function(data){
            $scope.supplier = data;
        });
	}
    $scope.reset = function(){
		$scope.supplier= {};
	};
}).factory("suppliersManager",function($rootScope,$http,$log, services){
	return {

        getSuppliers: function (successCallback, errorCallback) {
        	services.get('/api/v1/suppliers/', '', function (response) {
        		if (response) {
					successCallback(response);
				}
			}, function (error) {
				errorCallback(error)
				swal("oops", error, "error");
			})
            // $http.get("/api/v1/suppliers/").then(function (response) {
            //     callback(response.data);
            // }, function (error) {
            //     swal("oops", error, "error");
            // });
        },

		addSupplier: function(fillingStation,callback) {
			$http.post("/api/v1/suppliers/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "Supplier has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			});
		},

		getSupplier : function(id,callback){
			$http.get("/api/v1/suppliers/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			});
		},

		getSuppliersBypartyType: function (partyType, callback) {
			$http.get("/api/v1/suppliers/getAllSuppliers/"+ partyType).then(function (response) {
				callback(response.data);
			}, function (error) {
				swal("oops", error, "error");
			});
		},

		updateSupplier : function(fillingStation,callback){
			$http.put("/api/v1/suppliers/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "Supplier has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			});
		},

		deleteSupplier: function (id) {
			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Supplier?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"
			}, function () {

				$http.delete('api/v1/suppliers/' + id).then(function (response) {
					$rootScope.$broadcast('reloadFillingStations');
					swal("Deleted!", "Supplier was successfully deleted!", "success");
				}, function () {
					swal("Oops", "We couldn't connect to the server!", "error");
				});
			});
		},

	};
});
