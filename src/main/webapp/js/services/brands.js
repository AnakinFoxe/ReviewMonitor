/**
 * Created by xing on 5/3/15.
 */
app.factory('brands', ['$http', 'CacheFactory', function($http, CacheFactory) {
    var brandsCache;

    if (!CacheFactory.get('brandsCache')) {
        brandsCache = CacheFactory('brandsCache', {
            deleteOnExpire: 'aggressive',
            onExpire: function(key, value) {
                $http.get(key).success(function(data) {
                    brandsCache.put(key, data);
                });
            }
        });
    }

    return $http.get('http://localhost:8080/webapi/rm/brand/', {cache: brandsCache})
        .success(function(data) {
            return data;
        })
        .error(function(data) {
            return data;
        });
}]);