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
        
        
        getActiveRouteNames: function() {
        	return $http({
        		method:'GET',
        		//url:'/api/v1/documents/route?fields=id,name'
        		url:'/api/v1/routes'
        			
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
            })
                .error(function (err,status) {
                    /*var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                    $log.error(errorMsg);
                    alert(errorMsg);*/
                    sweetAlert("Error",err.message,"error");
                });
        },

        deleteRoute: function(routeId) {
            swal({
                title: "Are you sure?",
                text: "Are you sure you want to delete this route?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "Yes, delete it!",
                confirmButtonColor: "#ec6c62"},function(){

                $http.delete('/api/v1/route/' + routeId).success(function (data) {
                    $rootScope.$broadcast('DeleteRouteCompleted');
                    swal("Deleted!", "Route was successfully deleted!", "success");
                    }).error(function () {
                       swal("Oops", "We couldn't connect to the server!", "error");
                    });
            })
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