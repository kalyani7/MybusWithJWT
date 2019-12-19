"use strict";

angular.module('myBus')
  .value('states',
    function (rawStates) {
      return _.map(rawStates, function (name, abbreviation) {
        return {abbreviation: abbreviation, name: name, displayName: (abbreviation + ' - ' + name)};
      });
    }({
      'AP': 'Andhra Pradesh',
      'KA': 'Karnataka',
      'MH': 'Maharastra',
      'TN': 'Tamilnadu',
      'TS': 'Telangana'
    }));