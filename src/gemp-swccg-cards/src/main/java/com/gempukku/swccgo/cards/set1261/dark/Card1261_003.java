package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 AT-AT Driver; departure snapshot comes from the integration prerequisite. */
public class Card1261_003 extends AbstractImperial {
    public Card1261_003() {
        super(Side.DARK, 3, 1, 0, 2, 2, "AT-AT Driver", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Piloting walkers high above the battlefield, AT-AT drivers are protected by 15 centimeters of reinforced armor. Accordingly, they are regarded with contempt by the infantry.");
        setGameText("Adds 2 to power of any combat vehicle he pilots. While piloting an AT-AT: draws one battle destiny if not able to otherwise, you may use 1 Force to deploy a vehicle site on this AT-AT from Reserve Deck; reshuffle, and if just lost, may use 1 Force to place this character in Used Pile.");
        addIcons(Icon.PILOT, Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.combat_vehicle),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.AT_AT), 1));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isPiloting(game, self, Filters.AT_AT)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            PhysicalCard walker = self.getAttachedTo();
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, id);
            action.setText("Deploy vehicle site from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action,
                    Filters.vehicle_site, Filters.sameCardId(walker), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromAttachedTo(game, result, self, Filters.AT_AT)
                && ((LostFromTableResult) result).wasInPilotCapacitySlot()
                && GameConditions.canUseForce(game, playerId, 1)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
