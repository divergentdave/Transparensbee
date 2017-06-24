package party.davidsherenowitsa.transparensbee.genutils

import com.sun.codemodel.ClassType
import com.sun.codemodel.JCodeModel
import com.sun.codemodel.JExpr
import groovy.json.JsonSlurper
import org.bouncycastle.util.io.pem.PemReader

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

import static com.sun.codemodel.JMod.*

public class LogListGenerator {
    private static
    final URL GOOGLE_LOG_LIST_URL = new URL('https://www.gstatic.com/ct/log_list/log_list.json')
    private static
    final URL GOOGLE_LOG_SIG_URL = new URL('https://www.gstatic.com/ct/log_list/log_list.sig')

    public static void downloadLogList(final File resourceDir) {
        def listFile = new File(resourceDir, 'log_list.json').newOutputStream()
        listFile << GOOGLE_LOG_LIST_URL.openStream()
        listFile.close()

        def sigFile = new File(resourceDir, 'log_list.sig').newOutputStream()
        sigFile << GOOGLE_LOG_SIG_URL.openStream()
        sigFile.close()
    }

    public static void generateLogListClass(final File resourceDir, final File outputDir) {
        def listBytes = new File(resourceDir, 'log_list.json').getBytes()
        def sigBytes = new File(resourceDir, 'log_list.sig').getBytes()

        def pemReader = new PemReader(new FileReader(new File(resourceDir, 'log_list_pubkey.pem')))
        def keySpec = new X509EncodedKeySpec(pemReader.readPemObject().getContent())
        def keyFactory = KeyFactory.getInstance("RSA")
        def pubKey = keyFactory.generatePublic(keySpec)
        def signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(pubKey)
        signature.update(listBytes)
        if (!signature.verify(sigBytes)) {
            throw new Exception("The signature for the log_list.json file is invalid")
        }

        def codeModel = new JCodeModel()

        def fqcn = LogListGenerator.class.getPackage().getName() + '.LogList'
        def logListClass = codeModel._class(PUBLIC | FINAL, fqcn, ClassType.CLASS)

        def logServerClass = codeModel.ref('party.davidsherenowitsa.transparensbee.LogServer')
        def arrayLogServerType = logServerClass.array()
        def array = JExpr.newArray(logServerClass)
        def base64Class = codeModel.ref("android.util.Base64")

        def slurper = new JsonSlurper()
        def result = slurper.parse(listBytes)
        for (obj in result.logs) {
            if (obj.disqualified_at && System.currentTimeSeconds() > obj.disqualified_at) {
                continue
            }

            def ctorInvocation = JExpr._new(logServerClass)
            ctorInvocation.arg(obj.url)
            def base64Invocation = base64Class.staticInvoke('decode')
            base64Invocation.arg(obj.key)
            base64Invocation.arg(base64Class.staticRef('DEFAULT'))
            ctorInvocation.arg(base64Invocation)
            ctorInvocation.arg(obj.description)
            array.add(ctorInvocation)
        }

        logListClass.field(PUBLIC | STATIC | FINAL, arrayLogServerType, 'CT_LOGS', array)
        logListClass.constructor(PRIVATE)

        if (!outputDir.isDirectory() && !outputDir.mkdirs()) {
            throw new IOException('Could not create directory: ' + outputDir)
        }
        codeModel.build(outputDir)
    }
}
