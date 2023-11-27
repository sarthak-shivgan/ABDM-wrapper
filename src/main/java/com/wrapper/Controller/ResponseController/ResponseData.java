//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Controller.ResponseController;

import com.wrapper.Controller.HRP.HipGateway;
import com.wrapper.ResponseController.OnAddCareContextResponse;
import com.wrapper.ResponseController.OnConfirmResponse;
import com.wrapper.ResponseController.OnInitResponse;
import com.wrapper.ServiceImpl.CareContextTableService;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@Component
@RestController
public class ResponseData {
	@Autowired
	CareContextTableService careContextTableService;
	@Autowired
	HipGateway hipGateway;
	private static final Logger log = LogManager.getLogger(ResponseData.class);

	public ResponseData() {
	}

	@PostMapping({"/v0.5/users/auth/on-init"})
	public void onInitCall(@RequestBody OnInitResponse data) throws IOException, URISyntaxException {
		log.info("getError in OnInitRequest callback: " + data.getError());
		if (data.getError() == null) {
			log.info(data.getAuth().getTransactionId());
			this.careContextTableService.setTransactionId(data);
			log.info("Stored TransactionId");
			data.printData();
			this.hipGateway.startConfirmCall(data);
		} else {
			log.info("Error in onInitCall: " + data.getError());
		}

	}

	@PostMapping({"/v0.5/users/auth/on-confirm"})
	public String onConfirmCall(@RequestBody OnConfirmResponse data) throws IOException, URISyntaxException {
		if (data.getError() == null) {
			log.info(data.printData());
			log.info("onConfirm : " + data.getAuth().getAccessToken());
			this.careContextTableService.setAccessToken(data);
			log.info("Stored AccessToken");
			data.printData();
			log.info("starting to add CareContext");
			this.hipGateway.startAddCareContextCall(data);
		}

		return "failed in on-confirm";
	}

	@PostMapping({"/v0.5/links/link/on-add-contexts"})
	public void onAddCareContext(@RequestBody OnAddCareContextResponse data) {
		if (data.getError() == null) {
			log.info("Linked CareContext STATUS :" + data.getAcknowledgement().getStatus());
			this.careContextTableService.setStatus(data.getResp().getRequestId(), data.getAcknowledgement().getStatus());
		} else {
			log.info("Failed to add Context");
		}

	}
}
