package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Rally To Our Cause
 */
public class Card1291_028 extends AbstractMission {
    public Card1291_028() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Rally To Our Cause", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on War Has Begun. During your control phase, for each system where you occupy a related site, may draw one destiny; if destiny + X + Y > 7, stack top card of Lost Pile under that system (limit one per system); it is 'aligned' (Mission complete).\nX = number of 'aligned' systems on table.\nY = number of unique (•) [Separatist] characters on table.\n{Reward}: Your battle destiny draws are +X (limit +2).\n{Turn the Tide}: If opponent just initiated a Force drain at an 'aligned' system, place stacked cards there in Used Pile (system no longer 'aligned'). If there are no 'aligned' systems on table, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.SEPARATIST);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("War Has Begun");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("War Has Begun");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter aligned = Filters.and(Filters.system, Filters.hasStacked(Filters.any));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, new MaxLimitEvaluator(new OnTableEvaluator(self, aligned), 2), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        Filter aligned = Filters.and(Filters.system, Filters.hasStacked(Filters.any));
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, aligned)) {
            PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();
            Collection<PhysicalCard> stacked = Filters.filterStacked(game, Filters.stackedOn(system));
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place stacked cards in Used Pile");
            action.appendEffect(new PutStackedCardsInUsedPileEffect(action, opponent, stacked, true));
            if (!GameConditions.canSpot(game, self, Filters.and(Filters.system, Filters.hasStacked(Filters.any), Filters.not(Filters.sameCardId(system))))) {
                action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
