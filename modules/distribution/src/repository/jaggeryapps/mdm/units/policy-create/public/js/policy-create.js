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

var validateStep = {};
var stepForwardFrom = {};
var stepBackFrom = {};
var policy = {};
var configuredFeatures = [];

var PlatformType = {
    ANDROID: "android",
    IOS: "ios"
};

stepForwardFrom["policy-platform"] = function (actionButton) {
    policy["platform"] = $(actionButton).data("platform");
    policy["platformId"] = $(actionButton).data("platform-id");
    // updating next-page wizard title with selected platform
    $("#policy-profile-page-wizard-title").text("ADD " + policy["platform"] + " POLICY");

    var deviceType = policy["platform"];
    var hiddenOperationsByDeviceType = $("#hidden-operations-" + deviceType);
    var hiddenOperationsByDeviceTypeCacheKey = deviceType + "HiddenOperations";
    var hiddenOperationsByDeviceTypeSrc = hiddenOperationsByDeviceType.attr("src");

    setTimeout(
        function () {
            $.template(hiddenOperationsByDeviceTypeCacheKey, hiddenOperationsByDeviceTypeSrc, function (template) {
                var content = template();
                $(".wr-advance-operations").html(content);
            });
        },
        250 // time delayed for the execution of above function, 250 milliseconds
    );
};

/**
 * Checks if provided number is valid against a range.
 *
 * @param numberInput
 * @param min
 * @param max
 * @returns {boolean}
 */
var inputIsValidAgainstRange = function (numberInput, min, max) {
    return (numberInput == min || (numberInput > min && numberInput < max) || numberInput == max);
};

