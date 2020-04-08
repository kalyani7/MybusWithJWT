"use strict";
/*global angular, _*/

angular.module('myBus.operatorAccountsModule', ['ngTable', 'ui.bootstrap'])
.controller("OperatorAccountsController",function($rootScope, $scope, $uibModal, $filter, $log, $state ,NgTableParams,opratingAccountsManager){
	$scope.headline = "OperatorAccounts";
    $scope.loading = false;
    $scope.accounts = [];

    $scope.count = 0;
    var loadTableData = function (tableParams) {
		$scope.loading = true;
        opratingAccountsManager.getAccounts( function(response){
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
        $state.go('addoperatoraccounts');
    };

    $scope.editoperatoraccounts = function (id) {
        $state.go('editoperatoraccounts', {id:id});
    };
})
// ========================== Modal - Update Amenity  =================================
    .controller('AddAccountController', function ($scope, $rootScope, $uibModal, $http, $state , opratingAccountsManager) {
        $scope.account = {};
        $scope.headline = "Add Operator Account";
        $scope.saveAccount =function() {
            opratingAccountsManager.saveAccount($scope.account, function (res) {
                swal("Great", "Your account has been sucessfully added", "success");
                $state.go('operatoraccounts');
            }, function (error) {
                console.log('error');
            });
        };
        $scope.cancel = function () {
            $state.go('operatoraccounts');
        };
    })


.controller('EditAccountController', function ($scope, $rootScope, $uibModal, $http, $state, opratingAccountsManager, $stateParams) {
	$scope.account = {};
    $scope.headline = "Edit Operator Account";
	$scope.saveAccount =function(){
        opratingAccountsManager.saveAccount($scope.account,function(res){
            swal("Great","Your account has been sucessfully added","success");
            $state.go('operatoraccounts');
        }, function (error) {
            console.log('error');
		});
	};
    $scope.cancel = function () {
        $state.go('operatoraccounts');
    };

     var accountId = $stateParams.id;

    if(accountId){
        opratingAccountsManager.getAccount(accountId,function(data){
            $scope.account = data;
        });
	}
})
.factory("opratingAccountsDataManager",function($rootScope,$http){
	var accounts = [];
	return {
        getAccounts: function (callback) {
            $http.get("/api/v1/operatorAccount/all").then(function (response) {
                accounts = response.data;
                callback(accounts);
            }, function (error) {
                swal("oops", error, "error");
            });
        },
        saveAccount: function (account, callback) {
            $http.post("/api/v1/operatorAccount/", account).then(function (response) {
                callback(response.data);
                $rootScope.$broadcast('reloadAccounts');
                swal("Great", "Account has been successfully added", "success");
            }, function (error) {
                swal("oops", error, "error");
            })
        },
        getAccount: function (id, callback) {
            $http.get("/api/v1/operatorAccount/" + id).then(function (response) {
                callback(response.data);
            }, function (error) {
                swal("oops", error, "error");
            })
        }
    }
});