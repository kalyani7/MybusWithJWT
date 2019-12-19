"use strict";
/*global angular, _*/

angular.module('myBus.layoutEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusLayoutEditController', function ($rootScope,$window, $scope, $http, $log, NgTableParams, $modal, $filter, busLayoutManager,cancelManager, $stateParams, $location, $cacheFactory) {
        $log.debug('BusLayoutController loading');
        var busLayoutEditCtrl = this;

        busLayoutEditCtrl.valid = false;

        busLayoutEditCtrl.totalSeats = 0;

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layout Details";

        busLayoutEditCtrl.busLayout = {
            rows : null,
            type: null,
            name : null,
            upper : null,
            lower : null,
            upperHeader : '',
            lowerHeader : ''
        };

        busLayoutEditCtrl.layouts  = [
              {id: 'SLEEPER', name: 'SLEEPER'},
              {id: 'SEMI_SLEEPER', name: 'SEMI_SLEEPER'},
              {id: 'AC_SEMI_SLEEPER', name: 'AC_SEMI_SLEEPER'}
            ];

       busLayoutEditCtrl.rows = [
            {id: '10', name: '10'},
            {id: '11', name: '11'},
            {id: '12', name: '12'},
            {id: '13', name: '13'},
            {id: '14', name: '14'},
            {id: '15', name: '15'},
            {id: '16', name: '16'},
            {id: '17', name: '17'}

           ];

      busLayoutEditCtrl.seats = [
             {id: 1, name: '1'},
              {id: 2, name: '2'},
              {id: 3, name: '3'},
              {id: 4, name: '4'}
           ];

      busLayoutEditCtrl.middleRows = [
             {id: 2, name: 'Middle Row After First Seat'},
             {id: 3, name: 'Middle Row After Second Seat'},
             {id: 4, name: 'Middle Row After Third Seat'}
           ];

      busLayoutEditCtrl.middleRowSeats = [
             {id: true, name: 'Yes'},
             {id: false, name: 'No'}
           ];

      var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};

      var layOutId = $stateParams.id;

      if(layOutId !== ''){
        var cache = $cacheFactory.get($rootScope.id);
        if(cache){
            busLayoutEditCtrl.busLayout = cache.get(layOutId);
        }
        if(busLayoutEditCtrl.busLayout && busLayoutEditCtrl.busLayout.id !== ''){
            busLayoutEditCtrl.name = busLayoutEditCtrl.busLayout.name;
            busLayoutEditCtrl.type = $filter('filter')(busLayoutEditCtrl.layouts, {id: busLayoutEditCtrl.busLayout.type})[0];
            busLayoutEditCtrl.seatsPerRow = $filter('filter')(busLayoutEditCtrl.seats, {id: busLayoutEditCtrl.busLayout.seatsPerRow})[0];
            busLayoutEditCtrl.totalRows = $filter('filter')(busLayoutEditCtrl.rows, {id: busLayoutEditCtrl.busLayout.totalRows})[0];
            busLayoutEditCtrl.middleRow = $filter('filter')(busLayoutEditCtrl.middleRows, {id: busLayoutEditCtrl.busLayout.middleRowPosition})[0];
            busLayoutEditCtrl.middleRowSeat = $filter('filter')(busLayoutEditCtrl.middleRowSeats, {id: busLayoutEditCtrl.busLayout.middleRowLastSeat})[0];
            busLayoutEditCtrl.active = busLayoutEditCtrl.busLayout.active;
            busLayoutEditCtrl.sleeper = false;
            console.log(busLayoutEditCtrl.busLayout.rows);
            var rows = angular.copy(busLayoutEditCtrl.busLayout.rows);
            if(busLayoutEditCtrl.type.id === 'SLEEPER'){
                busLayoutEditCtrl.sleeper = true;
                busLayoutEditCtrl.layoutCls = 'sleeper';
            }else{
                busLayoutEditCtrl.layoutCls = 'seat';
            }
            if(busLayoutEditCtrl.sleeper && busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                for(var k = 0; k < 2; k++){
                    if(k===0){
                       busLayoutEditCtrl.busLayout.upper = getSeats(true, rows);
                       busLayoutEditCtrl.busLayout.upperHeader = 'Upper';
                    }else{
                       busLayoutEditCtrl.busLayout.lower = getSeats(true, rows);
                       busLayoutEditCtrl.busLayout.lowerHeader = 'Lower';
                    }
                }
            }else if(busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                busLayoutEditCtrl.busLayout.rows = getSeats(false, rows);
            }
        }else{
            busLayoutEditCtrl.busLayout = {
                rows : null,
                type: null,
                name : null,
                upper : null,
                lower : null,
                upperHeader : '',
                lowerHeader : ''
            }
        }
      }



        function getName(id){
            return $filter('filter')(seatNames.seats, {id: id })[0];
        }

        busLayoutEditCtrl.getSeatName = function(seat){
            return seat.number;
        }

        function initialize(){
        	busLayoutEditCtrl.busLayout.name = null;
            busLayoutEditCtrl.busLayout.type = null;
            busLayoutEditCtrl.busLayout.rows = null;
            busLayoutEditCtrl.busLayout.upper = null;
            busLayoutEditCtrl.busLayout.lower = null;
            busLayoutEditCtrl.busLayout.isBig = false;
            busLayoutEditCtrl.busLayout.upperHeader = '';
            busLayoutEditCtrl.busLayout.lowerHeader = '';
            busLayoutEditCtrl.totalSeats = 0;
        }

        busLayoutEditCtrl.doLayout = function (){
            initialize();

            // layout css class
            busLayoutEditCtrl.sleeper = false;
            if(busLayoutEditCtrl.type.id === 'SLEEPER'){
                busLayoutEditCtrl.sleeper = true;
                busLayoutEditCtrl.layoutCls = 'sleeper';
            }else{
                busLayoutEditCtrl.layoutCls = 'seat';
            }

            // building the rows and columns
            if(busLayoutEditCtrl.sleeper && busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                for(var k = 0; k < 2; k++){
                    if(k===0){
                       busLayoutEditCtrl.busLayout.upper = getSeats(true, null);
                       busLayoutEditCtrl.busLayout.upperHeader = 'Upper';
                    }else{
                       busLayoutEditCtrl.busLayout.lower = getSeats(true, null);
                       busLayoutEditCtrl.busLayout.lowerHeader = 'Lower';
                    }
                }
            }else if(busLayoutEditCtrl.seatsPerRow && busLayoutEditCtrl.totalRows){
                busLayoutEditCtrl.busLayout.rows = getSeats(false, null);
            }

        };

        function getSeats(sleeper, oldrows){
            var rows = [], middleseatpos = 0, middleseat = 0;
            if(busLayoutEditCtrl.middleRow){
                middleseatpos = busLayoutEditCtrl.middleRow.id;
            }
            if(busLayoutEditCtrl.middleRowSeat){
                middleseat = busLayoutEditCtrl.middleRowSeat.id;
            }
            var cols = busLayoutEditCtrl.seatsPerRow.id;

            if(sleeper && cols > 2){
                cols = 2;
            }

            if(middleseatpos > 0){
                cols = parseInt(cols) +1;
            }

            if (cols > 4){
                busLayoutEditCtrl.busLayout.isBig = true;
            }

            for (var i = 1; i <= cols; i++){
                var seats = [];
                if(i === parseInt(middleseatpos)){
                    for (var j = 1; j <= busLayoutEditCtrl.totalRows.id; j++){
                        var number = getName(j).name+''+i;
                        console.log(j+','+busLayoutEditCtrl.totalRows.id);
                        if(angular.equals(middleseat, true) && angular.equals(j, parseInt(busLayoutEditCtrl.totalRows.id))){
                            if(!sleeper){
                                busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                                seats.push({
                                	number : number, 
                                	[number]: number
                                });
                            }
                        }else{
                            seats.push({number : null, [number]: null});
                        }
                    }
                }else{
                    for (var j = 1; j <= busLayoutEditCtrl.totalRows.id; j++){
                        var number = getName(j).name+''+i;
                        var displayName = number;
                        if(oldrows && !sleeper){
                            console.log(rows);
                            var row = oldrows[i-1].seats;
                            displayName = $filter('filter')(row, {number: number})[0].displayName;
                        }
                        busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                        seats.push({number : number, [number]: displayName});
                    }
                }
                rows.push({seats :seats})
            }
            return rows;
        }

        busLayoutEditCtrl.goToLayouts = function(){
            $location.url('/layouts');
        };

        $scope.$on('layoutsCreateComplete', function (e, value) {
             busLayoutEditCtrl.goToLayouts();
        });

        busLayoutEditCtrl.saveLayout = function (){
            var rows = [];
            
            if(busLayoutEditCtrl.type.id === 'SLEEPER'){
            	
            	angular.forEach(busLayoutEditCtrl.busLayout.upper, function(row, key) {
            		var seats = [];
            		angular.forEach(row, function(busseats, key) {
            			angular.forEach(busseats, function(busseat, key) {
            				var seat = {
            						number : null,
            						displayName : null,
            						upperDeck:true,
            						sleeper:true,
            						seatStatus:"UNAVAILABLE"
            				};
            				seat.number = busseat.number;
            				seat.displayName = busseat[seat.number];
            				if(busseat.number)
            					seat.seatStatus="AVAILABLE"
            				seats.push(seat);
            			});
            		});
            		rows.push({seats: seats});
            	});
            	
            	angular.forEach( busLayoutEditCtrl.busLayout.lower, function(row, key) {
            		var seats = [];
            		angular.forEach(row, function(busseats, key) {
            			angular.forEach(busseats, function(busseat, key) {
            				var seat = {
            						number : null,
            						displayName : null,
            						upperDeck:false,
            						sleeper:true,
            						seatStatus:"UNAVAILABLE"
            				};
            				seat.number = busseat.number;
            				if(busseat.number)
            					seat.seatStatus="AVAILABLE"
            				seat.displayName = busseat[seat.number];
            				seats.push(seat);
            			});
            		});
            		rows.push({seats: seats});
            	});
            	
            }else{
            	angular.forEach(busLayoutEditCtrl.busLayout.rows, function(row, key) {
            		var seats = [];
            		angular.forEach(row, function(busseats, key) {
            			angular.forEach(busseats, function(busseat, key) {
            				var seat = {
            						number : null,
            						displayName : null,
            						seatStatus:"UNAVAILABLE"
            				};
            				seat.number = busseat.number;
            				if(busseat.number)
            					seat.seatStatus="AVAILABLE"
            				seat.displayName = busseat[seat.number];
            				seats.push(seat);
            			});
            		});
            		rows.push({seats: seats});
            	});
            }
            var layoutToSave = {
                name : busLayoutEditCtrl.name,
                type: busLayoutEditCtrl.type.id,
                totalSeats : busLayoutEditCtrl.totalSeats,
                seatsPerRow : busLayoutEditCtrl.seatsPerRow.id,
                totalRows : busLayoutEditCtrl.totalRows.id,
                middleRowPosition : busLayoutEditCtrl.middleRow.id,
                middleRowLastSeat : busLayoutEditCtrl.middleRowSeat.id,
                rows: rows,
                active : busLayoutEditCtrl.active,
                id : busLayoutEditCtrl.busLayout.id
            };

            if(layoutToSave.id && layoutToSave.id !== 'create'){
                busLayoutManager.updateLayout(layoutToSave);
            }else{
                busLayoutManager.createLayout(layoutToSave);
            }
        };

        busLayoutEditCtrl.cancel = function (theForm){
            cancelManager.cancel(theForm);
        }

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layouts";

        return busLayoutEditCtrl;

  })
