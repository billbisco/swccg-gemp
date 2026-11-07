package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_075_Tests {
    protected VirtualTableScenario GetScenario() {
        return GetScenario(false);
    }

    // With the objective, the deck starts with Hidden Base (V) (alien pair), which deploys Yavin 4 and the
    // Hidden Landing Site there. Without it, the default starting location is used.
    protected VirtualTableScenario GetScenario(boolean withObjective) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("talon", "1240_75");
                    put("hls", "1240_102");
                    put("yavin", "1_135");
                    put("uncharted", "1240_91");
                    put("alien", "1_4"); // BoShek
                    put("yerka", "1210_143");
                }},
                new HashMap<>() {{
                    put("dsalien", "1_177");
                }},
                10, 10,
                withObjective ? HiddenBaseAlienPair : StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    static final StartingSetup HiddenBaseAlienPair = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("hiddenbase"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("battleground")
                        || scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck")) {
                    scn.LSChooseCard(scn.GetLSCard("yavin"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Hidden Landing Site")) {
                    scn.LSChooseCard(scn.GetLSCard("hls"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Uncharted Settlements")) {
                    scn.LSChooseCard(scn.GetLSCard("uncharted"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Choose pair")) {
                    scn.LSChoose("Alien / Independent starship");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };

    @Test
    public void TalonKarrdeVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("talon").getBlueprint();

        assertEquals("Talon Karrde", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
            add(Keyword.INFORMATION_BROKER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_II);
            add(Icon.PILOT);
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TalonKarrdeVAliensAtRebelCellSitesForfeitPlus2WithBlock4Objective() {
        var scn = GetScenario(true);
        var talon = scn.GetLSCard("talon");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var hls = scn.GetLSCard("hls");
        var alien = scn.GetLSCard("alien");
        var dsalien = scn.GetDSCard("dsalien");
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        assertEquals(Zone.SIDE_OF_TABLE, hiddenbase.getZone());
        assertEquals(Zone.LOCATIONS, hls.getZone());
        scn.MoveCardsToLocation(other, alien, dsalien);
        int alienBase = scn.GetForfeit(alien);
        int dsBase = scn.GetForfeit(dsalien);

        scn.MoveCardsToLocation(hls, alien, dsalien);
        assertEquals(alienBase, scn.GetForfeit(alien));
        scn.MoveCardsToLocation(hls, talon);
        assertEquals(alienBase + 2, scn.GetForfeit(alien));
        assertEquals(6, scn.GetForfeit(talon));
        // Printed text is "aliens", not "your aliens".
        assertEquals(dsBase + 2, scn.GetForfeit(dsalien));

        scn.MoveCardsToLocation(other, alien);
        assertEquals(alienBase, scn.GetForfeit(alien));
    }

    @Test
    public void TalonKarrdeVNoForfeitBonusWithoutBlock4Objective() {
        var scn = GetScenario();
        var talon = scn.GetLSCard("talon");
        var hls = scn.GetLSCard("hls");
        var alien = scn.GetLSCard("alien");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveCardsToLocation(hls, alien);
        int alienBase = scn.GetForfeit(alien);
        scn.MoveCardsToLocation(hls, talon);
        assertEquals(alienBase, scn.GetForfeit(alien));
    }

    @Test
    public void TalonKarrdeVYerkaMigImmuneToAlterOnlyWithObjective() {
        var scn = GetScenario(false);
        var talon = scn.GetLSCard("talon");
        var hls = scn.GetLSCard("hls");
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveCardsToLocation(hls, talon);
        scn.AttachCardsTo(hls, yerka);
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), yerka, Title.Alter));

        var scn2 = GetScenario(true);
        var talon2 = scn2.GetLSCard("talon");
        var hls2 = scn2.GetLSCard("hls");
        var yerka2 = scn2.GetLSCard("yerka");
        scn2.StartGame();
        scn2.AttachCardsTo(hls2, yerka2);
        assertFalse(scn2.game().getModifiersQuerying().isImmuneToCardTitle(scn2.gameState(), yerka2, Title.Alter));
        scn2.MoveCardsToLocation(hls2, talon2);
        assertTrue(scn2.game().getModifiersQuerying().isImmuneToCardTitle(scn2.gameState(), yerka2, Title.Alter));
    }

    @Test
    public void TalonKarrdeVGrantsYerkaMigDeploymentToRebelCellSiteWithObjective() {
        var scn = GetScenario(true);
        var talon = scn.GetLSCard("talon");
        var hls = scn.GetLSCard("hls");
        var yerka = scn.GetLSCard("yerka");
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(yerka);
        var mq = scn.game().getModifiersQuerying();
        assertFalse(mq.isGrantedToDeployTo(scn.gameState(), yerka, hls, null));

        scn.MoveCardsToLocation(hls, talon);
        assertTrue(mq.isGrantedToDeployTo(scn.gameState(), yerka, hls, null));
        assertFalse(mq.isGrantedToDeployTo(scn.gameState(), yerka, other, null));
    }

    @Test
    public void TalonKarrdeVDoesNotGrantYerkaMigDeploymentWithoutObjective() {
        var scn = GetScenario(false);
        var talon = scn.GetLSCard("talon");
        var hls = scn.GetLSCard("hls");
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveCardsToLocation(hls, talon);
        scn.MoveCardsToLSHand(yerka);
        assertFalse(scn.game().getModifiersQuerying().isGrantedToDeployTo(scn.gameState(), yerka, hls, null));
    }
}
