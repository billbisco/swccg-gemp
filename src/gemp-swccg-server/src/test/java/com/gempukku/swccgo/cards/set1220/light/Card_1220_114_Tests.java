package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1220_114_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("asteroids", "1220_114");
                    put("sanctuary", "4_17");
                    put("field", "4_81");
                    put("hoth", "3_55");
                    put("luke", "1_19");
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
    public void AsteroidsDoNotConcernMeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("asteroids").getBlueprint();

        assertEquals("Asteroids Do Not Concern Me", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AsteroidsDoNotConcernMeCancelsAsteroidSanctuaryOnTable() {
        var scn = GetScenario();
        var asteroids = scn.GetLSCard("asteroids");
        var sanctuary = scn.GetLSCard("sanctuary");
        var field = scn.GetLSCard("field");
        var hoth = scn.GetLSCard("hoth");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLSHand(asteroids);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveLocationToTable(field);
        scn.MoveCardsToLocation(field, luke);
        scn.AttachCardsTo(field, sanctuary);
        assertTrue(scn.LSDeployAvailable(asteroids) || scn.LSCardPlayAvailable(asteroids));
        if (scn.LSDeployAvailable(asteroids)) {
            scn.LSDeployCard(asteroids);
        }
        else {
            scn.LSPlayCard(asteroids);
        }
        scn.PassAllResponses();
        assertTrue(sanctuary.getZone() == Zone.LOST_PILE || sanctuary.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void AsteroidsDoNotConcernMeDeploysAsteroidSectorFromReserveDeck() {
        var scn = GetScenario();
        var asteroids = scn.GetLSCard("asteroids");
        var field = scn.GetLSCard("field");
        var hoth = scn.GetLSCard("hoth");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLSSideOfTable(asteroids);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(field);
        assertTrue(scn.LSCardActionAvailable(asteroids, "Deploy Rycar's Run or asteroid sector"));
        scn.LSUseCardAction(asteroids, "Deploy Rycar's Run or asteroid sector");
        for (int i = 0; i < 20; i++) {
            if (field.getZone() == Zone.LOCATIONS || field.getZone().isInPlay()) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(field)) {
                scn.LSChooseCard(field);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(hoth)) {
                scn.LSChooseCard(hoth);
                continue;
            }
            if (scn.LSDecisionAvailable("which side") || scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
                continue;
            }
            scn.PassAllResponses();
        }
        scn.PassAllResponses();
        assertTrue(field.getZone() == Zone.LOCATIONS || field.getZone().isInPlay());
    }
}
