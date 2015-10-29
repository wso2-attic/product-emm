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
var modalPopup = '.wr-modalpopup',
    modalPopupContainer = modalPopup + ' .modalpopup-container',
    modalPopupContent = modalPopup + ' .modalpopup-content';
function openCollapsedNav(){
    $(".wr-hidden-nav-toggle-btn").addClass("active");
    $("#hiddenNav").slideToggle("slideDown", function () {
        if ($(this).css("display") == "none") {
            $(".wr-hidden-nav-toggle-btn").removeClass("active");
        }
    });
}


/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    var maxHeight = "max-height";
    var marginTop = "margin-top";
    var body = "body";
    $(modalPopupContent).css(maxHeight, ($(body).height() - ($(body).height()/100 * 30)));
    $(modalPopupContainer).css(marginTop, (-($(modalPopupContainer).height()/2)));
}

/*
 * show popup function.
 */
function showPopup() {
    $(modalPopup).show();
    setPopupMaxHeight();
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modalPopupContent).html("");
    $(modalPopupContent).removeClass("operation-data");
    $(modalPopup).hide();
}


function generateQRCode(qrCodeClass){
    var enrollmentURL = $("#qr-code-modal").data("enrollment-url");
    $(qrCodeClass).qrcode({
        text	: enrollmentURL,
        width: 200,
        height: 200
    });
}

function toggleEnrollment(){
    $(".modalpopup-content").html($("#qr-code-modal").html());
    generateQRCode(".modalpopup-content .qr-code");
    showPopup();
}

function loadNotifications(){

    var serviceURL = "/mdm-admin/notifications/NEW";

    var successCallback = function (data) {
        data = JSON.parse(data);
        if(data.length > 0){
            $("#notification-bubble").html(data.length);
        }

    };

    invokerUtil.get(serviceURL,
        successCallback, function(message){
            console.log(message);
    });
}

$(document).ready(function () {
    loadNotifications();
});