validateStep["policy-profile"] = function () {
    var validationStatusArray = [];
    var validationStatus;

    // starting validation process and updating validationStatus
    if (policy.platform == PlatformType.ANDROID) {
        if (configuredFeatures.length == 0) {
            validationStatus = {
                "error": true,
                "mainErrorMsg": "You cannot continue. Zero configured features."
            };
            validationStatusArray.push(validationStatus);
        } else {
            if ($.inArray("PASSCODE_POLICY", configuredFeatures) != -1) {
                // if PASSCODE_POLICY is configured
                var maxPasscodeAgeInDays = $("input#maxPINAgeInDays").val();
                var passcodeHistory = $("input#pinHistory").val();
                // initializing continueToCheckNextInput to true
                var continueToCheckNextInput = true;

                // validating first input: maxPasscodeAgeInDays
                if (maxPasscodeAgeInDays) {
                    if (!$.isNumeric(maxPasscodeAgeInDays)) {
                        continueToCheckNextInput = false;
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Provided passcode age is not a number. Please check.",
                            "erroneousFeature": "passcode-policy"
                        };
                        validationStatusArray.push(validationStatus);
                    } else {
                        maxPasscodeAgeInDays = parseInt(maxPasscodeAgeInDays);
                        if (!inputIsValidAgainstRange(maxPasscodeAgeInDays, 1, 730)) {
                            continueToCheckNextInput = false;
                            validationStatus = {
                                "error": true,
                                "subErrorMsg":
                                    "Provided passcode age is not with in the range of 1-to-730. Please check.",
                                "erroneousFeature": "passcode-policy"
                            };
                            validationStatusArray.push(validationStatus);
                        } else {
                            continueToCheckNextInput = true;
                        }
                    }
                } else {
                    continueToCheckNextInput = true;
                }

                // validating second and last input: passcodeHistory
                if (continueToCheckNextInput) {
                    if (passcodeHistory) {
                        if (!$.isNumeric(passcodeHistory)) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "Provided passcode history is not a number. Please check.",
                                "erroneousFeature": "passcode-policy"
                            };
                            validationStatusArray.push(validationStatus);
                        } else if ($.isNumeric(passcodeHistory)) {
                            passcodeHistory = parseInt(passcodeHistory);
                            if (!inputIsValidAgainstRange(passcodeHistory, 1, 50)) {
                                validationStatus = {
                                    "error": true,
                                    "subErrorMsg":
                                        "Provided passcode history is not with in the range" +
                                        " of 1-to-50. Please check.",
                                    "erroneousFeature": "passcode-policy"
                                };
                                validationStatusArray.push(validationStatus);
                            } else {
                                validationStatus = {
                                    "error": false,
                                    "okFeature": "passcode-policy"
                                };
                                validationStatusArray.push(validationStatus);
                            }
                        }
                    } else {
                        validationStatus = {
                            "error": false,
                            "okFeature": "passcode-policy"
                        };
                        validationStatusArray.push(validationStatus);
                    }
                }
            }
            if ($.inArray("CAMERA", configuredFeatures) != -1) {
                // if CAMERA is configured
                validationStatus = {
                    "error": false,
                    "okFeature": "camera"
                };
                validationStatusArray.push(validationStatus);
            }
            if ($.inArray("ENCRYPT_STORAGE", configuredFeatures) != -1) {
                // if ENCRYPT_STORAGE is configured
                validationStatus = {
                    "error": false,
                    "okFeature": "encrypt-storage"
                };
                validationStatusArray.push(validationStatus);
            }
            if ($.inArray("WIFI", configuredFeatures) != -1) {
                // if WIFI is configured
                var ssid = $("input#ssid").val();
                if (!ssid) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID is not given. You cannot proceed.",
                        "erroneousFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                } else if (!inputIsValidAgainstLength(ssid, 1, 30)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID exceeds maximum allowed length. Please check.",
                        "erroneousFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
        }
    } else if (policy.platform == PlatformType.IOS) {
        if (configuredFeatures.length == 0) {
            validationStatus = {
                "error": true,
                "mainErrorMsg": "You cannot continue. Zero configured features."
            };
            validationStatusArray.push(validationStatus);
        } else {
            if ($.inArray("PASSCODE_POLICY", configuredFeatures) != -1) {
                // if PASSCODE_POLICY is configured
                maxPasscodeAgeInDays = $("input#maxPINAgeInDays").val();
                passcodeHistory = $("input#pinHistory").val();
                // initializing continueToCheckNextInput to true
                continueToCheckNextInput = true;

                // validating first input: maxPasscodeAgeInDays
                if (maxPasscodeAgeInDays) {
                    if (!$.isNumeric(maxPasscodeAgeInDays)) {
                        continueToCheckNextInput = false;
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Provided passcode age is not a number. Please check.",
                            "erroneousFeature": "passcode-policy"
                        };
                        validationStatusArray.push(validationStatus);
                    } else {
                        maxPasscodeAgeInDays = parseInt(maxPasscodeAgeInDays);
                        if (!inputIsValidAgainstRange(maxPasscodeAgeInDays, 1, 730)) {
                            continueToCheckNextInput = false;
                            validationStatus = {
                                "error": true,
                                "subErrorMsg":
                                    "Provided passcode age is not with in the range of 1-to-730. Please check.",
                                "erroneousFeature": "passcode-policy"
                            };
                            validationStatusArray.push(validationStatus);
                        } else {
                            continueToCheckNextInput = true;
                        }
                    }
                } else {
                    continueToCheckNextInput = true;
                }

                // validating second and last input: passcodeHistory
                if (continueToCheckNextInput) {
                    if (passcodeHistory) {
                        if (!$.isNumeric(passcodeHistory)) {
                            validationStatus = {
                                "error": true,
                                "subErrorMsg": "Provided passcode history is not a number. Please check.",
                                "erroneousFeature": "passcode-policy"
                            };
                            validationStatusArray.push(validationStatus);
                        } else if ($.isNumeric(passcodeHistory)) {
                            passcodeHistory = parseInt(passcodeHistory);
                            if (!inputIsValidAgainstRange(passcodeHistory, 1, 50)) {
                                validationStatus = {
                                    "error": true,
                                    "subErrorMsg":
                                        "Provided passcode history is not with in the range" +
                                        " of 1-to-50. Please check.",
                                    "erroneousFeature": "passcode-policy"
                                };
                                validationStatusArray.push(validationStatus);
                            } else {
                                validationStatus = {
                                    "error": false,
                                    "okFeature": "passcode-policy"
                                };
                                validationStatusArray.push(validationStatus);
                            }
                        }
                    } else {
                        validationStatus = {
                            "error": false,
                            "okFeature": "passcode-policy"
                        };
                        validationStatusArray.push(validationStatus);
                    }
                }
            }
            if ($.inArray("WIFI_SETTINGS", configuredFeatures) != -1) {
                // if WIFI is configured
                ssid = $("input#ssid").val();
                if (!ssid) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID is not given. You cannot proceed.",
                        "erroneousFeature": "wifi-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } else if (!inputIsValidAgainstLength(ssid, 1, 30)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID exceeds maximum allowed length. Please check.",
                        "erroneousFeature": "wifi-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "wifi-settings"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
            if ($.inArray("CONTACTS", configuredFeatures) != -1) {
                /* Validating hostname of the CardDAV server */
                var accountHostname = $("input#accountHostname").val();
                if (!accountHostname) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Hostname of the target CardDAV server is not provided. Please provide a valid hostname to proceed.",
                        "erroneousFeature": "contacts"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = false;
                } else if (!inputIsValid(/^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/, accountHostname)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Hostname provided is invalid. Please provide a valid hostname to proceed",
                        "erroneousFeature": "contacts"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = false;
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "contacts"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = true;
                }

                if (continueToCheckNextInput) {
                    /* Validating port of the CardDAV server */
                    var accountPort = $("input#accountPort").val();
                    if (!accountPort) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port of the target CardDAV server is not provided. Please provide a valid port number to proceed.",
                            "erroneousFeature": "contacts"
                        };
                        validationStatusArray.push(validationStatus);
                    } else if (!inputIsValidAgainstLength(accountPort, 1, 6)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port provided is invalid and outside the allowed range. Please provide a valid port number to proceed",
                            "erroneousFeature": "contacts"
                        };
                        validationStatusArray.push(validationStatus);
                    } else if (!$.isNumeric(accountPort)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port provided is not a valid number. Please provide a valid port number to proceed",
                            "erroneousFeature": "contacts"
                        };
                        validationStatusArray.push(validationStatus);
                    } else {
                        validationStatus = {
                            "error": false,
                            "okFeature": "contacts"
                        };
                        validationStatusArray.push(validationStatus);
                    }
                }
            }
            if ($.inArray("CALENDAR", configuredFeatures) != -1) {
                /* Validating hostname of the CardDAV server */
                var calAccountHostname = $("input#calAccountHostname").val();
                if (!calAccountHostname) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Hostname of the target CalDAV server is not provided. Please provide a valid hostname to proceed.",
                        "erroneousFeature": "calendar"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = false;
                } else if (!inputIsValid(/^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/, calAccountHostname)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Hostname provided is invalid. Please provide a valid hostname to proceed",
                        "erroneousFeature": "calendar"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = false;
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "calendar"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = true;
                }

                if (continueToCheckNextInput) {
                    /* Validating port of the CardDAV server */
                    var calAccountPort = $("input#calAccountPort").val();
                    if (!calAccountPort) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port of the target CalDAV server is not provided. Please provide a valid port number to proceed.",
                            "erroneousFeature": "calendar"
                        };
                        validationStatusArray.push(validationStatus);
                    } else if (!inputIsValidAgainstLength(accountPort, 1, 6)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port provided is invalid and outside the allowed range. Please provide a valid port number to proceed",
                            "erroneousFeature": "calendar"
                        };
                        validationStatusArray.push(validationStatus);
                    } else if (!$.isNumeric(calAccountPort)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "Port provided is not a valid number. Please provide a valid port number to proceed",
                            "erroneousFeature": "calendar"
                        };
                        validationStatusArray.push(validationStatus);
                    } else {
                        validationStatus = {
                            "error": false,
                            "okFeature": "calendar"
                        };
                        validationStatusArray.push(validationStatus);
                    }
                }
            }
            if ($.inArray("SUBSCRIBED_CALENDARS", configuredFeatures) != -1) {
                /* Validating hostname of the CardDAV server */
                var csURL = $("input#csURL").val();
                if (!csURL) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "URL of the calendar file is not provided. Please provide a valid URL to proceed.",
                        "erroneousFeature": "subscribed-calendars"
                    };
                    validationStatusArray.push(validationStatus);
                } if (!inputIsValid(/^(ht|f)tps?:\/\/[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/, csURL)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "URL of the calendar file provided is invalid. Please provide a valid URL to proceed.",
                        "erroneousFeature": "subscribed-calendars"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "subscribed-calendars"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
            if ($.inArray("APN_SETTINGS", configuredFeatures) != -1) {
                /* Validating Access Point Name */
                var apnAccessPointName = $("input#apnAccessPointName").val();
                if (!apnAccessPointName) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Name of the carrier (GPRS) access point is not provided. Please provide a valid name to proceed.",
                        "erroneousFeature": "apn-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "apn-settings"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
            if ($.inArray("WEB_CLIPS", configuredFeatures) != -1) {
                /* Validating hostname of the CardDAV server */
                var wcLabel = $("input#wcLabel").val();
                if (!wcLabel) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "The name to display for the Web Clip is not provided. Please provide a valid name to proceed.",
                        "erroneousFeature": "web-clips"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = false;
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "web-clips"
                    };
                    validationStatusArray.push(validationStatus);
                    continueToCheckNextInput = true;
                }

                if (continueToCheckNextInput) {
                    /* Validating hostname of the CardDAV server */
                    var wcURL = $("input#wcURL").val();
                    if (!wcURL) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "URL to be displayed when opening the Web Clip is not provided. Please provide a valid URL to proceed.",
                            "erroneousFeature": "web-clips"
                        };
                        validationStatusArray.push(validationStatus);
                    } if (!inputIsValid(/^(ht|f)tps?:\/\/[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/, wcURL)) {
                        validationStatus = {
                            "error": true,
                            "subErrorMsg": "URL to be displayed when opening the Web Clip provided is invalid. Please provide a valid URL to proceed.",
                            "erroneousFeature": "web-clips"
                        };
                        validationStatusArray.push(validationStatus);
                    } else {
                        validationStatus = {
                            "error": false,
                            "okFeature": "web-clips"
                        };
                        validationStatusArray.push(validationStatus);
                    }
                }
            }
            if ($.inArray("SCEP_SETTINGS", configuredFeatures) != -1) {
                /* Validating hostname of the CardDAV server */
                var scepURL = $("input#scepURL").val();
                if (!scepURL) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "URL of the SCEP base server is not provided. Please provide a valid URL to proceed.",
                        "erroneousFeature": "scep-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } if (!inputIsValid(/^(ht|f)tps?:\/\/[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/, scepURL)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "URL of the SCEP base server provided is invalid. Please provide a valid URL to proceed.",
                        "erroneousFeature": "scep-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "scep-settings"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
            if ($.inArray("EMAIL_SETTINGS", configuredFeatures) != -1) {
                /* Validating Access Point Name */
                var emAccountDescription = $("input#emAccountDescription").val();
                if (!emAccountDescription) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "Display name of the account is not provided. Please provide a valid name to proceed.",
                        "erroneousFeature": "email-settings"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "email-settings"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
        }
    }
    // ending validation process

    /**
     * Checks if provided input is valid against RegEx input.
     *
     * @param regExp Regular expression
     * @param inputString Input string to check
     * @returns {boolean} Returns true if input matches RegEx
     */
    function inputIsValid(regExp, inputString) {
        return regExp.test(inputString);
    }

    // start taking specific notifying actions upon validation
    var wizardIsToBeContinued;
    var errorCount = 0;
    var mainErrorMsgWrapper, mainErrorMsg, subErrorMsgWrapper, subErrorMsg, subErrorIcon, subOkIcon;
    var i;
    for (i = 0; i < validationStatusArray.length; i++) {
        validationStatus = validationStatusArray[i];
        if (validationStatus["error"]) {
            errorCount++;
            if (validationStatus["mainErrorMsg"]) {
                mainErrorMsgWrapper = "#policy-profile-main-error-msg";
                mainErrorMsg = mainErrorMsgWrapper + " span";
                $(mainErrorMsg).text(validationStatus["mainErrorMsg"]);
                $(mainErrorMsgWrapper).removeClass("hidden");
            } else if (validationStatus["subErrorMsg"]) {
                subErrorMsgWrapper = "#" + validationStatus["erroneousFeature"] + "-feature-error-msg";
                subErrorMsg = subErrorMsgWrapper + " span";
                subErrorIcon = "#" + validationStatus["erroneousFeature"] + "-error";
                subOkIcon = "#" + validationStatus["erroneousFeature"] + "-ok";
                $(subErrorMsg).text(validationStatus["subErrorMsg"]);
                $(subErrorMsgWrapper).removeClass("hidden");
                if (!$(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).addClass("hidden");
                }
                $(subErrorIcon).removeClass("hidden");
            }
        } else {
            if (validationStatus["okFeature"]) {
                subErrorMsgWrapper = "#" + validationStatus["okFeature"] + "-feature-error-msg";
                subOkIcon = "#" + validationStatus["okFeature"] + "-ok";
                subErrorIcon = "#" + validationStatus["okFeature"] + "-error";
                if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).addClass("hidden");
                }
                if (!$(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).addClass("hidden");
                }
                $(subOkIcon).removeClass("hidden");
            }
        }
    }

    wizardIsToBeContinued = (errorCount == 0);
    return wizardIsToBeContinued;
};

