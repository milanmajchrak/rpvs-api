package sk.majchrak.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sk.majchrak.model.KonecnyUzivatelVyhod;
import sk.majchrak.model.Partner;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(Controller.class);
    private static final String PARTNERI_V_SEKTORA_URL = "https://rpvs.gov.sk/opendatav2/PartneriVerejnehoSektora?filter=ico eq '{ICO}'&$expand=Adresa($expand=Stat),Partner";
    private static final String PARTNERI_URL = "https://rpvs.gov.sk/opendatav2/Partneri?$filter=id eq {ID}&$expand=KonecniUzivateliaVyhod($expand=statnaPrislusnost)";

    private final RestTemplate restTemplate = new RestTemplate();
    @GetMapping("/rpvs")
    public ResponseEntity<Partner> index(@RequestParam(name = "ico") String ico) throws JsonProcessingException {
        log.info("Going to fetch data from the rpvs and parse the request.");
        String url = PARTNERI_V_SEKTORA_URL.replace("{ICO}", ico);

        Partner partner = null;
        try {
            JSONArray partneriJsonArray = getJsonArrayValue(createJsonFromString(getRawResponseAsString(url)));
            partner = getCurrentPartner(partneriJsonArray);
            partner.setKonecnyUzivatelVyhodList(getKonecnyUzivatelVyhodByPartnerId(partner.getId()));
        } catch (Exception e) {
            log.error("Cannot compose the request from the rpvs because: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(partner, HttpStatus.OK);

    }

    private String getRawResponseAsString(String url) {
        String rawResponse = "";
        try {
            rawResponse = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Cannot get the raw response from the rpvs, URL: " + url + ". The exception: " + e.getMessage());
        }
        return rawResponse;
    }

    private JSONObject createJsonFromString(String rawResponseString) {
        JSONObject rawResponseJsonObject = null;
        try {
            rawResponseJsonObject = new JSONObject(rawResponseString);
        } catch (Exception e) {
            log.error("Cannot parse raw string response into JSONObject, because: " + e.getMessage());
        }
        return rawResponseJsonObject;
    }

    private JSONArray getJsonArrayValue(JSONObject rawJsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = rawJsonObject.getJSONArray("value");
        } catch (Exception e) {
            log.error("Cannot fetch the value from the JSONObject response because: " + e.getMessage());
        }
        return jsonArray;
    }

    private Partner getCurrentPartner(JSONArray partnersJsonArray) {
        log.info("Going to parse Partner from the response JSON into Partner class.");

        JSONObject partnerVSJSON = null;
        JSONObject adresa = null;
        JSONObject statJSON = null;
        JSONObject partnerJSON = null;
        try {
            // The last partner from the list is the current partner.
            partnerVSJSON = partnersJsonArray.getJSONObject(partnersJsonArray.length()-1);
            adresa = partnerVSJSON.getJSONObject("Adresa");
            statJSON = adresa.getJSONObject("Stat");
            partnerJSON = partnerVSJSON.getJSONObject("Partner");
        } catch (Exception e) {
            log.error("Cannot process parse JSONObject/JSONArray to fetch info for the Partner class because: " +
                    e.getMessage());
        }

        Partner partner = new Partner();
        try {
            partner.setId(partnerJSON.getInt("CisloVlozky"));
            partner.setIco(partnerVSJSON.getString("Ico"));
            partner.setName(partnerVSJSON.getString("ObchodneMeno"));
            partner.setState(statJSON.getString("Meno"));
        } catch (Exception e) {
            log.error("Cannot create a Partner class because: " + e.getMessage());
        }


        return partner;
    }

    private List<KonecnyUzivatelVyhod> getKonecnyUzivatelVyhodByPartnerId(Integer partnersId) {
        log.info("Going to get KonecnyUzivatelVyhod for the partner with the ID: " + partnersId);
        String url = PARTNERI_URL.replace("{ID}", String.valueOf(partnersId));
        List<KonecnyUzivatelVyhod> konecnyUzivatelVyhodList = new ArrayList<>();

        try {
            JSONArray kuvJsonArrayValue = getJsonArrayValue(createJsonFromString(getRawResponseAsString(url)));
            JSONObject kuvJSONWithArray = (JSONObject) kuvJsonArrayValue.get(0);
            JSONArray kuvJsonArray = kuvJSONWithArray.getJSONArray("KonecniUzivateliaVyhod");

            for (int i = 0; i < kuvJsonArray.length(); i++) {
                JSONObject kuv = kuvJsonArray.getJSONObject(i);
                JSONObject statnaPrislusnostJSON = kuv.getJSONObject("StatnaPrislusnost");

                KonecnyUzivatelVyhod konecnyUzivatelVyhod = new KonecnyUzivatelVyhod();
                konecnyUzivatelVyhod.setName(kuv.getString("Meno"));
                konecnyUzivatelVyhod.setState(statnaPrislusnostJSON.getString("Meno"));

                konecnyUzivatelVyhodList.add(konecnyUzivatelVyhod);
            }
        } catch (Exception e) {
            log.error("Cannot fetch/parse konecny uzivatel vyhod because: " + e.getMessage());
        }

        return konecnyUzivatelVyhodList;
    }
}
