package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Starts Light Side with Watch Your Step (V) and its pulled cards: Corellia system, an unpiloted Falcon with Han
 * aboard, and Spaceport City at Corellia.
 */
public class WatchYourStepTestSupport {

    private WatchYourStepTestSupport() {
    }

    /**
     * The Light Side cards Watch Your Step (V) pulls when it deploys.
     * @return card names to blueprint ids
     */
    public static Map<String, String> pulledCards() {
        return new HashMap<>() {{
            put("corellia", "2_61");
            put("falcon", "1_143");
            put("han", "1_11");
            put("city", "7_125");
        }};
    }

    public static final StartingSetup WatchYourStepV = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("wys", "601_146");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("wys"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("Choose Corellia system")) {
                    scn.LSChooseCard(scn.GetLSCard("corellia"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("unpiloted freighter")) {
                    scn.LSChooseCard(scn.GetLSCard("falcon"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Corellian pilot")) {
                    scn.LSChooseCard(scn.GetLSCard("han"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Spaceport City")) {
                    scn.LSChooseCard(scn.GetLSCard("city"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("capacity slot")) {
                    scn.LSChoose("Pilot");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
