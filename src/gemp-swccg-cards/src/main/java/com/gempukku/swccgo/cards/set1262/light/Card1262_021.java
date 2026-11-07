package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceBattleDamageEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/** Gundark, Virtual Block 6.2. */
public class Card1262_021 extends AbstractCreature {
    public Card1262_021() {
        super(Side.LIGHT, 3, 4, null, 4, 0, "Gundark", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setGameText("* Ferocity = 3 + destiny. Habitat: forests, jungles, swamps and exterior Endor sites. During battle here (even if excluded), may place Gundark in Lost Pile to reduce your battle damage by 5.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.forest, Filters.jungle, Filters.swamp, Filters.and(Filters.Endor_site, Filters.exterior_site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DefinedByGameTextFerocityModifier(self, 3, 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return reduction(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(String playerId,
            SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (game.getGameState().isCardInPlayActive(self, true, false, false, false, false, false, false, false))
            return reduction(playerId, game, effectResult, self, gameTextSourceCardId);
        return null;
    }

    private List<OptionalGameTextTriggerAction> reduction(String playerId, SwccgGame game, EffectResult effectResult,
            PhysicalCard self, int gameTextSourceCardId) {
        if (playerId.equals(self.getOwner())
                && TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleAt(game, Filters.sameLocation(self))
                && GameConditions.isBattleDamageRemaining(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place Gundark in Lost Pile to reduce battle damage by 5");
            action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(new ReduceBattleDamageEffect(action, playerId, 5));
            return Collections.singletonList(action);
        }
        return null;
    }
}
