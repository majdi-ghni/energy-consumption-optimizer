package de.adesso.energyconsumptionoptimizer.model.elecetricityprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

public class ElectricityPriceDeserializer extends JsonDeserializer<ElectricityPriceDto> {

    @Override
    public ElectricityPriceDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ElectricityPriceDto electricityPriceDto = new ElectricityPriceDto();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();

            switch (fieldName) {

                case "start_timestamp":
                    long startTimeStampAsLong = fieldValue.asLong();
                    Instant startTime = Instant.ofEpochMilli(startTimeStampAsLong);
                    electricityPriceDto.setStartTimeStamp(startTime);
                    break;

                case "end_timestamp":
                    long endTimestampAsLong = fieldValue.asLong();
                    Instant endTime = Instant.ofEpochMilli(endTimestampAsLong);
                    electricityPriceDto.setEndTimeStamp(endTime);
                    break;

                case "marketprice":
                    double marketPrice = fieldValue.asDouble();
                    electricityPriceDto.setMarketPrice(marketPrice);
                    break;

                case "unit":
                    String unit = fieldValue.asText();
                    electricityPriceDto.setUnit(unit);
                    break;

                case "localprice":
                    double localPrice = fieldValue.asDouble();
                    electricityPriceDto.setLocalPrice(localPrice);
                    break;

                case "localcell":
                    String localCell = fieldValue.asText(); // Example value: Lübeck(23552)

                    int indexOfOpenBracket = localCell.indexOf('(');
                    String city = localCell.substring(0, indexOfOpenBracket);
                    String zipCode = localCell.substring(indexOfOpenBracket + 1, localCell.length() - 1);

                    electricityPriceDto.setCity(city);
                    electricityPriceDto.setZipCode(zipCode);
                    break;

                default:
                    break;
            }
        }
        return electricityPriceDto;
    }
}
