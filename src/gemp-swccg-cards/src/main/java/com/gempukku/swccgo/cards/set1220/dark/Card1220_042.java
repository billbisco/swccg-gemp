package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardsFromHandEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Much Anger In Him
 */
public class Card1220_042 extends AbstractNormalEffect {
    public Card1220_042() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Much_Anger_In_Him, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Adventure, heh. Excitement, heh. A Jedi craves not these things. You are reckless.'");
        setGameText("Use 4 Force to deploy on opponent's non-droid, non-Jedi Master character (or 2 Force on a Rebel). Character's game text is canceled and they draw one battle destiny if unable to otherwise. Opponent may lose three cards from hand (may not be reduced) to cancel this Effect.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.droid), Filters.not(Filters.Jedi_Master));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 2, Filters.Rebel));
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 4, Filters.not(Filters.Rebel)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, attachedTo));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, attachedTo, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.numCardsInHand(game, playerId) >= 3
                && GameConditions.canTargetToCancel(game, self, self)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Lose 3 cards from hand to cancel");
            action.appendCost(
                    new ChooseAndLoseCardsFromHandEffect(action, playerId, 3, 3, Filters.any));
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
