
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
            $state.go('home.agentsadd');
        };

        $scope.editAgent = function(agentId){
            $state.go('home.agentsedit', {id: agentId});
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
                    $state.go('home.agents');
                });
            } else {
                agentManager.addAgent($scope.agent, function (response) {
                    if(response) {
                        $state.go('home.agents');
                    }
                });
            }
        };
        $scope.cancel = function () {
            $state.go('home.agents');
        };

        $scope.launchAddBranchOffice = function() {
            $scope.cancel();
            $state.go()
            $location.url('home.branchoffice');
        }
    })
    .factory('agentManager', function ($http, $log,$rootScope, services) {
    var agents = {};
    return {
        getAgents: function (query, showInvalid, pageable, callback) {
            services.get('/api/v1/agents?query='+query+"&showInvalid="+showInvalid, pageable, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving agents");
            })
            // $http({url:'/api/v1/agents?query='+query+"&showInvalid="+showInvalid,method: "GET",params: pageable})
            //     .then(function (response) {
            //         callback(response.data);
            //     },function (error) {
            //         $log.debug("error retrieving agents");
            //     });
        },
        count: function (query, showInvalid, callback) {
            if(!showInvalid || showInvalid.trim().length == 0){
                showInvalid = false;
            }
            services.get('/api/v1/agent/count?query='+query+"&showInvalid="+showInvalid, '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error retrieving route count");
            })
            // $http.get('/api/v1/agent/count?query='+query+"&showInvalid="+showInvalid)
            //     .then(function (response) {
            //         callback(response.data);
            //     }, function (error) {
            //         $log.debug("error retrieving route count");
            //     });
        },

        addAgent : function (agent, callback) {
            services.post('/api/v1/agent/addAgent', agent, function (response) {
                if (response) {
                    callback(response.data)
                    $rootScope.$broadcast('AgentAdded');
                }
            }, function (error) {
                sweetAlert("Error", err.data.message, "error");
            })
            // $http.post('/api/v1/agent/addAgent', agent).then(function (response) {
            //     if (angular.isFunction(callback)) {
            //         callback(response.data);
            //     }
            //     $rootScope.$broadcast('AgentAdded');
            // }, function (err, status) {
            //     sweetAlert("Error", err.data.message, "error");
            // });
        },

        download: function (callback) {
            services.get('/api/v1/agent/download', '', function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error downloading agents");
                sweetAlert("Error",error.data.message,"error");
            })
            // $http.get('/api/v1/agent/download')
            //     .then(function (response) {
            //         callback(response.data);
            //     },function (error) {
            //         $log.debug("error downloading agents");
            //         sweetAlert("Error",error.data.message,"error");
            //     });
        },
        save: function(agent, callback) {
            if(agent.id) {
                services.put('/api/v1/agent/update', '', agent, function (response) {
                    if(angular.isFunction(callback)){
                        callback(response.data);
                    }
                    $rootScope.$broadcast('AgentUpdated');
                }, function (error) {
                    sweetAlert("Error",error.data.message,"error");
                })
                // $http.put('/api/v1/agent/update',agent).then(function(response){
                //     if(angular.isFunction(callback)){
                //         callback(response.data);
                //     }
                //     $rootScope.$broadcast('AgentUpdated');
                // },function (err,status) {
                //     sweetAlert("Error",err.data.message,"error");
                // });
            }
        },
        load: function(agentId,callback) {
            services.get('/api/v1/agent/'+agentId, function (response) {
                if (response) {
                    callback(response.data)
                    $rootScope.$broadcast('AgentLoadComplete');
                }
            }, function (error) {
                $log.debug("error retrieving agent info");
                sweetAlert("Error",error.data.message,"error");
            })
            // $http.get('/api/v1/agent/'+agentId)
            //     .then(function (response) {
            //         callback(response.data);
            //         $rootScope.$broadcast('AgentLoadComplete');
            //     },function (error) {
            //         $log.debug("error retrieving agent info");
            //         sweetAlert("Error",error.data.message,"error");
            //     });
        },
        getNames: function(callback) {
            services.get('/api/v1/agentNames/', '', function (response) {
                if (response) {
                    callback(response.data)
                    $rootScope.$broadcast('AgentNamesLoadComplete');
                }
            }, function (error) {
                $log.debug("error retrieving agent info");
                sweetAlert("Error",error.data.message,"error");
            })
            // $http.get('/api/v1/agentNames/')
            //     .then(function (response) {
            //         callback(response.data);
            //         $rootScope.$broadcast('AgentNamesLoadComplete');
            //     },function (error) {
            //         $log.debug("error retrieving agent info");
            //         sweetAlert("Error",error.data.message,"error");
            //     });
        },
        updateBranchOffice:function (agent,callback) {
            services.put('/api/v1/agent/updateBranchOffice', '', agent, function (response) {
                if (response) {
                    callback(response.data)
                }
            }, function (error) {
                $log.debug("error downloading agents");
                sweetAlert("Error",error.data.message,"error");
            })
            // $http.put('/api/v1/agent/updateBranchOffice',agent)
            //     .then(function (response) {
            //         callback(response.data);
            //         $rootScope.$broadcast('AgentUpdatedInServiceReports');
            //         },function (error) {
            //         $log.debug("error downloading agents");
            //         sweetAlert("Error",error.data.message,"error");
            //     });
        }
    }
});



