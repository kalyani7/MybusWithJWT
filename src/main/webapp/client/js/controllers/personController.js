"use strict";
/*global angular, _*/

angular.module('myBus.personModules', ['ngTable', 'ui.bootstrap'])
    .controller('PersonController', function ($http,$scope,$modal,$filter,NgTableParams, personService) {
        $scope.persons = [];
        $scope.currentPageOfPerson = [];
        $scope.headline="Person Details";
      
        $scope.loadPersons = function () {
            personService.fechAmenities();
        };

        $scope.loadPersons();

        $scope.addPersonOnClick = function(){
            var modalInstance = $modal.open({
                templateUrl: 'add-person-modal.html',
                controller: 'AddPersonModalController'
            });
        };

        $scope.deletePersonOnClick = function(personId){
        	 personService.deletePerson(personId);
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
      
        var loadTableData = function (tableParams, $defer) {
            var data = personService.getPersons();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.amenities = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfPerson = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };
        
        $scope.$on('personInitComplete', function (e, value) {
            loadTableData($scope.personContentTableParams);
        });
        $scope.$on('personinitStart', function (e, value) {
        	personService.fechAmenities();
        });
        $scope.personContentTableParams = new NgTableParams(
       		 {
       			 page: 1,
       			 count:25,
       			 sorting: {
       				 state: 'asc',
       				 name: 'asc'
       			 }
       		 }, 
       		 {
       			 total: $scope.currentPageOfPerson.length,
       			 getData: function ($defer, params) {
       				 $scope.$on('personInitComplete', function (e, value) {
       					 loadTableData(params);
       				 });
       			 }
       		 }
        );
    })

    .controller('AddPersonModalController',function($scope,$modalInstance,$http,$log,personService){
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

    .controller('UpdatePersonModalController',function($scope,$modalInstance,$http,$log,personService,fetchId, cityManager){
        $scope.person = {};
        $scope.personId=fetchId;
        $scope.citySelected = null;
        $scope.citySelectedForUi=[];
        $scope.addLivingCity = function(city){
        	city= JSON.parse(city);
        	$log.debug("city -"+city);
        	if(!$scope.person.citiesLived){
        		$scope.person.citiesLived = [];
        		$scope.citySelectedForUi = [];
        	}
        	if($scope.person.citiesLived.indexOf(city.id) == -1) {
                $scope.person.citiesLived.push(city.id);
                $scope.citySelectedForUi.push(city);
            }else {
            	$log.debug("city already added");
            }
        };
        $scope.removeLivingCity = function(cityId){
            var index = $scope.person.citiesLived.indexOf(cityId);
            if(index!= -1) {
                $scope.person.citiesLived.splice(index, 1);
                $scope.citySelectedForUi.splice(index,1)
            }else {
            	$log.debug("city already removed");
            }
        };
        $scope.displayPersons = function(data){
            $scope.person = data;
            $scope.cities = [];
            cityManager.getCities(function(data) {
            	$scope.cities = data;
            	angular.forEach(data,function(city){
            		if($scope.person.citiesLived){
            			if($scope.person.citiesLived.indexOf(city.id)!=-1){
            				$scope.citySelectedForUi.push(city);
            			}
            		}
            	})
            });
        };

        $scope.setPersonIntoView = function(fetchId){
            personService.findByIdPerson(fetchId,$scope.displayPersons);
        };
        $scope.setPersonIntoView(fetchId);

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.firstCallBack = function(){
        	$log.debug("executing function1");
        }

        $scope.ok = function (fetchId) {
        	if ($scope.person.name === null || $scope.person.age === null || $scope.person.phone == null) {
        		$log.error("Empty person data.  nothing was added.");
        		$modalInstance.close(null);
        	}
        	personService.updatePerson($scope.person, function(data){
        		$log.debug("we are at OK");
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
    });
