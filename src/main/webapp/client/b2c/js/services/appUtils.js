"use strict";

var portalApp = angular.module('myBusB2c');

portalApp.factory('appUtils', function () {

  return {
    /**
     * convert time(hh:mm a) to date
     */
    getDateFromString : function(time){
    	var dateFromString = new Date();
    	var timeSplitArray = time.split(' ');
    	if(timeSplitArray[1]==='AM' ||timeSplitArray[1]==='am'){
    		dateFromString.setHours(timeSplitArray[0].split(':')[0]);
    		dateFromString.setMinutes(timeSplitArray[0].split(':')[1]);
    	}else if(timeSplitArray[1]==='PM' ||timeSplitArray[1]==='pm'){
    		dateFromString.setHours(parseInt(timeSplitArray[0].split(':')[0])+12);
    		dateFromString.setMinutes(parseInt(timeSplitArray[0].split(':')[1])+12);
    	}else{
    		dateFromString = 'invalid time formate'
    	}
    	return dateFromString
    }
    
  };
});


