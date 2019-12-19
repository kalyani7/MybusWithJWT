'use strict';

/* b2c App Module */

var myBusB2c = angular.module('myBusB2c', [
                                        'ui.router',
                                        'ngAnimate',
                                        'ngTouch',
                                        'ngTable',
                                        'dndLists',
                                        'ui.bootstrap',
                                        'unsavedChanges',
                                        'angularSpinner',
                                        'myBusB2c.b2cHome',
                                        'myBusB2c.b2cStatic',
                                        'myBusB2c.b2cResults',
                                        'myBusB2c.b2cDetailsPayment',
                                        'myBusB2c.b2cEticket'
                                        ]);
myBusB2c.config(['$stateProvider','$urlRouterProvider',function($stateProvider,$urlRouterProvider){
	 $stateProvider.
     state('home', {
     	 url:'/home',
         templateUrl: 'b2c_partials/b2cHome.tpl.html',
         controller: 'B2cHomeController'
     }).
     state('aboutUs', {
     	 url:'/aboutUs',
         templateUrl: 'b2c_partials/about-us.tpl.html',
         controller: 'B2cStaticController'
     }).
     state('printTicket', {
     	 url:'/printTicket',
         templateUrl: 'b2c_partials/b2cHome.tpl.html',
         controller: 'B2cHomeController'
     }).
     state('cancelTicket', {
     	 url:'/cancelTicket',
         templateUrl: 'b2c_partials/b2cHome.tpl.html',
         controller: 'B2cHomeController'
     }).
     state('gallery', {
     	 url:'/gallery',
         templateUrl: 'b2c_partials/b2cHome.tpl.html',
         controller: 'B2cHomeController'
     }).
     state('contactUs', {
     	 url:'/contactUs',
         templateUrl: 'b2c_partials/b2cHome.tpl.html',
         controller: 'B2cHomeController'
     }).
     state('results', {
     	 url:'/results',
         templateUrl: 'b2c_partials/b2cResults.tpl.html',
         controller: 'B2cResultsController'
     }).
     state('detailsPayment', {
     	 url:'/detailsPayment',
         templateUrl: 'b2c_partials/b2cDetailsPayment.tpl.html',
         controller: 'B2cDetailsPaymentController'
     }).
     state('eticket', {
     	 url:'/eticket',
         templateUrl: 'b2c_partials/b2cEticket.tpl.html',
         controller: 'B2cEticketController'
     });
	 $urlRouterProvider.otherwise( '/');
}]);