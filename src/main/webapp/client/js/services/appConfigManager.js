"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('appConfigManager', function ($http, $log) {

  var appConfigProperties = null;

  return {

    fetchAppSettings: function (callback, forceRefresh) {
      if (appConfigProperties === null || forceRefresh) {
        $http.get('/api/v1/appconfig')
            .then(function (response) {
              appConfigProperties = response.data;
              $log.debug("App configuration properties loaded:\n" + angular.toJson(appConfigProperties));
              return angular.isFunction(callback) && callback(null, appConfigProperties);
            },function (err, status) {
              var errorMsg = 'Error getting app settings from server. Status code ' + status + ".  " + angular.toJson(err);
              $log.error(errorMsg);
              return angular.isFunction(callback) && callback(errorMsg, null);
            });
      } else {
        return angular.isFunction(callback) && callback(null, appConfigProperties);
      }
    },

    areConditionsEnabled: function () {
      return appConfigProperties && appConfigProperties['conditions.enabled'];
    },

    areProceduresEnabled: function () {
      return appConfigProperties && appConfigProperties['procedures.enabled'];
    }
  };
});