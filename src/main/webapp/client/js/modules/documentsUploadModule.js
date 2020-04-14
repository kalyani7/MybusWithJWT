"use strict";

angular.module('myBus.documentsUploadModule', ['ngTable', 'ui.bootstrap'])

.controller("DocumentsUploadController",function($scope,DocumentUploadManager,$state){
    $scope.upload = function(){
        DocumentUploadManager.upload($scope.fileName,$scope.file,$scope.description,function(successCallback){
            },function(errorCallback){
        });
    };
    $scope.cancel = function(){
        $state.go('home.documentsupload');
    };
    $scope.$on('uploadCompleted', function (e, value) {
        $state.go('home.documentsupload');
    });
}).controller("DocumentsUploadListController",function($scope,DocumentUploadManager,$state,NgTableParams,paginationService,userManager){
    $scope.query = {};
    $scope.removeFile = function(key,id){
        $scope.data = {};
        $scope.data.key = key;
        $scope.data.id = id;
        DocumentUploadManager.removeFile($scope.data,function(successCallback){
            $scope.init();
        });
    };
    $scope.uploadDocument = function(){
        $state.go('home.uploadDocument');
    };
    DocumentUploadManager.getUsers(function(response){
        $scope.users = response;
    });
    var pageable;
    var loadTableParams = function (tableParams){
        paginationService.pagination(tableParams, function(response){
            pageable = {
                page:tableParams.page(),
                size:tableParams.count(),
                sort:response
            };
        });
        $scope.query.page = pageable.page;
        $scope.query.sort = pageable.sort;
        $scope.query.size = pageable.size;
        DocumentUploadManager.getAllUploads($scope.query,function(successCallback){
            $scope.uploads = successCallback.data.content;
            $scope.count = successCallback.data.totalElements;
            tableParams.data = $scope.uploads;
            $scope.currentPageOfUploads = $scope.uploads;
        },function(errorCallback){

        });
    };

    $scope.init = function() {
        DocumentUploadManager.count($scope.query,function (count) {
            $scope.documentUploadsTableParams = new NgTableParams({
                page: 1, // show first page
                size: 10,
                sorting: {
                    date: 'desc'
                }
            }, {
                counts: [20, 50, 100],
                total: count.data,
                getData: function (params) {
                    loadTableParams(params);
                }
            });
        });
    };
    $scope.init();
}).factory("DocumentUploadManager", function ($http,Upload,$rootScope, $cookies, services) {
    var token = $cookies.get('token');
    var tokenType = $cookies.get('tokenType');
    var sendToken = tokenType + ' ' + token;
    return {
        upload:function(fileName,file,description){
            Upload.upload({
                url: '/api/v1/documentUpload/upload',
                headers: {
                    "Authorization": sendToken
                },
                data: {
                    fileName: fileName,
                    description: description,
                    files: file
                }
            }).then(function (success) {
                if (success) {
                    swal({
                        title: "Wow!",
                        text: "File uploaded successfully!",
                        type: "success"
                    });
                    $rootScope.$broadcast('uploadCompleted');
                }
            });
        },
        getAllUploads: function(data, callback) {
            services.post('/api/v1/documentUpload/getAllUploads', data, function (response) {
                if (response) {
                    callback(response)
                }
            }, function (error) {

            })
            // $http.post('/api/v1/documentUpload/getAllUploads', data)
            //     .then(function (response) {
            //         callback(response);
            //     }, function (err) {
            //     });
        },
        removeFile: function(data, callback) {
            services.post('/api/v1/documentUpload/deleteUpload', data, function (response) {
                if (response) {
                    callback(response);
                }
            }, function (error) {

            })
            // $http.post('/api/v1/documentUpload/deleteUpload', data)
            //     .then(function (response) {
            //         callback(response);
            //     }, function (err) {
            //     });
        },
        count:function(data,callback){
            services.post('/api/v1/documentUpload/count', data, function (response) {
                if (response) {
                    callback(response)
                }
            }, function (error) {

            })
            // $http.post('/api/v1/documentUpload/count', data)
            //     .then(function (response) {
            //         callback(response);
            //     }, function (err) {
            //     });
        },
        getUsers:function(callback){
            services.get('/api/v1/users', '', function (response) {
                if (response) {
                    callback(response)
                }
            }, function (error) {

            })
            // $http.get('/api/v1/users')
            //     .then(function (response) {
            //         callback(response.data);
            //     },function (error) {
            //     });
        }
    }
});
