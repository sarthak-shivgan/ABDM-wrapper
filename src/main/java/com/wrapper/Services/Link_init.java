//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.ResponseController.LinkRecordsResponse;
import java.net.URISyntaxException;

public interface Link_init {
	ObjectNode authInit(LinkRecordsResponse data) throws URISyntaxException;
}
