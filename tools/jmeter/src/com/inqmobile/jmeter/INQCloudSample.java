package com.inqmobile.jmeter;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Iterator;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * The INQCloudSample sampler is an abstract class that can be used to quickly
 * create a JMeter JavaTest test sampler which will perform a series of
 * interdependent calls to the INQ Cloud. It includes utility functions for
 * basic HTTP requests with the time-stamp, auth token and other headers set
 * correctly.
 */
public abstract class INQCloudSample extends AbstractJavaSamplerClient implements Serializable {  // NOPMD

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8616989300268020792L;

    /** Prefix for the OAUTH header. */
    protected static final String OAUTH_PREFIX = "oauth2 inqcloud ";

    /** String to use for OAuth generation. */
    protected String oauthToken = DEFAULT_OAUTH_VALUE;
    /** Default value for the OAuth token. */
    protected static final String DEFAULT_OAUTH_VALUE = "missing-token";
    /** Name value of the OAuth Token. */
    protected static final String OAUTH_NAME = "OAuthValue";

    /** String to use for the name of the server to test. */
    protected String inqServer = DEFAULT_INQSERVER_VALUE;
    /** Default value for the server to test. */
    protected static final String DEFAULT_INQSERVER_VALUE = "qa.env.inqlabs.com";
    /** Name value of the server to test. */
    protected static final String INQSERVER_NAME = "INQServer";

    /**
     * Request timeout. This is the time in milliseconds that the test client
     * will wait for the server to connect.
     */
    protected int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    /**
     * Read timeout. This is the time in milliseconds that the test client will
     * wait for a read or write to be [ACK]ed.
     */
    protected int readTimeout = DEFAULT_READ_TIMEOUT;
    /** Default value for the request timeout. 1 minute. */
    protected static final int DEFAULT_REQUEST_TIMEOUT = 60000;
    /** Default value for the read timeout. 5 seconds. */
    protected static final int DEFAULT_READ_TIMEOUT = 5000;
    /** Name value of the request timeout. */
    protected static final String REQUEST_TIMEOUT_NAME = "Request-Timeout";
    /** Name value of the read timeout. */
    protected static final String READ_TIMEOUT_NAME = "Read-Timeout";

