/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.certificate.mgt.core.dao.CertificateManagementDAOException;
import org.wso2.carbon.certificate.mgt.core.dto.CertificateResponse;
import org.wso2.carbon.certificate.mgt.core.exception.KeystoreException;
import org.wso2.carbon.certificate.mgt.core.service.CertificateManagementService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.PaginationRequest;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.EnrollmentCertificate;
import org.wso2.carbon.mdm.exception.*;
import org.wso2.carbon.mdm.exception.BadRequestException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * All the certificate related tasks such as saving certificates, can be done through this endpoint.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Certificate {

    private static Log log = LogFactory.getLog(Operation.class);

    /**
     * Save a list of certificates and relevant information in the database.
     *
     * @param enrollmentCertificates List of all the certificates which includes the tenant id, certificate as
     *                               a pem and a serial number.
     * @return Status of the data persist operation.
     * @throws MDMAPIException
     */
    @POST
    public Response saveCertificate(@HeaderParam("Accept") String acceptHeader,
                                    EnrollmentCertificate[] enrollmentCertificates) throws MDMAPIException {
        MediaType responseMediaType = MDMAPIUtils.getResponseMediaType(acceptHeader);
        CertificateManagementService certificateService;
        List<org.wso2.carbon.certificate.mgt.core.bean.Certificate> certificates = new ArrayList<>();
        org.wso2.carbon.certificate.mgt.core.bean.Certificate certificate;
        certificateService = MDMAPIUtils.getCertificateManagementService();
        try {
            for (EnrollmentCertificate enrollmentCertificate : enrollmentCertificates) {
                certificate = new org.wso2.carbon.certificate.mgt.core.bean.Certificate();
                certificate.setTenantId(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
                certificate.setSerial(enrollmentCertificate.getSerial());
                certificate.setCertificate(certificateService.pemToX509Certificate(enrollmentCertificate.getPem()));
                certificates.add(certificate);
            }
            certificateService.saveCertificate(certificates);
            return Response.status(Response.Status.CREATED).entity("").
                    type(responseMediaType).build();
        } catch (KeystoreException e) {
            String msg = "Error occurred while converting PEM file to X509Certificate.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

    /**
     * Get a certificate when the serial number is given.
     *
     * @param serialNumber serial of the certificate needed.
     * @return certificate response.
     * @throws MDMAPIException
     */
    @GET
    @Path("{serialNumber}")
    public Response getCertificate(@HeaderParam("Accept") String acceptHeader,
                                   @PathParam("serialNumber") String serialNumber) throws MDMAPIException {
        MediaType responseMediaType = MDMAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        if (serialNumber == null || serialNumber.isEmpty()) {
            message.setErrorMessage("Invalid serial number");
            message.setDiscription("Serial number is missing or invalid.");
            throw new BadRequestException(message, responseMediaType);
        }

        CertificateManagementService certificateService = MDMAPIUtils.getCertificateManagementService();
        List<CertificateResponse> certificateResponse;
        try {
            certificateResponse = certificateService.searchCertificates(serialNumber);
            return Response.status(Response.Status.OK).entity(certificateResponse).type(responseMediaType).build();
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while converting PEM file to X509Certificate";
            log.error(msg, e);
            return Response.serverError().build();
        }
    }

    /**
     * Get all certificates in a paginated manner.
     *
     * @param startIndex index of the first record to be fetched
     * @param length     number of records to be fetched starting from the start index.
     * @return paginated result of certificate.
     * @throws MDMAPIException
     */
    @GET
    @Path("paginate")
    public Response getAllCertificates(@HeaderParam("Accept") String acceptHeader,
                                       @QueryParam("start") int startIndex,
                                       @QueryParam("length") int length)
            throws MDMAPIException {
        MediaType responseMediaType = MDMAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        if (startIndex < 0) {
            message.setErrorMessage("Invalid start index.");
            message.setDiscription("Start index cannot be less that 0.");
            throw new BadRequestException(message, responseMediaType);
        } else if (length <= 0) {
            message.setErrorMessage("Invalid length value.");
            message.setDiscription("Length should be a positive integer.");
            throw new BadRequestException(message, responseMediaType);
        }

        CertificateManagementService certificateService = MDMAPIUtils.getCertificateManagementService();
        PaginationRequest paginationRequest = new PaginationRequest(startIndex, length);
        try {
            PaginationResult certificates = certificateService.getAllCertificates(paginationRequest);
            return Response.status(Response.Status.OK).entity(certificates).type(responseMediaType).build();
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while fetching all certificates.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

    /**
     * Get all certificates
     *
     * @return certificate details in an array.
     * @throws MDMAPIException
     */
    @GET
    public Response getAllCertificates(@HeaderParam("Accept") String acceptHeader)
            throws MDMAPIException {
        MediaType responseMediaType = MDMAPIUtils.getResponseMediaType(acceptHeader);

        CertificateManagementService certificateService = MDMAPIUtils.getCertificateManagementService();
        try {
            List<CertificateResponse> certificates = certificateService.getCertificates();
            return Response.status(Response.Status.OK).entity(certificates).type(responseMediaType).build();
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while fetching all certificates.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

    @DELETE
    @Path("{serialNumber}")
    public Response removeCertificate(@HeaderParam("Accept") String acceptHeader,
                                       @PathParam("serialNumber") String serialNumber) throws MDMAPIException {
        MediaType responseMediaType = MDMAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        if (serialNumber == null || serialNumber.isEmpty()) {
            message.setErrorMessage("Invalid serial number");
            message.setDiscription("Serial number is missing or invalid.");
            throw new BadRequestException(message, responseMediaType);
        }

        CertificateManagementService certificateService = MDMAPIUtils.getCertificateManagementService();
        boolean deleted;
        try {
            deleted = certificateService.removeCertificate(serialNumber);
            if(deleted){
                return Response.status(Response.Status.OK).entity("").type(responseMediaType).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("").type(responseMediaType).build();
            }
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while converting PEM file to X509Certificate";
            log.error(msg, e);
            return Response.serverError().build();
        }
    }
}
