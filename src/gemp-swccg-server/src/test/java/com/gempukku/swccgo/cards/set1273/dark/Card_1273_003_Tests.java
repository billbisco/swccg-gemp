package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("squad", "1273_3");
                    put("racks", "14_96"); // Droid Racks
                    put("bridge", "12_164"); // Blockade Flagship: Bridge
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
    public void BattleDroidSquadStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("squad").getBlueprint();
        assertEquals("Battle Droid Squad", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.BATTLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.PRESENCE);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BattleDroidSquadIsLostWhenDroidRacksOnTable() {
        var scn = GetScenario();
        var squad = scn.GetDSCard("squad");
        var racks = scn.GetDSCard("racks");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), squad);
        scn.MoveCardsToHand(racks);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(Zone.AT_LOCATION, squad.getZone());
        scn.DSDeployCard(racks);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, squad);
    }

    private int darkBattleDestinyDrawn(boolean flagshipSiteOnTable) {
        var scn = GetScenario();
        var squad = scn.GetDSCard("squad");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        if (flagshipSiteOnTable) {
            scn.MoveLocationToTable(scn.GetDSCard("bridge"));
        }
        scn.MoveCardsToLocation(site, squad, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(2);
        scn.SkipToEndOfPowerSegment(true);
        return scn.GetDSBattleDestinyCount();
    }

    @Test
    public void BattleDroidSquadDrawsTwoBattleDestinyWhileBlockadeFlagshipSiteOnTable() {
        assertEquals(2, darkBattleDestinyDrawn(true));
    }

    @Test
    public void BattleDroidSquadDrawsNoBattleDestinyWithoutBlockadeFlagshipSite() {
        assertEquals(0, darkBattleDestinyDrawn(false));
    }
}
