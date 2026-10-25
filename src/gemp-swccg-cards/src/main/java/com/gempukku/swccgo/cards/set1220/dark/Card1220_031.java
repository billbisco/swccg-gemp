package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MaximumToReduceDeployCostByToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Hoth Blockade
 */
public class Card1220_031 extends AbstractNormalEffect {
    public Card1220_031() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Hoth Blockade", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setLore("'Make ready to land our troops beyond their energy field and deploy the fleet so that nothing gets off the system.'");
        setGameText("Deploy on Hoth. Haven here is suspended. Executor is deploy -4 here (to a maximum of -6). For each marker site you control with an AT-AT and an Imperial, your total power here is +1. If opponent controls Hoth, place this Effect in Used Pile. (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Hoth_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter here = Filters.here(self);
        Filter markerWithATATAndImperial = Filters.and(Filters.marker_site,
                Filters.controlsWith(playerId, self, Filters.AT_AT),
                Filters.controlsWith(playerId, self, Filters.Imperial));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.Haven, here)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Executor, -4, here));
        modifiers.add(new MaximumToReduceDeployCostByToLocationModifier(self, Filters.Executor, 6, here));
        modifiers.add(new TotalPowerModifier(self, here, new OccupiesEvaluator(playerId, markerWithATATAndImperial), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, Filters.Hoth_system)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
