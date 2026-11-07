package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardsFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_049;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5: Strategic Reserves (V). */
public class Card1250_023 extends Card1116_049 {
    public Card1250_023() {
        setLore("The Empire is able to quickly relocate its manpower, deploying its troops as efficiently as possible.");
        setGameText("Deploy on table. Your clones and Coruscant guards are destiny +1 when drawn for weapon or battle destiny. Once per turn, may cancel a Force drain by placing here from hand a clone or Coruscant guard. Characters may deploy from here as if from hand.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    private Filter eligibleTroops() {
        return Filters.or(Filters.clone, Filters.Coruscant_Guard);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourTroops = Filters.and(Filters.your(self), eligibleTroops());
        return List.of(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourTroops, 1),
                new DestinyWhenDrawnForBattleDestinyModifier(self, yourTroops, 1),
                new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.character, Filters.stackedOn(self))));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                && GameConditions.hasInHand(game, playerId, eligibleTroops())) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, id);
            action.setText("Stack clone or Coruscant guard to cancel Force drain");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new StackCardsFromHandEffect(action, playerId, 1, 1, self, false, eligibleTroops()));
            action.appendEffect(new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
