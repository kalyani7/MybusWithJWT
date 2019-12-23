// angular.module('myBus.loginModule', ['ngTable', 'ui.bootstrap'])
myBus.controller('LoginUserController', function($scope,$state, $http, $log, $cookies, loginManager) {
    $scope.user = {};
    $scope.logIn = function () {
        console.log($scope.user)
        loginManager.userLogin($scope.user, function (success) {
            if (success) {
                $scope.data = success;
                $cookies.put("token", $scope.data.accessToken);
                $cookies.put("tokenType", $scope.data.tokenType);
                $state.go('home')
            }
        })
    }

}).factory('loginManager', function ($http) {
    return {
        userLogin: function (userdetails, successCallback, errorCallback) {
            $http.post('/api/auth/signin', userdetails).then(function (response) {
                if (response) {
                    successCallback(response.data);
                    swal("Success", "Login Success", 'success');
                }
            }, function (error, status) {
                errorCallback(error)
                // swal("Error", error, 'error');
            });
        },
    }
})
