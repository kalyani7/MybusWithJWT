"use strict";
/*global angular, _*/

angular.module('myBus.operatorAccountsModule', ['ngTable', 'ui.bootstrap'])
.controller("OperatorAccountsController",function($rootScope, $scope, $uibModal, $filter, $log, $state ,NgTableParams,operatingAccountsManager){
	$scope.headline = "OperatorAccounts";
    $scope.loading = false;
    $scope.accounts = [];

    $scope.count = 0;
    var loadTableData = function (tableParams) {
		$scope.loading = true;
        operatingAccountsManager.getAccounts( function(response){
			if(angular.isArray(response)){
				$scope.loading = false;
				$scope.accounts = response;
                $scope.count = $scope.accounts.length;
            }
		})
	 };

	 $scope.init = function(){
             $scope.accountTableParams = new NgTableParams(
                 {
                     page: 1,
                     size: 20,
                     count: 10,
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

	$scope.$on('reloadAccounts', function (e, value) {
        $scope.init();
	});

    $scope.addoperatoraccounts = function () {
        $state.go('home.addoperatoraccounts');
    };

    $scope.editoperatoraccounts = function (id) {
        $state.go('home.editoperatoraccounts', {id:id});
    };
})
// ========================== Modal - Update Amenity  =================================
    .controller('AddAccountController', function ($scope, $rootScope, $uibModal, $http, $state , operatingAccountsManager) {
        $scope.account = {};
        $scope.headline = "Add Operator Account";
        $scope.saveAccount =function() {
            operatingAccountsManager.saveAccount($scope.account, function (res) {
                swal("Great", "Your account has been sucessfully added", "success");
                $state.go('home.operatoraccounts');
            }, function (error) {
                console.log('error');
            });
        };
        $scope.cancel = function () {
            $state.go('home.operatoraccounts');
        };
    })


.controller('EditAccountController', function ($scope, $rootScope, $uibModal, $http, $state, operatingAccountsManager, $stateParams) {
	$scope.account = {};
    $scope.headline = "Edit Operator Account";
	$scope.saveAccount =function(){
        operatingAccountsManager.saveAccount($scope.account,function(res){
            swal("Great","Your account has been sucessfully added","success");
            $state.go('home.operatoraccounts');
        }, function (error) {
            console.log('error');
		});
	};
    $scope.cancel = function () {
        $state.go('home.operatoraccounts');
    };

     var accountId = $stateParams.id;

    if(accountId){
        operatingAccountsManager.getAccount(accountId,function(data){
            $scope.account = data;
        });
	}
})
.factory("operatingAccountsManager",function($rootScope, $http, services){
	var accounts = [];
	return {
        getAccounts: function (callback) {
            services.get("/api/v1/operatorAccount/all", '', function (response) {
                if (response) {
                    accounts = response.data;
                    callback(accounts);
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http.get("/api/v1/operatorAccount/all").then(function (response) {
            //     accounts = response.data;
            //     callback(accounts);
            // }, function (error) {
            //     swal("oops", error, "error");
            // });
        },
        saveAccount: function (account, callback) {
            services.post("/api/v1/operatorAccount/", account, function (response) {
                if (response) {
                    callback(response.data);
                    $rootScope.$broadcast('reloadAccounts');
                    swal("Great", "Account has been successfully added", "success");
                }
            }, function (error) {
                swal("oops", error, "error");
            })
            // $http.post("/api/v1/operatorAccount/", account).then(function (response) {
            //     callback(response.data);
            //     $rootScope.$broadcast('reloadAccounts');
            //     swal("Great", "Account has been successfully added", "success");
            // }, function (error) {
            //     swal("oops", error, "error");
            // })
        },
        getAccount: function (id, callback) {
            services.get('/api/v1/operatorAccount/' + id, {}, function (response) {
                if (response) {
                    callback(response.data)
                }
            })
            // $http.get("/api/v1/operatorAccount/" + id).then(function (response) {
            //     callback(response.data);
            // }, function (error) {
            //     swal("oops", error, "error");
            // })
        }
    }
});