/**
 * Created by xing on 5/10/15.
 */
app.factory('crawlers', ['$http', function($http) {

    // TODO: remove in release version
    //var host = 'http://localhost:8080';
    var host = '';

    var url = host + '/rm/webapi/crawl/';

    return {
        addCrawler: function(brand, key) {
          var randomStr = Date.now().toString();
          return $http.get(url + brand, {params: {key: key, date: randomStr}})
              .success(function(data) {
                  return data;
              })
              .error(function(data) {
                  return data;
              });
        },

        stopCrawler: function(brand, key) {
            var randomStr = Date.now().toString();
            return $http.get(url + 'stop/' + brand, {params: {key: key, date: randomStr}})
                .success(function(data) {
                    return data;
                })
                .error(function(data) {
                    return data;
                });
        },

        getStatus: function() {
            var randomStr = Date.now().toString();
            return $http.get(url + 'status', {params: {date: randomStr}})
                .success(function (data) {
                    return data;
                })
                .error(function (data) {
                    return data;
                });
        }
    };

}]);