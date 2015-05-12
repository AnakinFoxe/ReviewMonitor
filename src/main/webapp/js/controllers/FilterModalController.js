/**
 * Created by xing on 5/5/15.
 */
app.controller('FilterModalController', function($scope, $modalInstance, filters) {
    $scope.filters = filters;
    $scope.selected = {
        filterDays: $scope.filters.filterDays,
        filterRates: $scope.filters.filterRates
    };


    $scope.ok = function(data) {
        $modalInstance.close(data);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});