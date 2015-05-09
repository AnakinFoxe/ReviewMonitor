/**
 * Created by xing on 4/30/15.
 */
app.controller('BrandController', ['$scope', 'brands', function($scope, brands) {
    brands.success(function(data) {
        $scope.brands = data;
    })
}]);
