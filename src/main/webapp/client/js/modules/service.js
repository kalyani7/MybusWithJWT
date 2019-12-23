angular.module('myBus')
myBus.factory('services', function ($http, $log, $cookies, $location, Upload, $rootScope, $timeout, $state) {
    var token = $cookies.get('token');
    var tokenType = $cookies.get('tokenType');
    var sendToken = tokenType + ' ' + token;
    return {
        post: function (url, data, successCallback, errorCallback) {
            $http({
                method: 'post',
                url: url,
                data: data,
                headers: {
                    "Authorization": sendToken
                }
            }).then(function (response) {
                successCallback(response);
            }, function (error) {
                // if(error.status === 403){
                //     // $cookies.remove('token');
                //     $rootScope.isLoggedIn = false;
                //     $location.url('login');
                // }
                errorCallback(error)
            });
        },
        get: function (url, successCallback, errorCallback) {
            $http({
                method: 'get',
                url: url,
                headers: {
                    "Authorization": sendToken
                }
            }).then(function (response) {
                successCallback(response);
            }, function (error) {
                // if(error.status === 403){
                //     // $cookies.remove('token');
                //     $rootScope.isLoggedIn = false;
                //     $location.url('login');
                // }
                // errorCallback(error);
            })
        },
        put: function (url, status, data, successCallback, errorCallback) {
            $http({
                method: 'put',
                url: url,
                params: {status: status},
                data: data,
                headers: {
                    "Authorization": sendToken
                }
            }).then(function (response) {
                successCallback(response);
            }, function (error) {
                // if(error.status === 403){
                //     // $cookies.remove('token');
                //     $rootScope.isLoggedIn = false;
                //     $location.url('login');
                // }
                errorCallback(error)
            });
        },
        unDelete: function (url, status, data, successCallback, errorCallback) {
            swal({
                title: "Are you sure?",
                text: "you want to Re-activate this",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#28a745",
                confirmButtonText: "Yes, Re-activate it!",
                closeOnConfirm: false
            }, function () {
                $http({
                    method: 'put',
                    url: url,
                    params: {status: status},
                    data: data,
                    headers: {
                        "Authorization": sendToken
                    }
                }).then(function (response) {
                    successCallback(response);
                    $rootScope.$broadcast('UnDelete')
                    swal("Re-activated!", "Successfully Re-activated!", "success");
                }, function (error) {
                    errorCallback(error)
                })
            })
        },
        delete: function (url, successCallback, errorCallback) {
            swal({
                title: "Are you sure?",
                text: "You will not be able to recover this !",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, delete it!",
                closeOnConfirm: false
            }, function () {
                $http({
                    method: 'delete',
                    url: url,
                    headers: {
                        "Authorization": sendToken
                    }
                }).then(function (response) {
                    $rootScope.$broadcast('DeleteCompleted');
                    swal("Deleted!", "successfully deleted!", "success");
                })
            });
        },
        disable: function (url, successCallback, errorCallback) {
            swal({
                title: "Are you sure?",
                text: "You will not be able to recover this !",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, disable it!",
                closeOnConfirm: false
            }, function () {
                $http({
                    method: 'delete',
                    url: url,
                    headers: {
                        "Authorization": sendToken
                    }
                }).then(function (response) {
                    $rootScope.$broadcast('ItemDisable');
                    swal("Disable!", "successfully disable!", "success");
                })
            });
        },

        disableAffected: function (url, successCallback, errorCallback) {
            $http({
                method: 'delete',
                url: url,
                headers: {
                    "Authorization": sendToken
                }
            }).then(function (response) {
                successCallback(response)
            })
        },

        uploadExportExcel: function (url, file, successCallback, errorCallback) {
            Upload.upload({
                // method: 'Upload',
                url: url,
                data: {
                    files: file
                },
                headers: {
                    "Authorization": sendToken
                }
            }).then(function (response) {
                successCallback(response)
                $timeout(function () {
                    var Result = response.data;
                });
            }, function (response) {
                if (response.status > 0) {
                    var errorMsg = response.status + ': ' + response.data;
                    // alert(errorMsg);
                }
            }, function (evt) {
                // var element = angular.element(document.querySelector('#dvProgress'));
                var Progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                // element.html('<div style="width: ' + Progress + '%">' + Progress + '%</div>');
                // ngToast.create({
                //     className: 'success',
                //     content: '<span class="progress"><span style="width: ' + Progress + '%">' + Progress + '%</span></span>',
                //     timeout: 30000 + Progress
                // });
            }, function (error) {
                if (error) {
                    errorCallback(error)
                }
            });
        }
    }
});
