package com.moekr.kubernetes.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.fabric8.kubernetes.api.model.NodeAddress;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ToolKit {
	public static final String BANNER = "K8S Demo";
	public static final String VERSION = "0.1.0";

	public static String formatAddressList(List<NodeAddress> addressList) {
		return String.join("/", addressList.stream().map(NodeAddress::getAddress).collect(Collectors.toSet()));
	}

	private static final ObjectWriter WRITER = new ObjectMapper().writer();
	private static final int INDENT_LENGTH = 4;

	public static void printObject(Object object) {
		printObject(object, System.out);
	}

	public static void printObject(Object object, PrintStream stream) {
		stream.println(toString(object));
	}

	public static String toString(Object object) {
		Assert.notNull(object, "Object can't be null.");
		String jsonStr;
		try {
			jsonStr = WRITER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			return "Json process error.";
		}
		JSONObject json = new JSONObject(jsonStr);
		return json.toString(INDENT_LENGTH);
	}
}
