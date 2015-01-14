function identifierFormatter(value, row, index) {
    return [
            '<a class="like" href="/cdm/devices/'+value+'" title="Like">',
                value,
            '</a>'
        ].join('');}