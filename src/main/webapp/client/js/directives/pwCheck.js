/**
 * Created by svanik on 2/22/2016.
 */

angular.module('myBus')
    .directive('pwCheck', function (){
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                var pwd = '#' + attrs.pwCheck;
                elem.on('keyup', function () {
                    scope.$apply(function () {
                        var v = elem.val()===$(pwd).val();
                        ctrl.$setValidity('pwmatch', v);
                    });
                });
            }
        };

    });
