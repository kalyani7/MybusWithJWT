/**
 * Created by yks_Srinivas
 */
"use strict";

angular.module('myBus.roleModule', ['ngTable', 'ui.bootstrap'])
	.controller("RoleController",function($scope, $rootScope, $log, NgTableParams, $location, $uibModal, $state, $filter,paginationService,userManager, roleManager){

		$scope.headline="User Roles";
		$scope.currentPageOfRoles={};
        $scope.loading = false;
        var pageable ;
		var loadTableData = function (tableParams) {
            $scope.loading = true;
            paginationService.pagination(tableParams, function(response){
            	pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
			roleManager.getAllRoles(pageable, function(response) {
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.roles = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.roles;
                    $scope.currentPageOfRoles = $scope.roles;
                }
        	})
		};

        $scope.init = function() {
        	roleManager.count(function(rolesCount){
		$scope.roleContentTableParams = new NgTableParams({
			page: 1,
			size:10,
			count:10,
			sorting: {
				name: 'asc'
			}
		}, {
            counts:[],
            total:rolesCount,
            getData: function (params) {
                loadTableData(params);
            	}
			});
            });
        };
        $scope.init();

		$scope.$on('roleInit', function (e, value) {
			$scope.init();
		});

		$scope.deleteRole = function(roleID){
			roleManager.deleteRole(roleID,function(data){})
		};


		$scope.handleClickAddNewRole = function (size) {
			$rootScope.modalInstance = $uibModal.open({
				templateUrl: 'addOrUpdate-newRoles.html',
				controller: 'AddOrUpdateNewRoleController',
				size: size,
				resolve: {
					neighborhoodId: function () {
						return null;
					}
				}
			});
			$rootScope.modalInstance.result.then(function (data) {
				$scope.init();
			});
		};

		$scope.initForUpdateRole = function(roleId){
			$rootScope.modalInstance = $uibModal.open({
				templateUrl : 'addOrUpdate-newRoles.html',
				controller : 'AddOrUpdateNewRoleController',
				resolve : {
					neighborhoodId : function(){
						return roleId;
					}
				}
			});
		};
	})
	.controller("AddOrUpdateNewRoleController",function($scope, $rootScope, $log, $uibModal, roleManager, neighborhoodId){

		$scope.role ={};
		$scope.isForRoleUpdate = false;

		if(neighborhoodId){
			roleManager.getRoleById(neighborhoodId,function(data){
				$scope.role=data;
				$scope.isForRoleUpdate = true
			})
		}

		$scope.addNewRole = function() {
			roleManager.createRole($scope.role,function(data){
				$rootScope.modalInstance.close();
			})
		};

		$scope.UpdateNewRole = function(roleID,role) {
			roleManager.updateRole(roleID,role,function(data){
				$rootScope.modalInstance.close();
			})
		};
		$scope.isInputValid = function () {
			return ($scope.role.name || '') !== '';
		};

		$scope.cancel = function () {
			$rootScope.modalInstance.dismiss('cancel');
		};
	})
	.controller('ManagingRolesController', function ($scope, $log, $state, roleManager) {
		$scope.headline = "Managing Roles";
		$scope.updateAllManagingRoles = [];
		$scope.isEditable = false;
		$scope.getPermissions = function(){
			roleManager.getAllRoles({},function(response){
				$scope.roles=response.content;

				$scope.menus = [];
				angular.forEach($scope.roles,function(role){
					$scope.updateAllManagingRoles[role.name]={'id':role.id,'name':role.name,'menus':role.menus};
				});
				angular.forEach($state.get(),function(eachState){
					if(eachState.level === 1) {
						$scope.menus.push({'name':eachState.name});
					}
				});
             	$scope.menus.sort(function (a, b) {
                    var namea = a.name.toLowerCase();
                    var nameb = b.name.toLowerCase();

                    if (namea > nameb) {
                        return 1;
                    } else if (namea < nameb) {
                        return -1;
                    } else if (namea === nameb) {
                        return 0;
                    }
				});
                angular.forEach($scope.menus,function(menu){
					angular.forEach($scope.roles,function(role){
						if(!menu.permissions){
							menu.permissions = [];
						}
						if(!role.menus) {
							role.menus=[];
						}
						if(role.menus.indexOf(menu.name)!=-1){
							menu.permissions.push({'id':role.id,'roleName':role.name, 'allowed':true});
						}else {
							menu.permissions.push({'id':role.id,'roleName':role.name, 'allowed':false});
						}
					});
				});
			});
		};
		$log.debug($scope.updateAllManagingRoles +"$scope.updateAllManagingRoles");
		$scope.getPermissions();

		$scope.addOrRemovefromRoles = function(checkedOrUnchecked,menuName,roleName){
			if(checkedOrUnchecked) {
				if($scope.updateAllManagingRoles[roleName]){
					if(!$scope.updateAllManagingRoles[roleName].menus){
						$scope.updateAllManagingRoles[roleName].menus=[];
						$scope.updateAllManagingRoles[roleName].menus.push(menuName);
					}else{
						$scope.updateAllManagingRoles[roleName].menus.push(menuName);
					}
				}else {
					$scope.updateAllManagingRoles[roleName].name=roleName;
					$scope.updateAllManagingRoles[roleName].menus=[];
					$scope.updateAllManagingRoles[roleName].menus.push(roleName);
				}
			}else {
				var index = $scope.updateAllManagingRoles[roleName].menus.indexOf(menuName);
				$scope.updateAllManagingRoles[roleName].menus.splice(index,1)
			}
			$log.debug("checkedOrUnchecked : "+checkedOrUnchecked+" menuName : "+menuName+"  roleName :"+roleName)
		}
		$scope.updateManagingRoles = function(){
			$log.debug("update managing roles");
			$scope.isEditable = false;
			angular.forEach($scope.roles,function(role){
				var manageRoles = $scope.updateAllManagingRoles[role.name];
				roleManager.updateManageingRole(manageRoles.id,manageRoles,function(data){
				})
			})
		}
		$scope.editManagingRoles = function(){
			$scope.isEditable = $scope.isEditable?false:true;
			$log.debug("edit managing roles");
		}
	}).factory('roleManager', function ($rootScope, $http, $filter, $log, services) {
	var roles=[];
	return {

		getAllRoles : function(pageable, callback) {
			services.get('/api/v1/roles', pageable, function (response) {
				if (response) {
					callback(response.data)
				}
			}, function(err) {
				sweetAlert("Error",err.message,"error");
			})
			// $http({url:'/api/v1/roles',method: "GET",params: pageable})
			// 	.then(function (response) {
            //         callback(response.data);
			// 	},function(err) {
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
        count: function (callback) {
			services.get('/api/v1/roles/count', {}, function (response) {
				if (response) {
					callback(response.data)
				}
			}, function (error) {
				$log.debug("error retrieving roles");
			})
            // $http.get('/api/v1/roles/count',{})
            //     .then(function (response) {
            //         callback(response.data);
            //     },function (error) {
            //         $log.debug("error retrieving roles");
            //     });
        },
		createRole : function (role, callback) {
			services.post('/api/v1/createRole', role, function (response) {
				if (response) {
					callback(response.data);
					swal("Great", "Role has been successfully added", "success");
					$rootScope.$broadcast("roleInit");
				}
			}, function(err) {
				sweetAlert("Error",err.message,"error");
			})
			// $http.post('/api/v1/createRole',role)
			// 	.then(function (response) {
			// 		callback(response.data);
			// 		swal("Great", "Role has been successfully added", "success");
            //         $rootScope.$broadcast("roleInit");
			// 	},function(err) {
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
		updateRole : function (roleID,role,callback) {
			services.put('/api/v1/role/'+roleID, role, function (response) {
				if (response) {
					callback(response.data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.$broadcast("roleInit");
				}
			}, function(err) {
				callback(err);
				sweetAlert("Error",err.message,"error");
			})
			// $http.put('/api/v1/role/'+roleID,role)
			// 	.then(function (response) {
			// 		callback(response.data);
			// 		swal("Great", "Roles has been updated successfully", "success");
			// 		$rootScope.$broadcast("roleInit");
			// 	},function(err) {
			// 		callback(err);
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
		deleteRole : function (roleID,callback) {
			services.delete('/api/v1/role/' + roleID, function (response) {
				if (response) {
					callback(response.data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.modalInstance.dismiss('success');
					$rootScope.$broadcast("roleInit");
				}
			}, function(err) {
				callback(err);
				sweetAlert("Error",err.message,"error");
			})
			// swal({
			// 	title: "Are you sure?",
			// 	text: "Are you sure you want to delete this Role?",
			// 	type: "warning",
			// 	showCancelButton: true,
			// 	closeOnConfirm: false,
			// 	confirmButtonText: "Yes, delete it!",
			// 	confirmButtonColor: "#ec6c62"},function(){
			//
			// 	$http.delete('/api/v1/role/'+roleID)
			// 		.then(function (response) {
            //             callback(response.data);
			// 			swal("Great", "Roles has been updated successfully", "success");
			// 			$rootScope.modalInstance.dismiss('success');
			// 			$rootScope.$broadcast("roleInit");
			// 		},function(err) {
			// 			callback(err);
			// 			sweetAlert("Error",err.message,"error");
			// 		});
			// })
		},
		getRoleById : function (roleID, callback) {
			services.get('/api/v1/role/' + roleID, '', function (response) {
				if (response.data) {
					callback(response.data)
				}
			}, function(err) {
				sweetAlert("Error",err.message,"error");
			})
			// $http.get('/api/v1/role/'+roleID)
			// 	.then(function (response) {
			// 		callback(response.data);
			// 	},function(err) {
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
		getRoleByRoleName : function (rolesName,callback) {
			services.get('/api/v1/roleByName/' + rolesName, '', function (response) {
				if (response) {
					callback(response.data)
				}
			}, function (err) {
				sweetAlert("Error",err.message,"error");
			})
			// $http.get('/api/v1/roleByName/'+rolesName)
			// 	.then(function (response) {
			// 		callback(response.data);
			// 	},function(err) {
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
		getRoleNames : function (callback) {
			services.get('/api/v1/role/names', '', function (response) {
				if (response) {
					callback(response.data)
				}
			}, function(err) {
				sweetAlert("Error",err.message,"error");
			})
			// $http.get('/api/v1/role/names')
			// 	.then(function (response) {
			// 		console.log(response);
			// 		callback(response.data);
			// 	},function(err) {
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		},
		updateManageingRole : function (roleID,role,callback) {
			services.put('/api/v1/manageRole', '', role, function (response) {
				callback(response.data);
				swal("Great", "Roles has been updated successfully", "success");
				$rootScope.$broadcast("roleInit");
			},function(err) {
				callback(err);
				sweetAlert("Error",err.message,"error");
			})
			// $http.put('/api/v1/manageRole', role)
			// 	.then(function (response) {
			// 		callback(response.data);
			// 		swal("Great", "Roles has been updated successfully", "success");
			// 		$rootScope.$broadcast("roleInit");
			// 	},function(err) {
			// 		callback(err);
			// 		sweetAlert("Error",err.message,"error");
			// 	});
		}
	};
})
