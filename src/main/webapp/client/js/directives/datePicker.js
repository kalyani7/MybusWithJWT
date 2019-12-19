
angular.module('myBus').directive('dateRangePicker', function() {
	return {
		restrict: 'A',
		templateUrl: '/partials/dateRangePicker.html',
		controller: 'DateRangePickerCtrl',
		replace: true
	};
}).directive('datePicker', function() {
    return {
        restrict: 'E',
        templateUrl: '/partials/datePicker.html',
        controller: 'DatePickerCtrl',
        replace: true
    };
}).controller('DateRangePickerCtrl', function ($scope) {
    console.log('DateRangePickerCtrl loading...');

    $scope.clearStart = function () {
        $scope.dateRangeStart = null;
    };

    $scope.clearEnd = function () {
        $scope.$parent.dateRangeEnd = null;
    };

    $scope.toggleMin = function() {
        $scope.minDate = $scope.minDate ? null : new Date();
    };
    $scope.toggleMin();

    $scope.openStart = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        console.log('open start');
        $scope.startOpened = true;
    };
    $scope.openEnd = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.endOpened = true;
    };

    $scope.minDate = new Date('2014-08-01');
    $scope.maxDate = new Date();
    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[1];

    $scope.dateOptions = {
        initDate: new Date()
    }
}).controller('DatePickerCtrl', function ($scope) {
    console.log('DatePickerCtrl loading...');

    $scope.parseDate = function(){
        $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getMonth()+1))).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
    }
    $scope.today = function() {
        var date = new Date();
        date.setDate(date.getDate() -1);
        $scope.dt = date;
        $scope.parseDate();
    };

    $scope.date = null;
    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];

    $scope.clear = function() {
        $scope.dt = null;
    };

    $scope.inlineOptions = {
        customClass: getDayClass,
        minDate: new Date(),
        showWeeks: true
    };
    $scope.dateChanged = function() {
    	console.log('date changed')
    }
    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };
  	$scope.open = function() {
        $scope.popup.opened = true;
    };
    $scope.setDate = function(year, month, day) {
        $scope.dt = new Date(year, month, day);
    };
    $scope.popup = {
        opened: false
    };
    function getDayClass(data) {
        var date = data.date,
            mode = data.mode;
        if (mode === 'day') {
            var dayToCheck = new Date(date).setHours(0,0,0,0);
            for (var i = 0; i < $scope.events.length; i++) {
                var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                if (dayToCheck === currentDay) {
                    return $scope.events[i].status;
                }
            }
        }
        return '';
    }
    $scope.monthNames = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
});