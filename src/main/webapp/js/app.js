/**
 * Created by xing on 4/30/15.
 */
var underscore = angular.module('underscore', []);
underscore.factory('_', function() {
   return window._;
});

var app = angular.module('ReviewMonitor',
    ['ngRoute', 'ui.bootstrap', 'angular-cache', 'underscore', 'filters-module']);


app.config(function($routeProvider, CacheFactoryProvider) {
   $routeProvider
       .when('/', {
           controller: 'BrandController',
           templateUrl: 'views/brand.html'
       })
       .when('/brand/:id', {
           controller: 'ReviewController',
           templateUrl: 'views/review.html'
       })
       .otherwise({
           redirectTo: '/'
       });

   angular.extend(CacheFactoryProvider.defaults, {maxAge: 1000 * 3600 * 24});
});

