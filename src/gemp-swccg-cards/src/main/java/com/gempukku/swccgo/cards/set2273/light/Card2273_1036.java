package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.EpicEventCalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Effect
 * Title: The Shield Is Down! (V)
 * Pre-errata version of 1273_36 (PC-E1: PC website card list by 2012-07-21; Advanced Rules 2.0 Appendix A by Aug 2012), replaced by the Virtual Cards Master List text (counts only your non-Jedi scouts at Bunker). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1036 extends AbstractNormalEffect {
    public Card2273_1036() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Shield Is Down!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Lando's confidence in the Rebel strike team on the forest moon was rewarded, and proved to be a decisive factor at the Battle of Endor.");
        setGameText("If [Virtual Block 7] Rebel Strike Team on table, deploy on table. Your [Endor] Epic Event total is +1 for each non-Jedi scout at Bunker. For each of your non-Jedi scouts in battle, attrition against opponent is +1. While opponent's [Endor] objective on table, Bunker may not be converted. Once per turn may take Bunker into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1273_36", "PC-E1",
                "PC forum errata (2012-01-13); PC website card list (by 2012-07-21); Advanced Rules 2.0 Appendix A (by 2012-08-27)", LocalDate.of(2012, 1, 13), LocalDate.of(2012, 10, 19)));
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter nonJediScout() {
        return Filters.and(Filters.scout, Filters.not(Filters.Jedi));
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, LegacyVb73Light.rebelStrikeTeamV());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EpicEventCalculationTotalModifier(self, Filters.and(Filters.your(playerId), CardType.EPIC_EVENT, Icon.ENDOR), null,
                new OnTableEvaluator(self, Filters.and(nonJediScout(), Filters.at(Filters.Bunker))), true)); // each non-Jedi scout at Bunker (either player's)
        modifiers.add(new AttritionModifier(self, new OnTableEvaluator(self, Filters.and(Filters.your(playerId), nonJediScout(), Filters.participatingInBattle)), opponent));
        modifiers.add(new MayNotBeConvertedModifier(self, Filters.Bunker,
                new OnTableCondition(self, Filters.and(Filters.opponents(playerId), CardType.OBJECTIVE, Icon.ENDOR))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_SHIELD_IS_DOWN_V__UPLOAD_BUNKER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Bunker into hand from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Bunker, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
