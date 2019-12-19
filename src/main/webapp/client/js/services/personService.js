var portalApp = angular.module('myBus');
portalApp.factory('personService', function ($http, $rootScope,$log) {
	var persons = [];
    return {

		fechAmenities : function(){
			$http.get('/api/v1/persons')
            .success(function (data) {
            	persons= data;
            	$rootScope.$broadcast('personInitComplete');
            }).error(function () {
                alert("Error getting the data from the server");
            });
		},
		loadPersons: function (callback) {
			$http.get('/api/v1/persons')
			.success(function (data) {
				callback(data);
				persons= data;
			}).error(function () {
				alert("Error getting the data from the server");
			});

		},
        getPersons : function(){
        	return persons;
        },
        createPersons: function (person, callback) {
            $http.post('/api/v1/person', person).success(function (data) {
                callback(data);
                $rootScope.$broadcast('personinitStart');
                }).error(function () {
                alert("Error saving the data");
            });

        },
        deletePerson:function(personId){
        	
        	swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Person?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

					$http.delete('/api/v1/person/'+personId).success(function(data){
		            	swal("Deleted!", "Person has been deleted successfully!", "success");
		            	$rootScope.$broadcast('personinitStart');
					}).error(function(error){
						swal("Oops", "We couldn't connect to the server!", "error");
					});
				})
           
        },
        findByIdPerson:function(personId,callback) {
            $http.get('/api/v1/person/' + personId).success(function (data) {
                callback(data);
            });
        },

        updatePerson: function(person,callback) {
            $http.put('/api/v1/person/'+person.id,person).success(function (data) {
                callback(data);
                $rootScope.$broadcast('personinitStart');
            });
        }
    }

});

