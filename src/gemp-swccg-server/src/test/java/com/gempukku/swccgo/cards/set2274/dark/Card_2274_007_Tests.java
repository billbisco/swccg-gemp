package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawDestinyExceptBattleDestinyModifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Occupied Territory (2011-12-16 sheet), a version of 1274_7: prevents only opponent's power-segment destiny draws. */
public class Card_2274_007_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
            put("dengar", "4_100");
            put("vader", "1_168");
            put("version", "2274_7");
            put("current", "1274_7");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalOccupiedTerritoryIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_7"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_7"), "1274_7", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent opponent from drawing any destinies during the power segment of that battle (except battle destiny).");
    }

    @Test
    public void OriginalOccupiedTerritoryIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.AttachCardsTo(s.GetDSStartingLocation(), c);
        });
    }

    @Test
    public void OriginalOccupiedTerritoryPreventsOnlyOpponentsPowerSegmentDestinyDraws() {
        var s = scenario();
        var territory = s.GetDSCard("version");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToLocation(site, s.GetDSCard("trooper"), s.GetDSCard("dengar"), s.GetDSCard("vader"));
        s.MoveCardsToLocation(site, s.GetLSCard("luke"));
        s.AttachCardsTo(site, territory);
        s.EnsureDSForcePile(5);
        s.EnsureLSForcePile(5);
        s.DSStartBattleAndSkipToWeaponsSegment(site);
        s.DSUseCardAction(territory, "prevent");
        s.PassAllResponses();
        assertTrue(territory.getZone() == Zone.USED_PILE || territory.getZone() == Zone.TOP_OF_USED_PILE);
        for (int i = 0; i < 40 && !(s.gameState().getBattleState() != null && s.IsReachedPowerSegment()); i++) {
            if (s.LSDecisionAvailable("Do you want to draw")) s.LSChooseYes();
            else if (s.DSDecisionAvailable("Do you want to draw")) s.DSChooseYes();
            else if (s.DSGetDecision() != null) s.DSPass();
            else if (s.LSGetDecision() != null) s.LSPass();
            else break;
        }
        assertTrue(s.IsReachedPowerSegment());
        assertTrue(MayNotDrawDestinyExceptBattleDestinyModifier.mayNotDrawDestiny(s.game(), s.LS, DestinyType.DESTINY_TO_REDUCE_ATTRITION_POWER));
        assertFalse(MayNotDrawDestinyExceptBattleDestinyModifier.mayNotDrawDestiny(s.game(), s.DS, DestinyType.DESTINY_TO_REDUCE_ATTRITION_POWER));
    }
}
