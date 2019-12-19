
"use strict";
/*global angular, _*/

angular.module('myBus.agentModule', ['ngTable', 'ui.bootstrap'])
    .controller('AgentController', function($scope,$rootScope,paginationService, $state, $http,$uibModal, $log, $filter, NgTableParams, $location, agentManager, userManager) {
        $scope.headline = "Agents";
        $scope.count = 0;
        $scope.agents = {};
        $scope.loading =false;
        $scope.currentPageOfAgents = [];
        $scope.agentsCount = 0;
        $scope.agentTableParams = {};
        $scope.showInvalid = false;
        $scope.query = "";
        var pageable;

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.loading = true;
            // var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            agentManager.getAgents($scope.query,$scope.showInvalid,pageable, function(response){
                $scope.invalidCount = 0;
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.agents = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.agents;
                    $scope.currentPageOfAgents =  $scope.agents;
                }
            });
        };

        $scope.init = function(query, showInvalid) {
            agentManager.count(query,showInvalid,function(agentsCount) {
                console.log(query, showInvalid)
                $scope.agentTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    count: 10,
                    sorting: {
                        username: 'asc'
                    },
                }, {
                    counts: [],
                    total: agentsCount,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            })
        };

        $scope.init('', $scope.showInvalid);
        $scope.searchFilter = function(){
            $scope.init('', '');
        }

        $scope.refreshAgents = function() {
            $scope.loading = true;
            agentManager.download(function(data) {
                $scope.loading = false;
                loadTableData($scope.agentTableParams);
            })
        };

        $scope.handleClickAddNewAgent = function() {
            $state.go('agentsedit');
        };

        $scope.editAgent = function(agentId){
            $state.go('agentsedit', {id: agentId});
        };
        $scope.$on('AgentUpdated', function (e, value) {
            loadTableData($scope.agentTableParams);
        });
        $scope.isAdmin = function(){
            return userManager.getUser().admin;
        };
    })

    .controller('AddAgentController', function($scope, $rootScope, $state, $location, $stateParams,agentManager, branchOfficeManager ) {
        $scope.headline = "Add Agent";
        $scope.agent = {};
        $scope.offices = [];
        $scope.agentId = $stateParams.id;
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });
        if($stateParams.id) {
            agentManager.load($stateParams.id, function (data) {
                if(data) {
                    $scope.agent = data;
                }
            });
        }

        $scope.saveAgent = function(){
            if ($stateParams.id) {
                agentManager.save($scope.agent, function (response) {
                    $state.go('agents');
                });
            } else {
                agentManager.addAgent($scope.agent, function (response) {
                    if(response) {
                        $state.go('agents');
                    }
                });
            }
        };
        $scope.cancel = function () {
            $state.go('agents');
        };

        $scope.launchAddBranchOffice = function() {
            $scope.cancel();
            $location.url('/branchoffice/');
        }
    })
    .factory('agentManager', function ($http, $log,$rootScope) {
    var agents = {};
    return {
        getAgents: function (query, showInvalid, pageable, callback) {
            $http({url:'/api/v1/agents?query='+query+"&showInvalid="+showInvalid,method: "GET",params: pageable})
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving agents");
                });
        },
        count: function (query, showInvalid, callback) {
            if(!showInvalid || showInvalid.trim().length == 0){
                showInvalid = false;
            }
            $http.get('/api/v1/agent/count?query='+query+"&showInvalid="+showInvalid)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving route count");
                });
        },

        addAgent : function (agent, callback) {
            $http.post('/api/v1/agent/addAgent', agent).then(function (response) {
                if (angular.isFunction(callback)) {
                    callback(response.data);
                }
                $rootScope.$broadcast('AgentAdded');
            }, function (err, status) {
                sweetAlert("Error", err.data.message, "error");
            });
        },

        download: function (callback) {
            $http.get('/api/v1/agent/download')
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error downloading agents");
                    sweetAlert("Error",error.data.message,"error");
                });
        },
        save: function(agent, callback) {
            if(agent.id) {
                $http.put('/api/v1/agent/update',agent).then(function(response){
                    if(angular.isFunction(callback)){
                        callback(response.data);
                    }
                    $rootScope.$broadcast('AgentUpdated');
                },function (err,status) {
                    sweetAlert("Error",err.data.message,"error");
                });
            }
        },
        load: function(agentId,callback) {
            $http.get('/api/v1/agent/'+agentId)
                .then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('AgentLoadComplete');
                },function (error) {
                    $log.debug("error retrieving agent info");
                    sweetAlert("Error",error.data.message,"error");
                });
        },
        getNames: function(callback) {
            $http.get('/api/v1/agentNames/')
                .then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('AgentNamesLoadComplete');
                },function (error) {
                    $log.debug("error retrieving agent info");
                    sweetAlert("Error",error.data.message,"error");
                });
        },
        updateBranchOffice:function (agent,callback) {
            $http.put('/api/v1/agent/updateBranchOffice',agent)
                .then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('AgentUpdatedInServiceReports');
                    },function (error) {
                    $log.debug("error downloading agents");
                    sweetAlert("Error",error.data.message,"error");
                });
        }
    }
});



