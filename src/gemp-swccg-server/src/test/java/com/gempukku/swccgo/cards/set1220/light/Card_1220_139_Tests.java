package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_139_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("away", "1220_139");
                    put("rifle", "1_153");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AwayPutYourWeaponStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("away").getBlueprint();

        assertEquals("Away Put Your Weapon", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AwayPutYourWeaponLostRetrievesWeaponsFromLostPile() {
        var scn = GetScenario();
        var away = scn.GetLSCard("away");
        var rifle = scn.GetLSCard("rifle");

        scn.StartGame();
        scn.MoveCardsToLSHand(away);
        scn.MoveCardsToTopOfLSLostPile(rifle);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue("ls=" + scn.GetLSAvailableActions() + " rifleZone=" + rifle.getZone(),
                scn.LSCardPlayAvailable(away, "Retrieve up to 5 weapons"));
        scn.LSPlayCard(away, "Retrieve up to 5 weapons");
        for (int i = 0; i < 28; i++) {
            if ((away.getZone() == Zone.LOST_PILE || away.getZone() == Zone.TOP_OF_LOST_PILE)
                    && rifle.getZone() != Zone.LOST_PILE && rifle.getZone() != Zone.TOP_OF_LOST_PILE) {
                break;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")
                    || scn.LSDecisionAvailable("Top card")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSDecisionAvailable("Choose value for X")) {
                scn.LSDecided(1);
                continue;
            }
            if (scn.LSDecisionAvailable("Choose where to retrieve")
                    || Boolean.TRUE.equals(scn.LSChoiceAvailable("Used Pile"))
                    || Boolean.TRUE.equals(scn.LSChoiceAvailable("Hand"))) {
                if (Boolean.TRUE.equals(scn.LSChoiceAvailable("Used Pile"))) {
                    scn.LSChoose("Used Pile");
                }
                else {
                    scn.LSChoose("Hand");
                }
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(rifle)) {
                scn.LSChooseCard(rifle);
                continue;
            }
            scn.PassAllResponses();
        }
        assertTrue("away=" + away.getZone() + " rifle=" + rifle.getZone()
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                away.getZone() == Zone.LOST_PILE || away.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("rifle zone=" + rifle.getZone(),
                rifle.getZone() == Zone.USED_PILE || rifle.getZone() == Zone.TOP_OF_USED_PILE
                        || rifle.getZone() == Zone.HAND);
    }
}
