/**
 * Created by xing on 5/10/15.
 */
app.controller('ManageModalController', ['$scope', '$modalInstance', 'crawlers', 'brands',
    function($scope, $modalInstance, crawlers, brands) {

    $scope.inputKey = '';
    $scope.inputBrand = '';

    crawlers.getStatus().success(function(data) {
        $scope.crawlers = parser(data);
    });

    $scope.add = function() {
        crawlers.addCrawler($scope.inputBrand, $scope.inputKey).success(function(data) {
            console.log($scope.inputBrand + ' crawler added');
        });
        $modalInstance.close();
    };

    $scope.stop = function (crawler) {
        crawlers.stopCrawler(crawler.brand, $scope.inputKey).success(function(data) {
            console.log(crawler.brand + ' should be stopped');
        });
        crawler.button = 'Stopping';
    };

    $scope.close = function() {
        $modalInstance.close();
    };

    $scope.delete = function() {
        for (var i = 0; i < $scope.crawlers.length; i++) {
            var crawler = $scope.crawlers[i];
            if (crawler.brand == $scope.inputBrand) {
                // stop crawler
                crawlers.stopCrawler(crawler.brand, $scope.inputKey).success(function(data) {});
                crawler.button = 'Stopping';

                console.log("stopping " + crawler.brand);
                break;
            }
        }

        console.log("start deleting " + $scope.inputBrand);

        // delete brand (and all its related products/reviews
        brands.deleteBrand($scope.inputBrand, $scope.inputKey).success(function(data) {
            console.log(crawler.brand + ' deleted');
        });

        $modalInstance.close();

    };
}]);


var parser = function(crawlers) {

    var results = [];

    for (var i = 0; i < crawlers.length; i++) {
        var status;
        var type;
        var progress;
        var running;
        if (crawlers[i].running && crawlers[i].crawling) {
            type = 'danger';
            progress = 100;
            status = 'Crawling...';
            running = true;
        } else if (crawlers[i].running && !crawlers[i].crawling) {
            type = 'success';
            progress = 20;
            status = 'Idle';
            running = true;
        } else {
            type = 'warning';
            progress = 100;
            status = 'Shutting down the crawler';
            running = false;
        }

        results.push({
            brand: crawlers[i].brand,
            status: status,
            type: type,
            progress: progress,
            button: 'Stop',
            running: running
        });
    }

    return results;
}