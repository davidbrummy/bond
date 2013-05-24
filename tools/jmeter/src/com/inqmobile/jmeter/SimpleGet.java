package com.inqmobile.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * The Class SimpleGet provides a sampler for a simple HTTP GET request to the
 * INQCloud.
 */
public class SimpleGet extends INQCloudSample {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4125002347527237701L;

    /** String to use for the server path to test. */
    protected String serverPath = DEFAULT_SERVER_PATH_VALUE;
    /** Default value for the server path to test. */
    protected static final String DEFAULT_SERVER_PATH_VALUE = "/";
    /** Name Value for the server path to test. */
    protected static final String SERVER_PATH_NAME = "ServerPath";

    /*
     * (non-Javadoc)
     * 
     * @see com.inqmobile.jmeter.INQCloudSample#getDefaultParameters()
     */
    public Arguments getDefaultParameters() {
        Arguments params = super.getDefaultParameters();
        params.addArgument(SERVER_PATH_NAME, DEFAULT_SERVER_PATH_VALUE);
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
        results.setSamplerData(date + "\nGET from: " + testURL.trim() + "\nfor: " + oauthToken);

        httpGetSample(results, testURL);
    }

}