    /** Date format used in the authentication. */
    private static final String ISO_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmssZ";
    /** The simple data format used in the authentication. */
    final private SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);  // NOPMD

    /** The date. */
    protected String date;

    /** The label to store in the sample result. */
    protected String label = LABEL_DEFAULT;
    /** The default value of the label. */
    protected static final String LABEL_DEFAULT = "INQCloud";
    /** Name value of the label. */
    protected static final String LABEL_NAME = "Label";

    /**
     * Default constructor for <code>SockTest</code>.
     * 
     * The Java Sampler uses the default constructor to instantiate an instance
     * of the client class.
     */
    public INQCloudSample() {
        super();
        getLogger().debug(whoAmI() + "\tConstruct");
    }

    /**
     * Binary to Hex converter.
     * 
     * @param data
     *            The data to convert
     * @return the String representing 'data' as Hexadecimal.
     */
    private static String bin2Hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xff & data[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }

        return sb.toString();
    }

    /**
     * Creates the INQCloud signature.
     * 
     * @param date
     *            the time-stamp to sign with
     * @return the signature
     */
    private static String createSignature(String date) {
        String signature = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            final String digest = date + "." + "cloud_secret";
            signature = bin2Hex(messageDigest.digest(digest.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  // NOPMD
        }
        return signature;
    }

    /**
     * Read a response from an InputStream to a string.
     * 
     * @param in
     *            the InputStream to read from
     * @return the response
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static String readToString(final InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final BufferedReader r = new BufferedReader(new InputStreamReader(in),
                1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    /**
     * Do any initialization required by this client.
     * 
     * There is none, as it is done in runTest() in order to be able to vary the
     * data for each sample.
     * 
     * @param context
     *            the context to run with. This provides access to
     *            initialization parameters.
     */
    public void setupTest(JavaSamplerContext context) {
        getLogger().debug(whoAmI() + "\tsetupTest()");
        listParameters(context);
    }

    /**
     * Provide a list of parameters which this test supports. Any parameter
     * names and associated values returned by this method will appear in the
     * GUI by default so the user doesn't have to remember the exact names. The
     * user can add other parameters which are not listed here. If this method
     * returns null then no parameters will be listed. If the value for some
     * parameter is null then that parameter will be listed in the GUI with an
     * empty value.
     * 
     * @return a specification of the parameters used by this test which should
     *         be listed in the GUI, or null if no parameters should be listed.
     */
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument(LABEL_NAME, LABEL_DEFAULT);
        params.addArgument(INQSERVER_NAME, DEFAULT_INQSERVER_VALUE);
        params.addArgument(REQUEST_TIMEOUT_NAME, String.valueOf(DEFAULT_REQUEST_TIMEOUT));
        params.addArgument(READ_TIMEOUT_NAME, String.valueOf(DEFAULT_READ_TIMEOUT));
        params.addArgument(OAUTH_NAME, String.valueOf(DEFAULT_OAUTH_VALUE));
        return params;
    }

    /**
     * Initialize the global parameters from the ones set buy the user.
     * 
     * @param context
     *            the sampler context
     */
    protected void initParameters(JavaSamplerContext context) {
        oauthToken = context.getParameter(OAUTH_NAME,
                DEFAULT_OAUTH_VALUE);
        label = context.getParameter(LABEL_NAME, LABEL_DEFAULT);
        inqServer = context.getParameter(INQSERVER_NAME, DEFAULT_INQSERVER_VALUE);
        requestTimeout = context.getIntParameter(REQUEST_TIMEOUT_NAME, DEFAULT_REQUEST_TIMEOUT);
        readTimeout = context.getIntParameter(READ_TIMEOUT_NAME, DEFAULT_READ_TIMEOUT);
    }

    /**
     * A utility function to be used by sub-classes to perform an HTTP GET based
     * sample. This could be a part of a larger group of samples. Note that it's
     * up to the caller to properly aggregate the samples together as a single
     * result.
     * 
     * @param results
     *            the SampleResult to populate for this particular sample
     * @param url
     *            the URL to use for the request.
     * @return true, if successful
     */
    protected boolean httpGetSample(SampleResult results, String url) {
        return httpRequestSample(results, url, "GET", null, null);
    }

    /**
     * A utility function to be used by sub-classes to perform an HTTP POST
     * based sample. This could be a part of a larger group of samples. Note
     * that it's up to the caller to properly aggregate the samples together as
     * a single result.
     * 
     * @param results
     *            the SampleResult to populate for this particular sample
     * @param url
     *            the URL to use for the request.
     * @param request
     *            the data to post with the request
     * @return true, if successful
     */
    protected boolean httpPostSample(SampleResult results, String url, String request) {
        return httpRequestSample(results, url, "POST", request, "application/json");
    }

    /**
     * A more generic utility function to be used by sub-classes to perform an
     * HTTP request based sample. This could be a part of a larger group of
     * samples. Note that it's up to the caller to properly aggregate the
     * samples together as a single result.
     * 
     * @param results
     *            The SampleResult to populate for this particular sample
     * @param url
     *            The URL to use for the request.
     * @param method
     *            A String value for the request method to pass to the
     *            connection. Must be one of "GET", "POST", "PUT", "DELETE",
     *            "OPTIONS", or "TRACE"
     * @param request
     *            The data to send with the request if the request is "POST" or
     *            "PUT"
     * @param contentType
     *            The content type to set in the header if the request is "POST"
     *            or "PUT"
     * @return true, if successful
     */
    protected boolean httpRequestSample(SampleResult results, String url, String method, String request, String contentType) {
        HttpURLConnection conn = null;
        String response = null;
        boolean success;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(requestTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod(method.toUpperCase());  // NOPMD
            conn.setRequestProperty(
                    "User-Agent",
                    "Dalvik/1.6.0 (Linux; U; Android 4.0.3; Full AOSP on Maguro Build/IML74K) INQCloudAndroidSDK");
            conn.setRequestProperty("X-API-Key", "cloud_key");
            conn.setRequestProperty("Authentication", OAUTH_PREFIX + oauthToken);
            conn.setRequestProperty("X-API-Timestamp", date);
            conn.setRequestProperty("X-API-Signature", createSignature(date));

            if (request != null && (method.toUpperCase().contentEquals("POST") || method.toUpperCase().contentEquals("PUT"))) {  // NOPMD
                // Send request data for a POST or PUT
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", contentType);
                DataOutputStream wr = new DataOutputStream(
                        conn.getOutputStream());
                wr.writeBytes(request);
                wr.flush();
                wr.close();
            }

            // Read the response
            response = readToString(conn.getInputStream());

            results.setSuccessful(true);
            success = true;
            if (response != null && response.length() > 0) {
                results.setResponseData(response.getBytes());
                results.setDataType(SampleResult.TEXT);
            }

            results.setResponseCodeOK();
        } catch (IOException e) {
            success = false;
            results.setSuccessful(false);
            try {
                results.setResponseCode(Integer.toString(conn.getResponseCode()));
            } catch (IOException e1) {
                results.setResponseCode(e.toString());
            }
            try {
                results.setResponseMessage(conn.getResponseMessage());
            } catch (IOException e1) {
                results.setResponseMessage(e.toString());
            }

            results.setResponseData(e.toString().getBytes());
            results.setDataType(SampleResult.TEXT);
            e.printStackTrace(); // NOPMD
        } catch (Exception e) {
            getLogger().error("INQCloudSample: error during sample", e);
            success = false;
            results.setSuccessful(false);
        } finally {
            // Close the connection
            conn.disconnect();
        }

        return success;
    }

    /**
     * Perform a single sample.<br>
     * In this case, this method will simply sleep for some amount of time.
     * 
     * This method returns a <code>SampleResult</code> object.
     * 
     * <pre>
     * The following fields are always set:
     * - responseCode (default "")
     * - responseMessage (default "")
     * - label (default "JavaTest")
     * - success (default true)
     * </pre>
     * 
     * The following fields are set from the user-defined parameters, if
     * supplied:
     * 
     * <pre>
     * -samplerData - responseData
     * </pre>
     * 
     * @param context
     *            the context to run with. This provides access to
     *            initialization parameters.
     * @return a SampleResult giving the results of this sample.
     * @see org.apache.jmeter.samplers.SampleResult#sampleStart()
     * @see org.apache.jmeter.samplers.SampleResult#sampleEnd()
     * @see org.apache.jmeter.samplers.SampleResult#setSuccessful(boolean)
     * @see org.apache.jmeter.samplers.SampleResult#setSampleLabel(String)
     * @see org.apache.jmeter.samplers.SampleResult#setResponseCode(String)
     * @see org.apache.jmeter.samplers.SampleResult#setResponseMessage(String)
     * @see org.apache.jmeter.samplers.SampleResult#setResponseData(byte [])
     * @see org.apache.jmeter.samplers.SampleResult#setDataType(String)
     */
    public SampleResult runTest(JavaSamplerContext context) {
        initParameters(context);

        SampleResult results = new SampleResult();

        results.setSampleLabel(label);

        sdf.setTimeZone(TimeZone.getDefault());
        date = sdf.format(new Date());

        // Record sample start time.
        results.sampleStart();

        makeINQCloudCalls(results);

        // Record end time and populate the results.
        results.sampleEnd();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    whoAmI() + "\trunTest()" + "\tTime:\t" + results.getTime());
            listParameters(context);
        }

        return results;
    }

    /**
     * Make one or more calls to the INQ Cloud. This method is abstracted so
     * that the sub-class can create a series of steps containing various calls
     * to the INQ Cloud servers representing actions performed by a handset.
     * 
     * @param result
     *            This SampleResult should represent an aggregated sequence of
     *            calls made to perform this sample.
     */
    abstract protected void makeINQCloudCalls(SampleResult result);

    /**
     * Do any clean-up required by this test. In this case no clean-up is
     * necessary, but some messages are logged for debugging purposes.
     * 
     * @param context
     *            the context to run with. This provides access to
     *            initialization parameters.
     */
    public void teardownTest(JavaSamplerContext context) {
        getLogger().debug(whoAmI() + "\tteardownTest()");
        listParameters(context);
    }

    /**
     * Dump a list of the parameters in this context to the debug log.
     * 
     * @param context
     *            the context which contains the initialization parameters.
     */
    private void listParameters(JavaSamplerContext context) {
        if (getLogger().isDebugEnabled()) {
            Iterator<String> argsIt = context.getParameterNamesIterator();
            while (argsIt.hasNext()) {
                String name = (String) argsIt.next();
                getLogger().debug(name + "=" + context.getParameter(name));
            }
        }
    }

    /**
     * Generate a String identifier of this test for debugging purposes.
     * 
     * @return a String identifier for this test instance
     */
    private String whoAmI() {
        StringBuffer sb = new StringBuffer();
        sb.append(Thread.currentThread().toString());
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

}
