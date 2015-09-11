/**
 * Created by xing on 5/5/15.
 */
app.controller('FilterModalController', function($scope, $modalInstance, filters) {

    $scope.filterDays = 0;  // init as 0 so it won't have effect to previous selected dates
    $scope.minDate = null;
    $scope.maxDate = new Date();
    $scope.format = 'MMMM-dd-yyyy';

    $scope.openStart = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        // only one is allowed to be opened at the same time
        $scope.startOpened = true;
        $scope.endOpened = false;
    };
    $scope.openEnd = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.startOpened = false;
        $scope.endOpened = true;
    };

    $scope.$watch('filterDays', function() {
        if ($scope.filterDays > 0) {
            $scope.tbc.filterDateEnd = new Date();
            $scope.tbc.filterDateStart
                = new Date().setDate($scope.tbc.filterDateEnd.getDate() - $scope.filterDays);
        }
    });

    /** Filters **/
    $scope.filters = filters;

    // tbc: to be changed
    $scope.tbc = {
        filterDateStart: $scope.filters.filterDateStart,
        filterDateEnd: $scope.filters.filterDateEnd,
        filterRates: $scope.filters.filterRates,
        filterStatus: $scope.filters.filterStatus,
        filterModel: $scope.filters.filterModel
    };

    $scope.ok = function() {
        $modalInstance.close($scope.tbc);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };


});