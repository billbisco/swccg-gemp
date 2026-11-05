package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.AbstractWeapon;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * Test-only stand-ins for cards the Virtual Block 7.3 Dark cards name that GEMP does not have yet.
 */
final class LegacyVb73DarkTestSupport {
    private LegacyVb73DarkTestSupport() {
    }

    /** Stand-in for a Shockstaff (no such card is coded yet): a character weapon that deploys on your warrior for 2. */
    static AbstractWeapon shockstaff() {
        return new AbstractWeapon(Side.DARK, 2f, PlayCardZoneOption.ATTACHED, 2f, "Shockstaff", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                setCardSubtype(CardSubtype.CHARACTER);
            }

            @Override
            public boolean canBeDeployedOnCharacter() {
                return true;
            }

            @Override
            protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
                return Filters.and(Filters.your(self), Filters.warrior);
            }

            @Override
            protected Filter getGameTextValidToUseWeaponFilter(SwccgGame game, PhysicalCard self) {
                return Filters.warrior;
            }
        };
    }

    /** Stand-in for Justifier (Virtual Block 6.3, not coded yet): a Dark capital starship matched by title, deploy 3. */
    static AbstractCapitalStarship justifier() {
        return new AbstractCapitalStarship(Side.DARK, 2, 3, 6, 5, null, 4, 7, "Justifier", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcons(Icon.NAV_COMPUTER);
                setPilotCapacity(6);
                setPassengerCapacity(6);
            }
        };
    }


    /** Resolves pending decisions until the given player is offered an action whose text contains the given text. */
    static void passUntilAction(com.gempukku.swccgo.framework.VirtualTableScenario scn, String player, String text) {
        resolveUntil(scn, () -> {
            var d = scn.GetAwaitingDecision(player);
            String[] texts = d == null ? null : d.getDecisionParameters().get("actionText");
            if (texts != null) {
                for (String t : texts) {
                    if (t != null && t.contains(text)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    /** Passes every pending response until the given player has a card choice decision. */
    static void passUntilCardChoice(com.gempukku.swccgo.framework.VirtualTableScenario scn, String player) {
        for (int i = 0; i < 30; i++) {
            var d = scn.GetAwaitingDecision(player);
            if (d != null && d.getDecisionParameters().get("cardId") != null && d.getDecisionParameters().get("actionText") == null) {
                return;
            }
            if (d != null && (d.getDecisionParameters().get("blueprintId") != null) && d.getDecisionParameters().get("actionText") == null) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    /**
     * Resolves pending decisions until done: card selections take the first selectable cards (as many as allowed),
     * multiple choices take the first option, everything else is passed.
     */
    static void resolveUntil(com.gempukku.swccgo.framework.VirtualTableScenario scn, java.util.function.BooleanSupplier done) {
        for (int i = 0; i < 40 && !done.getAsBoolean(); i++) {
            String decider = scn.GetDecidingPlayer();
            var d = scn.GetAwaitingDecision(decider);
            if (d == null) {
                return;
            }
            var params = d.getDecisionParameters();
            if (params.get("min") != null && params.get("cardId") == null && params.get("max") != null) {
                scn.PlayerDecided(decider, params.get("max")[0]);
            } else if (params.get("min") != null && params.get("cardId") != null && params.get("actionText") == null) {
                String[] ids = params.get("cardId");
                String[] selectable = params.get("selectable");
                int max = Integer.parseInt(params.get("max")[0]);
                java.util.List<String> chosen = new java.util.ArrayList<>();
                for (int j = 0; j < ids.length && chosen.size() < max; j++) {
                    if (selectable == null || "true".equals(selectable[j])) {
                        chosen.add(ids[j]);
                    }
                }
                scn.PlayerDecided(decider, String.join(",", chosen));
            } else if (params.get("results") != null) {
                scn.PlayerDecided(decider, "0");
            } else {
                scn.PlayerPass(decider);
            }
        }
    }

    /** Passes every pending response until the given player has a multiple-choice decision offering the given text. */
    static void passUntilChoice(com.gempukku.swccgo.framework.VirtualTableScenario scn, String player, String text) {
        for (int i = 0; i < 30; i++) {
            var d = scn.GetAwaitingDecision(player);
            if (d != null && d.getDecisionParameters().get("results") != null) {
                for (String r : d.getDecisionParameters().get("results")) {
                    if (r.contains(text)) {
                        return;
                    }
                }
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    /** Stand-in Kessel site (the Virtual Block 4 Kessel sites are not coded yet), with one Force icon on each side. */
    static AbstractSite kesselSite(String title) {
        return new AbstractSite(Side.DARK, title, "Kessel", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcon(Icon.DARK_FORCE, 1);
                addIcon(Icon.LIGHT_FORCE, 1);
                addIcons(Icon.INTERIOR_SITE, Icon.PLANET);
            }
        };
    }

    static com.gempukku.swccgo.game.PhysicalCardImpl putKesselSiteOnTable(com.gempukku.swccgo.framework.VirtualTableScenario scn, int id, String title) {
        com.gempukku.swccgo.cards.legacy.MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
        var card = com.gempukku.swccgo.cards.legacy.MissionTestSupport.create(scn, id, scn.DS, kesselSite(title));
        scn.MoveLocationToTable(card);
        return card;
    }
}
