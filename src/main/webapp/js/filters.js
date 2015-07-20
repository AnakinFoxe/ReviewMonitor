/**
 * Created by xing on 6/30/15.
 */
angular.module('filters-module', []).filter('trustAsResourceUrl', ['$sce', function($sce) {
    return function(val) {
        return $sce.trustAsResourceUrl(val);
    };
}]);