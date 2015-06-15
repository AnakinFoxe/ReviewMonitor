/**
 * Created by xing on 4/30/15.
 */
app.controller('ReviewController', ['$scope', 'reviews', '$modal', '_', function($scope, reviews, $modal, _) {

    $scope.color = function(status) {
        if (status == 'REPLIED') {
            return {"background-color": "#FFDC00"};
        } else {
            return {"background-color": "#FFF"};
        }
    }

    $scope.currentPage = 1;
    $scope.pageSize = 20;
    $scope.maxSize = 10;
    $scope.displayReviews = [];

    reviews.getReviews().success(function(data) {
        $scope.reviews = data;
        $scope.updatedReviews = data;

        // the reason I put these initialization at here is to trigger $watch
        // sorting
        $scope.sorting = {
            latest: true,
            rating: -1
        };

        // filters
        $scope.filters = {
            filterDays: 1,
            filterRates: [true, true, true, false, false]
        };

        /** Pagination **/
        $scope.$watch('currentPage + pageSize', function() {
            var begin = (($scope.currentPage - 1) * $scope.pageSize);
            var end = begin + $scope.pageSize;

            $scope.displayReviews = $scope.updatedReviews.slice(begin, end);
        });
    });


    /** Settings **/
    $scope.open = function(size) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'views/filter.html',
            controller: 'FilterModalController',
            size: size,
            resolve: {
                filters: function() {
                    return $scope.filters;
                }
            }
        });

        modalInstance.result.then(function(updatedFilters) {
            $scope.filters = updatedFilters;
        });
    };

    // Make the settings work
    $scope.$watch('sorting.latest + sorting.rating + filters.filterDays + filters.filterRates', function() {
        if ($scope.reviews) {
             // first apply filter
             // by date
             // convert to GMT first, and offset another 19 hours because
             // Amazon review starts at 5AM-GMT everyday
            //var targetDayValue = Date.now() - (new Date()).getTimezoneOffset() * 60 * 1000
            //    - ($scope.filters.filterDays * 24 + 19) * 3600 * 1000;
            var targetDayValue = Date.now() - $scope.filters.filterDays * 24 * 3600 * 1000;
            $scope.updatedReviews = _.filter($scope.reviews, function(review) {
                return review.date > targetDayValue;
            })
            // by rate
            $scope.updatedReviews = _.filter($scope.updatedReviews, function(review) {
                return ($scope.filters.filterRates[0] && review.rate == 1)
                    || ($scope.filters.filterRates[1] && review.rate == 2)
                    || ($scope.filters.filterRates[2] && review.rate == 3)
                    || ($scope.filters.filterRates[3] && review.rate == 4)
                    || ($scope.filters.filterRates[4] && review.rate == 5);
            })

            // then sort the rest reviews
            $scope.updatedReviews = sorts($scope.sorting.latest, $scope.sorting.rating, $scope.updatedReviews, _);

            // update display
            var begin = (($scope.currentPage - 1) * $scope.pageSize);
            var end = begin + $scope.pageSize;
            $scope.displayReviews = $scope.updatedReviews.slice(begin, end);

            console.log(Date.now() + " => " + targetDayValue);
            console.log($scope.displayReviews);
        }
    });

}]);


var sorts = function(latest, rating, reviews, _) {
    var updatedReviews;
    if (latest) {   // latest first
        updatedReviews = _.sortBy(reviews, function(review) {
            return -review.date;
        });
    } else {    // oldest first
        updatedReviews = _.sortBy(reviews, function(review) {
            return review.date;
        });
    }

    if (rating == -1) { // lowest first
        updatedReviews = _.sortBy(updatedReviews, function(review) {
            return review.rate;
        });
    }
    else if (rating == 1) { // highest first
        updatedReviews = _.sortBy(updatedReviews, function(review) {
            return -review.rate;
        });
    }
    // else: off

    return updatedReviews;
};


