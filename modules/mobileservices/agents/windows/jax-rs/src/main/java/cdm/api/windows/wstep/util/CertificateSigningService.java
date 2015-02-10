package cdm.api.windows.wstep.util;

//REMOVE THIS LATER

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//import org.bouncycastle.pkcs.PKCS10CertificationRequestHolder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateSigningService {

	private static Logger LOGGER = Logger.getLogger(CertificateSigningService.class);

    /*public static X509Certificate sign(PKCS10CertificationRequest inputCSR, PrivateKey caPrivate, X509Certificate caCertificate)
            throws InvalidKeyException, NoSuchAlgorithmException,NoSuchProviderException, SignatureException, IOException,
            OperatorCreationException, CertificateException {

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

        AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(caPrivate.getEncoded());

        //SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded());

        PKCS10CertificationRequestHolder pk10Holder = new PKCS10CertificationRequestHolder(inputCSR);
        SubjectPublicKeyInfo csrKeyInfo = pk10Holder.getSubjectPublicKeyInfo();

        LOGGER.info("CN of the Device's CSR : " + pk10Holder.getSubject().toString());


        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
                new X500Name(caCertificate.getIssuerX500Principal().getName()), BigInteger.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)), new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)), new X500Name("CN=abimaran"), csrKeyInfo);

        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(foo);

        X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
        X509CertificateStructure eeX509CertificateStructure = holder.toASN1Structure();

        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

        // Read Certificate
        InputStream is1 = new ByteArrayInputStream(eeX509CertificateStructure.getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);

        LOGGER.info("Signed Certificate CN : " + theCert.getSubjectDN().getName());

        LOGGER.info("Signed CSR's public key : " + theCert.getPublicKey());

        is1.close();
        return theCert;
    }*/

	public static X509Certificate signCSR(JcaPKCS10CertificationRequest jcaRequest, PrivateKey privateKey, X509Certificate caCert) throws Exception{
		try {

			X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(caCert,
			                                                                              BigInteger.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)), new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30),
			                                                                              new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)), new X500Name("CN=abimaran"), jcaRequest.getPublicKey());

			JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

			ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);

			X509Certificate theCert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateBuilder.build(signer));

			LOGGER.info("Signed Certificate CN : " + theCert.getSubjectDN().getName());

			LOGGER.info("Signed CSR's public key : " + theCert.getPublicKey());

			return theCert;

		} catch (Exception e) {
			throw new Exception("Error in signing the certificate", e);
		}
	}
}
