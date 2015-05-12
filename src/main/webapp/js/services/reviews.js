/**
 * Created by xing on 5/3/15.
 */
app.factory('reviews', ['$http', '$routeParams', 'CacheFactory', function($http, $routeParams, CacheFactory) {
    var reviewsCache;
    if (!CacheFactory.get('reviewsCache')) {
        reviewsCache = CacheFactory('reviewsCache', {
            deleteOnExpire: 'aggressive',
            onExpire: function(key, value) {
                $http.get(key).success(function(data) {
                    reviewsCache.put(key, data);
                });
            }
        });
    }

    return $http.get('http://localhost:8080/webapi/rm/review/brand/' + $routeParams.id, {cache: reviewsCache})
        .success(function(data) {
            return data;
        })
        .error(function(data) {
            return data;
        });
}]);