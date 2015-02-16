var log = new Log('fuse.handlebars');
//TODO: create a different set of helpers for init parsing

var Handlebars = require('handlebars-v2.0.0.js').Handlebars;

var getScope = function (unit) {
    var jsFile = fuse.getFile(unit, '', '.js');
    var viewModel = {};
    if (jsFile.isExists()) {
        log.error(jsFile.getPath());
        viewModel = require(jsFile.getPath()).onRequest();
    }
    viewModel.app = {
        url: '/' + fuseState.appName
    };
    viewModel.self = {
        publicURL: '/' + fuseState.appName + '/public/' + unit,
        "class": unit + '-unit'
    };
    return viewModel;
};

Handlebars.innerZones = [];
Handlebars.innerZonesFromUnit = null;

Handlebars.registerHelper('defineZone', function (zoneName, zoneContent) {
    var result = '';
    var zone = Handlebars.Utils.escapeExpression(zoneName);
    fuseState.currentZone.push(zone);
    var unitsToRender = fuseState.zones[zone] || [];

    if (Handlebars.innerZones.length > 0) {
        unitsToRender = fuseState.zones[Handlebars.innerZones[0]] || [];
    }

    // if there is no one overriding, then display inline zone
    if (zoneContent['fn'] && unitsToRender.length == 0) {
        return zoneContent.fn(this).trim();
    }

    for (var i = 0; i < unitsToRender.length; i++) {
        var unit = unitsToRender[i];
        if (Handlebars.innerZonesFromUnit == null || Handlebars.innerZonesFromUnit.unitName == unit.unitName) {
            var template = fuse.getFile(unit.originUnitName || unit.unitName, '', '.hbs');
            log.debug('[' + requestId + '] for zone "' + zone + '" including template :"' + template.getPath() + '"');
            result += Handlebars.compileFile(template)(getScope(unit.unitName));
        }
    }

    // we go to inner zones if result is empty, what we should really do it
    // if matched zone is fully made of sub-zones. this is a hack to
    // make it easy to implement.
    if (result.trim().length == 0 && zoneContent['fn']) {
        Handlebars.innerZones.push(zoneName);
        for (i = 0; i < unitsToRender.length; i++) {
            unit = unitsToRender[i];
            Handlebars.innerZonesFromUnit = unit;
            result += zoneContent.fn(this).trim();
            Handlebars.innerZonesFromUnit = null;
        }
        Handlebars.innerZones.pop();
        return result;
    }

    fuseState.currentZone.pop();
    return new Handlebars.SafeString(result);
});

Handlebars.registerHelper('zone', function (zoneName, zoneContent) {
    var currentZone = fuseState.currentZone[fuseState.currentZone.length - 1];
    if (currentZone == null) {
        return 'zone_' + zoneName;
    }

    // if it's exact zone match or if any in inner zone matches we render zone.
    // this second condition is a hack. what we should really do is to keep another stack,
    // and only match with the peek of that stack and always fill it with next in innerZone stack.
    if (zoneName == currentZone || Handlebars.innerZones.indexOf(zoneName) >= 0) {
        return zoneContent.fn(this).trim();
    } else {
        return '';
    }
});

Handlebars.registerHelper('layout', function (layoutName) {
    var currentZone = fuseState.currentZone[fuseState.currentZone.length - 1];
    if (currentZone == null) {
        return 'layout_' + layoutName;
    } else {
        return '';
    }
});

Handlebars.registerHelper('unit', function (unitName) {
    //TODO warn when unspecified decencies are included.
    fuseState.currentZone.push('main');
    var template = fuse.getFile(unitName, '', '.hbs');
    log.info('[' + requestId + '] including "' + unitName + '"');
    var result = new Handlebars.SafeString(Handlebars.compileFile(template)(getScope(unitName)))
    fuseState.currentZone.pop();
    return result;
});

Handlebars.compileFile = function (file) {
    //TODO: remove this overloaded argument
    var f = (typeof file === 'string') ? new File(file) : file;

    if (!Handlebars.cache) {
        Handlebars.cache = {};
    }

    if (Handlebars.cache[f.getPath()] != null) {
        return Handlebars.cache[f.getPath()];
    }

    f.open('r');
    log.debug('[' + requestId + '] reading file "' + f.getPath() + '"');
    var content = f.readAll().trim();
    f.close();
    var compiled = Handlebars.compile(content);
    Handlebars.cache[f.getPath()] = compiled;
    return compiled;
};
