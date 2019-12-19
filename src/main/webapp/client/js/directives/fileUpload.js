"use strict";

angular.module('myBus')
    .directive('fileUpload',['$rootScope', '$http', 'Upload' ,'$stateParams','S3UploadManager', function ($rootScope, $http, Upload, $stateParams, S3UploadManager) {
    return {
        restrict: 'E',
        scope: {
            ngModel: "=",
            type: "=",
        },
        template: ' <div>\n' +
            '                <div>\n' +
            '                    <div class="col-sm-4">\n' +
            '                        <label class=" control-label" for="uploadFiles">Upload Files</label>\n' +
            '                        <input type="file" ng-model="file" ngf-select ngf-multiple="true" class="form-control"\n' +
            '                               id="uploadFiles">\n' +
            '                    </div>\n' +
            '                    <div class="col-sm-1 mt-30">\n' +
            '                        <button class="btn btn-xs btn-info" ng-click="fileUpload(file);">Upload</button>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '                <div class="clearfix"></div>\n' +
            '                <div class="row col-xs-8" ng-if="images.length > 0">\n' +
            '                    <div class="uploaded-files">\n' +
            '                        <h4>Uploaded Files</h4>\n' +
            '                        <ol class="text-left">\n' +
            '                            <li ng-repeat="image in images track by $index">\n' +
            '                                <div class="col-sm-8">\n' +
            '                                    <span class="text-left" target="_blank">{{image.displayName}} </span>\n' +
            '                                </div>\n' +
            '    \n' +
            '                                <div class="col-sm-1">\n' +
            '                                    <a href="{{image.url}}" title="Download" download>\n' +
            '                                        <span class="glyphicon glyphicon-download-alt"></span>\n' +
            '                                    </a>\n' +
            '                                </div>\n' +
            '                                <div class="col-sm-1">\n' +
            '                                    <a href="" title="Delete" ng-click="removeFile(refId, image.fileName)">\n' +
            '                                        <span class="glyphicon glyphicon-trash"></span>\n' +
            '                                    </a>\n' +
            '                                </div>\n' +
            '                            </li>\n' +
            '                        </ol>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>',
        require: 'ngModel',
        link: function (scope) {

            scope.getUploads = function(){
                scope.file = '';
                scope.refId = $stateParams.id;
                S3UploadManager.getUploads($stateParams.id, function (response) {
                    if(response){
                        scope.images = response;
                    }
                });
            };

            scope.getUploads();

            scope.removeFile = function (refId, name) {
                $http({
                    url: '/api/v1/deleteUpload',
                    method: "POST",
                    data:{"fileName":name, "refId":refId}
                }).then(function (success){
                    scope.getUploads();
                    }, function (error) {
                });
            };
            var type = scope.type;


            scope.fileUpload = function(file){
                if (!file) {
                    swal("Error!", "Please select File", "error");
                }else {
                    scope.uploadFile(file, $stateParams.id, type);
                }

            };
            scope.uploadFile = function (file, id, type) {
                Upload.upload({
                    url: '/api/v1/fileUpload',
                    data: {
                        id: id,
                        type: type,
                        files: file,
                    },
                }).then(function (success) {
                    if (success) {
                        swal({
                            title: "Wow!",
                            text: "File uploaded successfully!",
                            type: "success",
                        });
                        scope.getUploads();
                    }
                });
            };


        }
    };

}]);