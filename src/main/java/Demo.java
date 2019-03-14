
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;


public class Demo {

	public static void main(String[] args) throws IOException {
		String json = readJsonFileToString("recipientForm.json");

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(json);

		Demo d = new Demo();
		d.renderSchema(root);
	}

	private static String readJsonFileToString(String fileName) throws IOException {
		InputStream file = Demo.class.getResourceAsStream(fileName);
		BufferedReader buf = new BufferedReader(new InputStreamReader(file));

		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();
		while(line != null){
			sb.append(line).append("\n");
			line = buf.readLine();
		}

		return sb.toString();
	}

	private void renderSchema(JsonNode schema) {
		renderSchema(schema, "root", 0);
	}

	private void renderSchema(JsonNode schema, String nodeName, int tabs) {
		if (schema.has("allOf")){
			handleAllOf(schema, tabs);
		} else if (schema.has("oneOf")){
			handleOneOf(schema, tabs);
		} else {
			renderObject(schema, nodeName, tabs);
		}
	}

	private void handleAllOf(JsonNode schema, int tabs) {
		printTabs(tabs);
		System.out.println("-------allOf------");
		for (JsonNode entry : schema.get("allOf")) {
			renderSchema(entry, "allOf", tabs + 1);
		}
		printTabs(tabs);
		System.out.println("-------end allOf--");
	}

	private void handleOneOf(JsonNode schema, int tabs) {
		printTabs(tabs);
		System.out.println("-------oneOf------");
		Iterator<JsonNode> nodes = schema.get("oneOf").getElements();
		while(nodes.hasNext()){
			renderSchema(nodes.next(), "oneOf", tabs + 1);
			if (nodes.hasNext()){
				printTabs(2 * tabs);
				System.out.println("-------or------");
			}
		}
		printTabs(tabs);
		System.out.println("-------end oneOf--");
	}

	void renderObject(JsonNode schema, String nodeName, int tabs) {
		switch (schema.get("type").asText()) {
			case "object":
				Iterator<Map.Entry<String, JsonNode>> entries = schema.get("properties").getFields();
				while(entries.hasNext()) {
					Map.Entry<String, JsonNode> next = entries.next();
					renderSchema(next.getValue(), next.getKey(), tabs + 1);
				}
				break;
				case "array":
				printTabs(tabs);
				System.out.println("array: [");
				renderSchema(schema.get("items"), nodeName, tabs + 1);
				printTabs(tabs);
				System.out.println("]");
				break;
			case "string":
				printTabs(tabs);
				System.out.print("string: " + nodeName);
				System.out.println();
				break;
			case "number":
				printTabs(tabs);
				System.out.print("number: " + nodeName);
				System.out.println();
				break;
			case "boolean":
				printTabs(tabs);
				System.out.print("bool: " + nodeName);
				System.out.println();
				break;
		}
	}

	private void printTabs(int tabs) {
		for (int i = 0; i < tabs; i++) {
			System.out.print("  ");
		}
	}
}
