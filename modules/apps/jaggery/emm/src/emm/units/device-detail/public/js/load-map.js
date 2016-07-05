/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var map;

function loadLeafletMap() {
    var deviceLocationID = "#device-location",
        lat = $(deviceLocationID).data("lat"),
        long = $(deviceLocationID).data("long"),
        container = "device-location",
        zoomLevel = 13,
        tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";

    if (lat && long) {
        map = L.map(container).setView([lat, long], zoomLevel);
        L.tileLayer(tileSet, {attribution: attribution}).addTo(map);
        L.marker([lat, long]).addTo(map).bindPopup("Device is here...").openPopup();

        $("#map-error").hide();
        $("#device-location").show();
    } else {
        $("#device-location").hide();
        $("#map-error").show();
    }
}

$(document).ready(function () {
    $("a[data-toggle='tab']").on("shown.bs.tab", function() {
        var url = $(this).prop("href");
        var hash = url.substring(url.indexOf("#") + 1);

        if (hash == "device_location") {
            if (!map) {
                loadLeafletMap();
            }
        }
    });
});