stepForwardFrom["policy-profile"] = function () {
    policy["profile"] = operationModule.generateProfile(policy["platform"], configuredFeatures);
    // updating next-page wizard title with selected platform
    $("#policy-criteria-page-wizard-title").text("ADD " + policy["platform"] + " POLICY");
};

stepBackFrom["policy-profile"] = function () {
    // reinitialize configuredFeatures
    configuredFeatures = [];
    // clearing already-loaded platform specific hidden-operations html content from the relevant div
    // so that, the wrong content would not be shown at the first glance, in case
    // the user selects a different platform
    $(".wr-advance-operations").html(
        "<div class='wr-advance-operations-init'>" +
            "<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
            "<i class='fw fw-settings fw-spin fw-2x'></i>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;" +
            "Loading Platform Features . . ." +
            "<br>" +
            "<br>" +
        "</div>"
    );
};

stepForwardFrom["policy-criteria"] = function () {
    $("input[type='radio'].select-users-radio").each(function () {
        if ( $(this).is(':radio')) {
            if ($(this).is(":checked")) {
                if($(this).attr("id") == "users-radio-btn") {
                    policy["selectedUsers"] = $("#users-input").val();
                } else if ($(this).attr("id") == "user-roles-radio-btn") {
                    policy["selectedUserRoles"] = $("#user-roles-input").val();
                }
            }
        }
    });
    policy["selectedNonCompliantAction"] = $("#action-input").find(":selected").data("action");
    policy["selectedOwnership"] = $("#ownership-input").val();
    // updating next-page wizard title with selected platform
    $("#policy-naming-page-wizard-title").text("ADD " + policy["platform"] + " POLICY");
};

