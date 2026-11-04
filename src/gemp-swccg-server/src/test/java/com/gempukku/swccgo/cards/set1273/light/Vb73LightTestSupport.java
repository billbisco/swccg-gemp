package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;

/**
 * Test-only stand-ins for the Virtual Block 7.3 Light wave.
 */
public final class Vb73LightTestSupport {
    private Vb73LightTestSupport() {
    }

    /**
     * A Rebel with the Insurgent keyword (no card in GEMP has it printed yet).
     */
    public static AbstractRebel insurgent() {
        return new AbstractRebel(Side.LIGHT, 2, 2, 2, 2, 2, "Test Insurgent", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addKeywords(Keyword.INSURGENT);
            }
        };
    }

    /**
     * A Rogue Squadron pilot of ability 2.
     */
    public static AbstractRebel rogueSquadronPilot() {
        return new AbstractRebel(Side.LIGHT, 2, 2, 2, 2, 3, "Test Rogue Pilot", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcons(Icon.PILOT);
                addKeywords(Keyword.ROGUE_SQUADRON);
            }
        };
    }

    /**
     * Puts a new copy of an objective (or other card) on the owner's side of the table, outside the deck, so the
     * deck does not start with an objective.
     */
    public static PhysicalCardImpl putOnTable(VirtualTableScenario scn, String owner, String blueprintId) {
        PhysicalCardImpl card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(owner, VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(card, Zone.SIDE_OF_TABLE, owner, true, false, false);
        return card;
    }

    /**
     * True if the condition holds (false if the queried player has no decision right now).
     */
    public static boolean holds(java.util.function.BooleanSupplier condition) {
        try {
            return condition.getAsBoolean();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Pays any pending Dark Side Force loss with whatever cards the game offers.
     */
    public static void payDSForceLoss(VirtualTableScenario scn) {
        for (int i = 0; i < 20 && holds(scn::AwaitingDSForceLossPayment); ++i) {
            if (scn.GetDSReserveDeckCount() > 0)
                scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            else if (scn.GetDSForcePileCount() > 0)
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            else
                scn.DSChooseCard(scn.GetTopOfDSUsedPile());
            scn.PassCardLeavingTable();
        }
    }

    /**
     * A Coruscant battleground site (one Force icon on each side).
     */
    public static AbstractSite coruscantBattlegroundSite() {
        return new AbstractSite(Side.LIGHT, "Coruscant: Test Plaza", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.LIGHT_FORCE, 1);
                addIcon(Icon.DARK_FORCE, 1);
                addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
            }
        };
    }

    /**
     * Picks the first selectable entry of an arbitrary-cards decision (e.g. a card in a pile).
     */
    public static void chooseFirstSelectable(VirtualTableScenario scn, String player) {
        String[] ids = scn.GetADParam(player, "cardId");
        String[] selectable = scn.GetADParam(player, "selectable");
        for (int i = 0; i < ids.length; ++i) {
            if (selectable == null || "true".equals(selectable[i])) {
                scn.PlayerDecided(player, ids[i]);
                return;
            }
        }
        throw new IllegalStateException("No selectable card");
    }
}
