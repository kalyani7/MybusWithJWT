/**
 * Created by sriharshakota on 3/25/17.
 */
angular.module('myBus.paginationService', ['ngTable'])

.factory('paginationService', function () {
    return {
        pagination: function (tableParams, callback) {
            var sortingProps = tableParams.sorting();
            var sortProps = "";
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            callback(sortProps);
        },
        exportToExcel : function(tableId, fileName){
            $("#"+tableId).table2excel({
                // exclude CSS class
                exclude: ".noExl",
                name: "Worksheet Name",
                filename: fileName
            });
        }
    }
});