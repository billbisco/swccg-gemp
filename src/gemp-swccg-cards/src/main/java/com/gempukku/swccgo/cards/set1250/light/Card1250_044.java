package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 Master Qui-Gon (V), final standard and alternate faces. Text = the PC forum errata of 2011-03-21 with its comma ("You Are Beaten, and attrition"), as the forum and Master List print it (Bill 2026-11-11). */
public class Card1250_044 extends AbstractJediMaster {
    public Card1250_044() {
        super(Side.LIGHT, 1, 7, 6, 7, 7, "Master Qui-Gon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jedi Master currently not on the Council. Although he serves the Council well, there have been times when he has defied their wishes to pursue a path he believes is right.");
        setGameText("Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. May not be Disarmed. Immune to You Are Beaten, and attrition.");
        addPersona(Persona.QUIGON);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeployCostToLocationModifier(self, -1, Icon.EPISODE_I));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotBeDisarmedModifier(self), new ImmuneToTitleModifier(self, Title.You_Are_Beaten),
                new ImmuneToAttritionModifier(self));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.MASTER_QUIGON__UPLOAD_INTERRUPT_OR_STACK_CARD_ON_CREDITS_WILL_DO_FINE;
        if (!TriggerConditions.justLostForceFromCard(game, result, playerId, Filters.and(Filters.opponents(self), Filters.Effect))
                || !GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, id)) return null;

        List<OptionalGameTextTriggerAction> actions = new ArrayList<>(2);
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, id);
            action.setText("Take an Episode I Interrupt into hand");
            action.appendUsage(new OncePerForceLossEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.and(Filters.Interrupt, Icon.EPISODE_I, Filters.except(Filters.Control)), true));
            actions.add(action);
        }

        PhysicalCard credits = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);
        if (credits != null && GameConditions.hasHand(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, id);
            action.setText("Stack a card on Credits Will Do Fine");
            action.appendUsage(new OncePerForceLossEffect(action));
            action.appendEffect(new StackCardFromHandEffect(action, playerId, credits, true));
            actions.add(action);
        }
        return actions;
    }
}
