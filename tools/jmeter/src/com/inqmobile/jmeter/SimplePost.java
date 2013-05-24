package com.inqmobile.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * The Class SimplePost provides a sampler for a simple HTTP POST request to the
 * INQCloud.
 */
public class SimplePost extends INQCloudSample {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4125002347527237701L;

    /** String to use for the server path to test. */
    protected String serverPath = DEFAULT_SERVER_PATH_VALUE;
    /** Default value for the server path to test. */
    protected static final String DEFAULT_SERVER_PATH_VALUE = "/";
    /** Name Value for the server path to test. */
    protected static final String SERVER_PATH_NAME = "ServerPath";

    /** Data to post. */
    protected String postData = DEFAULT_POST_DATA_VALUE;
    /** Default value for the data to post. */
    protected static final String DEFAULT_POST_DATA_VALUE = "";
    /** Name value for the data to post. */
    protected static final String POST_DATA_NAME = "POSTData";

    /*
     * (non-Javadoc)
     * 
     * @see com.inqmobile.jmeter.INQCloudSample#getDefaultParameters()
     */
    public Arguments getDefaultParameters() {
        Arguments params = super.getDefaultParameters();
        params.addArgument(SERVER_PATH_NAME, DEFAULT_SERVER_PATH_VALUE);
        params.addArgument(POST_DATA_NAME, DEFAULT_POST_DATA_VALUE);
        return params;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.inqmobile.jmeter.INQCloudSample#initParameters(org.apache.jmeter.
     * protocol.java.sampler.JavaSamplerContext)
     */
    protected void initParameters(JavaSamplerContext context) {
        super.initParameters(context);
        serverPath = context.getParameter(SERVER_PATH_NAME, DEFAULT_SERVER_PATH_VALUE);
        postData = context.getParameter(POST_DATA_NAME, DEFAULT_POST_DATA_VALUE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.inqmobile.jmeter.INQCloudSample#makeINQCloudCalls(org.apache.jmeter
     * .samplers.SampleResult)
     */
    protected void makeINQCloudCalls(SampleResult results) {
        String testURL = "http://" + inqServer + serverPath;
        results.setSamplerData(date + "\nPOST to: " + testURL.trim() + "\nfor: " + oauthToken + "\ndata: [" + postData + "]");

        httpPostSample(results, testURL, postData);
    }

}
