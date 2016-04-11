package ru.runa.notifier.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.runa.notifier.WFEConnection;
import ru.runa.wfe.webservice.User;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * 
 * Комментарий The code has been written with help
 * com.sun.jna.platform.win32.Secur32Test. Class get a HANDLE for outbound
 * (client) credentials and creates a client-side security context with help
 * Security Support Provider Interface(SSPI)
 * 
 * @author riven 15.02.2012
 */
public class SSPIKerberosAuthenticator implements Authenticator {
    private static final Log log = LogFactory.getLog(SSPIKerberosAuthenticator.class);

    @Override
    public User authenticate() throws Exception {
        CredHandle phClientCredential = null;
        CtxtHandle phClientContext = null;
        SecBufferDesc pbClientToken = null;
        log.info("--- start");
        try {
            phClientCredential = new CredHandle();
            phClientContext = new CtxtHandle();
            NativeLongByReference pfClientContextAttr = new NativeLongByReference();
            pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            int clientRc = -1;
            log.info("- before Secur32.INSTANCE.AcquireCredentialsHandle");
            TimeStamp ptsClientExpiry = new TimeStamp();
            int result = Secur32.INSTANCE.AcquireCredentialsHandle(null, "Kerberos", new NativeLong(Sspi.SECPKG_CRED_OUTBOUND), null, null, null,
                    null, phClientCredential, ptsClientExpiry);
            log.info("- after Secur32.INSTANCE.AcquireCredentialsHandle");
            if (W32Errors.SEC_E_OK != result) {
                log.error("AcquireCredentialsHandle result=" + result + ", lastErrorCode=" + Native.getLastError());
                throw new RuntimeException("Can't init AcquireCredentialsHandle");
            }
            clientRc = Secur32.INSTANCE.InitializeSecurityContext(phClientCredential, null, LoginModuleResources.getServerPrincipal(),
                    new NativeLong(Sspi.ISC_REQ_CONNECTION), new NativeLong(0), new NativeLong(Sspi.SECURITY_NATIVE_DREP), null, new NativeLong(0),
                    phClientContext, pbClientToken, pfClientContextAttr, null);
            log.info("- after Secur32.INSTANCE.InitializeSecurityContext");
            if (clientRc != W32Errors.SEC_E_OK) {
                log.error("InitializeSecurityContext result=" + clientRc + ", lastErrorCode=" + Native.getLastError());
                throw new RuntimeException("Can't init InitializeSecurityContext");
            }
            User user = WFEConnection.getAuthenticationAPI().authenticateByKerberos(pbClientToken.pBuffers[0].getBytes());
            log.info("- after authenticationServiceDelegate.authenticate");
            return user;
        } finally {
            // release client context
            Secur32.INSTANCE.DeleteSecurityContext(phClientContext);
            Secur32.INSTANCE.FreeCredentialsHandle(phClientCredential);
            log.info("- after finally");
        }
    }

    @Override
    public String getParamForWeb() {
        return "";
    }

    @Override
    public boolean isRetryDialogEnabled() {
        return false;
    }
}