/**
 * Checks if provided input is valid against provided length range.
 *
 * @param input Alphanumeric or non-alphanumeric input
 * @param minLength Minimum Required Length
 * @param maxLength Maximum Required Length
 * @returns {boolean} Returns true if input matches the provided minimum length and maximum length
 */
var inputIsValidAgainstLength = function (input, minLength, maxLength) {
    var length = input.length;
    return (length == minLength || (length > minLength && length < maxLength) || length == maxLength);
};

validateStep["policy-naming"] = function () {
    var validationStatus = {};

    // taking values of inputs to be validated
    var policyName = $("input#policy-name-input").val();
    // starting validation process and updating validationStatus
    if (!policyName) {
        validationStatus["error"] = true;
        validationStatus["mainErrorMsg"] = "Policy name is empty. You cannot proceed.";
    } else if (!inputIsValidAgainstLength(policyName, 1, 30)) {
        validationStatus["error"] = true;
        validationStatus["mainErrorMsg"] =
            "Policy name exceeds maximum allowed length. Please check.";
    } else {
        validationStatus["error"] = false;
    }
    // ending validation process

    // start taking specific actions upon validation
    var wizardIsToBeContinued;
    if (validationStatus["error"]) {
        wizardIsToBeContinued = false;
        var mainErrorMsgWrapper = "#policy-naming-main-error-msg";
        var mainErrorMsg = mainErrorMsgWrapper + " span";
        $(mainErrorMsg).text(validationStatus["mainErrorMsg"]);
        $(mainErrorMsgWrapper).removeClass("hidden");
    } else {
        wizardIsToBeContinued = true;
    }

    return wizardIsToBeContinued;
};

