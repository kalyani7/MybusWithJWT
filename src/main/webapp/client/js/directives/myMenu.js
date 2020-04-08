angular.module('myBus')
.directive('myMenu',['userManager', function(userManager){
	return {
		 restrict: "E",
	     template: '<li>' +
			 			'<a ng-if="label != \'Configuration\'" ui-sref="home.{{label | lowercase}}">{{label}}</a>'+
	     				'<a ng-if="label == \'Configuration\'" class="dropdown-toggle" data-toggle="dropdown">{{label}}<span class="caret"></span></a>'+
	     			'</li>',
	     scope: {label:'@', noSecond: '='},
        link: function (scope, element, attrs){
		 	var permission = attrs.permission;
			if (true) {
				// user has permission, no work for me
				return false;
			}
		}
	};
}]);