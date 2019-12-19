"use strict";

angular.module('myBus.verifyInvoiceModule', ['ngTable', 'ui.bootstrap'])
.controller("VerifyInvoiceController", function ($scope, $state, uploadInvoiceManager) {
    $scope.header = 'Verification Invoice';

    $scope.query = {
        isCompleted : false
    };
    $scope.getVerifyInvoiceEntries = function(){
        uploadInvoiceManager.getVerifyInvoiceEntries(function(response){
            $scope.invoiceEntries = response;
        });
    };
    $scope.addUploadInvoice = function () {
        $state.go('addEditUploadInvoice');
    };

    $scope.save = function(){
        if ($scope.uploadInvoice.startDate) {
            var startDate = new Date($scope.uploadInvoice.startDate);
            var startYear = startDate.getFullYear();
            var startMonth = startDate.getMonth() + 1;
            var startDay = startDate.getDate();
            $scope.uploadInvoice.startDate = startYear + '-' + startMonth + '-' + startDay;
        }
        if ($scope.uploadInvoice.endDate) {
            var endDate = new Date($scope.uploadInvoice.endDate);
            var endYear = endDate.getFullYear();
            var endMonth = endDate.getMonth() + 1;
            var endDay = endDate.getDate();
            $scope.uploadInvoice.endDate = endYear + '-' + endMonth + '-' + endDay;
        }
        $scope.query = {
            startDate: $scope.uploadInvoice.startDate,
            endDate: $scope.uploadInvoice.endDate,
            key: $scope.uploadInvoice.file
        };
        uploadInvoiceManager.createUploadInvoice($scope.query, function (data) {

        }, function (error) {
            console.log('error');
        });
    };
    $scope.cancel = function() {
        $state.go('uploadinvoice');
    };

    $scope.uploadByStatus = function(status){
        if(status){
            $scope.showTab = status;
            $scope.query.isCompleted = status;
        }else{
            $scope.query.isCompleted = status;
        }
    };
    $scope.getVerificationDetails = function(verificationId){
        $state.go('verificationDetails',{verificationId:verificationId});
    };
    $scope.deleteEntry = function (entryId) {
        uploadInvoiceManager.deleteEntry(entryId,function (response) {
            $scope.getVerifyInvoiceEntries();
        });
    };
    $scope.getVerifyInvoiceEntries();
    $scope.downloadFile = function(fileName){
        $scope.url = "https://mybus-prod-uploads.s3.amazonaws.com/"+fileName;
    };

}).controller('verificationDetailsController',function (uploadInvoiceManager,$stateParams,$scope, $state) {

    $scope.viewBack = function () {
        $state.go('verifyinvoice');
    };

    $scope.getVerificationDetails = function(){
        uploadInvoiceManager.getVerificationDetails($stateParams.verificationId,function(data){
            $scope.bookingsNotInInvoice = data.bookingsNotInInvoice;
            $scope.invoiceBookingNotInBookings = data.invoiceBookingNotInBookings;
            $scope.cancelledBookings = data.cancelledBookings;
        });
    };
    uploadInvoiceManager.getVerifyInvoice($stateParams.verificationId,function(response){
        $scope.fileName = response.fileName;
        $scope.getVerificationDetails();
    });
}).factory('uploadInvoiceManager', function ($rootScope, $http, $log,$q) {
        return {
            createUploadInvoice: function (query, callback, error) {
                $http.post('/api/v1/bookings/createAnInvoiceEntry',query).then(function (response) {
                    callback(response.data);
                    swal("Great", "Your Upload Invoice has been success", "success");
                },function (err,status) {
                    sweetAlert("Error Saving Vehicle info",err.data.message,"error");
                });
            },
            getVerifyInvoiceEntries:function (callback) {
                $http.get('/api/v1/bookings/getVerifyInvoiceEntries')
                    .then(function (response) {
                        callback(response.data);
                    },function(err) {});
            },
            getVerificationDetails:function (verificationId,callback) {
                $http.get('/api/v1/getVerificationDetails/'+verificationId)
                    .then(function (response) {
                        callback(response.data);
                    },function(err) {});
            },
            getVerifyInvoice:function (verificationId,callback) {
                $http.get('/api/v1/getVerifyInvoice/'+verificationId)
                    .then(function (response) {
                        callback(response.data);
                    },function(err) {});
            },
            deleteEntry:function(id,callback) {
                swal({   title: "Are you sure?",   text: "You will not be able to recover this !",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false
                }, function() {
                    $http.delete('/api/v1/bookings/deleteVerifyInvoice/' + id)
                        .then(function (response) {
                            callback(response);
                            sweetAlert("Great", "successfully deleted", "success");
                        },function (error) {
                            sweetAlert("Oops...", "Error finding data!", "error" + angular.toJson(error));
                        });
                });
            }
        }
    });