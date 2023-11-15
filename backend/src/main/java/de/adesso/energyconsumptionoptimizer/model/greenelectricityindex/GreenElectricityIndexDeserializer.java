package de.adesso.energyconsumptionoptimizer.model.greenelectricityindex;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

public class GreenElectricityIndexDeserializer extends JsonDeserializer<GreenElectricityIndexDto> {
    @Override
    public GreenElectricityIndexDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        GreenElectricityIndexDto greenElectricityIndex = new GreenElectricityIndexDto();

        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();

            switch (fieldName) {

            /*    case "epochtime":
                    long startTimeStampAsLong = fieldValue.asLong();
                    Instant startTime = Instant.ofEpochMilli(startTimeStampAsLong);
                    greenElectricityIndex.setStartTime(startTime);
                    greenElectricityIndex.setEndTime(startTime.plus(Duration.ofHours(1)));
                    break;
             */

                case "timeframe":
                    JsonNode startTimeFrame = fieldValue.get("start");
                    JsonNode endTimeFrame = fieldValue.get("end");
                    if (startTimeFrame != null && endTimeFrame != null) {
                        Instant startTime = Instant.ofEpochMilli(startTimeFrame.asLong());
                        Instant endTime = Instant.ofEpochMilli(endTimeFrame.asLong());
                        greenElectricityIndex.setStartTimeStamp(startTime);
                        greenElectricityIndex.setEndTimeStamp(endTime);
                    }
                    break;

                case "gsi":
                    double gsi = fieldValue.asDouble();
                    greenElectricityIndex.setGsi(gsi);
                    break;

                case "zip":
                    String zip = fieldValue.textValue();
                    greenElectricityIndex.setZipCode(zip);
                    break;

                case "co2_g_standard":
                    int co2EmissionsStandard = fieldValue.asInt();
                    greenElectricityIndex.setStandardElectricityCo2InGram(co2EmissionsStandard);
                    break;

                case "co2_g_oekostrom":
                    int co2EmissionsEco = fieldValue.asInt();
                    greenElectricityIndex.setEcoElectricityCo2InGram(co2EmissionsEco);
                    break;

                default:
                    // Ignore unrecognized fields
                    break;
            }
        }
        return greenElectricityIndex;
    }
}
