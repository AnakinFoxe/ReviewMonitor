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
    };

    $scope.currentPage = 1;
    $scope.pageSize = 20;
    $scope.maxSize = 10;
    $scope.displayReviews = []; // for display (after pagination)

    reviews.getReviews().success(function(data) {
        $scope.reviews = data;
        $scope.updatedReviews = data;   // filter & sorting applied
        //$scope.selectedReviews = data;  // for display (before pagination)

        // the reason I put these initialization at here is to trigger $watch
        // sorting
        $scope.sorting = {
            latest: true,
            rating: -1
        };

        // filters
        $scope.filters = {
            filterDateStart: new Date().setDate(new Date().getDate() - 1),
            filterDateEnd: new Date(),
            filterRates: [true, true, true, false, false],
            filterStatus: 0,
            filterModel: 'All Models'
        };

        /** Pagination **/
        $scope.$watch('currentPage + pageSize', function() {
            var begin = (($scope.currentPage - 1) * $scope.pageSize);
            var end = begin + $scope.pageSize;

            $scope.displayReviews = $scope.updatedReviews.slice(begin, end);
        });
    });


    /** Filters Settings **/
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

    // Make the filters settings work
    $scope.$watch(
        'filters.filterDateStart + filters.filterDateEnd + filters.filterRates ' +
        '+ filters.filterStatus + filters.filterModel' +
        '+ sorting.latest + sorting.rating',
        function() {
            if ($scope.reviews) {
                // first apply filter
                // by date
                $scope.updatedReviews = _.filter($scope.reviews, function(review) {
                    return review.date >= $scope.filters.filterDateStart
                        && review.date <= $scope.filters.filterDateEnd;
                });
                // by rate
                $scope.updatedReviews = _.filter($scope.updatedReviews, function(review) {
                    return ($scope.filters.filterRates[0] && review.rate == 1)
                        || ($scope.filters.filterRates[1] && review.rate == 2)
                        || ($scope.filters.filterRates[2] && review.rate == 3)
                        || ($scope.filters.filterRates[3] && review.rate == 4)
                        || ($scope.filters.filterRates[4] && review.rate == 5);
                });
                // by status
                if ($scope.filters.filterStatus == -1) {
                    $scope.updatedReviews = _.filter($scope.updatedReviews, function(review) {
                        return review.status == 'REPLIED';
                    });
                } else if ($scope.filters.filterStatus == 1) {
                    $scope.updatedReviews = _.filter($scope.updatedReviews, function(review) {
                        return review.status == 'NEEDS_REPLY';
                    });
                }
                // by model
                if ($scope.filters.filterModel != 'All Models') {
                    $scope.updatedReviews = _.filter($scope.updatedReviews, function(review) {
                        return review.modelNum == $scope.filters.filterModel;
                    });
                }

                // then sort the rest reviews
                $scope.updatedReviews = sorts($scope.sorting.latest, $scope.sorting.rating, $scope.updatedReviews, _);

                // update display
                var begin = (($scope.currentPage - 1) * $scope.pageSize);
                var end = begin + $scope.pageSize;
                $scope.displayReviews = $scope.updatedReviews.slice(begin, end);
            }
        });

    // Model filter setter
    $scope.selectModel = function(model) {
        if (model != null) {
            $scope.filters.filterModel = model;
        } else {
            $scope.filters.filterModel = 'All Models';
        }
    };

    /** Display Amazon Review Page **/
    $scope.openReview = function(review) {
        var action = {
            id: review.id,
            permalink: review.permalink,
            replied: false
        };
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'views/reviewPage.html',
            controller: 'ReviewModalController',
            size: 'lg',
            backdrop: 'static',
            resolve: {
                action: function () {
                    return action;
                }
            }
        });

        modalInstance.result.then(function(reaction) {
            if (reaction.replied == true) {
                reviews.replyReview(reaction.id).success(function(data) {
                    console.log('ReviewId=' + reaction.id + ' marked as replied.');

                    // update display reviews and reviews
                    review.status = 'REPLIED';
                });
            }
        });
    };

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


