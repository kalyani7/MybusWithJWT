// angular.module('myBus.homeModule', ['ngTable', 'ui.bootstrap'])
myBus.controller('HomeController', function($scope,$state, $http, $log, $cookies, homeManager) {

            $scope.branchOffice = {};
            $scope.user = {};
            $scope.currentDate = function(){
                var today = new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; //January is 0!

                var yyyy = today.getFullYear();
                if(dd<10){
                    dd='0'+dd;
                }
                if(mm<10){
                    mm='0'+mm;
                }
                var today = dd+'/'+mm+'/'+yyyy;
                return today;
            };

            $scope.userName = function() {
                $scope.user= homeManager.getUser();
                if($scope.user != null) {
                    return $scope.user.firstName+" ,"+ $scope.user.lastName;
                } else {
                    return null;
                }
            }
            $scope.amountToBePaid = function() {
                var user = homeManager.getUser();
                return user? user.amountToBePaid: "";
            }
            $scope.amountToBeCollected = function() {
                var user = homeManager.getUser();
                return user? user.amountToBeCollected: "";
            }

            $scope.isAdmin = function() {
                var user = homeManager.getUser();
                if(user != null) {
                    return user.admin;
                } else {
                    return false;
                }
            }
            $scope.updateHeader = function(){

                if($scope.user && $scope.user.branchOfficeId) {
                    /*branchOfficeManager.load($scope.user.branchOfficeId, function(data){
                        $scope.branchOffice = data;
                    });*/
                    console.log('updating header');
                    homeManager.getCurrentUser(null, true);
                }
            };
            $scope.$on('UpdateHeader', function(){
                $scope.updateHeader();
            });

}).factory('homeManager', function ($http, $log, $rootScope) {
    var currentUser = null;
    return {
        getUser: function(){
            return currentUser;
        },
    }
})
