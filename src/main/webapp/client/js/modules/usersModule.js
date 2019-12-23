"use strict";
/*global angular, _*/

// angular.module('myBus.userModule', ['ngTable', 'ui.bootstrap'])

  //
  // ============================= List All ===================================
  //
    myBus.controller('UsersController', function($scope,$state, $http, $log, $filter, NgTableParams, $location,userManager, roleManager) {
      $scope.headline = "Users";
      //$scope.users = [];
      $scope.userCount = 0;

      $scope.currentPageOfUsers = [];
      var loadTableData = function (tableParams, $defer) {
          userManager.getUsers(function (data) {
              if(angular.isArray(data)) {
                  $scope.users = data;
                  $scope.userCount = data.length;
                  var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                  $scope.allUsers = orderedData;
                  tableParams.total(data.length);
                  if (angular.isDefined($defer)) {
                      $defer.resolve(orderedData);
                  }
                  $scope.currentPageOfUsers = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
              }
          });
      };
      $scope.userContentTableParams = new NgTableParams({
        page: 1,
        count: 100,
        sorting: {
          fullName: 'asc'
        }
      }, {
        total: $scope.currentPageOfUsers.length,
        getData: function (params) {
            loadTableData(params);
        }
      });
    $scope.$on('UsersInitComplete', function (e, value) {
        loadTableData( $scope.userContentTableParams );
    });
    userManager.fetchAllUsers();

    $scope.$on('CreateUserCompleted',function(e,value){
        userManager.fetchAllUsers();
    });

    $scope.$on('DeleteUserCompleted',function(e,value){
        userManager.fetchAllUsers();
    });

    $scope.addUser = function () {
        $state.go('user');
    };

    $scope.editUser = function(userId){
        $location.url('user/'+userId,{'idParam':userId});
    };
    $scope.deleteUser = function(id){
        userManager.deleteUser(id)
    };

    $scope.isAdmin = function(){
        return userManager.getUser().admin;
    };
    })

    //
    // ============================= Add ========================================
    //
    .controller('UserAddController', function($scope,userManager,$window,$log, $location, roleManager, cityManager, branchOfficeManager, cancelManager ) {
        $scope.headline = "Add New User";
        //$scope.isAdd = false;
        $scope.ConfirmPassword = "";
        $scope.user = {};
        $scope.planTypes = [];
        $scope.roles =[];
        $scope.cities = [];
        $scope.offices = [];
        var pageable;

        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });
        $scope.rolesInit = function(){
        	roleManager.getAllRoles(pageable,function(data){
        		$scope.roles = data.content;
        	});
            cityManager.getActiveCityNames(function(data){
                $scope.cities = data;
            });
        }
        $scope.rolesInit();
        $scope.usersFromManager=[];
        $scope.onMouseLeave = function(userNameFromUI){
            userManager.getUsers(function(data){
                $scope.usersFromManager=data;
            });
            angular.forEach($scope.usersFromManager,function(user){
                if(user.userName==userNameFromUI){
                    swal("oops!","Username already exist","error");
                }
            });
        };

        $scope.callBlurFunction = function(userPassword){
            $scope.user.password = userPassword;
        };

        $scope.passwordCheck = function(gotPassword){
            if (gotPassword != $scope.user.password) {
                swal("oops!", "Password should match", "error");
            }
        };

        $scope.save = function(){
            if($scope.userForm.$dirty) {
                $scope.userForm.submitted = true;
                if ($scope.userForm.$invalid) {
                    swal("Error!", "Please fix the errors in the user form", "error");
                    return;
                }
                userManager.createUser($scope.user, function (data) {
                    swal("success", "User successfully added", "success");
                });
            }
            $location.url('/users');
        };
        $scope.launchAddBranchOffice = function() {
            $location.url('/branchoffice/');
        }
        $scope.cancelUser = function(theForm){
            cancelManager.cancel(theForm);
        }
        $scope.launchRoleAdd = function(){
            $location.url('/roles');
        }
    })
    .controller('UpdatePasswordController', function ($scope,$stateParams, $location, userManager) {
        $scope.user= {};
        $scope.user.userName = userManager.getUser().userName;
        $scope.updatePassword = function (){
            if($scope.user.currentPassword !== userManager.getUser().password){
                swal("Error!", "Wrong current password", "error");
                return;
            }
            if($scope.user.password !== $scope.user.confirmPassword){
                swal("Error!", "Password and confirm password do not match", "error");
                return;
            }
            userManager.updatePassword($scope.user, function(data){
                swal("success", "Password successfully updated", "success");
            }, function(error){
                swal("Error!", "Failed to update password", "error");
            });
        }
    })
    //
  // ======================== Edit User =====================================
  //
  .controller('UpdateUserController', function ($scope,$stateParams, $location, $rootScope, $http, $log,userManager,cityManager,roleManager,cancelManager, branchOfficeManager) {
        $scope.headline = "Edit User";
        $scope.id=$stateParams.id;
        $scope.user={};
        $scope.roles =[];
        $scope.cities =[];
        $scope.allModules =[];
        $scope.currentUser    = null;
        var pageable;

        $scope.rolesInit = function(){
        	roleManager.getAllRoles(pageable,function(data){
        		$scope.roles = data.content;
        	});
        }


        $scope.offices = [];
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });
        $scope.rolesInit();
        $scope.loadUserWithId = function(){
            cityManager.getActiveCityNames(function(data) {
                $scope.cities = data;
                //console.log("city names..." + JSON.stringify($scope.cities));
                userManager.getUserWithId($scope.id, function (data) {
                    $scope.user = data;
                    $scope.confirmPassword = $scope.user.password;
                    $scope.currentUser = $rootScope.currentuser;
                    $scope.allModules = $scope.user.attrs.allModules.split(",");
                });
            });
        };

        $scope.toggleSelection = function(module) {
            if(!$scope.user.accessibleModules){
                $scope.user.accessibleModules = [];
            }
            var idx = $scope.user.accessibleModules.indexOf(module);
            if (idx > -1) {
                $scope.user.accessibleModules.splice(idx, 1);
            } else {
                $scope.user.accessibleModules.push(module);
            }
        };
        $scope.loadUserWithId();
        $scope.save = function(){
            if($scope.userForm.$invalid) {
                swal("Error!","Please fix the errors in the user form","error");
                return;
            }
            if(userManager.validateUserInfo($scope.user, $scope.confirmPassword)) {
                userManager.updateUser($scope.user,function(data){
                    swal("success","User Updated","success");
                    $location.url('/users');
                },function(error) {
                    swal(error.message,"Error saving the user form","error");
                    return;
                });
            } else {
                swal("Error!","Please fix the errors in the user form","error");
                return;
            }
        };
        $scope.cancelUser = function(theForm){
            cancelManager.cancel(theForm);
        };
        $scope.launchAddBranchOffice = function() {
            $location.url('/branchoffice/');
        };
        $scope.launchRoleAdd = function(){
            $location.url('/roles');
        };
    }).controller('CashBalancesController', function ($scope,$stateParams, $location, $filter,$http, $log,userManager,NgTableParams, branchOfficeManager) {
        $scope.headline = "User Cash balances";
        //$scope.users = [];
        $scope.userCount = 0;
        $scope.query = '';

        $scope.currentPageOfUsers = [];
        var loadTableData = function (tableParams, $defer) {
            userManager.getUserCashbalances($scope.query, function (data) {
                if(angular.isArray(data)) {
                    $scope.users = data;
                    $scope.userCount = data.length;
                    var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    $scope.allUsers = orderedData;
                    tableParams.total(data.length);
                    if (angular.isDefined($defer)) {
                        $defer.resolve(orderedData);
                    }
                    $scope.currentPageOfUsers = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    $scope.cashBalanceArray =  $scope.currentPageOfUsers;
                }
            },function(error){

            });
        };

        $scope.init = function() {
            $scope.cashBalanceTableParams = new NgTableParams({
                page: 1,
                count: 100,
                sorting: {
                    fullName: 'asc'
                }
            }, {
                total: $scope.currentPageOfUsers.length,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };

    $scope.init();

    $scope.searchBranchOffice = function(branchName) {
        $scope.cashBalanceArray = [];
        if(branchName === "All") {
            $scope.cashBalanceArray = $scope.currentPageOfUsers;
        } else {
            for (var i = 0; i < $scope.currentPageOfUsers.length; i++) {
                if (branchName === $scope.currentPageOfUsers[i].branchOfficeId) {
                    $scope.cashBalanceArray.push($scope.currentPageOfUsers[i]);
                }
            }
        }
    };

    $scope.getBranchOffice = function () {
        branchOfficeManager.loadNames(function (res) {
            $scope.branchOfficeNames = res;
        })
    };
    $scope.getBranchOffice();


}).factory('userManager', function ($http, $log,$rootScope, services) {

        var GRP_READ_ONLY = "Read-only"
            , GRP_AUTHOR = "Author"
            , GRP_PUBLISHER = "Publisher"
            , GRP_ADMIN = "Admin"
            , GRP_DEVELOPER = "Developer"
            , GRP_BUSINESS_ADMIN = "Business Admin"
            , currentUser = null
            , currentGroups = null
            , hasRoleReadOnly = null
            , hasRoleAuthor = null
            , hasRolePublisher = null
            , hasRoleAdmin = null
            , hasRoleDeveloper = null
            , hasRoleBusinessAdmin = null;

        var users = {};

        return {
            validateUserInfo: function(user, confirmPassword) {
                if(user.password !== confirmPassword) {
                    return false;
                }
                return true;
            },
            fetchAllUsers: function () {
                $log.debug("fetching routes data ...");
                $http.get('/api/v1/users')
                    .then(function (response) {
                        users=response.data;
                        $rootScope.$broadcast('UsersInitComplete');
                    },function (error) {
                        $log.debug("error retrieving users");
                    });
            },
            getUsers: function (callback) {
                if(users) {
                    callback(users);
                } else {
                    $http.get('/api/v1/users')
                        .then(function (response) {
                            users=response.data;
                            callback(response.data);
                            $rootScope.$broadcast('FetchingUsersComplete');
                        },function (error) {
                            $log.debug("error retrieving cities");
                        });
                }

            },
            getUserNames: function (callback) {
                $http.get('/api/v1/userNames')
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('FetchingUserNamesComplete');
                    },function (error) {
                        $log.debug("error retrieving user names");
                    });
            },
            getUserCashbalances: function (query, callback) {
                $http.get('/api/v1/user/cashBalances')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving user balances");
                    });
            },
            getAllUsers: function () {
                return users;
            },
            createUser: function(user,callback){
                $http.post('/api/v1/user',user).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('CreateUserCompleted');
                },function (err,status) {
                    sweetAlert("Error",err.message,"error");
                });
            },
            getUserWithId:function(id,callback){
                $http.get("/api/v1/userId/" + id).then(function(response){
                    callback(response.data);
                })
            },
            updateUser : function (user,callback,errorcallback) {
                $http.put('/api/v1/userEdit/'+user.id,user).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('UpdateUserCompleted');
                },function (data, status, header, config) {
                    errorcallback(data);
                });
            },
            updatePassword : function (user,callback,errorcallback) {
                $http.put('/api/v1/user/updatePassword',user).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('UpdateUserCompleted');
                },function (data) {
                    errorcallback(data);
                });
            },
            count : function (callback,errorcallback) {
                $http.get('/api/v1/user/count').then(function(response){
                    callback(response.data);
                },function (data) {
                    errorcallback(data);
                });
            },
            deleteUser : function (id){
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this user?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){

                    $http.delete('api/v1/user/'+id).then(function(response){
                        $rootScope.$broadcast('DeleteUserCompleted');
                        swal("Deleted!", "Route was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            },

            getCurrentUser: function (callback, forceRefresh) {
                if (currentUser === null || forceRefresh) {
                    services.get('/api/v1/user/me', function (response) {
                        if (response) {
                            console.log(response.data)
                            currentUser = response.data;
                        }
                    })
                    // $http.get('/api/v1/user/me')
                    //     .then(function (response) {
                    //         currentUser = response.data;
                    //         return angular.isFunction(callback) && callback(null, currentUser);
                    //     },function (err, status) {
                    //         $log.error('Error getting current user. Status code ' + status + ".  " + angular.toJson(err));
                    //         //angular.isFunction(callback) && callback(err);
                    //         document.location = "/"; // redirect to login
                    //     });
                } else {
                    // return angular.isFunction(callback) && callback(null, currentUser);
                }
            },
            getUser: function(){
                return currentUser;
            },

            getGroupsForCurrentUser: function (callback, forceRefresh) {
                if (currentGroups === null || forceRefresh) {
                    $http.get('/api/v1/user/groups')
                        .then(function (response) {
                            currentGroups = response.data;
                            return angular.isFunction(callback) && callback(null, currentGroups);
                        },function (err) {
                            $log.error('Error getting current user\'s groups. ' + angular.toJson(err));
                            return angular.isFunction(callback) && callback(err);
                        });
                } else {
                    return angular.isFunction(callback) && callback(null, currentGroups);
                }
            },

            isReadOnly: function () {
                if (hasRoleReadOnly === null && currentGroups) {
                    hasRoleReadOnly = _.any(currentGroups, function (grp) {
                        return GRP_READ_ONLY === grp.name;
                    });
                }
                return hasRoleReadOnly;
            },

            isDeveloper: function () {
                if (hasRoleDeveloper === null && currentGroups) {
                    hasRoleDeveloper = _.any(currentGroups, function (grp) {
                        return GRP_DEVELOPER === grp.name;
                    });
                }
                return hasRoleDeveloper;
            },

            isAuthor: function () {
                if (hasRoleAuthor === null && currentGroups) {
                    hasRoleAuthor = _.any(currentGroups, function (grp) {
                        return GRP_AUTHOR === grp.name;
                    });
                }
                return hasRoleAuthor;
            },

            isPublisher: function () {
                if (hasRolePublisher === null && currentGroups) {
                    hasRolePublisher = _.any(currentGroups, function (grp) {
                        return GRP_PUBLISHER === grp.name;
                    });
                }
                return hasRolePublisher;
            },

            isAdmin: function () {
                if (hasRoleAdmin === null && currentGroups) {
                    hasRoleAdmin = _.any(currentGroups, function (grp) {
                        return GRP_ADMIN === grp.name;
                    });
                }
                return hasRoleAdmin;
            },

            isBusinessAdmin: function (businessId) {
                var isBusAdm = false;
                if (hasRoleBusinessAdmin === null && currentGroups) {
                    hasRoleBusinessAdmin = _.any(currentGroups, function (grp) {
                        return GRP_BUSINESS_ADMIN === grp.name;
                    });
                }
                if (hasRoleBusinessAdmin) {
                    if (businessId) {
                        isBusAdm = currentUser && currentUser.customData && _.contains(currentUser.customData.businessIds, businessId);
                    } else {
                        isBusAdm = true;
                    }
                }
                return isBusAdm;
            }
        };
    });




