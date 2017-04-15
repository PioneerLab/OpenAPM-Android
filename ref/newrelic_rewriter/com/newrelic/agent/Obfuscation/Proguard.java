// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.Obfuscation;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.newrelic.com.google.common.io.BaseEncoding;
import java.util.Iterator;
import java.util.Collection;
import com.newrelic.org.apache.commons.io.filefilter.IOFileFilter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import com.newrelic.org.apache.commons.io.input.ReversedLinesFileReader;
import com.newrelic.org.apache.commons.io.FileUtils;
import com.newrelic.org.apache.commons.io.filefilter.TrueFileFilter;
import com.newrelic.org.apache.commons.io.filefilter.FileFilterUtils;
import java.io.File;
import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
import com.newrelic.agent.compile.RewriterAgent;
import java.util.Map;
import com.newrelic.agent.compile.Log;

public class Proguard
{
    public static final String NR_PROPERTIES = "newrelic.properties";
    public static final String MAPPING_FILENAME = "mapping.txt";
    private static final String PROP_NR_APP_TOKEN = "com.newrelic.application_token";
    private static final String PROP_UPLOADING_ENABLED = "com.newrelic.enable_proguard_upload";
    private static final String PROP_MAPPING_API_HOST = "com.newrelic.mapping_upload_host";
    private static final String DEFAULT_MAPPING_API_HOST = "mobile-symbol-upload.newrelic.com";
    private static final String MAPPING_API_PATH = "/symbol";
    private static final String LICENSE_KEY_HEADER = "X-APP-LICENSE-KEY";
    private static final String NR_MAP_PREFIX = "# NR_BUILD_ID -> ";
    private final Log log;
    private final String buildId;
    private String projectRoot;
    private String licenseKey;
    private boolean uploadingEnabled;
    private String mappingApiHost;
    private static Map<String, String> agentOptions;
    private static String newLn;
    
    public Proguard(final Log log) {
        this(log, RewriterAgent.getAgentOptions(), NewRelicClassVisitor.getBuildId());
    }
    
    public Proguard(final Log log, final Map<String, String> agentOptions, final String buildId) {
        this.licenseKey = null;
        this.uploadingEnabled = true;
        this.mappingApiHost = null;
        this.log = log;
        Proguard.agentOptions = agentOptions;
        this.buildId = buildId;
    }
    
    public void findAndSendMapFile() {
        if (this.getProjectRoot() != null) {
            if (!this.fetchConfiguration()) {
                return;
            }
            final File projectRoot = new File(this.getProjectRoot());
            final IOFileFilter fileFilter = FileFilterUtils.nameFileFilter("mapping.txt");
            final Collection<File> files = FileUtils.listFiles(projectRoot, fileFilter, TrueFileFilter.INSTANCE);
            if (files.isEmpty()) {
                this.log.error("While evidence of ProGuard/Dexguard was detected, New Relic failed to find your mapping.txt file.");
                this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                return;
            }
            for (final File file : files) {
                String mappingString = "";
                this.log.debug("Found mapping.txt[" + file.getPath() + "]");
                try {
                    final ReversedLinesFileReader revReader = new ReversedLinesFileReader(file);
                    String lastLine;
                    try {
                        lastLine = revReader.readLine();
                    }
                    finally {
                        revReader.close();
                    }
                    if (lastLine.startsWith("# NR_BUILD_ID -> ")) {
                        continue;
                    }
                    final FileWriter fileWriter = new FileWriter(file, true);
                    fileWriter.write("# NR_BUILD_ID -> " + this.buildId + Proguard.newLn);
                    fileWriter.close();
                    mappingString += FileUtils.readFileToString(file);
                    if (!this.uploadingEnabled || mappingString.length() <= 0) {
                        continue;
                    }
                    this.sendMapping(mappingString);
                }
                catch (FileNotFoundException e) {
                    this.log.error("Unable to open your mapping.txt file: " + e.getLocalizedMessage());
                    this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                }
                catch (IOException e2) {
                    this.log.error("Unable to open your mapping.txt file: " + e2.getLocalizedMessage());
                    this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                }
            }
        }
    }
    
    private String getProjectRoot() {
        if (this.projectRoot == null) {
            final String encodedProjectRoot = Proguard.agentOptions.get("projectRoot");
            if (encodedProjectRoot == null) {
                this.log.info("Unable to determine project root, falling back to CWD.");
                this.projectRoot = System.getProperty("user.dir");
            }
            else {
                this.projectRoot = new String(BaseEncoding.base64().decode(encodedProjectRoot));
            }
            this.log.debug("Project root[" + this.projectRoot + "]");
        }
        return this.projectRoot;
    }
    
    private boolean fetchConfiguration() {
        try {
            final Reader propsReader = new BufferedReader(new FileReader(this.getProjectRoot() + File.separator + "newrelic.properties"));
            final Properties newRelicProps = new Properties();
            newRelicProps.load(propsReader);
            this.licenseKey = newRelicProps.getProperty("com.newrelic.application_token");
            this.uploadingEnabled = newRelicProps.getProperty("com.newrelic.enable_proguard_upload", "true").equals("true");
            this.mappingApiHost = newRelicProps.getProperty("com.newrelic.mapping_upload_host");
            if (this.licenseKey == null) {
                this.log.error("Unable to find a value for com.newrelic.application_token in your newrelic.properties");
                this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
                return false;
            }
            propsReader.close();
        }
        catch (FileNotFoundException e) {
            this.log.error("Unable to find your newrelic.properties in the project root (" + this.getProjectRoot() + "): " + e.getLocalizedMessage());
            this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            return false;
        }
        catch (IOException e2) {
            this.log.error("Unable to read your newrelic.properties in the project root (" + this.getProjectRoot() + "): " + e2.getLocalizedMessage());
            this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            return false;
        }
        return true;
    }
    
    private void sendMapping(final String mapping) {
        final StringBuilder requestBody = new StringBuilder();
        requestBody.append("proguard=" + URLEncoder.encode(mapping));
        requestBody.append("&buildId=" + this.buildId);
        try {
            String host = "mobile-symbol-upload.newrelic.com";
            if (this.mappingApiHost != null) {
                host = this.mappingApiHost;
            }
            final URL url = new URL("https://" + host + "/symbol");
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-APP-LICENSE-KEY", this.licenseKey);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(requestBody.length()));
            final DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            request.writeBytes(requestBody.toString());
            request.close();
            final int responseCode = connection.getResponseCode();
            if (responseCode == 400) {
                final InputStream inputStream = connection.getErrorStream();
                final String response = convertStreamToString(inputStream);
                this.log.error("Unable to send your ProGuard/Dexguard mapping.txt to New Relic as the params are incorrect: " + response);
                this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            }
            else if (responseCode > 400) {
                final InputStream inputStream = connection.getErrorStream();
                final String response = convertStreamToString(inputStream);
                this.log.error("Unable to send your ProGuard/DexGuard mapping.txt to New Relic - received status " + responseCode + ": " + response);
                this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
            }
            else {
                this.log.info("Successfully sent mapping.txt to New Relic.");
            }
            connection.disconnect();
        }
        catch (IOException e) {
            this.log.error("Encountered an error while uploading your ProGuard/Dexguard mapping to New Relic", e);
            this.log.error("To de-obfuscate your builds, you'll need to upload your mapping.txt manually.");
        }
    }
    
    private static String convertStreamToString(final InputStream is) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + Proguard.newLn);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    static {
        Proguard.agentOptions = Collections.emptyMap();
        Proguard.newLn = System.getProperty("line.separator");
    }
}
