/**
 * Created by srinikandula on 9/25/16.
 */
angular.module('myBus')
.directive('number', function() {
    return {
        template: function(elem, attr) {
        	
        	var minlength = 7;
        	if ( attr.minlength != null ){
        		minlength = attr.minlength;
        	}
        	
            return "<input type='text' placeholder='"+attr.placeholder+"' " +
            "class='form-control' data-ng-model='"+attr.model+"' " +
                "onkeypress='return event.charCode >= 48 && event.charCode <= 57' " +
                " data-ng-minlength='" + minlength +"' data-ng-maxlength='10' " +
                " data-ng-required='true' data-ng-disabled='"+attr.disabled+"' />";
        }
    };
}).directive('autoFocus', function($timeout) {
        return {
            restrict: 'AC',
            link: function(_scope, _element) {
                console.log('focusing...');
                $timeout(function(){
                    _element[0].focus();
                }, 10);
            }
        };
    }).directive('myDatepicker', function () {
    return {
        restrict: 'E',
        scope: {
            ngModel: "=",
        },
        template:
        '    <div>\n' +
        '        <p class="input-group" style="margin: 0;">\n' +
        '          <input type="text" class="form-control" uib-datepicker-popup ng-model="ngModel" is-open="opened" ng-required="true"  />\n' +
        '          <span class="input-group-btn">\n' +
        '            <button type="button" class="btn btn-default" ng-click="open($event)">' +
        '<i class="glyphicon glyphicon-calendar"></i></button>\n' +
        '          </span>\n' +
        '        </p>\n' +
        '    </div>\n',
        require: 'ngModel',
        link: function (scope) {
            scope.opened=false;
            scope.open = function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                if(scope.opened){
                    scope.opened=!scope.opened;
                }else{
                    scope.opened = !scope.opened;
                }
            };

        }
    };
});
