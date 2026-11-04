package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Test-only stand-ins for cards this wave references that GEMP does not have yet
 * (8.1 Dark characters/Effects coded in a parallel wave, Son Of Vader, Your Destiny, Rebel Aces).
 */
public final class Vb81RestTestSupport {
    private Vb81RestTestSupport() {
    }

    public static AbstractAlien assassin(String title) {
        return new AbstractAlien(Side.DARK, 2, 2, 3, 2, 3, title, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addKeywords(Keyword.ASSASSIN);
            }
        };
    }

    public static AbstractAlien plottingAlien() {
        return new AbstractAlien(Side.DARK, 2, 2, 2, 2, 2, "Plotting Test Alien", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                setLore("Bounty hunter plotting to kill Jabba.");
            }
        };
    }

    public static AbstractImperial sonOfVader() {
        return new AbstractImperial(Side.DARK, 1, 5, 6, 6, 8, "Son Of Vader", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addPersona(Persona.SON_OF_VADER);
            }
        };
    }

    /**
     * (Virtual Block 8) Death Mark stand-in: a top-level action that draws destiny for Death Mark.
     */
    public static AbstractNormalEffect deathMark() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Death Mark", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V) {
            {
                addIcons(Icon.LEGACY_BLOCK_8);
            }

            @Override
            protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                action.setText("Draw destiny for Death Mark");
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            }
                        });
                return Collections.singletonList(action);
            }
        };
    }

    /**
     * On The Hunt stand-in: deploys on Contract Killers.
     */
    public static AbstractNormalEffect onTheHunt() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.ATTACHED, "On The Hunt", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V) {
            @Override
            protected com.gempukku.swccgo.filters.Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, com.gempukku.swccgo.common.PlayCardOptionId playCardOptionId, boolean asReact) {
                return com.gempukku.swccgo.filters.Filters.title("Contract Killers");
            }
        };
    }

    public static AbstractNormalEffect lightEffect(String title) {
        return new AbstractNormalEffect(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    public static AbstractNormalEffect darkEffect(String title) {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    /**
     * Passes optional responses one at a time until the condition holds (or no optional response is pending).
     */
    public static boolean passUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 40; ++i) {
            if (holds(condition))
                return true;
            if (!scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"))
                return false;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        return holds(condition);
    }

    private static boolean holds(BooleanSupplier condition) {
        try {
            return condition.getAsBoolean();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Pays any pending Force loss of either player from Reserve Deck, then passes the remaining responses.
     */
    public static void payForceLossFromReserveDeck(VirtualTableScenario scn) {
        for (int i = 0; i < 10; ++i) {
            if (holds(scn::AwaitingLSForceLossPayment)) {
                scn.LSPayRemainingForceLossFromReserveDeck();
            }
            else if (holds(scn::AwaitingDSForceLossPayment)) {
                scn.DSPayRemainingForceLossFromReserveDeck();
            }
            else if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                return;
            }
        }
    }

    /**
     * True if the Dark Side player has a decision offering that card action (false if DS has no decision right now).
     */
    public static boolean dsCardActionAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        return holds(() -> scn.DSCardActionAvailable(card, text));
    }

    public static boolean lsCardActionAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        return holds(() -> scn.LSCardActionAvailable(card, text));
    }

    public static boolean inZone(com.gempukku.swccgo.game.PhysicalCard card, com.gempukku.swccgo.common.Zone zone) {
        return com.gempukku.swccgo.logic.GameUtils.getZoneFromZoneTop(card.getZone()) == zone;
    }

    public static boolean dsCardChoiceAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        return holds(() -> scn.DSHasCardChoiceAvailable(card));
    }

    public static boolean holdsDecision(VirtualTableScenario scn, String player, String text) {
        return holds(() -> scn.DecisionAvailable(player, text));
    }
}