stepForwardFrom["policy-naming"] = function () {
    policy["policyName"] = $("#policy-name-input").val();
    policy["policyDescription"] = $("#policy-description-input").val();
    //All data is collected. Policy can now be created.
    savePolicy(policy);
};

var savePolicy = function (policy) {
    var profilePayloads = [];
    // traverses key by key in policy["profile"]
    var key;
    for (key in policy["profile"]) {
        if (policy["profile"].hasOwnProperty(key)) {
            profilePayloads.push({
                "featureCode": key,
                "deviceTypeId": policy["platformId"],
                "content": policy["profile"][key]
            });
        }
    }
    var payload = {
        "policyName": policy["policyName"],
        "compliance": policy["selectedNonCompliantAction"],
        "ownershipType": policy["selectedOwnership"],
        "profile": {
            "profileName": policy["policyName"],
            "deviceType": {
                "id": policy["platformId"]
            },
            "profileFeaturesList": profilePayloads
        }
    };

    if (policy["selectedUsers"]) {
        payload["users"] = policy["selectedUsers"];
    } else if (policy["selectedUserRoles"]) {
        payload["roles"] = policy["selectedUserRoles"];
    } else {
        payload["users"] = [];
        payload["roles"] = [];
    }

    console.log(JSON.stringify(payload));

    invokerUtil.post(
        "/mdm-admin/policies",
        payload,
        function () {
            $(".policy-message").removeClass("hidden");
            $(".add-policy").addClass("hidden");
        },
        function () {

        }
    );
};

