package org.davidbrummy.bond.api.rest.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/api/v1/heartbeat")
public class HeartBeatController {
    @RequestMapping(value = "/self", method = RequestMethod.GET)
    @ResponseBody
    public ResponsePayload self() {
        ResponsePayload responsePayload = new ResponsePayload();
        return responsePayload;

    }

}
