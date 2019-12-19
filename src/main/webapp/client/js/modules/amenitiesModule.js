"use strict";
/*global angular, _*/

angular.module('myBus.amenitiesModule', ['ngTable', 'ui.bootstrap'])
.controller("AmenitiesController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,paginationService, amenitiesManager){
	$scope.headline = "Amenities";
	$scope.amenityTableParams = {};
    $scope.loading = false;
	$scope.currentPageOfAmenities=[];
    $scope.amenitiesCount = 0;
	$scope.amenity = {};
	var pageable ;
	console.log("amenities controller...");
	var loadTableData = function (tableParams) {
		paginationService.pagination(tableParams, function(response){
			pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
		});
		$scope.loading = true;
		amenitiesManager.getAllAmenities(pageable, function(response){
			if(angular.isArray(response.content)){
				$scope.loading = false;
				$scope.amenities = response.content;
				tableParams.total(response.totalElements);
				$scope.count = response.totalElements;
				tableParams.data = $scope.amenities;
				$scope.currentPageOfAmenities = $scope.amenities;
			}
		})
	 };

	 $scope.init = function(){
	 	amenitiesManager.count(function(amenitiesCount){
             $scope.amenityTableParams = new NgTableParams(
                 {
                     page: 1,
                     size: 10,
                     count: 10,
                     sorting: {
                         name: 'asc'
                     }
                 },
                 {
                 	counts:[],
                     total: amenitiesCount,
                     getData: function (params) {
                         loadTableData(params);
                     }
                 }
             )
         })
    };
	 $scope.init();

	$scope.$on('amenitiesInitComplete', function (e, value) {
        $scope.init();
	});

	 $scope.getAllAmenities = function(){
		amenitiesManager.getAllAmenities(function(data){
			$scope.amenities =data;
		});
	 };

	$scope.deleteAmenityById = function(amenityID){ 
		amenitiesManager.deleteAmenity(amenityID,function(data){
			$scope.amenity = data;
		});
	};

	$scope.handleClickAddAmenity = function (size) {
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl: 'add-Amenity-modal.html',
	        controller: 'AddAmenityModalController',
	        size: size
	    	});
	    };

	$scope.handleClickUpdateAmenity = function(amenityID){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'update-amenity-modal.html',
	        controller : 'UpdateAmenityModalController',
	        resolve : {
	            amenityId : function(){
	                return amenityID;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('UpdateAmenityModalController', function ($scope, $rootScope, $uibModal, $http, $log, amenitiesManager, amenityId) {
	$log.debug("in UpdateAmenityModalController");
    $scope.amenity = {};
	$scope.updateAmenity =function(){ 
		amenitiesManager.updateAmenity($scope.amenity,function(data){
			$scope.amenity = data;
			$rootScope.modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
    };
    amenitiesManager.getAmenityByID(amenityId,function(data){
    	$scope.amenity = data;
	});
    $scope.resetAmenity = function(){
		$scope.amenity= {};
	};
})

//
// ========================== Modal - Add Amenity =================================
//
.controller('AddAmenityModalController', function ($scope,$state, $http, $log, $rootScope,amenitiesManager) {
	$log.debug("in AddAmenityModalController");
	
    $scope.amenity = {
        name: null,
        active: false
    };
    
    $scope.addAmenity = function(){ 
    	amenitiesManager.addAmenity($scope.amenity,function(data){
    		$scope.amenity = data;
			$rootScope.modalInstance.close(data);
    	});
    };
	
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
    };
    
})
.factory("amenitiesManager",function($rootScope,$http,$window,$log){
	var amenities = [];
	return {

        fechAmenities: function () {
            $http.get("/api/v1/amenities").then(function (response) {
                amenities = response.data;
            }, function (error) {
                swal("oops", error, "error");
            });
        },

        getAmenities: function () {
            return amenities;
        },
        getAllAmenities: function ( pageable, callback) {
            $http({url: '/api/v1/amenities', method: "GET", params: pageable})
				.then(function (response) {
			callback(response.data);
        }, function(error){
            swal("oops", error, "error");
        });
		},
        count: function ( callback) {
            $http.get('/api/v1/amenities/count',{})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving amenities");
                });
        },

		getAmenitiesName : function() {
			return $http(
				{
					method:'GET',
					url:'/api/v1/amenities'
				}
			);
		},
		addAmenity: function(amenity,callback) {
			$http.post("/api/v1/amenity",amenity).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('amenitiesInitComplete');
				swal("Great", "Amenity has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},

		getAmenityByID : function(amenityID,callback){
			$http.get("/api/v1/amenity/"+amenityID).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},

		updateAmenity : function(amenity,callback){
			$http.put("/api/v1/amenity",amenity).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('amenitiesInitComplete');
				swal("Great", "Amenity has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},
		deleteAmenity : function(amenityID,callback){

			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Amenity?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

				$http.delete("/api/v1/amenity/"+amenityID).then(function(data){
					callback(data);
					$rootScope.$broadcast('amenitiesInitComplete');
					swal("Deleted!", "Amenity has been deleted successfully!", "success");
				},function(error){
					swal("Oops", "We couldn't connect to the server!", "error");
				});

			});
		}
	}
});