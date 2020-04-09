angular.module('myBus.homeModule', ['ngTable', 'ui.bootstrap'])
myBus.controller('HomeController', function ($scope, $state, $http, $log, $cookies, homeManager, vehicleManager) {

    $scope.branchOffice = {};
    $scope.user = {};
    $scope.currentDate = function () {
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth() + 1; //January is 0!

        var yyyy = today.getFullYear();
        if (dd < 10) {
            dd = '0' + dd;
        }
        if (mm < 10) {
            mm = '0' + mm;
        }
        var today = dd + '/' + mm + '/' + yyyy;
        return today;
    };

    $scope.userName = function () {
        $scope.user = homeManager.getUser();
        if ($scope.user != null) {
            return $scope.user.firstName + " ," + $scope.user.lastName;
        } else {
            return null;
        }
    }
    $scope.amountToBePaid = function () {
        var user = homeManager.getUser();
        return user ? user.amountToBePaid : "";
    }
    $scope.amountToBeCollected = function () {
        var user = homeManager.getUser();
        return user ? user.amountToBeCollected : "";
    }

    $scope.isAdmin = function () {
        var user = homeManager.getUser();
        if (user != null) {
            return user.admin;
        } else {
            return false;
        }
    }
    $scope.updateHeader = function () {
        var data = '';
        if ($scope.user && $scope.user.branchOfficeId) {
            /*branchOfficeManager.load($scope.user.branchOfficeId, function(data){
                $scope.branchOffice = data;
            });*/
            console.log('updating header');
            // homeManager.getCurrentUser(null, true);
            userManager.getCurrentUser(function (response) {
                if (response) {
                    console.log('response', response)
                }
            })
        }
    };
    $scope.$on('UpdateHeader', function () {
        $scope.updateHeader();
    });


    $scope.currentUser = null;
    $scope.viewModal = false;
    $scope.query = {
        isCompleted: false,
    };

    $scope.$on('currentuserLoaded', function () {
        console.log($rootScope.currentuser)
        $scope.currentUser = $rootScope.currentuser;
        if ($scope.currentUser.accessibleModules.length > 0) {
            var accessibleModules = $scope.currentUser.accessibleModules;
            var allModules = $scope.currentUser.attrs.allModules.split(",");
            for (var a = 0; a < allModules.length; a++) {
                if (accessibleModules.indexOf(allModules[a]) > -1) {
                    $scope.currentUser['canAccess' + allModules[a]] = true;
                }
            }
        }
    });

    $scope.canAccessModule = function (moduleName) {
        // console.log('moduleName ' + moduleName);
        if ($scope.currentUser && $scope.currentUser.superAdmin) {
            // console.log('if', $scope.currentUser)
            return true;
        } else {
            // console.log('else', $scope.currentUser)
            if ($scope.currentUser) {
                var accessibleModules = $scope.currentUser.accessibleModules;
                if (accessibleModules.indexOf(moduleName) != -1) {
                    return true;
                }
                return false;
            }
        }
    };

    setTimeout(function () {
        $('#myModal').modal('show');
    }, 3000);

    setTimeout(function () {
        $('#myModal').modal('hide');
    }, 35000);

    $scope.loadVehicles = function () {
        var today = new Date();
        today = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
        vehicleManager.getExpiringVehicles({}, today, $scope.query).then(function (response) {
            // console.log(response)
            $scope.haltedVehicles = response[1].data;
            $scope.reminders = response[2].data;
            if (Object.getOwnPropertyNames(response[0].data).length !== 0 || $scope.haltedVehicles.length != 0) {
                $scope.viewModal = true;
                $scope.authExpiringVehicles = response[0].data.authExpiring;
                $scope.fitnessExpiringVehicles = response[0].data.fitnessExpiring;
                $scope.insuranceExpiringVehicles = response[0].data.insuranceExpiring;
                $scope.permitExpiringVehicles = response[0].data.permitExpiring;
                $scope.pollutionExpiringVehicles = response[0].data.pollutionExpiring;
            }
        })
    };
    $scope.loadVehicles();
    $scope.$on('reloadReminders', function (e, value) {
        $scope.loadVehicles();
        setTimeout(function () {
            $('#myModal').modal('show');
        }, 3000);
    });

    $scope.updateVehicle = function (id) {
        $location.url('vehicle/' + id);
        setTimeout(function () {
            $('#myModal').modal('hide');
        });
    };
    $scope.goToServiceReport = function (service) {
        if (service.attrs.formId) {
            $location.url('serviceform/' + service.attrs.formId);
        } else {
            $location.url('servicereport/' + service.id);
        }
    }

    $scope.logOut = function () {
        var t = '';
        $cookies.remove('token');
        $cookies.remove('tokenType');
        if (!$cookies.get('token')) {
            // if (setTimeout("location.reload(true);", t * 1)) {
            $state.go('login');
            // }
        }
    };


})
//     .controller('MenuBarController', function ($scope, $rootScope, $location, $stateParams, userManager, vehicleManager, remainderManager) {
//     $scope.currentUser = null;
//     $scope.viewModal = false;
//     $scope.query = {
//         isCompleted: false,
//     };
//     $scope.$on('currentuserLoaded', function () {
//         $scope.currentUser = $rootScope.currentuser;
//         if ($scope.currentUser.accessibleModules.length > 0) {
//             var accessibleModules = $scope.currentUser.accessibleModules;
//             var allModules = $scope.currentUser.attrs.allModules.split(",");
//             for (var a = 0; a < allModules.length; a++) {
//                 if (accessibleModules.indexOf(allModules[a]) > -1) {
//                     $scope.currentUser['canAccess' + allModules[a]] = true;
//                 }
//             }
//         }
//     });
//
//     $scope.canAccessModule = function (moduleName) {
//         if ($scope.currentUser && $scope.currentUser.superAdmin) {
//             return true;
//         } else {
//             if ($scope.currentUser) {
//                 var accessibleModules = $scope.currentUser.accessibleModules;
//                 if (accessibleModules.indexOf(moduleName) != -1) {
//                     return true;
//                 }
//                 return false;
//             }
//         }
//     };
//
//     setTimeout(function () {
//         $('#myModal').modal('show');
//     }, 3000);
//
//     setTimeout(function () {
//         $('#myModal').modal('hide');
//     }, 35000);
//
//     $scope.loadVehicles = function () {
//         var today = new Date();
//         today = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
//         vehicleManager.getExpiringVehicles({}, today, $scope.query).then(function (response) {
//             $scope.haltedVehicles = response[1].data;
//             $scope.reminders = response[2].data;
//             if (Object.getOwnPropertyNames(response[0].data).length !== 0 || $scope.haltedVehicles.length != 0) {
//                 $scope.viewModal = true;
//                 $scope.authExpiringVehicles = response[0].data.authExpiring;
//                 $scope.fitnessExpiringVehicles = response[0].data.fitnessExpiring;
//                 $scope.insuranceExpiringVehicles = response[0].data.insuranceExpiring;
//                 $scope.permitExpiringVehicles = response[0].data.permitExpiring;
//                 $scope.pollutionExpiringVehicles = response[0].data.pollutionExpiring;
//             }
//         })
//     };
//     $scope.loadVehicles();
//     $scope.$on('reloadReminders', function (e, value) {
//         $scope.loadVehicles();
//         setTimeout(function () {
//             $('#myModal').modal('show');
//         }, 3000);
//     });
//
//     $scope.updateVehicle = function (id) {
//         $location.url('vehicle/' + id);
//         setTimeout(function () {
//             $('#myModal').modal('hide');
//         });
//     };
//     $scope.goToServiceReport = function (service) {
//         if (service.attrs.formId) {
//             $location.url('serviceform/' + service.attrs.formId);
//         } else {
//             $location.url('servicereport/' + service.id);
//         }
//     }
// })
    .factory('homeManager', function ($http, $log, $rootScope) {
    var currentUser = null;
    return {
        getUser: function () {
            return currentUser;
        },
    }
})
