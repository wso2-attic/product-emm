/**
 * Following module act as a client to create a saml request and also to
 * unwrap and return attributes of a returning saml response
 * @type {{}}
 */

var client = {};

(function (client) {

    var Util = Packages.org.wso2.store.sso.common.util.Util,
        carbon = require('carbon'),
        log = new Log();

    /**
     * obtains an encoded saml response and return a decoded/unmarshalled saml obj
     * @param samlResp
     * @return {*}
     */
    client.getSamlObject = function (samlResp) {
        var marshalledResponse;
        try {
            var decodedResp = Util.decode(samlResp);
            marshalledResponse = Util.unmarshall(decodedResp);
        } catch (e) {
            log.error('Unable to unmarshall SAML response');
            log.error(e);
        }
        return marshalledResponse;

    };

    /**
     * validating the signature of the response saml object
     */
    client.validateSignature = function (samlObj, config) {
        var tDomain = Util.getDomainName(samlObj);
        var tId = carbon.server.tenantId({domain: tDomain});

        return Util.validateSignature(samlObj,
            config.KEY_STORE_NAME, config.KEY_STORE_PASSWORD, config.IDP_ALIAS, tId, tDomain);
    };

    /**
     * Checking if the request is a logout call
     */
    client.isLogoutRequest = function (samlObj) {
        return samlObj instanceof Packages.org.opensaml.saml2.core.LogoutRequest;
    };

    /**
     * Checking if the request is a logout call
     */
    client.isLogoutResponse = function (samlObj) {
        return samlObj instanceof Packages.org.opensaml.saml2.core.LogoutResponse;
    };

    /**
     * getting url encoded saml authentication request
     * @param issuerId
     */
    client.getEncodedSAMLAuthRequest = function (issuerId) {
        return Util.encode(
            Util.marshall(
                new Packages.org.wso2.store.sso.common.builders.AuthReqBuilder().buildAuthenticationRequest(issuerId)
            ));
    };

    /**
     * get url encoded saml logout request
     */
    client.getEncodedSAMLLogoutRequest = function (user, sessionIndex, issuerId) {
        return Util.encode(
            Util.marshall(
                new Packages.org.wso2.store.sso.common.builders.LogoutRequestBuilder().buildLogoutRequest(user, sessionIndex,
                    Packages.org.wso2.store.sso.common.constants.SSOConstants.LOGOUT_USER,
                    issuerId)));
    };

    /**
     * Reads the returning SAML login response and populates a session info object
     */
    client.decodeSAMLLoginResponse = function (samlObj, samlResp, sessionId) {
        var samlSessionObj = {
            // sessionId, loggedInUser, sessionIndex, samlToken
        };

        if (samlObj instanceof Packages.org.opensaml.saml2.core.Response) {

            var assertions = samlObj.getAssertions();

            // extract the session index
            if (assertions != null && assertions.size() > 0) {
                var authenticationStatements = assertions.get(0).getAuthnStatements();
                var authnStatement = authenticationStatements.get(0);
                if (authnStatement != null) {
                    if (authnStatement.getSessionIndex() != null) {
                        samlSessionObj.sessionIndex = authnStatement.getSessionIndex();
                    }
                }
            }

            // extract the username
            if (assertions != null && assertions.size() > 0) {
                var subject = assertions.get(0).getSubject();
                if (subject != null) {
                    if (subject.getNameID() != null) {
                        samlSessionObj.loggedInUser = subject.getNameID().getValue();
                    }
                }
            }
            samlSessionObj.sessionId = sessionId;
            samlSessionObj.samlToken = samlResp;
        }

        return samlSessionObj;
    };

    /**
     * This method is to get the session index when a single logout happens
     * The IDP sends a logout request to the ACS with the session index, so that
     * the app can invalidate the associated HTTP Session
     */
    client.decodeSAMLLogoutRequest = function (samlObj) {
        var sessionIndex = null;

        if (samlObj instanceof org.opensaml.saml2.core.LogoutRequest) {
            var sessionIndexes = samlObj.getSessionIndexes();
            if (sessionIndexes != null && sessionIndexes.size() > 0) {
                sessionIndex = sessionIndexes.get(0).getSessionIndex();
            }
        }

        return sessionIndex;

    };

    /**
 * The method is used to encapsulate all of the validations that
 * should be performed on a SAML Response
 */
client.validateSamlResponse = function(samlObj, props, keyStoreProps) {
    props = props || {};
    var Util = Packages.org.wso2.store.sso.common.util.Util;
    var propList = createProperties(props);
    var DEFAULT_TO_TRUE = true;
    var DEFAULT_TO_FALSE = false;
    var isValid = true; //Assume all validations will be succeed
    var isAssertionValidityPeriodChecked = props.validateAssertionValidityPeriod ? props.validateAssertionValidityPeriod : DEFAULT_TO_FALSE;
    var isAudienceRestrictionChecked = props.validateAudienceRestriction ? props.validateAudienceRestriction : DEFAULT_TO_FALSE;
    var isAssertionSigningEnabled = props.assertionSigningEnabled ? props.assertionSigningEnabled : DEFAULT_TO_FALSE;
    var isResponseSigningEnabled = props.responseSigningEnabled ? props.responseSigningEnabled : DEFAULT_TO_FALSE;

    //Step #1: Validate the token validity period
    if (isAssertionValidityPeriodChecked) {
        isValid = Util.validateAssertionValidityPeriod(samlObj, propList);
    }

    //Break processing if the assertion validity period has expired
    if (!isValid) {
        return isValid;
    }
    //Step #2: Validate the assertion audience
    if (isAudienceRestrictionChecked) {
        isValid = Util.validateAudienceRestriction(samlObj, propList);
    }
    //Break processing if the audience restriction check fails
    if (!isValid) {
        return isValid;
    }

    //Step #3: Validate the response signature
    if (isResponseSigningEnabled) {
        isValid = client.validateSignature(samlObj, keyStoreProps);
    }

    //Break processing if the signature validation fails
    if (!isValid) {
        return isValid;
    }

    //Step #4: Perform assertion signature verification
    if (isAssertionSigningEnabled) {
        isValid = callValidateAssertionSignature(samlObj, keyStoreProps);
    }
    return isValid;
};

/**
 * getting url encoded signed saml authentication request
 */
client.getEncodedSignedSAMLAuthRequest = function (issuerId, destination, acsUrl, isPassive, tenantId, tenantDomain, nameIdPolicy) {
    return Util.encode(
        Util.marshall(
            new Packages.org.jaggeryjs.modules.sso.common.builders.AuthReqBuilder().buildAuthenticationRequest(issuerId, destination, acsUrl,
                isPassive, tenantId, tenantDomain, nameIdPolicy)
        ));
};

/**
 * get url encoded signed saml logout request
 */
client.getEncodedSignedSAMLLogoutRequest = function (user, sessionIndex, issuerId, tenantId, tenantDomain, destination, nameIdFormat) {
    return Util.encode(
        Util.marshall(
            new Packages.org.jaggeryjs.modules.sso.common.builders.LogoutRequestBuilder().buildLogoutRequest(user, sessionIndex,
                Packages.org.jaggeryjs.modules.sso.common.constants.SSOConstants.LOGOUT_USER,
                issuerId, tenantId, tenantDomain, destination, nameIdFormat)));

};

/**
 * A utility method used to convert a JSON object to
 * a properties object
 */
function createProperties(props) {
    var javaPropertyList = new java.util.Properties();
    Object.keys(props).forEach(function(key) {
        if (props.hasOwnProperty(key)) {
            javaPropertyList.setProperty(key, props[key]);
        }
    });
    return javaPropertyList;
}
/**
 * Invokes the validateAssertionSignature method by first
 * resolving tenant details
 */
function callValidateAssertionSignature(samlObj, config) {
    var Util = Packages.org.wso2.store.sso.common.util.Util;
    var tDomain, tId;
    var carbon = require('carbon');
    if (config.USE_ST_KEY) {
        tDomain = carbon.server.superTenant.domain;
        tId = carbon.server.superTenant.tenantId;
    } else {
        tDomain = Util.getDomainName(samlObj);
        tId = carbon.server.tenantId({
            domain: tDomain
        });
    }
    return Util.validateAssertionSignature(samlObj, config.KEY_STORE_NAME, config.KEY_STORE_PASSWORD, config.IDP_ALIAS, tId, tDomain);
}

}(client));