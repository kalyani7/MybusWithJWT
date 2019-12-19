"use strict";
/*global angular, _*/

angular.module('myBus.collectionZoneModule', ['ngTable', 'ui.bootstrap'])
.controller("CollectionZonesController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,collectionZonesManager){
	$scope.headline = "Collection Zones";
	$scope.collectionZones = [];
    $scope.loading = false;
	var loadTableData = function (tableParams) {
		$scope.loading = true;
        collectionZonesManager.getAll(function(response){
			if(angular.isArray(response.content)){
				$scope.loading = false;
				$scope.collectionZones = response.content;
			}
		})
	 };

	 $scope.init = function(){
             $scope.tableParams = new NgTableParams(
                 {
                     page: 1,
                     sorting: {
                         name: 'asc'
                     }
                 },
                 {
                 	counts:[],
                     getData: function (params) {
                         loadTableData(params);
                     }
                 }
			 );
    };
	 $scope.init();

	$scope.deleteById = function(id){
		collectionZonesManager.deleteCollectionZone(id,function(data){
            loadTableData();
		});
	};

	$scope.handleClickAddCollectionZone = function () {
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl: 'add-collectionZone-modal.html',
	        controller: 'EditCollectionZoneController',
            resolve: {
	            id: function () {
                    return undefined;
                }
            }
	    }),
            $scope.modalInstance.result.then(function () {
                $scope.init();
            });
	    };

	$scope.handleClickUpdateAmenity = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'add-collectionZone-modal.html',
	        controller : 'EditCollectionZoneController',
	        resolve : {
	            id : function(){
	                return id;
	            }
	        }
	    }),
            $scope.modalInstance.result.then(function () {
                $scope.init();
            });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditCollectionZoneController', function ($scope, $rootScope, $uibModal, $http, $log, collectionZonesManager, id) {
	$log.debug("in UpdateAmenityModalController");
    $scope.collectionZone = {};
	$scope.save =function(){
		if(!$scope.collectionZone.id){
            collectionZonesManager.addCollectionZone($scope.collectionZone,function(data){
                $scope.collectionZone = data;
                $rootScope.modalInstance.close(data);
            });
		} else{
            collectionZonesManager.updateCollectionZone($scope.collectionZone,function(data){
                $scope.collectionZone = data;
                $rootScope.modalInstance.close(data);
            });
		}

	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };
    if(id) {
        collectionZonesManager.getCollectionZoneByID(id,function(data){
            $scope.collectionZone = data;
        });
	}

    $scope.resetCollectionZone = function(){
		$scope.collectionZone= {};
	};
})
.factory("collectionZonesManager",function($rootScope,$http,$window,$log){
	var collectionZones = [];
	return {

        getAll: function (callback) {
            $http.get("/api/v1/collectionZones/all").then(function (response) {
                collectionZones = response.data;
                callback(collectionZones);
            }, function (error) {
                swal("oops", error, "error");
            });
        },
        count: function ( callback) {
            $http.get('/api/v1/collectionZones/count',{})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving collectionZones");
                });
        },
		addCollectionZone: function(collectionZone,callback) {
			$http.post("/api/v1/collectionZones/",collectionZone).then(function(response){
				callback(response.data);
				swal("Great", "CollectionZone has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},

		getCollectionZoneByID : function(id,callback){
			$http.get("/api/v1/collectionZones/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},

		updateCollectionZone : function(collectionZone,callback){
			$http.put("/api/v1/collectionZones/",collectionZone).then(function(response){
				callback(response.data);
				swal("Great", "CollectionZone has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},
		deleteCollectionZone : function(id,callback){

			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this CollectionZone?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

				$http.delete("/api/v1/collectionZones/"+id).then(function(data){
					callback(data);
					swal("Deleted!", "CollectionZone has been deleted successfully!", "success");
				},function(error){
					swal("Oops", "We couldn't connect to the server!", "error");
				});

			});
		}
	}
});