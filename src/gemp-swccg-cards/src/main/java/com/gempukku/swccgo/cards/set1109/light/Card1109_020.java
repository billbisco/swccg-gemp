package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: That's One
 */
public class Card1109_020 extends AbstractNormalEffect {
    public Card1109_020() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Thats_One, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Considering the life debt he owed to Han and his personal commitment to the Rebel Alliance, Chewie quickly volunteered to fly the stolen shuttle to Endor.");
        setGameText("Use 2 Force to deploy on Chewie or Back Door; may place up to two cards from hand on Force Pile. Your battle destiny draws are +1. Once per game, may place this Effect out of play to cancel a battle just initiated here.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS, Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Chewie, Filters.title(Title.Back_Door));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.Chewie, Filters.title(Title.Back_Door));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasHand(game, playerId)) {
            final OptionalGameTextTriggerAction place = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            place.setText("Place up to two cards from hand on Force Pile");
            place.appendEffect(
                    new PutCardsFromHandOnForcePileEffect(place, playerId, 1, 2));
            return Collections.singletonList(place);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OVS__THATS_ONE__CANCEL_BATTLE;
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final OptionalGameTextTriggerAction cancel = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            cancel.setText("Place out of play to cancel battle");
            cancel.appendUsage(
                    new OncePerGameEffect(cancel));
            cancel.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(cancel, self));
            cancel.appendEffect(
                    new CancelBattleEffect(cancel));
            return Collections.singletonList(cancel);
        }
        return null;
    }
}
