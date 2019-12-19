/* 
* Creates a range
* Usage example: <option ng-repeat="y in [] | range:1998:1900">{{y}}</option>
*/
var app = angular.module('myBus');

app.filter('range', function () {
        return function (input, start, end) {
            start = parseInt(start);
            end = parseInt(end);
            var direction = (start <= end) ? 1 : -1;
            while (start != end) {
                input.push(start);
                if (direction < 0 && start == end + 1) {
                    input.push(end);
                }
                if (direction > 0 && start == end - 1) {
                    input.push(end);
                }
                start += direction;
            }
            return input;
        };
    });