var showAdvanceOperation = function (operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
};

$(document).ready(function () {

    // Adding initial state of wizard-steps.
    $("#policy-platform-wizard-steps").html($(".wr-steps").html());

    $("select.select2[multiple=multiple]").select2({
        "tags": true
    });

    $("#users-select-field").hide();
    $("#user-roles-select-field").show();

    $("input[type='radio'].select-users-radio").change(function () {
        if ($("#users-radio-btn").is(":checked")) {
            $("#user-roles-select-field").hide();
            $("#users-select-field").show();
        }
        if ($("#user-roles-radio-btn").is(":checked")) {
            $("#users-select-field").hide();
            $("#user-roles-select-field").show();
        }
    });

    // Support for special input type "ANY" on user(s) & user-role(s) selection
    $("#users-input, #user-roles-input").select2({
        "tags": true
    }).on("select2:select", function (e) {
        if (e.params.data.id == "ANY") {
            $(this).val("ANY").trigger("change");
        } else {
            $("option[value=ANY]", this).prop("selected", false).parent().trigger("change");
        }
    });

    // Maintains an array of configured features of the profile
    var advanceOperations = ".wr-advance-operations";
    $(advanceOperations).on("click", ".wr-input-control.switch", function (event) {
        var operationCode = $(this).parents(".operation-data").data("operation-code");
        var operation = $(this).parents(".operation-data").data("operation");
        var operationDataWrapper = $(this).data("target");
        // prevents event bubbling by figuring out what element it's being called from.
        if (event.target.tagName == "INPUT") {
            var zeroConfiguredFeaturesErrorMsg = "#policy-profile-main-error-msg";
            if (!$(this).hasClass("collapsed")) {
                configuredFeatures.push(operationCode);
                // when a feature is enabled, if "zero-configured-features" msg is available, hide that.
                if (!$(zeroConfiguredFeaturesErrorMsg).hasClass("hidden")) {
                    $(zeroConfiguredFeaturesErrorMsg).addClass("hidden");
                }
            } else {
                //splicing the array if operation is present.
                var index = $.inArray(operationCode, configuredFeatures);
                if (index != -1) {
                    configuredFeatures.splice(index, 1);
                }
                // when a feature is disabled, clearing all its current error or success states
                var subErrorMsgWrapper = "#" + operation + "-feature-error-msg";
                var subErrorIcon = "#" + operation + "-error";
                var subOkIcon = "#" + operation + "-ok";
                if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).addClass("hidden");
                }
                if (!$(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).addClass("hidden");
                }
                if (!$(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).addClass("hidden");
                }
                // reinitializing input fields into the defaults
                $(operationDataWrapper + " input").each(
                    function () {
                        if ($(this).is("input:text") || $(this).is("input:password")) {
                            $(this).val("");
                        } else if ($(this).is("input:checkbox")) {
                            $(this).prop("checked", $(this).data("default"));
                            // if this checkbox is the parent input of a grouped-input
                            if ($(this).hasClass("parent-input")) {
                                var groupedInput = $(this).parent().parent().parent();
                                if ($(this).is(":checked")) {
                                    $(".child-input", groupedInput).each(function () {
                                        $(this).prop('disabled', false);
                                    });
                                    if ($("ul", groupedInput).hasClass("disabled")) {
                                        $("ul", groupedInput).removeClass("disabled");
                                    }
                                } else {
                                    $(".child-input", groupedInput).each(function () {
                                        $(this).prop('disabled', true);
                                    });
                                    if (!$("ul", groupedInput).hasClass("disabled")) {
                                        $("ul", groupedInput).addClass("disabled");
                                    }
                                }
                            }
                        }
                    }
                );
                // reinitializing select fields
                $(operationDataWrapper + " select").each(
                    function () {
                        $("option:first", this).prop("selected", "selected");
                    }
                );
            }
        }
    });

    // adding support for cloning multiple profiles per feature with cloneable class definitions
    $(advanceOperations).on("click", ".multi-view.add.enabled", function () {
        // get a copy of .cloneable and create new .cloned div element
        var cloned = "<div class='cloned'><hr>" + $(".cloneable", $(this).parent().parent()).html() + "</div>";
        // append newly created .cloned div element to panel-body
        $(this).parent().parent().append(cloned);
        // enable remove action of newly cloned div element
        $(".cloned", $(this).parent().parent()).each(
            function () {
                if ($(".multi-view.remove", this).hasClass("disabled")) {
                    $(".multi-view.remove", this).removeClass("disabled");
                }
                if (!$(".multi-view.remove", this).hasClass("enabled")) {
                    $(".multi-view.remove", this).addClass("enabled");
                }
            }
        );
    });

    $(advanceOperations).on("click", ".multi-view.remove.enabled", function () {
        $(this).parent().remove();
    });

    // enabling or disabling grouped-input based on the status of a parent check-box
    $(advanceOperations).on("click", ".grouped-input", function () {
        if ($(".parent-input", this).is(":checked")) {
            if ($("ul", this).hasClass("disabled")) {
                $("ul", this).removeClass("disabled");
            }
            $(".child-input", this).each(function () {
                $(this).prop('disabled', false);
            });
        } else {
            if (!$("ul", this).hasClass("disabled")) {
                $("ul", this).addClass("disabled");
            }
            $(".child-input", this).each(function () {
                $(this).prop('disabled', true);
            });
        }
    });

    $(".wizard-stepper").click(function () {
        // button clicked here can be either a continue button or a back button.
        var currentStep = $(this).data("current");
        var validationIsRequired = $(this).data("validate");
        var wizardIsToBeContinued;

        if (validationIsRequired) {
            wizardIsToBeContinued = validateStep[currentStep]();
        } else {
            wizardIsToBeContinued = true;
        }

        if (wizardIsToBeContinued) {
            // When moving back and forth, following code segment will
            // remove if there are any visible error-messages.
            var errorMsgWrappers = ".alert.alert-danger";
            $(errorMsgWrappers).each(
                function () {
                    if (!$(this).hasClass("hidden")) {
                        $(this).addClass("hidden");
                    }
                }
            );

            var nextStep = $(this).data("next");
            var isBackBtn = $(this).data("is-back-btn");

            // if current button is a continuation...
            if (!isBackBtn) {
                // initiate stepForwardFrom[*] functions to gather form data.
                if (stepForwardFrom[currentStep]) {
                    stepForwardFrom[currentStep](this);
                }
            } else {
                // initiate stepBackFrom[*] functions to rollback.
                if (stepBackFrom[currentStep]) {
                    stepBackFrom[currentStep]();
                }
            }

            // following step occurs only at the last stage of the wizard.
            if (!nextStep) {
                window.location.href = $(this).data("direct");
            }

            // updating next wizard step as current.
            $(".itm-wiz").each(function () {
                var step = $(this).data("step");
                if (step == nextStep) {
                    $(this).addClass("itm-wiz-current");
                } else {
                    $(this).removeClass("itm-wiz-current");
                }
            });

            // adding next update of wizard-steps.
            $("#" + nextStep + "-wizard-steps").html($(".wr-steps").html());

            // hiding current section of the wizard and showing next section.
            $("." + currentStep).addClass("hidden");
            $("." + nextStep).removeClass("hidden");
        }
    });
});