/**
 * Created by xing on 4/30/15.
 */
app.controller('BrandController', ['$scope', 'brands', '$modal', function($scope, brands, $modal) {
    brands.success(function(data) {
        $scope.brands = data;
    });


    $scope.open = function(size) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'views/manage.html',
            controller: 'ManageModalController',
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
}]);
