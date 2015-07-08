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

    // TODO: remove in release version
    //var host = 'http://localhost:8080';
    var host = '';

    return {
        getBrands: function() {
            return $http.get(host + '/rm/webapi/brand/', {cache: brandsCache})
                .success(function (data) {
                    return data;
                })
                .error(function (data) {
                    return data;
                });
        },

        deleteBrand: function(brand, key) {
            return $http.delete(host + '/rm/webapi/brand/' + brand, {params: {key: key}})
                .success(function (data) {
                    return data;
                })
                .error(function (data) {
                    return data;
                });
        }
    }
}]);