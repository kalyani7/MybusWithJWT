
"use strict";

angular.module('myBus.bankModule', ['ngTable', 'ui.bootstrap'])
    .controller("bankController", function($scope, $rootScope, $log, NgTableParams, $location, bankManager, $uibModal, $state) {
        $scope.headline = 'Bank List';

        $scope.bankDetails = function () {
            bankManager.getBank(function (data) {
                $scope.bankList = data
            });
        }

        $scope.bankDetails()

        $scope.addBank = function () {
            $state.go('home.bankAdd')
        };

        $scope.editBank = function (id) {
            $state.go('home.bankEdit', {id:id});
        }

        $scope.deleteBank = function (id) {
            bankManager.deleteBank(id)
        }

        $scope.$on('DeleteBankCompleted',function(e,value){
            $scope.bankDetails()
        });

    })
    .controller("addEditBankController", function($scope, $rootScope, $stateParams, $log, NgTableParams, $location, bankManager, $uibModal, $state) {
        $scope.bankId = $stateParams.id;

        if ($stateParams.id) {
            $scope.headline = 'Edit Bank Details';
            bankManager.getBankWithId($stateParams.id, function (data) {
                $scope.bank = data
            })
        } else {
            $scope.headline = 'Add Bank Details'
        }

        $scope.save = function() {
            if ($stateParams.id) {
                bankManager.updateBank($scope.bank, function (data) {
                    swal("success","Bank Updated","success");
                    $state.go('home.banks');
                })
            } else {
                if (!$scope.bank) {
                    swal("error", "Fill the bank details", "error");
                } else {
                    bankManager.createBankDetails($scope.bank, function (data) {
                        swal("success", "Bank successfully added", "success");
                        $state.go('home.banks');
                    });
                }
            }
        };

        $scope.cancelBank = function () {
            $state.go('home.banks');
        }
    })
    .factory('bankManager', function ($http, $log, $rootScope, services) {
        return {
            createBankDetails: function (user, callback) {
                services.post('/api/v1/bank/addBankDetails', user, function (response) {
                    if (response) {
                        callback(response);
                        $rootScope.$broadcast('CreateUserCompleted');
                    }
                }, function (error) {
                    sweetAlert("Error", error.message, "error");
                })
                // $http.post('/api/v1/bank/addBankDetails', user).then(function (response) {
                //     callback(response);
                //     $rootScope.$broadcast('CreateUserCompleted');
                // }, function (err, status) {
                //     sweetAlert("Error", err.message, "error");
                // });
            },
            getBank: function (callback) {
                services.get('/api/v1/bank/getAllBankDetails', '', function (response) {
                    if (response) {
                        callback(response.data);
                        $rootScope.$broadcast('FetchingUserNamesComplete');
                    }
                }, function (error) {
                    $log.debug("error retrieving user names");
                })
                // $http.get('/api/v1/bank/getAllBankDetails')
                //     .then(function (response) {
                //         callback(response.data);
                //         $rootScope.$broadcast('FetchingUserNamesComplete');
                //     }, function (error) {
                //         $log.debug("error retrieving user names");
                //     });
            },
            getBankWithId:function(bankId, callback){
                services.get("/api/v1/bank/getBankInfo/" + bankId, '', function (response) {
                    if (response) {
                        callback(response.data);
                    }
                })
                // $http.get("/api/v1/bank/getBankInfo/" + bankId).then(function(response){
                //     callback(response.data);
                // })
            },
            updateBank : function (bank, callback, errorcallback) {
                services.put('/api/v1/bank/updateBankInfo/'+bank.id, bank, function (response) {
                    if (response) {
                        callback(response.data);
                        $rootScope.$broadcast('UpdateUserCompleted');
                    }
                }, function (data, status, header, config) {
                    errorcallback(data);
                })
                // $http.put('/api/v1/bank/updateBankInfo/'+bank.id,bank).then(function(response){
                //     callback(response.data);
                //     $rootScope.$broadcast('UpdateUserCompleted');
                // },function (data, status, header, config) {
                //     errorcallback(data);
                // });
            },
            deleteBank : function (bankId){
                services.delete('api/v1/bank/delete/' + bankId, function (response) {
                    if (response) {

                    }
                })
                // swal({
                //     title: "Are you sure?",
                //     text: "Are you sure you want to delete this bank?",
                //     type: "warning",
                //     showCancelButton: true,
                //     closeOnConfirm: false,
                //     confirmButtonText: "Yes, delete it!",
                //     confirmButtonColor: "#ec6c62"},function(){
                //
                //     $http.delete('api/v1/bank/delete/'+bankId).then(function(response){
                //         $rootScope.$broadcast('DeleteBankCompleted');
                //         swal("Deleted!", "Route was successfully deleted!", "success");
                //     },function () {
                //         swal("Oops", "We couldn't connect to the server!", "error");
                //     });
                // })
            }
        }
    });