"use strict";
/*global angular, _*/

angular.module('myBus.serviceComboModule', ['ngTable', 'ui.bootstrap'])
.controller("ServiceComboController",function($rootScope, $scope, $uibModal, $location, $filter, $log,NgTableParams,paginationService,serviceComboManager) {
    $scope.headline = "Service Combos";
    $scope.loading = false;
    $scope.currentPageOfCombos = [];
    var pageable ;
    var loadTableData = function (tableParams) {
        $scope.loading = true;
        paginationService.pagination(tableParams, function(response){
            pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
        });
        serviceComboManager.getAll(pageable, function (response) {
        	console.log(response);
            if (angular.isArray(response)) {
                $scope.loading = false;
                $scope.serviceCombos = response;
                tableParams.total(response.totalElemets);
                $scope.count = response.totalElements;
                tableParams.data = $scope.serviceCombos;
                $scope.currentPageOfCombos = $scope.serviceCombos;
            }
        });
    };

    $scope.init = function() {
        serviceComboManager.count(function (serviceCombosCount) {
            $scope.serviceComboTableParams = new NgTableParams({
                    page: 1,
                    size: 10,
                    count: 10,
                    sorting: {
                        serviceNumber : 'asc'
                    }
                },{
                    counts:[],
                    total: serviceCombosCount,
                    getData: function (params) {
                        loadTableData(params);
                    }
			});
		})
	}

    $scope.init();

    $scope.$on('reloadServiceCombo', function (e, value) {
        $scope.init();
    });
	$scope.delete = function(id){
		serviceComboManager.delete(id,function(data){
		});
	}

	$scope.handleClickEdit = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'update-serviceCombo-modal.html',
	        controller : 'EditServiceComboModalController',
	        resolve : {
				comboId : function(){
	                return id;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditServiceComboModalController', function ($scope, $rootScope, $location, $uibModal, $http, $log, serviceComboManager, comboId) {
    $scope.serviceCombo = {};
	$scope.save =function(){
		serviceComboManager.save($scope.serviceCombo,function(data){
			$rootScope.modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.serviceCombo.name || '') !== '';
    };
	if(comboId) {
		serviceComboManager.getByID(comboId,function(data){
			$scope.serviceCombo = data;
		});
	}

    $scope.resetAmenity = function(){
		$scope.serviceCombo= {};
	};
})
.factory("serviceComboManager",function($rootScope,$http,$location,$log){
	var serviceCombos = [];
	return {
		getAll : function(pageable, callback){
			$http({url: '/api/v1/serviceCombos',method: "GET",params: pageable})
                .then(function(response){
				serviceCombos= response.data;
				callback(serviceCombos);
			},function(error){
				swal("oops", error, "error");
			});
		},
		getServiceCombos :function(){
			return serviceCombos;
		},
		count: function (callback) {
        $http.get('/api/v1/serviceCombos/count',{})
            .then(function (response) {
                callback(response.data);
            },function (error) {
                $log.debug("error retrieving service combos");
            });
        },
		save: function(serviceCombo,callback) {
			if(!serviceCombo.id) {
				$http.post("/api/v1/serviceCombo",serviceCombo).then(function(response){
					$location.url('servicecombo');
					$rootScope.modalInstance.close();
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Great", "ServiceCombo has been successfully added", "success");
				},function(error){
					swal("oops", error, "error");
				});
			} else {
				$http.put("/api/v1/serviceCombo",serviceCombo).then(function(response){
					$location.url('servicecombo');
					$rootScope.modalInstance.close();
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Great", "ServiceCombo has been updated successfully", "success");
				},function(error){
					swal("oops", error, "error");
				});
			}
		},
		getByID : function(id,callback){
			$http.get("/api/v1/serviceCombo/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},
		delete : function(id,callback){
			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this ServiceCombo?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){
				$http.delete("/api/v1/serviceCombo/"+id).then(function(data){
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Deleted!", "ServiceCombo has been deleted successfully!", "success");
				},function(error){
					swal("Oops", "We couldn't connect to the server!", "error");
				})
			})
		}
	}
});;